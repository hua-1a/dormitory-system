package com.dorm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dorm.common.Result;
import com.dorm.dto.CheckInDTO;
import com.dorm.dto.CheckOutDTO;
import com.dorm.service.CheckInService;
import com.dorm.vo.CheckInVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 入住/退宿管理
 */
@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Resource
    private CheckInService checkInService;

    @GetMapping
    public Result<IPage<CheckInVO>> page(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String type) {
        return Result.ok(checkInService.page(page, size, type));
    }

    @PostMapping
    public Result<Void> checkIn(@RequestBody CheckInDTO dto) {
        checkInService.checkIn(dto.getStudentId(), dto.getRoomId(), dto.getOperator());
        return Result.ok();
    }

    @PostMapping("/checkout")
    public Result<Void> checkOut(@RequestBody CheckOutDTO dto) {
        checkInService.checkOut(dto.getStudentId(), dto.getOperator());
        return Result.ok();
    }
}
