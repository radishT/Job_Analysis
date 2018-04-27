/*
Navicat MySQL Data Transfer

Source Server         : localDB
Source Server Version : 50554
Source Host           : localhost:3306
Source Database       : crawler_db

Target Server Type    : MYSQL
Target Server Version : 50554
File Encoding         : 65001

Date: 2018-04-27 20:06:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for job_message
-- ----------------------------
DROP TABLE IF EXISTS `job_message`;
CREATE TABLE `job_message` (
  `url_id` int(11) NOT NULL AUTO_INCREMENT,
  `key_word` varchar(255) DEFAULT NULL,
  `url` varchar(500) DEFAULT NULL,
  `message` varchar(2000) DEFAULT NULL,
  `status` int(10) DEFAULT NULL,
  `message_map` blob,
  PRIMARY KEY (`url_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46354 DEFAULT CHARSET=utf8;
