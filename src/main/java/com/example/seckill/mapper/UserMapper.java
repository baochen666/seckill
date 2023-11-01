package com.example.seckill.mapper;

import com.example.seckill.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

     User getById(long id);

    void update(User toBeUpdate);
}
