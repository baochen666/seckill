package com.example.seckill.mapper;

import com.example.seckill.bean.SeckillGoods;
import com.example.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsMapper {

    List<GoodsVo> listGoodsVo();

    GoodsVo getGoodsVoByGoodsId(long goodsId);

    int reduceStockByVersion(SeckillGoods seckillGoods);

    int getVersionByGoodsId(long goodsId);
}
