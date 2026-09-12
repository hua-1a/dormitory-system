package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.entity.RepairRecord;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 报修记录 Mapper
 */
public interface RepairRecordMapper extends BaseMapper<RepairRecord> {

    /**
     * 报修状态分布（用于首页图表）
     */
    @Select("SELECT CASE status WHEN 0 THEN '待处理' WHEN 1 THEN '处理中' ELSE '已完成' END AS name, " +
            "COUNT(*) AS value FROM repair_record GROUP BY status")
    List<Map<String, Object>> selectCountByStatus();
}
