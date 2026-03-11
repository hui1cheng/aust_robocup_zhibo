package com.hui;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // 关键：排除数据库自动配置
public class Main {
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}