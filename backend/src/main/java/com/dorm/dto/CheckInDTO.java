package com.dorm.dto;

import lombok.Data;

/**
 * 办理入住请求
 */
@Data
public class CheckInDTO {

    private Long studentId;

    private Long roomId;

    private String operator;
}
