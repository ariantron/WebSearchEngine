/*
 Navicat Premium Data Transfer

 Source Server         : MariaDB
 Source Server Type    : MySQL
 Source Server Version : 100310
 Source Host           : localhost:3306
 Source Schema         : search_engine

 Target Server Type    : MySQL
 Target Server Version : 100310
 File Encoding         : 65001

 Date: 27/09/2019 21:08:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for doc_size
-- ----------------------------
DROP TABLE IF EXISTS `doc_size`;
CREATE TABLE `doc_size`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_url_id` int(11) NULL DEFAULT NULL,
  `doc_size` int(11) NULL DEFAULT NULL,
  `created_at` timestamp(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `doc_size_id_uindex`(`id`) USING BTREE,
  INDEX `doc_url_id`(`doc_url_id`) USING BTREE,
  CONSTRAINT `doc_size_ibfk_1` FOREIGN KEY (`doc_url_id`) REFERENCES `urls` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for links
-- ----------------------------
DROP TABLE IF EXISTS `links`;
CREATE TABLE `links`  (
  `source` int(11) NOT NULL,
  `target` int(11) NOT NULL,
  `created_at` timestamp(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0)
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pagerank
-- ----------------------------
DROP TABLE IF EXISTS `pagerank`;
CREATE TABLE `pagerank`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_url_id` int(11) NULL DEFAULT NULL,
  `vektor` double NOT NULL DEFAULT 0,
  `pagerank` double NOT NULL DEFAULT 0,
  `outgoing` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pagerank_id_uindex`(`id`) USING BTREE,
  INDEX `doc_url_id`(`doc_url_id`) USING BTREE,
  CONSTRAINT `pagerank_ibfk_1` FOREIGN KEY (`doc_url_id`) REFERENCES `urls` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 286 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_idf
-- ----------------------------
DROP TABLE IF EXISTS `tf_idf`;
CREATE TABLE `tf_idf`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score` double NULL DEFAULT NULL,
  `doc_url_id` int(11) NULL DEFAULT NULL,
  `word_id` int(11) NULL DEFAULT NULL,
  `created_at` timestamp(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tf_idf_id_uindex`(`id`) USING BTREE,
  INDEX `doc_url_id`(`doc_url_id`) USING BTREE,
  INDEX `word_id`(`word_id`) USING BTREE,
  CONSTRAINT `tf_idf_ibfk_1` FOREIGN KEY (`doc_url_id`) REFERENCES `urls` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tf_idf_ibfk_2` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2292 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for urls
-- ----------------------------
DROP TABLE IF EXISTS `urls`;
CREATE TABLE `urls`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `title` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `is_index` bit(1) NOT NULL DEFAULT b'0',
  `created_at` timestamp(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `urls_id_uindex`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 287 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for websites
-- ----------------------------
DROP TABLE IF EXISTS `websites`;
CREATE TABLE `websites`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `websites_id_uindex`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1931 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for word_doc
-- ----------------------------
DROP TABLE IF EXISTS `word_doc`;
CREATE TABLE `word_doc`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word_id` int(11) NULL DEFAULT NULL,
  `doc_url_id` int(11) NULL DEFAULT NULL,
  `freq` int(11) NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `word_doc_id_uindex`(`id`) USING BTREE,
  INDEX `word_id`(`word_id`) USING BTREE,
  INDEX `doc_url_id`(`doc_url_id`) USING BTREE,
  CONSTRAINT `word_doc_ibfk_1` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `word_doc_ibfk_2` FOREIGN KEY (`doc_url_id`) REFERENCES `urls` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 869 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for words
-- ----------------------------
DROP TABLE IF EXISTS `words`;
CREATE TABLE `words`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `created_at` timestamp(0) NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `words_id_uindex`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 511 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
