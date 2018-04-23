/*
 *这是BOSS直聘的URL存储的sql脚本
 */
CREATE TABLE `url_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `province` varchar(50) NOT NULL,
  `city` varchar(100) NOT NULL,
  `url` varchar(500) NOT NULL,
  `key_word` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1857 DEFAULT CHARSET=utf8;
