package com.dorm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修记录视图（含房间号）
 */
@Data
public class RepairVO {

    private Long id;

    private Long roomId;

    private String roomNo;

    private String reporter;

    private String phone;

    private String content;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime finishTime;
}
