package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.entity.CheckInRecord;
import com.dorm.vo.CheckInVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 入住记录 Mapper
 */
public interface CheckInRecordMapper extends BaseMapper<CheckInRecord> {

    /**
     * 查询全部记录（联表带学生、房间信息），按时间倒序
     */
    @Select("SELECT c.id, c.type, c.time, c.operator, s.name AS student_name, s.student_no AS student_no, " +
            "r.room_no AS room_no, d.building_no AS building_no " +
            "FROM check_in_record c " +
            "LEFT JOIN student s ON c.student_id = s.id " +
            "LEFT JOIN room r ON c.room_id = r.id " +
            "LEFT JOIN dormitory d ON r.dormitory_id = d.id " +
            "ORDER BY c.time DESC")
    List<CheckInVO> selectAllWithDetail();
}
