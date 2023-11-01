package com.example.seckill.service.impl;

import com.example.seckill.bean.OrderInfo;
import com.example.seckill.bean.SeckillOrder;
import com.example.seckill.bean.User;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.redis.OrderKey;
import com.example.seckill.redis.RedisService;
import com.example.seckill.service.OrderService;
import com.example.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RedisService redisService;

    @Override
    public SeckillOrder getOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderMapper.getOrderByUserIdGoodsId(userId, goodsId);
    }

    @Override
    public long insert(OrderInfo orderInfo) {
        return orderMapper.insert(orderInfo);
    }

    @Override
    public int insertSeckillOrder(SeckillOrder order) {
        return orderMapper.insertSeckillOrder(order);
    }

    @Override
    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }


    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderMapper.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderMapper.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), seckillOrder);
        return orderInfo;
    }
}
