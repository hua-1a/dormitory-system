package com.dorm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生列表视图（含房间号）
 */
@Data
public class StudentVO {

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

    private String roomNo;

    private LocalDateTime createTime;
}
