package com.dorm.controller;

import com.dorm.common.Result;
import com.dorm.entity.Dormitory;
import com.dorm.service.DormitoryService;
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

/**
 * 宿舍楼栋管理
 */
@RestController
@RequestMapping("/api/dormitories")
public class DormitoryController {

    @Resource
    private DormitoryService dormitoryService;

    @GetMapping
    public Result<List<Dormitory>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(dormitoryService.list(keyword));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Dormitory dormitory) {
        dormitoryService.save(dormitory);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Dormitory dormitory) {
        dormitoryService.update(dormitory);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dormitoryService.delete(id);
        return Result.ok();
    }
}
