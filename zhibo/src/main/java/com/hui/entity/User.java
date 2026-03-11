package com.hui.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * MP 实体类，对应 t_user 表
 * TableName 显式指定表名（或通过全局 table-prefix 省略）
 */
@Data
@TableName("t_user") // 等价于 @TableName("user") + 全局 table-prefix: t_
public class User {

    /**
     * PG 自增主键：用 IdType.AUTO 适配 BIGSERIAL
     * 注意：PG 中自增列需要先创建序列（CREATE SEQUENCE），但 BIGSERIAL 会自动创建
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学号（唯一），对应数据库 student_id
     * 驼峰转换开启后，无需手动指定 @TableField
     */
    private String studentId;

    private String name;       // 姓名，对应 name
    private String className;  // 班级，对应 class_name
    private String major;      // 专业，对应 major
    private String teamName;   // 队伍名，对应 team_name
}