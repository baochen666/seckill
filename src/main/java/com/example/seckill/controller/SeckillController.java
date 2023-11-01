package com.example.seckill.controller;

import com.example.seckill.bean.SeckillOrder;
import com.example.seckill.bean.User;
import com.example.seckill.rabbitmq.MQConfig;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.rabbitmq.SeckillMessage;
import com.example.seckill.redis.GoodsKey;
import com.example.seckill.redis.RedisService;
import com.example.seckill.result.CodeMsg;
import com.example.seckill.result.Result;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.impl.GoodsServiceImpl;
import com.example.seckill.service.impl.OrderServiceImpl;
import com.example.seckill.service.impl.SeckillServiceImpl;
import com.example.seckill.vo.GoodsVo;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsServiceImpl goodsService;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    SeckillServiceImpl seckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    //基于令牌桶算法的限流实现类
    RateLimiter rateLimiter = RateLimiter.create(10);

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    private static Logger log = LoggerFactory.getLogger(SeckillController.class);

    /**
     * GET POST
     * 1、GET幂等,服务端获取数据，无论调用多少次结果都一样
     * 2、POST，向服务端提交数据，不是幂等
     * <p>
     * 将同步下单改为异步下单
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST, produces = "text/html")
//    @ResponseBody
    public String list(HttpServletResponse response,Model model, User user, @RequestParam("goodsId") long goodsId) throws Exception {
        response.setContentType("text/html");
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
//            return  Result.error(CodeMsg.ACCESS_LIMIT_REACHED);

            return "error.html";
        }
        if (!Objects.nonNull(user)){
//            return Result.error(CodeMsg.SESSION_ERROR);
            return "error.html";
        }

        model.addAttribute("user",user);

        if (Objects.nonNull(localOverMap.get(goodsId))&&localOverMap.get(goodsId).equals(true)){
//            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //预减库存
        Long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
        if (stock<0){
            afterPropertiesSet();
            Long stock2 = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
            if (stock2<0){
                localOverMap.put(goodsId,true);
//                return Result.error(CodeMsg.SECKILL_OVER);
                return "error.html";
            }
        }
        //System.out.println(user);
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (Objects.nonNull(order)){
//            return Result.error(CodeMsg.REPEATE_SECKILL);
            return "error.html";
        }
        //入队
        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        sender.sendSeckillMessage(seckillMessage);
//        return Result.success("排队中");//排队中
        return "redirect:/success.html";
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId){

        model.addAttribute("user",user);
        if (Objects.nonNull(user)){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(orderId);
    }

//    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
//    public GoodsVo receiveRes(String message){
//        log.info("receive message:" +  message);
//        return  RedisService.stringToBean(message, GoodsVo.class);
//    }


    @RequestMapping(produces = "text/html")

    public String seckillSuccess(HttpServletRequest request, HttpServletResponse response, Model model){




        return "success";
    }










    /**
     * 系统初始化,将商品信息加载到redis和本地内存
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        //System.out.println(goodsVos.toString());
        if (!Objects.nonNull(goodsVos)){
            return;
        }
        for (GoodsVo goods:goodsVos
             ) {
            if (redisService.set(GoodsKey.getGoodsStock,""+goods.getId(),goods.getGoodsStock())){
                localOverMap.put(goods.getId(),false);
            }else {
                return ;
            }

        }

    }
}
