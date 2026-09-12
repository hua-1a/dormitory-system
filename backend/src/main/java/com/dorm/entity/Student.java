package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生
 */
@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;

    private String name;

    private String gender;

    private String college;

    private String major;

    private String grade;

    private String phone;

    private Long roomId;

    private Integer status;

    private LocalDateTime createTime;
}
