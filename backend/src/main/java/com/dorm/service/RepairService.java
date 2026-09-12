package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.RepairRecord;
import com.dorm.entity.Room;
import com.dorm.mapper.RepairRecordMapper;
import com.dorm.mapper.RoomMapper;
import com.dorm.vo.RepairVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报修管理
 */
@Service
public class RepairService {

    @Resource
    private RepairRecordMapper repairRecordMapper;
    @Resource
    private RoomMapper roomMapper;

    public IPage<RepairVO> page(int page, int size, Integer status) {
        Page<RepairRecord> p = new Page<>(page, size);
        repairRecordMapper.selectPage(p, new LambdaQueryWrapper<RepairRecord>()
                .eq(status != null, RepairRecord::getStatus, status)
                .orderByDesc(RepairRecord::getCreateTime));

        Map<Long, String> roomNoMap = roomMapper.selectList(null).stream()
                .collect(Collectors.toMap(Room::getId, Room::getRoomNo, (a, b) -> a));

        List<RepairVO> records = p.getRecords().stream().map(r -> {
            RepairVO vo = new RepairVO();
            vo.setId(r.getId());
            vo.setRoomId(r.getRoomId());
            vo.setRoomNo(roomNoMap.getOrDefault(r.getRoomId(), ""));
            vo.setReporter(r.getReporter());
            vo.setPhone(r.getPhone());
            vo.setContent(r.getContent());
            vo.setStatus(r.getStatus());
            vo.setCreateTime(r.getCreateTime());
            vo.setFinishTime(r.getFinishTime());
            return vo;
        }).collect(Collectors.toList());

        Page<RepairVO> voPage = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        voPage.setRecords(records);
        return voPage;
    }

    public void save(RepairRecord record) {
        record.setStatus(0);
        repairRecordMapper.insert(record);
    }

    public void update(RepairRecord record) {
        if (repairRecordMapper.selectById(record.getId()) == null) {
            throw new RuntimeException("报修记录不存在");
        }
        if (record.getStatus() != null && record.getStatus() == 2) {
            record.setFinishTime(LocalDateTime.now());
        }
        repairRecordMapper.updateById(record);
    }

    public void delete(Long id) {
        repairRecordMapper.deleteById(id);
    }
}
