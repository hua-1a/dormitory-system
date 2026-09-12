package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.entity.Dormitory;
import com.dorm.entity.Room;
import com.dorm.mapper.DormitoryMapper;
import com.dorm.mapper.RoomMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宿舍楼栋管理
 */
@Service
public class DormitoryService {

    @Resource
    private DormitoryMapper dormitoryMapper;
    @Resource
    private RoomMapper roomMapper;

    public List<Dormitory> list(String keyword) {
        return dormitoryMapper.selectList(new LambdaQueryWrapper<Dormitory>()
                .like(StringUtils.hasText(keyword), Dormitory::getBuildingNo, keyword)
                .orderByAsc(Dormitory::getBuildingNo));
    }

    public void save(Dormitory dormitory) {
        Long count = dormitoryMapper.selectCount(
                new LambdaQueryWrapper<Dormitory>().eq(Dormitory::getBuildingNo, dormitory.getBuildingNo()));
        if (count > 0) {
            throw new RuntimeException("楼栋编号已存在：" + dormitory.getBuildingNo());
        }
        if (dormitory.getFloorCount() == null || dormitory.getFloorCount() < 1) {
            dormitory.setFloorCount(6);
        }
        dormitory.setRoomCount(0);
        dormitory.setBedCount(0);
        dormitory.setUsedBedCount(0);
        dormitoryMapper.insert(dormitory);
    }

    public void update(Dormitory dormitory) {
        if (dormitoryMapper.selectById(dormitory.getId()) == null) {
            throw new RuntimeException("楼栋不存在");
        }
        Long count = dormitoryMapper.selectCount(new LambdaQueryWrapper<Dormitory>()
                .eq(Dormitory::getBuildingNo, dormitory.getBuildingNo())
                .ne(Dormitory::getId, dormitory.getId()));
        if (count > 0) {
            throw new RuntimeException("楼栋编号已存在：" + dormitory.getBuildingNo());
        }
        dormitoryMapper.updateById(dormitory);
    }

    public void delete(Long id) {
        Long roomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>().eq(Room::getDormitoryId, id));
        if (roomCount > 0) {
            throw new RuntimeException("该楼栋下还有 " + roomCount + " 个房间，请先删除房间");
        }
        dormitoryMapper.deleteById(id);
    }
}
