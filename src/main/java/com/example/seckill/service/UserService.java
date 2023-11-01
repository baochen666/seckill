package com.example.seckill.service;

import com.example.seckill.bean.User;
import com.example.seckill.vo.LoginVo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface UserService {

    String login(HttpServletResponse response, LoginVo loginVo);

    void update(User toBeUpdate);
}
