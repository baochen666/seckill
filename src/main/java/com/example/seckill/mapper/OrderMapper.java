package com.example.seckill.mapper;

import com.example.seckill.bean.OrderInfo;
import com.example.seckill.bean.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderMapper {
    SeckillOrder getOrderByUserIdGoodsId(long userId,long goodsId);

    long insert(OrderInfo orderInfo);

    int insertSeckillOrder(SeckillOrder order);

    OrderInfo getOrderById(long orderId);


}
