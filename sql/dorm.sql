-- ============================================================
-- 宿舍管理系统 数据库初始化脚本
-- 适用：MySQL 5.7+ / 8.0
-- 说明：执行本脚本会删除并重建 dorm_manage 数据库，并写入示例数据
-- 默认账号：admin / 123456（密码为 MD5 加密存储）
-- ============================================================

DROP DATABASE IF EXISTS dorm_manage;
CREATE DATABASE dorm_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE dorm_manage;

-- ----------------------------
-- 1. 系统用户表
-- ----------------------------
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(MD5)',
    real_name   VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
    role        VARCHAR(20)  DEFAULT 'ADMIN' COMMENT '角色',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT = '系统用户表';

-- ----------------------------
-- 2. 宿舍楼栋表
-- ----------------------------
CREATE TABLE dormitory (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    building_no    VARCHAR(20)  NOT NULL COMMENT '楼栋编号，如A栋',
    floor_count    INT          DEFAULT 6 COMMENT '层数',
    room_count     INT          DEFAULT 0 COMMENT '房间数',
    bed_count      INT          DEFAULT 0 COMMENT '总床位数',
    used_bed_count INT          DEFAULT 0 COMMENT '已入住床位数',
    manager        VARCHAR(50)  DEFAULT NULL COMMENT '宿管员',
    phone          VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    remark         VARCHAR(200) DEFAULT NULL COMMENT '备注',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_building_no (building_no)
) ENGINE = InnoDB COMMENT = '宿舍楼栋表';

-- ----------------------------
-- 3. 房间表
-- ----------------------------
CREATE TABLE room (
    id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    dormitory_id BIGINT      NOT NULL COMMENT '楼栋ID',
    room_no      VARCHAR(20) NOT NULL COMMENT '房间号，如A-101',
    bed_count    INT         DEFAULT 4 COMMENT '床位数',
    used_count   INT         DEFAULT 0 COMMENT '已入住人数',
    status       TINYINT     DEFAULT 1 COMMENT '状态：1可用 0停用',
    create_time  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room (dormitory_id, room_no)
) ENGINE = InnoDB COMMENT = '房间表';

-- ----------------------------
-- 4. 学生表
-- ----------------------------
CREATE TABLE student (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_no  VARCHAR(20) NOT NULL COMMENT '学号',
    name        VARCHAR(50) NOT NULL COMMENT '姓名',
    gender      VARCHAR(10) DEFAULT NULL COMMENT '性别：男/女',
    college     VARCHAR(50) DEFAULT NULL COMMENT '学院',
    major       VARCHAR(50) DEFAULT NULL COMMENT '专业',
    grade       VARCHAR(10) DEFAULT NULL COMMENT '年级',
    phone       VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    room_id     BIGINT      DEFAULT NULL COMMENT '房间ID，NULL为未入住',
    status      TINYINT     DEFAULT 0 COMMENT '状态：0未入住 1已入住',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (student_no)
) ENGINE = InnoDB COMMENT = '学生表';

-- ----------------------------
-- 5. 入住/退宿记录表
-- ----------------------------
CREATE TABLE check_in_record (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT      NOT NULL COMMENT '学生ID',
    room_id    BIGINT      NOT NULL COMMENT '房间ID',
    type       VARCHAR(10) NOT NULL DEFAULT 'IN' COMMENT '类型：IN入住 OUT退宿',
    time       DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    operator   VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    PRIMARY KEY (id),
    KEY idx_time (time)
) ENGINE = InnoDB COMMENT = '入住/退宿记录表';

-- ----------------------------
-- 6. 报修记录表
-- ----------------------------
CREATE TABLE repair_record (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    room_id     BIGINT       DEFAULT NULL COMMENT '房间ID',
    reporter    VARCHAR(50)  DEFAULT NULL COMMENT '报修人',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    content     VARCHAR(500) DEFAULT NULL COMMENT '报修内容',
    status      TINYINT      DEFAULT 0 COMMENT '状态：0待处理 1处理中 2已完成',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
    finish_time DATETIME     DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '报修记录表';

-- ----------------------------
-- 默认账号：admin / 123456
-- ----------------------------
INSERT INTO sys_user (username, password, real_name, role) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'ADMIN');

-- ----------------------------
-- 示例数据：楼栋
-- ----------------------------
INSERT INTO dormitory (building_no, floor_count, room_count, bed_count, used_bed_count, manager, phone, remark) VALUES
('A栋', 6, 36, 144, 0, '王阿姨', '13800000001', '男生宿舍'),
('B栋', 6, 36, 144, 0, '李阿姨', '13800000002', '女生宿舍'),
('C栋', 6, 36, 144, 0, '张叔叔', '13800000003', '研究生宿舍');

-- ----------------------------
-- 示例数据：房间、学生、入住记录（存储过程生成，可复现）
-- ----------------------------
DELIMITER $$
CREATE PROCEDURE seed_dorm_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE floor_no INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    DECLARE dorm_id BIGINT DEFAULT 0;
    DECLARE room_no VARCHAR(20) DEFAULT '';
    DECLARE s_no VARCHAR(20) DEFAULT '';
    DECLARE s_name VARCHAR(50) DEFAULT '';
    DECLARE s_gender VARCHAR(10) DEFAULT '';
    DECLARE s_college VARCHAR(50) DEFAULT '';
    DECLARE s_major VARCHAR(50) DEFAULT '';
    DECLARE s_grade VARCHAR(10) DEFAULT '';
    DECLARE s_phone VARCHAR(20) DEFAULT '';
    DECLARE r_id BIGINT DEFAULT NULL;

    -- 1) 生成房间：每栋楼 6 层 × 每层 6 间 = 36 间（A栋1~36、B栋37~72、C栋73~108）
    WHILE i <= 3 DO
        SELECT id INTO dorm_id FROM dormitory WHERE building_no = CONCAT(CHAR(64 + i), '栋') LIMIT 1;
        SET floor_no = 1;
        WHILE floor_no <= 6 DO
            SET j = 1;
            WHILE j <= 6 DO
                SET room_no = CONCAT(CHAR(64 + i), '-', LPAD(floor_no, 2, '0'), LPAD(j, 2, '0'));
                INSERT INTO room (dormitory_id, room_no, bed_count) VALUES (dorm_id, room_no, 4);
                SET j = j + 1;
            END WHILE;
            SET floor_no = floor_no + 1;
        END WHILE;
        SET i = i + 1;
    END WHILE;

    -- 2) 生成 260 名学生，约 80% 入住
    SET i = 1;
    WHILE i <= 260 DO
        SET s_no = CONCAT('2023', LPAD(500 + i, 4, '0'));
        SET s_name = CONCAT(
            ELT(MOD(i, 20) + 1, '张','李','王','赵','刘','陈','杨','黄','周','吴','徐','孙','马','朱','胡','郭','何','林','罗','郑'),
            ELT(MOD(i * 7, 20) + 1, '伟','芳','娜','敏','静','磊','军','洋','勇','艳','杰','娟','涛','明','超','霞','平','刚','雪','强'));
        SET s_gender = ELT(MOD(i, 2) + 1, '男', '女');
        SET s_college = ELT(MOD(i, 6) + 1, '计算机学院','软件学院','经济管理学院','外国语学院','机械工程学院','电气工程学院');
        SET s_major = ELT(MOD(i, 7) + 1, '计算机科学与技术','软件工程','信息管理与信息系统','金融学','英语','机械设计制造及其自动化','电气工程及其自动化');
        SET s_grade = ELT(MOD(i, 10) + 1, '2024级','2023级','2023级','2023级','2023级','2022级','2022级','2022级','2021级','2021级');
        SET s_phone = CONCAT('138', LPAD(10000000 + i * 37, 8, '0'));
        SET r_id = NULL;

        IF MOD(i, 5) <> 0 THEN
            IF MOD(i, 20) = 0 THEN
                SET r_id = MOD(i, 36) + 73;   -- C栋 73~108
            ELSEIF s_gender = '男' THEN
                SET r_id = MOD(i, 36) + 1;    -- A栋 1~36
            ELSE
                SET r_id = MOD(i, 36) + 37;   -- B栋 37~72
            END IF;
        END IF;

        INSERT INTO student (student_no, name, gender, college, major, grade, phone, room_id, status)
        VALUES (s_no, s_name, s_gender, s_college, s_major, s_grade, s_phone, r_id, IF(r_id IS NULL, 0, 1));
        SET i = i + 1;
    END WHILE;

    -- 3) 同步房间已住人数、楼栋已住床位数
    UPDATE room r SET used_count = (SELECT COUNT(*) FROM student s WHERE s.room_id = r.id);
    UPDATE dormitory d SET used_bed_count = (SELECT COALESCE(SUM(r.used_count), 0) FROM room r WHERE r.dormitory_id = d.id);

    -- 4) 生成入住记录
    INSERT INTO check_in_record (student_id, room_id, type, time, operator)
    SELECT id, room_id, 'IN', DATE_SUB(NOW(), INTERVAL MOD(id, 45) DAY), '系统导入'
    FROM student WHERE room_id IS NOT NULL;
END$$
DELIMITER ;

CALL seed_dorm_data();
DROP PROCEDURE seed_dorm_data;

-- ----------------------------
-- 示例数据：报修记录
-- ----------------------------
INSERT INTO repair_record (room_id, reporter, phone, content, status, create_time) VALUES
(1,  '张伟', '13800001001', '宿舍灯管坏了，需要更换',     0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5,  '李娜', '13800001002', '空调不制冷，制冷效果差',     1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(12, '王芳', '13800001003', '门锁损坏，无法正常反锁',     0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(18, '刘敏', '13800001004', '卫生间水龙头漏水',           1, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(25, '陈静', '13800001005', '窗户玻璃破裂，存在安全隐患', 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(33, '杨磊', '13800001006', '床板断裂，需要更换',         0, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(41, '黄军', '13800001007', '热水器无法加热',             1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(50, '周洋', '13800001008', '网络端口损坏，无法上网',     2, DATE_SUB(NOW(), INTERVAL 4 DAY));

UPDATE repair_record SET finish_time = create_time WHERE status = 2;
