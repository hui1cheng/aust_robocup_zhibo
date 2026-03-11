package com.hui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hui.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 继承 MP 的 BaseMapper，无需写基础 CRUD SQL
 */
@Mapper // 或在启动类加 @MapperScan("com.example.demo.mapper")
public interface UserMapper extends BaseMapper<User> {
    // 基础 CRUD 无需手动实现，MP 自动生成
    // 可自定义复杂 SQL，比如根据专业查询：
    // List<User> selectByMajor(@Param("major") String major);
}