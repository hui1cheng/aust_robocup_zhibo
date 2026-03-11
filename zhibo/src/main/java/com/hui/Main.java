package com.hui;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan("com.hui.mapper") // 扫描 Mapper 包
public class Main {
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}