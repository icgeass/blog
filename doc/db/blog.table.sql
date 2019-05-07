/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.7.16 : Database - blog
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
USE `blog`;

/*Table structure for table `attach` */

CREATE TABLE `attach` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `md5` varchar(255) DEFAULT NULL COMMENT '文件md5',
  `size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `local_ctime` timestamp NULL DEFAULT NULL COMMENT '本地创建时间',
  `local_mtime` timestamp NULL DEFAULT NULL COMMENT '本地修改时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` tinyint(4) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='附件';

/*Table structure for table `comment` */

CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `url` varchar(255) DEFAULT NULL COMMENT '链接',
  `content` text COMMENT '内容',
  `post_id` bigint(20) DEFAULT NULL COMMENT '文章id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父id',
  `parent_type` tinyint(4) DEFAULT NULL COMMENT '父类型，1，文章，2，评论',
  `ip` varchar(255) DEFAULT NULL COMMENT 'ip地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` tinyint(4) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='评论';

/*Table structure for table `dict` */

CREATE TABLE `dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type` tinyint(4) DEFAULT NULL COMMENT '字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息，7，系统配置',
  `dict_key` varchar(255) DEFAULT NULL COMMENT '字典键',
  `dict_value` text COMMENT '字典值',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` tinyint(4) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COMMENT='字典';

/*Table structure for table `post` */

CREATE TABLE `post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `content` text COMMENT '内容',
  `post_type` tinyint(4) DEFAULT NULL COMMENT '文章类型，1，文章，2，留言',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态，1，未发布，2，已发布',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` tinyint(4) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COMMENT='文章';

/*Table structure for table `relation` */

CREATE TABLE `relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint(4) DEFAULT NULL COMMENT '类型，1，文章标签，2，文章分类',
  `parent_id` varchar(255) DEFAULT NULL COMMENT '父id',
  `child_id` varchar(255) DEFAULT NULL COMMENT '子id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` tinyint(4) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COMMENT='关系';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
