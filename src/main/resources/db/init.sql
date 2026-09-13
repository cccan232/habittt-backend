/*
 Navicat Premium Dump SQL

 Source Server         : habittt
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : localhost:3307
 Source Schema         : habittt

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '正常' COMMENT '账号状态：正常/封禁',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for costume
-- ----------------------------
DROP TABLE IF EXISTS `costume`;
CREATE TABLE `costume`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色分类ID (1-14)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名字',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片路径',
  `price` int NULL DEFAULT 0 COMMENT '价格',
  `sort` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '普通' COMMENT '风格分类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '装扮表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of costume
-- ----------------------------
INSERT INTO `costume` VALUES (1, 1, '崔樱桃', '/images/costumes/role1.png', 300, '普通');
INSERT INTO `costume` VALUES (2, 2, '兔拉米', '/images/costumes/role2.png', 300, '普通');
INSERT INTO `costume` VALUES (3, 3, '刷粟米', '/images/costumes/role3.png', 300, '普通');
INSERT INTO `costume` VALUES (4, 4, '开关锁', '/images/costumes/role4.png', 300, '普通');
INSERT INTO `costume` VALUES (5, 5, '眈眈', '/images/costumes/role5.png', 300, '普通');
INSERT INTO `costume` VALUES (6, 6, '狐袋子', '/images/costumes/role6.png', 300, '普通');
INSERT INTO `costume` VALUES (7, 7, '饭粒', '/images/costumes/role7.png', 300, '普通');
INSERT INTO `costume` VALUES (8, 8, '帕哩', '/images/costumes/role8.png', 300, '普通');
INSERT INTO `costume` VALUES (9, 9, 'KimJa', '/images/costumes/role9.png', 300, '普通');
INSERT INTO `costume` VALUES (10, 10, 'DOA', '/images/costumes/role10.png', 300, '普通');
INSERT INTO `costume` VALUES (11, 11, '夫小橘', '/images/costumes/role11.png', 300, '普通');
INSERT INTO `costume` VALUES (12, 12, '哝啵', '/images/costumes/role12.png', 300, '普通');
INSERT INTO `costume` VALUES (13, 13, '灿獭哩', '/images/costumes/role13.png', 300, '普通');
INSERT INTO `costume` VALUES (14, 14, '蹦蹦', '/images/costumes/role14.png', 300, '普通');
INSERT INTO `costume` VALUES (29, 1, '崔樱桃', '/uploads/costumes/fb776425-f632-4598-882f-0d13c92df8ac.png', 500, 'Night');
INSERT INTO `costume` VALUES (30, 2, '兔拉米', '/uploads/costumes/dd7f85f2-6e9f-44fb-83c1-6d92cd9e4152.png', 500, 'Night');
INSERT INTO `costume` VALUES (31, 3, '刷粟米', '/uploads/costumes/e1a24758-8577-4361-bf96-7aec969677d8.png', 500, 'Night');
INSERT INTO `costume` VALUES (32, 4, '开关锁', '/uploads/costumes/f937e83f-655d-4f93-a22c-b85c2b00ac07.png', 500, 'Night');
INSERT INTO `costume` VALUES (33, 5, '眈眈', '/uploads/costumes/a4399cd6-86a2-4af1-9838-78c1441452a9.png', 500, 'Night');
INSERT INTO `costume` VALUES (34, 6, '狐袋子', '/uploads/costumes/a45141c0-04de-4ae1-bf0b-8e255ccee8ad.png', 500, 'Night');
INSERT INTO `costume` VALUES (35, 7, '饭粒', '/uploads/costumes/b4c48e72-cd23-488a-a6ca-7989f13bebeb.png', 500, 'Night');
INSERT INTO `costume` VALUES (36, 8, '帕哩', '/uploads/costumes/037b7c07-c1be-4105-91e5-a7c589b0a683.png', 500, 'Night');
INSERT INTO `costume` VALUES (37, 9, 'KimJa', '/uploads/costumes/7ab7f943-f9d1-4602-9bfb-0bd8a247c654.png', 500, 'Night');
INSERT INTO `costume` VALUES (38, 10, 'DOA', '/uploads/costumes/32287876-45a5-4c4b-8780-d6f031a1b4c0.png', 500, 'Night');
INSERT INTO `costume` VALUES (39, 11, '夫小橘', '/uploads/costumes/439bf86b-f9fa-48f1-aeb6-3a0d354ea809.png', 500, 'Night');
INSERT INTO `costume` VALUES (40, 12, '哝啵', '/uploads/costumes/cfbb47d9-8b2e-434c-951a-ac8f1fc5b923.png', 500, 'Night');
INSERT INTO `costume` VALUES (41, 13, '灿獭哩', '/uploads/costumes/249c9c46-69df-457d-b896-6f52c30588db.png', 500, 'Night');
INSERT INTO `costume` VALUES (42, 14, '蹦蹦', '/uploads/costumes/d84f8201-054a-4f56-832e-ef9c40520939.png', 500, 'Night');
INSERT INTO `costume` VALUES (43, 1, '崔樱桃', '/uploads/costumes/ebbdb898-d689-4ebe-997a-5928b5c9a11e.png', 500, '万圣');
INSERT INTO `costume` VALUES (44, 2, '兔拉米', '/uploads/costumes/ab6adec8-1dc0-401d-b790-cbaedba23112.png', 500, '万圣');
INSERT INTO `costume` VALUES (45, 3, '刷粟米', '/uploads/costumes/e169ee5a-069d-40ef-8a09-587744feb7a8.png', 500, '万圣');
INSERT INTO `costume` VALUES (46, 4, '开关锁', '/uploads/costumes/edecdac2-d481-4822-ac8d-dc910e266113.png', 500, '万圣');
INSERT INTO `costume` VALUES (47, 5, '眈眈', '/uploads/costumes/896c0631-4a89-463b-bbc8-98454d651cf2.png', 500, '万圣');
INSERT INTO `costume` VALUES (48, 6, '狐袋子', '/uploads/costumes/49cbe5d5-724c-41a3-98a2-1299d5d2d344.png', 500, '万圣');
INSERT INTO `costume` VALUES (49, 7, '饭粒', '/uploads/costumes/ffdd5abd-fea1-4f86-9c81-a695acfa2afe.png', 500, '万圣');
INSERT INTO `costume` VALUES (50, 8, '帕哩', '/uploads/costumes/872390fa-a9a7-4132-bcdd-f192b6f2afd4.png', 500, '万圣');
INSERT INTO `costume` VALUES (51, 9, 'KimJa', '/uploads/costumes/826ca0ae-2600-4f83-bd2f-24b8a68055cb.png', 500, '万圣');
INSERT INTO `costume` VALUES (52, 10, 'DOA', '/uploads/costumes/aaf6a0bf-4ac4-44ac-a148-baaf8df35e1f.png', 500, '万圣');
INSERT INTO `costume` VALUES (53, 11, '夫小橘', '/uploads/costumes/1c2f0eb5-0a7c-4cb9-a804-b8ccd06a5ba1.png', 500, '万圣');
INSERT INTO `costume` VALUES (54, 12, '哝啵', '/uploads/costumes/c04aee06-6c0c-423e-b832-559b87e63b86.png', 500, '万圣');
INSERT INTO `costume` VALUES (55, 13, '灿獭哩', '/uploads/costumes/919399a9-0cac-4fbf-893e-c163bede813c.png', 500, '万圣');
INSERT INTO `costume` VALUES (56, 14, '蹦蹦', '/uploads/costumes/3c424e9c-ba8e-403a-9aa5-5473c32c0486.png', 500, '万圣');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `is_read` tinyint NULL DEFAULT 0,
  `related_id` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `action_taken` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reward
-- ----------------------------
DROP TABLE IF EXISTS `reward`;
CREATE TABLE `reward`  (
  `reward_id` bigint NOT NULL AUTO_INCREMENT COMMENT '奖励编号',
  `user_id` bigint NOT NULL COMMENT '创建奖励的用户ID',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片URL',
  `price` int NOT NULL COMMENT '兑换所需货币数量',
  PRIMARY KEY (`reward_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `reward_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义奖励表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '未完成' COMMENT '状态：未完成/已完成',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '难度：easy/normal/hard',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `team_id` bigint NOT NULL AUTO_INCREMENT,
  `team_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `creator_id` bigint NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'waiting',
  `challenge_start_time` datetime NULL DEFAULT NULL,
  `challenge_end_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`team_id`) USING BTREE,
  INDEX `creator_id`(`creator_id` ASC) USING BTREE,
  CONSTRAINT `team_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team_application
-- ----------------------------
DROP TABLE IF EXISTS `team_application`;
CREATE TABLE `team_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint NOT NULL,
  `applicant_id` bigint NOT NULL,
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `handle_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `team_id`(`team_id` ASC) USING BTREE,
  INDEX `applicant_id`(`applicant_id` ASC) USING BTREE,
  CONSTRAINT `team_application_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `team_application_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team_chat
-- ----------------------------
DROP TABLE IF EXISTS `team_chat`;
CREATE TABLE `team_chat`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'text',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `team_id`(`team_id` ASC) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  CONSTRAINT `team_chat_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `team_chat_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team_member
-- ----------------------------
DROP TABLE IF EXISTS `team_member`;
CREATE TABLE `team_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'member',
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_team_user`(`team_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `team_member_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `team_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` bigint NULL DEFAULT NULL COMMENT '当前穿戴装扮ID（外键关联 costume.id）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `points` int NULL DEFAULT 0 COMMENT '货币数量',
  `team_id` bigint NULL DEFAULT NULL COMMENT '所属队伍 ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '正常' COMMENT '账号状态',
  `create_quota` int NULL DEFAULT 0 COMMENT '自定义奖励添加资格次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  INDEX `fk_user_avatar`(`avatar` ASC) USING BTREE,
  CONSTRAINT `fk_user_avatar` FOREIGN KEY (`avatar`) REFERENCES `costume` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_costume
-- ----------------------------
DROP TABLE IF EXISTS `user_costume`;
CREATE TABLE `user_costume`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `costume_id` bigint NOT NULL,
  `obtain_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户拥有的装扮表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
