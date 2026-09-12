package com.dorm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dorm.common.Result;
import com.dorm.entity.Student;
import com.dorm.service.StudentService;
import com.dorm.vo.StudentVO;
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
 * 学生管理
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Resource
    private StudentService studentService;

    @GetMapping
    public Result<IPage<StudentVO>> page(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String gender) {
        return Result.ok(studentService.page(page, size, keyword, gender));
    }

    /**
     * 学生下拉选项：?status=0 未入住 / 1 已入住
     */
    @GetMapping("/options")
    public Result<List<Student>> options(@RequestParam(required = false) Integer status) {
        return Result.ok(studentService.options(status));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Student student) {
        studentService.save(student);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Student student) {
        studentService.update(student);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.ok();
    }
}
