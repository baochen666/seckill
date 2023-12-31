package com.example.seckill.rabbitmq;

import com.example.seckill.redis.RedisService;
import com.example.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendSeckillMessage(SeckillMessage seckillMessage){
        String msg = RedisService.beanToString(seckillMessage);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    public void sendSeckillResult(GoodsVo goodsVo){
        String msg = RedisService.beanToString(goodsVo);
        log.info("send seckkillresult message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,"topic.key2",msg);
    }


}
