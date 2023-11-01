package com.example.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.seckill.bean.OrderInfo;
import com.example.seckill.bean.SeckillOrder;
import com.example.seckill.bean.User;
import com.example.seckill.rabbitmq.MQReceiver;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.redis.RedisService;
import com.example.seckill.redis.SeckillKey;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.websocket.MyMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class SeckillServiceImpl {

    private static Logger log = LoggerFactory.getLogger(SeckillServiceImpl.class);
    @Autowired
    GoodsServiceImpl goodsService;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @Autowired
    MyMessageHandler myMessageHandler;

    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods){
        boolean success=goodsService.reduceStock(goods);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (success){
                GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goods.getId());
                myMessageHandler.sendMessageToUI(JSON.toJSONString(goodsVo) );
//            sender.sendSeckillResult(goodsVo);
                return orderService.createOrder(user,goods);
            }else {
                setGoodsOver(goods.getId());
            }
            log.info("非常开心！！！！！秒杀成功");

            return null;
        }

    }

    @Transactional
    public long getSeckillResult(long userId, long goodsId){
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (Objects.nonNull(order)){
            return order.getOrderId();
        }else {
            boolean over = getGoodsOver(goodsId);
            if (over){
                return -1;
            }else {
                return 0;
            }
        }
    }



    private void setGoodsOver(long goodsId){
        redisService.set(SeckillKey.isGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId){
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }
}
