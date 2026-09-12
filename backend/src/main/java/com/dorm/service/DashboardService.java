package com.dorm.service;

import com.dorm.entity.Dormitory;
import com.dorm.mapper.CheckInRecordMapper;
import com.dorm.mapper.DormitoryMapper;
import com.dorm.mapper.RepairRecordMapper;
import com.dorm.mapper.RoomMapper;
import com.dorm.mapper.StudentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页数据看板
 */
@Service
public class DashboardService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private DormitoryMapper dormitoryMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private CheckInRecordMapper checkInRecordMapper;
    @Resource
    private RepairRecordMapper repairRecordMapper;

    public Map<String, Object> stats() {
        Map<String, Object> data = new HashMap<>();

        long totalStudents = studentMapper.selectCount(null);
        long totalDorms = dormitoryMapper.selectCount(null);
        long totalRooms = roomMapper.selectCount(null);

        List<Dormitory> dormitories = dormitoryMapper.selectList(null);
        long totalBeds = dormitories.stream().mapToLong(d -> d.getBedCount() == null ? 0 : d.getBedCount()).sum();
        long usedBeds = dormitories.stream().mapToLong(d -> d.getUsedBedCount() == null ? 0 : d.getUsedBedCount()).sum();

        double occupancyRate = totalBeds == 0 ? 0
                : BigDecimal.valueOf(usedBeds * 100.0 / totalBeds).setScale(1, RoundingMode.HALF_UP).doubleValue();

        data.put("totalStudents", totalStudents);
        data.put("totalDorms", totalDorms);
        data.put("totalRooms", totalRooms);
        data.put("totalBeds", totalBeds);
        data.put("usedBeds", usedBeds);
        data.put("occupancyRate", occupancyRate);

        data.put("genderDist", studentMapper.countByGender());
        data.put("gradeDist", studentMapper.countByGrade());
        data.put("buildingStats", dormitoryMapper.selectBuildingStats());
        data.put("repairStats", repairRecordMapper.selectCountByStatus());

        List<?> recent = checkInRecordMapper.selectAllWithDetail();
        data.put("recentCheckIns", recent.size() > 8 ? recent.subList(0, 8) : recent);
        return data;
    }
}
