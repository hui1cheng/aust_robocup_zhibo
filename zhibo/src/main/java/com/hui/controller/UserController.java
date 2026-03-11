package com.hui.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hui.entity.User;
import com.hui.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 新增
    @PostMapping("/save")
    public String save(@RequestBody User user) {
        int rows = userMapper.insert(user);
        return rows > 0 ? "新增成功" : "新增失败";
    }

    // 根据学号查询（唯一）
    @GetMapping("/{studentId}")
    public User getByStudentId(@PathVariable String studentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, studentId);
        return userMapper.selectOne(wrapper);
    }

    // 查询所有
    @GetMapping("/list")
    public List<User> list() {
        return userMapper.selectList(null);
    }

    // 修改
    @PutMapping("/update")
    public String update(@RequestBody User user) {
        int rows = userMapper.updateById(user);
        return rows > 0 ? "修改成功" : "修改失败";
    }

    // 删除
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        int rows = userMapper.deleteById(id);
        return rows > 0 ? "删除成功" : "删除失败";
    }
}