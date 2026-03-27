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

    private String username;

    private String password;


    @TableId(type = IdType.AUTO)
    private Long id;

}