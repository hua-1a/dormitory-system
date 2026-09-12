package com.dorm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dorm.common.Result;
import com.dorm.entity.Room;
import com.dorm.service.RoomService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 房间管理
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Resource
    private RoomService roomService;

    @GetMapping
    public Result<IPage<Map<String, Object>>> page(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) Long dormitoryId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status) {
        return Result.ok(roomService.page(page, size, dormitoryId, keyword, status));
    }

    /**
     * 全部房间（带楼栋名），用于报修等下拉选择
     */
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> all() {
        return Result.ok(roomService.listAll());
    }

    /**
     * 可入住房间（未住满且启用）
     */
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options() {
        return Result.ok(roomService.availableRooms());
    }

    @PostMapping
    public Result<Void> save(@RequestBody Room room) {
        roomService.save(room);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Room room) {
        roomService.update(room);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.ok();
    }
}
