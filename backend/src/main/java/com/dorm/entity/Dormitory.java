package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宿舍楼栋
 */
@Data
@TableName("dormitory")
public class Dormitory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String buildingNo;

    private Integer floorCount;

    private Integer roomCount;

    private Integer bedCount;

    private Integer usedBedCount;

    private String manager;

    private String phone;

    private String remark;

    private LocalDateTime createTime;
}
