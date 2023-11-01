package com.example.seckill.service;

import com.example.seckill.bean.SeckillGoods;
import com.example.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoodsService {

     List<GoodsVo> listGoodsVo();

     GoodsVo getGoodsVoByGoodsId(long goodsId);

     int reduceStockByVersion(SeckillGoods seckillGoods);

     int getVersionByGoodsId(long goodsId);
}
