package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.Student;
import com.dorm.mapper.StudentMapper;
import com.dorm.vo.StudentVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 学生管理
 */
@Service
public class StudentService {

    @Resource
    private StudentMapper studentMapper;

    public IPage<StudentVO> page(int page, int size, String keyword, String gender) {
        return studentMapper.selectPageWithRoom(new Page<>(page, size), keyword, gender);
    }

    public void save(Student student) {
        Long count = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, student.getStudentNo()));
        if (count > 0) {
            throw new RuntimeException("学号已存在：" + student.getStudentNo());
        }
        student.setStatus(0);
        student.setRoomId(null);
        studentMapper.insert(student);
    }

    public void update(Student student) {
        if (studentMapper.selectById(student.getId()) == null) {
            throw new RuntimeException("学生不存在");
        }
        Long count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, student.getStudentNo())
                .ne(Student::getId, student.getId()));
        if (count > 0) {
            throw new RuntimeException("学号已存在：" + student.getStudentNo());
        }
        studentMapper.updateById(student);
    }

    public void delete(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (student.getStatus() != null && student.getStatus() == 1) {
            throw new RuntimeException("该学生已入住，请先办理退宿");
        }
        studentMapper.deleteById(id);
    }

    /**
     * 学生下拉选项：status=0 未入住 / 1 已入住
     */
    public List<Student> options(Integer status) {
        return studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .eq(status != null, Student::getStatus, status)
                .orderByAsc(Student::getStudentNo));
    }
}
