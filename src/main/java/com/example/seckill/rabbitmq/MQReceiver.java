package com.example.seckill.rabbitmq;

import com.example.seckill.bean.OrderInfo;
import com.example.seckill.bean.SeckillOrder;
import com.example.seckill.bean.User;
import com.example.seckill.controller.SeckillController;
import com.example.seckill.redis.RedisService;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.impl.SeckillServiceImpl;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.websocket.MyMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);


    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;


    @Autowired
    OrderService orderService;

    @Autowired
    SeckillServiceImpl seckillService;


    @Autowired
     MyMessageHandler messageHandler;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        SeckillMessage m = RedisService.stringToBean(message, SeckillMessage.class);
        User user = m.getUser();
        long goodsId = m.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 0) {
            log.info("Inventory shortage");
            return;
        }
        //判断重复秒杀
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (Objects.nonNull(order)) {
            log.info("Repeated actions");
            return;
        }
        //减库存 下订单 写入秒杀订单
        log.info("do seckill");
        seckillService.seckill(user, goodsVo);



    }

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveRes(String message) {
        log.info("receive seckkillresult message:" + message);
        GoodsVo goodsVo = RedisService.stringToBean(message, GoodsVo.class);
        messageHandler.sendMessageToUI(goodsVo);

    }


    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info(" topic  queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info(" topic  queue2 message:" + message);
    }


}
