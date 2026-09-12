package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.Dormitory;
import com.dorm.entity.Room;
import com.dorm.mapper.DormitoryMapper;
import com.dorm.mapper.RoomMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房间管理
 */
@Service
public class RoomService {

    @Resource
    private RoomMapper roomMapper;
    @Resource
    private DormitoryMapper dormitoryMapper;

    /**
     * 分页查询房间（带楼栋名）
     */
    public IPage<Map<String, Object>> page(int page, int size, Long dormitoryId, String keyword, Integer status) {
        Page<Room> p = new Page<>(page, size);
        LambdaQueryWrapper<Room> qw = new LambdaQueryWrapper<Room>()
                .eq(dormitoryId != null, Room::getDormitoryId, dormitoryId)
                .eq(status != null, Room::getStatus, status)
                .like(StringUtils.hasText(keyword), Room::getRoomNo, keyword)
                .orderByAsc(Room::getRoomNo);
        Page<Room> result = roomMapper.selectPage(p, qw);

        Map<Long, String> buildingMap = buildingMap();
        List<Map<String, Object>> records = result.getRecords().stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("dormitoryId", r.getDormitoryId());
            m.put("buildingNo", buildingMap.getOrDefault(r.getDormitoryId(), ""));
            m.put("roomNo", r.getRoomNo());
            m.put("bedCount", r.getBedCount());
            m.put("usedCount", r.getUsedCount());
            m.put("status", r.getStatus());
            return m;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> vo = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        vo.setRecords(records);
        return vo;
    }

    /**
     * 全部房间（带楼栋名），用于报修等下拉选择
     */
    public List<Map<String, Object>> listAll() {
        Map<Long, String> buildingMap = buildingMap();
        return roomMapper.selectList(new LambdaQueryWrapper<Room>().orderByAsc(Room::getRoomNo))
                .stream().map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", r.getId());
                    m.put("roomNo", r.getRoomNo());
                    m.put("buildingNo", buildingMap.getOrDefault(r.getDormitoryId(), ""));
                    return m;
                }).collect(Collectors.toList());
    }

    /**
     * 可入住房间（状态可用且未住满）
     */
    public List<Map<String, Object>> availableRooms() {
        Map<Long, String> buildingMap = buildingMap();
        return roomMapper.selectList(new LambdaQueryWrapper<Room>()
                        .eq(Room::getStatus, 1)
                        .apply("used_count < bed_count")
                        .orderByAsc(Room::getRoomNo))
                .stream().map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", r.getId());
                    m.put("roomNo", r.getRoomNo());
                    m.put("buildingNo", buildingMap.getOrDefault(r.getDormitoryId(), ""));
                    m.put("usedCount", r.getUsedCount());
                    m.put("bedCount", r.getBedCount());
                    return m;
                }).collect(Collectors.toList());
    }

    public void save(Room room) {
        if (dormitoryMapper.selectById(room.getDormitoryId()) == null) {
            throw new RuntimeException("楼栋不存在");
        }
        Long count = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getDormitoryId, room.getDormitoryId())
                .eq(Room::getRoomNo, room.getRoomNo()));
        if (count > 0) {
            throw new RuntimeException("该楼栋下房间号已存在：" + room.getRoomNo());
        }
        if (room.getBedCount() == null || room.getBedCount() < 1) {
            room.setBedCount(4);
        }
        room.setUsedCount(0);
        room.setStatus(1);
        roomMapper.insert(room);
        refreshDormitoryCounts(room.getDormitoryId());
    }

    public void update(Room room) {
        Room old = roomMapper.selectById(room.getId());
        if (old == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getBedCount() != null && room.getBedCount() < old.getUsedCount()) {
            throw new RuntimeException("床位数不能小于已入住人数");
        }
        roomMapper.updateById(room);
        refreshDormitoryCounts(old.getDormitoryId());
    }

    public void delete(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getUsedCount() != null && room.getUsedCount() > 0) {
            throw new RuntimeException("该房间还有学生入住，无法删除");
        }
        roomMapper.deleteById(id);
        refreshDormitoryCounts(room.getDormitoryId());
    }

    private Map<Long, String> buildingMap() {
        return dormitoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Dormitory::getId, Dormitory::getBuildingNo));
    }

    /**
     * 根据房间重新统计楼栋的房间数与床位数
     */
    private void refreshDormitoryCounts(Long dormitoryId) {
        List<Room> rooms = roomMapper.selectList(
                new LambdaQueryWrapper<Room>().eq(Room::getDormitoryId, dormitoryId));
        Dormitory d = new Dormitory();
        d.setId(dormitoryId);
        d.setRoomCount(rooms.size());
        d.setBedCount(rooms.stream().mapToInt(r -> r.getBedCount() == null ? 0 : r.getBedCount()).sum());
        dormitoryMapper.updateById(d);
    }
}
