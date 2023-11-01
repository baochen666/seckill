package com.example.seckill.rabbitmq;

import com.example.seckill.bean.User;

/*
消息体
 */
public class SeckillMessage {
    private User user;
    private long goodsId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public SeckillMessage() {
    }

    public SeckillMessage(User user, long goodsId) {
        this.user = user;
        this.goodsId = goodsId;
    }
}
