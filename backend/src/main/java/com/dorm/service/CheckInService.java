package com.dorm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.CheckInRecord;
import com.dorm.entity.Dormitory;
import com.dorm.entity.Room;
import com.dorm.entity.Student;
import com.dorm.mapper.CheckInRecordMapper;
import com.dorm.mapper.DormitoryMapper;
import com.dorm.mapper.RoomMapper;
import com.dorm.mapper.StudentMapper;
import com.dorm.vo.CheckInVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入住/退宿管理
 */
@Service
public class CheckInService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private DormitoryMapper dormitoryMapper;
    @Resource
    private CheckInRecordMapper checkInRecordMapper;

    /**
     * 办理入住：更新学生、房间、楼栋占用数，并写入记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long studentId, Long roomId, String operator) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (student.getStatus() != null && student.getStatus() == 1) {
            throw new RuntimeException("该学生已入住，不能重复办理");
        }

        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getStatus() == null || room.getStatus() != 1) {
            throw new RuntimeException("该房间已停用");
        }
        if (room.getUsedCount() != null && room.getUsedCount() >= room.getBedCount()) {
            throw new RuntimeException("该房间已住满");
        }

        student.setRoomId(roomId);
        student.setStatus(1);
        studentMapper.updateById(student);

        room.setUsedCount(room.getUsedCount() == null ? 1 : room.getUsedCount() + 1);
        roomMapper.updateById(room);

        Dormitory dorm = dormitoryMapper.selectById(room.getDormitoryId());
        if (dorm != null) {
            dorm.setUsedBedCount(dorm.getUsedBedCount() == null ? 1 : dorm.getUsedBedCount() + 1);
            dormitoryMapper.updateById(dorm);
        }

        CheckInRecord record = new CheckInRecord();
        record.setStudentId(studentId);
        record.setRoomId(roomId);
        record.setType("IN");
        record.setOperator(operator);
        checkInRecordMapper.insert(record);
    }

    /**
     * 办理退宿：释放床位并写入记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Long studentId, String operator) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (student.getStatus() == null || student.getStatus() != 1 || student.getRoomId() == null) {
            throw new RuntimeException("该学生未入住");
        }

        Long roomId = student.getRoomId();
        Room room = roomMapper.selectById(roomId);
        if (room != null) {
            room.setUsedCount(Math.max(0, room.getUsedCount() - 1));
            roomMapper.updateById(room);

            Dormitory dorm = dormitoryMapper.selectById(room.getDormitoryId());
            if (dorm != null) {
                dorm.setUsedBedCount(Math.max(0, dorm.getUsedBedCount() - 1));
                dormitoryMapper.updateById(dorm);
            }
        }

        student.setRoomId(null);
        student.setStatus(0);
        studentMapper.updateById(student);

        CheckInRecord record = new CheckInRecord();
        record.setStudentId(studentId);
        record.setRoomId(roomId);
        record.setType("OUT");
        record.setOperator(operator);
        checkInRecordMapper.insert(record);
    }

    /**
     * 分页查询记录（数据量小，联表查询后内存分页）
     */
    public IPage<CheckInVO> page(int page, int size, String type) {
        List<CheckInVO> all = checkInRecordMapper.selectAllWithDetail();
        if (StringUtils.hasText(type)) {
            all = all.stream().filter(v -> type.equals(v.getType())).collect(Collectors.toList());
        }
        int total = all.size();
        int from = Math.min((page - 1) * size, total);
        int to = Math.min(page * size, total);
        Page<CheckInVO> result = new Page<>(page, size, total);
        result.setRecords(all.subList(from, to));
        return result;
    }
}
