package com.hui.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hui.entity.User;
import com.hui.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println(">>>> [后端收到请求] 学号: " + user.getUsername());
        Map<String, Object> result = new HashMap<>();

        String username = user.getUsername();
        String password = user.getPassword();

        // 1. 校验非空
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            result.put("msg", "用户名或密码不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        // 2. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser == null) {
            result.put("msg", "用户不存在");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        // 3. 校验密码
        if (!dbUser.getPassword().equals(password)) {
            result.put("msg", "密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        // 4. 登录成功，生成 Token
        String token = "1212";

        result.put("code", 200);
        result.put("msg", "登录成功");
        result.put("token", token);
        result.put("username", username);

        return ResponseEntity.ok(result);
    }
}