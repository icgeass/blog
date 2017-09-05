/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.7.19-0ubuntu0.17.04.1 : Database - blog
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `attach` */

CREATE TABLE `attach` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '文件名',
  `md5` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '文件md5',
  `size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `local_ctime` timestamp NULL DEFAULT NULL COMMENT '本地创建时间',
  `local_mtime` timestamp NULL DEFAULT NULL COMMENT '本地修改时间',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` int(11) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `attach` */

/*Table structure for table `comment` */

CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户名',
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '链接',
  `content` text COLLATE utf8_unicode_ci COMMENT '内容',
  `post_id` bigint(20) DEFAULT NULL COMMENT '文章id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父id',
  `parent_type` int(11) DEFAULT NULL COMMENT '父类型，1，文章，2，评论',
  `ip` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'ip地址',
  `user_agent` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` int(11) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='评论';

/*Data for the table `comment` */

/*Table structure for table `dict` */

CREATE TABLE `dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type` int(11) DEFAULT NULL COMMENT '字典类型，1，分类，2，标签，3，链接，4，历史，5，社交，6，站点信息',
  `dict_key` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '字典键',
  `dict_value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '字典值',
  `dict_desc` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` int(11) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='字典';

/*Data for the table `dict` */

insert  into `dict`(`id`,`dict_type`,`dict_key`,`dict_value`,`dict_desc`,`remark`,`modified_time`,`created_time`,`yn`) values (1,1,NULL,'默认分类',NULL,NULL,'2017-08-31 02:22:30','2017-08-31 02:22:30',NULL),(2,2,NULL,'默认标签',NULL,NULL,'2017-08-31 02:22:46','2017-08-31 02:22:46',NULL),(3,3,'https://www.youtube.com','YouTube',NULL,NULL,'2017-08-31 02:23:20','2017-08-31 02:23:20',NULL),(4,4,'1','这是历史',NULL,NULL,'2017-08-31 02:27:41','2017-08-31 02:27:15',NULL),(5,5,'github','icgeass',NULL,NULL,'2017-08-31 02:29:56','2017-08-31 02:27:53',NULL),(6,5,'weibo','6zeroq',NULL,NULL,'2017-08-31 02:29:58','2017-08-31 02:27:57',NULL),(7,5,'twitter','icgeass',NULL,NULL,'2017-08-31 02:29:59','2017-08-31 02:28:07',NULL),(8,6,'email','icgeass@hotmail.com',NULL,NULL,'2017-08-31 02:30:01','2017-08-31 02:28:48',NULL),(9,6,'username','icgeass',NULL,NULL,'2017-08-31 02:30:02','2017-08-31 02:29:08',NULL),(10,6,'title','待定',NULL,NULL,'2017-08-31 02:43:17','2017-08-31 02:29:31',NULL),(11,6,'subtitle','icgeass的博客',NULL,NULL,'2017-08-31 02:30:06','2017-08-31 02:29:41',NULL),(12,6,'protocol','https',NULL,NULL,'2017-08-31 02:31:39','2017-08-31 02:31:19',NULL),(13,6,'domain','6zeroq.com',NULL,NULL,'2017-08-31 02:32:03','2017-08-31 02:31:31',NULL),(14,6,'tagUri','/tags',NULL,NULL,'2017-08-31 02:43:21','2017-08-31 02:32:15',NULL),(15,6,'categoryUri','/category',NULL,NULL,'2017-08-31 02:43:21','2017-08-31 02:42:28',NULL),(16,6,'postUri','/post/show',NULL,NULL,'2017-08-31 02:43:22','2017-08-31 02:42:48',NULL),(17,6,'atomUri','/atom.xml',NULL,NULL,'2017-08-31 02:43:25','2017-08-31 02:43:07',NULL);

/*Table structure for table `post` */

CREATE TABLE `post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '标题',
  `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户名',
  `content` text COLLATE utf8_unicode_ci COMMENT '内容',
  `post_type` int(11) DEFAULT NULL COMMENT '文章类型，1，文章，2，留言',
  `status` int(11) DEFAULT NULL COMMENT '状态，1，未发布，2，已发布',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` int(11) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='文章';

/*Data for the table `post` */

/*Table structure for table `relation` */

CREATE TABLE `relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int(11) DEFAULT NULL COMMENT '类型，1，文章标签，2，文章分类，3，链接，4，历史，5，社交，6，站点信息',
  `parent_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '父id',
  `child_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '子id',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yn` int(11) DEFAULT NULL COMMENT '是否有效，1，有效，0，无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='关系';

/*Data for the table `relation` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


