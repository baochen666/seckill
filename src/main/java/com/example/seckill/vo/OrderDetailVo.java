package com.example.seckill.vo;

import com.example.seckill.bean.OrderInfo;

public class OrderDetailVo {
    private GoodsVo goods;

    private OrderInfo order;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }

}
