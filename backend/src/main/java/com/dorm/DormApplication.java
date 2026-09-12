package com.dorm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 宿舍管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.dorm.mapper")
public class DormApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormApplication.class, args);
        System.out.println("宿舍管理系统后端启动成功：http://localhost:8080");
    }
}
