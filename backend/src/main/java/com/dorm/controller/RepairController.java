package com.dorm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dorm.common.Result;
import com.dorm.entity.RepairRecord;
import com.dorm.service.RepairService;
import com.dorm.vo.RepairVO;
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

/**
 * 报修管理
 */
@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    @Resource
    private RepairService repairService;

    @GetMapping
    public Result<IPage<RepairVO>> page(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Integer status) {
        return Result.ok(repairService.page(page, size, status));
    }

    @PostMapping
    public Result<Void> save(@RequestBody RepairRecord record) {
        repairService.save(record);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody RepairRecord record) {
        repairService.update(record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        repairService.delete(id);
        return Result.ok();
    }
}
