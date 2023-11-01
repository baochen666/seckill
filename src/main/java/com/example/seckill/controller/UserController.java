package com.example.seckill.controller;

import com.example.seckill.bean.User;
import com.example.seckill.redis.RedisService;
import com.example.seckill.result.Result;
import com.example.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    public Result<User> info(Model model, User user) {
        return Result.success(user);
    }

}
