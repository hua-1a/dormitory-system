package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.Student;
import com.dorm.vo.StudentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 学生 Mapper
 */
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生（左连接房间，带房间号）
     */
    @Select("<script>" +
            "SELECT s.id, s.student_no, s.name, s.gender, s.college, s.major, s.grade, s.phone, s.room_id, s.status, s.create_time, r.room_no AS room_no " +
            "FROM student s LEFT JOIN room r ON s.room_id = r.id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (s.name LIKE CONCAT('%', #{keyword}, '%') OR s.student_no LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "  <if test='gender != null and gender != \"\"'>AND s.gender = #{gender}</if>" +
            "</where>" +
            "ORDER BY s.create_time DESC" +
            "</script>")
    IPage<StudentVO> selectPageWithRoom(Page<?> page, @Param("keyword") String keyword, @Param("gender") String gender);

    /**
     * 性别分布
     */
    @Select("SELECT gender AS name, COUNT(*) AS value FROM student GROUP BY gender")
    List<Map<String, Object>> countByGender();

    /**
     * 年级分布
     */
    @Select("SELECT grade AS name, COUNT(*) AS value FROM student GROUP BY grade ORDER BY grade DESC")
    List<Map<String, Object>> countByGrade();
}
