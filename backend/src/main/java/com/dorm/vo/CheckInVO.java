package com.dorm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入住记录视图（含学生、房间信息）
 */
@Data
public class CheckInVO {

    private Long id;

    private String studentName;

    private String studentNo;

    private String roomNo;

    private String buildingNo;

    private String type;

    private LocalDateTime time;

    private String operator;
}
