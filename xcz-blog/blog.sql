/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : blog

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 12/03/2026 00:15:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for id_generate
-- ----------------------------
DROP TABLE IF EXISTS `id_generate`;
CREATE TABLE `id_generate`  (
  `id` bigint(20) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT COMMENT '主键',
  `init_id` bigint NOT NULL DEFAULT 0 COMMENT '初始id',
  `max_id` bigint NULL DEFAULT 0 COMMENT '当前最大id',
  `step` bigint NOT NULL DEFAULT 100 COMMENT '步长',
  `end_id` bigint NOT NULL COMMENT '结束id',
  `tag` varchar(100) CHARACTER SET utf8mb4    NOT NULL COMMENT '业务标识(tag)',
  `description` varchar(255) CHARACTER SET utf8mb4    NOT NULL COMMENT 'id描述',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4  COMMENT = 'ID生成配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of id_generate
-- ----------------------------
INSERT INTO `id_generate` VALUES (00000000000000000001, 100000, 100100, 100, 110000, 'USER', '用户id', '2026-03-12 00:12:58');
INSERT INTO `id_generate` VALUES (00000000000000000002, 200000, 200100, 100, 210000, 'USER', '用户id', '2026-03-12 00:13:02');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_username` varchar(50) CHARACTER SET utf8mb4    NOT NULL COMMENT '用户账号',
  `user_name` varchar(50) CHARACTER SET utf8mb4    NOT NULL COMMENT '用户名',
  `user_password` varchar(100) CHARACTER SET utf8mb4    NOT NULL COMMENT '用户密码',
  `user_role` int NOT NULL DEFAULT 0 COMMENT '用户角色',
  `user_login_time` datetime NULL DEFAULT NULL COMMENT '用户最后登录时间',
  `user_ip` varchar(128) CHARACTER SET utf8mb4    NULL DEFAULT NULL COMMENT '用户最后登录ip',
  `user_type` tinyint(1) NULL DEFAULT 1 COMMENT '账号状态 (0-禁用, 1-正常)',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`user_username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 200068 CHARACTER SET = utf8mb4  COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (200067, '1604862728@qq.com', 'wua', '$2a$10$3PZwge0KewuXKUJY8NXDh.dB01z1ry/.Mts5IRgzkDGoMyu7SGAO2', 1, '2026-03-11 21:22:17', '127.0.0.1', 1);

SET FOREIGN_KEY_CHECKS = 1;
