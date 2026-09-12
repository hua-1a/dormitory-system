package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.entity.Dormitory;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 楼栋 Mapper
 */
public interface DormitoryMapper extends BaseMapper<Dormitory> {

    /**
     * 各楼栋统计（用于首页图表）
     */
    @Select("SELECT building_no AS name, room_count, bed_count, used_bed_count FROM dormitory ORDER BY building_no")
    List<Map<String, Object>> selectBuildingStats();
}
