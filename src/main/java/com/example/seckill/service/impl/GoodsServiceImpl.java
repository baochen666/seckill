package com.example.seckill.service.impl;

import com.example.seckill.bean.SeckillGoods;
import com.example.seckill.mapper.GoodsMapper;
import com.example.seckill.service.GoodsService;
import com.example.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    GoodsMapper goodsMapper;

    public List<GoodsVo> listGoodsVo(){
        return goodsMapper.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }


    @Override
    public int reduceStockByVersion(SeckillGoods seckillGoods) {
        return goodsMapper.reduceStockByVersion(seckillGoods);
    }

    @Override
    public int getVersionByGoodsId(long goodsId) {
        return goodsMapper.getVersionByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods){
        int numAttempts=0;
        int ret =0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goods.getId());
        sg.setVersion(goods.getVersion());
        do {
           numAttempts++;
           try{
               sg.setVersion(goodsMapper.getVersionByGoodsId(goods.getId()));
               ret = goodsMapper.reduceStockByVersion(sg);
           }catch (Exception e){
               e.printStackTrace();
           }
           if(ret!=0){
               break;
           }

        }while (numAttempts < DEFAULT_MAX_RETRIES);
        return ret>0;

    }
}
