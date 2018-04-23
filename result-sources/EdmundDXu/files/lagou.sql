/*
Navicat MySQL Data Transfer

Source Server         : edmund
Source Server Version : 50554
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50554
File Encoding         : 65001

Date: 2018-04-23 20:11:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for lagou
-- ----------------------------
DROP TABLE IF EXISTS `lagou`;
CREATE TABLE `lagou` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key_word` varchar(10) NOT NULL,
  `job` varchar(30) DEFAULT NULL,
  `salary` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `experience` varchar(50) NOT NULL,
  `education` varchar(20) NOT NULL,
  `company` varchar(100) NOT NULL,
  `key_words` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of lagou
-- ----------------------------
