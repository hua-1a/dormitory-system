package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修记录
 */
@Data
@TableName("repair_record")
public class RepairRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private String reporter;

    private String phone;

    private String content;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime finishTime;
}
