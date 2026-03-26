package com.hui.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hui.entity.User;
import com.hui.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    //只要负责登录就可以了
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        //1. 获取用户名和密码
        String username = user.getUsername();
        String password = user.getPassword();

        //2. 判断用户名和密码不能为空
        if (username == null || "".equals(username) || password == null || "".equals(password)) {
            return "用户名或密码不能为空";
        }

        //3. 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User dbUser = userMapper.selectOne(queryWrapper);
        if (dbUser == null) {
            return "用户不存在";
        }
        if (!dbUser.getPassword().equals(password)) {
            return "密码错误";
        }
        return "登录成功";
    }
}