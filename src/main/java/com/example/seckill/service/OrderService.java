package com.example.seckill.service;

import com.example.seckill.bean.OrderInfo;
import com.example.seckill.bean.SeckillOrder;
import com.example.seckill.mapper.OrderMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    SeckillOrder getOrderByUserIdGoodsId(long userId,long goodsId);

    long insert(OrderInfo orderInfo);

    int insertSeckillOrder(SeckillOrder order);

    OrderInfo getOrderById(long orderId);




}
