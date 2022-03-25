/*
 Navicat Premium Data Transfer

 Source Server         : docker
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3307
 Source Schema         : supermall_wms

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 06/02/2022 17:38:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of undo_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wms_purchase
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint DEFAULT NULL,
  `assignee_name` varchar(255) DEFAULT NULL,
  `phone` char(13) DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `ware_id` bigint DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购信息';

-- ----------------------------
-- Records of wms_purchase
-- ----------------------------
BEGIN;
INSERT INTO `wms_purchase` (`id`, `assignee_id`, `assignee_name`, `phone`, `priority`, `status`, `ware_id`, `amount`, `create_time`, `update_time`) VALUES (1, 2, 'litianyi', '17631650305', 1, 4, 1, 0.0000, '2022-01-20 11:30:42', '2022-01-20 17:49:08');
INSERT INTO `wms_purchase` (`id`, `assignee_id`, `assignee_name`, `phone`, `priority`, `status`, `ware_id`, `amount`, `create_time`, `update_time`) VALUES (2, 1, 'admin', '13612345678', NULL, 3, 2, 0.0000, '2022-01-20 18:04:00', '2022-01-21 16:08:04');
COMMIT;

-- ----------------------------
-- Table structure for wms_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `status` int DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  `description` varchar(255) DEFAULT NULL COMMENT '采购说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of wms_purchase_detail
-- ----------------------------
BEGIN;
INSERT INTO `wms_purchase_detail` (`id`, `purchase_id`, `sku_id`, `sku_num`, `sku_price`, `ware_id`, `status`, `description`) VALUES (3, 1, 13, 200, NULL, 1, 3, '');
INSERT INTO `wms_purchase_detail` (`id`, `purchase_id`, `sku_id`, `sku_num`, `sku_price`, `ware_id`, `status`, `description`) VALUES (4, 1, 14, 200, NULL, 1, 4, '没货了');
INSERT INTO `wms_purchase_detail` (`id`, `purchase_id`, `sku_id`, `sku_num`, `sku_price`, `ware_id`, `status`, `description`) VALUES (5, 2, 19, 100, NULL, 2, 3, '搞定');
INSERT INTO `wms_purchase_detail` (`id`, `purchase_id`, `sku_id`, `sku_num`, `sku_price`, `ware_id`, `status`, `description`) VALUES (6, 2, 20, 200, NULL, 2, 3, '搞定');
INSERT INTO `wms_purchase_detail` (`id`, `purchase_id`, `sku_id`, `sku_num`, `sku_price`, `ware_id`, `status`, `description`) VALUES (7, 2, 21, 200, NULL, 2, 3, '搞定');
COMMIT;

-- ----------------------------
-- Table structure for wms_ware_info
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='仓库信息';

-- ----------------------------
-- Records of wms_ware_info
-- ----------------------------
BEGIN;
INSERT INTO `wms_ware_info` (`id`, `name`, `address`, `areacode`) VALUES (1, '1号仓库', '北京市', '1');
INSERT INTO `wms_ware_info` (`id`, `name`, `address`, `areacode`) VALUES (2, '2号仓库', '上海市', '2');
COMMIT;

-- ----------------------------
-- Table structure for wms_ware_order_task
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存工作单';

-- ----------------------------
-- Records of wms_ware_order_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wms_ware_order_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `lock_status` int DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存工作单';

-- ----------------------------
-- Records of wms_ware_order_task_detail
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wms_ware_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `stock` int DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int DEFAULT '0' COMMENT '锁定库存',
  PRIMARY KEY (`id`),
  KEY `sku_id` (`sku_id`) USING BTREE,
  KEY `ware_id` (`ware_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品库存';

-- ----------------------------
-- Records of wms_ware_sku
-- ----------------------------
BEGIN;
INSERT INTO `wms_ware_sku` (`id`, `sku_id`, `ware_id`, `stock`, `sku_name`, `stock_locked`) VALUES (1, 13, 1, 200, 'Apple iPhone 13 (A2634) 星光色 128G', 0);
INSERT INTO `wms_ware_sku` (`id`, `sku_id`, `ware_id`, `stock`, `sku_name`, `stock_locked`) VALUES (2, 19, 2, 300, 'Apple iPhone 13 (A2634) 蓝色 128G', 0);
INSERT INTO `wms_ware_sku` (`id`, `sku_id`, `ware_id`, `stock`, `sku_name`, `stock_locked`) VALUES (3, 20, 2, 600, 'Apple iPhone 13 (A2634) 蓝色 256G', 0);
INSERT INTO `wms_ware_sku` (`id`, `sku_id`, `ware_id`, `stock`, `sku_name`, `stock_locked`) VALUES (4, 21, 2, 600, 'Apple iPhone 13 (A2634) 蓝色 512G', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
