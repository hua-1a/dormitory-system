package com.dorm.dto;

import lombok.Data;

/**
 * 办理退宿请求
 */
@Data
public class CheckOutDTO {

    private Long studentId;

    private String operator;
}
