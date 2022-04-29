/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `oj` DEFAULT CHARACTER SET utf8;

USE `oj`;

DROP TABLE IF EXISTS `announcement`;

CREATE TABLE `announcement` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `uid` varchar(32) NOT NULL,
  `status` int(11) DEFAULT '0' COMMENT '0可见，1不可见',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `gid` (`gid`),
  CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `announcement_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `auth`;

CREATE TABLE `auth` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '权限名称',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限字符串',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0可用，1不可用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `code_template`;

CREATE TABLE `code_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) unsigned NOT NULL,
  `lid` bigint(20) unsigned NOT NULL,
  `code` longtext NOT NULL,
  `status` tinyint(1) DEFAULT '0',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`),
  KEY `lid` (`lid`),
  CONSTRAINT `code_template_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `code_template_ibfk_2` FOREIGN KEY (`lid`) REFERENCES `language` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cid` bigint(20) unsigned DEFAULT NULL COMMENT 'null表示无引用比赛',
  `did` int(11) DEFAULT NULL COMMENT 'null表示无引用讨论',
  `content` longtext COMMENT '评论内容',
  `from_uid` varchar(32) NOT NULL COMMENT '评论者id',
  `from_name` varchar(255) DEFAULT NULL COMMENT '评论者用户名',
  `from_avatar` varchar(255) DEFAULT NULL COMMENT '评论组头像地址',
  `from_role` varchar(20) DEFAULT NULL COMMENT '评论者角色',
  `like_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `status` int(11) DEFAULT '0' COMMENT '是否封禁或逻辑删除该评论',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `from_uid` (`from_uid`),
  KEY `from_avatar` (`from_avatar`),
  KEY `did` (`did`),
  KEY `cid` (`cid`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`from_avatar`) REFERENCES `user_info` (`avatar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`did`) REFERENCES `discussion` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `comment_like`;

CREATE TABLE `comment_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL,
  `cid` int(11) NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `cid` (`cid`),
  CONSTRAINT `comment_like_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_like_ibfk_2` FOREIGN KEY (`cid`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest`;

CREATE TABLE `contest` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL COMMENT '比赛创建者id',
  `author` varchar(255) DEFAULT NULL COMMENT '比赛创建者的用户名',
  `title` varchar(255) DEFAULT NULL COMMENT '比赛标题',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '0为acm赛制，1为比分赛制',
  `description` longtext COMMENT '比赛说明',
  `source` int(11) DEFAULT '0' COMMENT '比赛来源，原创为0，克隆赛为比赛id',
  `auth` int(11) NOT NULL COMMENT '0为公开赛，1为私有赛（访问有密码），2为保护赛（提交有密码）',
  `pwd` varchar(255) DEFAULT NULL COMMENT '比赛密码',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` bigint(20) DEFAULT NULL COMMENT '比赛时长(s)',
  `seal_rank` tinyint(1) DEFAULT '0' COMMENT '是否开启封榜',
  `seal_rank_time` datetime DEFAULT NULL COMMENT '封榜起始时间，一直到比赛结束，不刷新榜单',
  `auto_real_rank` tinyint(1) DEFAULT '1' COMMENT '比赛结束是否自动解除封榜,自动转换成真实榜单',
  `status` int(11) DEFAULT NULL COMMENT '-1为未开始，0为进行中，1为已结束',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见',
  `open_print` tinyint(1) DEFAULT '0' COMMENT '是否打开打印功能',
  `open_account_limit` tinyint(1) DEFAULT '0' COMMENT '是否开启账号限制',
  `account_limit_rule` mediumtext COMMENT '账号限制规则',
  `rank_show_name` varchar(20) DEFAULT 'username' COMMENT '排行榜显示（username、nickname、realname）',
  `open_rank` tinyint(1) DEFAULT '0' COMMENT '是否开放比赛榜单',
  `star_account` mediumtext COMMENT '打星用户列表',
  `oi_rank_score_type` varchar(255) DEFAULT 'Recent' COMMENT 'oi排行榜得分方式，Recent、Highest',
  `is_public` tinyint(1) DEFAULT '1',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`uid`),
  KEY `uid` (`uid`),
  KEY `gid` (`gid`),
  CONSTRAINT `contest_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_announcement`;

CREATE TABLE `contest_announcement` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `aid` bigint(20) unsigned NOT NULL COMMENT '公告id',
  `cid` bigint(20) unsigned NOT NULL COMMENT '比赛id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cid` (`cid`),
  KEY `aid` (`aid`),
  CONSTRAINT `contest_announcement_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_announcement_ibfk_2` FOREIGN KEY (`aid`) REFERENCES `announcement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_explanation`;

CREATE TABLE `contest_explanation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cid` bigint(20) unsigned NOT NULL,
  `uid` varchar(32) NOT NULL COMMENT '发布者（必须为比赛创建者或者超级管理员才能）',
  `content` longtext COMMENT '内容(支持markdown)',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `cid` (`cid`),
  CONSTRAINT `contest_explanation_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_explanation_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_print`;

CREATE TABLE `contest_print` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `realname` varchar(100) DEFAULT NULL,
  `cid` bigint(20) unsigned DEFAULT NULL,
  `content` longtext NOT NULL,
  `status` int(11) DEFAULT '0',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cid` (`cid`),
  KEY `username` (`username`),
  CONSTRAINT `contest_print_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_print_ibfk_2` FOREIGN KEY (`username`) REFERENCES `user_info` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_problem`;

CREATE TABLE `contest_problem` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `display_id` varchar(255) NOT NULL COMMENT '该题目在比赛中的顺序id',
  `cid` bigint(20) unsigned NOT NULL COMMENT '比赛id',
  `pid` bigint(20) unsigned NOT NULL COMMENT '题目id',
  `display_title` varchar(255) NOT NULL COMMENT '该题目在比赛中的标题，默认为原名字',
  `color` varchar(255) DEFAULT NULL COMMENT '气球颜色',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`cid`,`pid`),
  UNIQUE KEY `display_id_cid_pid_unique` (`display_id`,`cid`,`pid`),
  KEY `cid` (`cid`),
  KEY `pid` (`pid`),
  CONSTRAINT `contest_problem_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_problem_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_record`;

CREATE TABLE `contest_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cid` bigint(20) unsigned DEFAULT NULL COMMENT '比赛id',
  `uid` varchar(255) NOT NULL COMMENT '用户id',
  `pid` bigint(20) unsigned DEFAULT NULL COMMENT '题目id',
  `cpid` bigint(20) unsigned DEFAULT NULL COMMENT '比赛中的题目的id',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `display_id` varchar(255) DEFAULT NULL COMMENT '比赛中展示的id',
  `submit_id` bigint(20) unsigned NOT NULL COMMENT '提交id，用于可重判',
  `status` int(11) DEFAULT NULL COMMENT '提交结果，0表示未AC通过不罚时，1表示AC通过，-1为未AC通过算罚时',
  `submit_time` datetime NOT NULL COMMENT '具体提交时间',
  `time` bigint(20) unsigned DEFAULT NULL COMMENT '提交时间，为提交时间减去比赛时间',
  `score` int(11) DEFAULT NULL COMMENT 'OI比赛的得分',
  `use_time` int(11) DEFAULT NULL COMMENT '运行耗时',
  `first_blood` tinyint(1) DEFAULT '0' COMMENT '是否为一血AC(废弃)',
  `checked` tinyint(1) DEFAULT '0' COMMENT 'AC是否已校验',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`submit_id`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`),
  KEY `cpid` (`cpid`),
  KEY `submit_id` (`submit_id`),
  KEY `cid` (`cid`),
  KEY `time` (`time`),
  CONSTRAINT `contest_record_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_record_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_record_ibfk_3` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_record_ibfk_4` FOREIGN KEY (`cpid`) REFERENCES `contest_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_record_ibfk_5` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_register`;

CREATE TABLE `contest_register` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cid` bigint(20) unsigned NOT NULL COMMENT '比赛id',
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `status` int(11) DEFAULT '0' COMMENT '默认为0表示正常，1为失效。',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`cid`,`uid`),
  UNIQUE KEY `cid_uid_unique` (`cid`,`uid`),
  KEY `uid` (`uid`),
  CONSTRAINT `contest_register_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_register_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `contest_score`;

CREATE TABLE `contest_score` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cid` bigint(20) unsigned NOT NULL,
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `last` int(11) DEFAULT NULL COMMENT '比赛前的score得分',
  `change` int(11) DEFAULT NULL COMMENT 'Score比分变化',
  `now` int(11) DEFAULT NULL COMMENT '现在的score',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`cid`),
  KEY `cid` (`cid`),
  KEY `uid` (`uid`),
  CONSTRAINT `contest_score_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `contest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contest_score_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `discussion`;

CREATE TABLE `discussion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL COMMENT '分类id',
  `title` varchar(255) DEFAULT NULL COMMENT '讨论标题',
  `description` varchar(255) DEFAULT NULL COMMENT '讨论简介',
  `content` longtext COMMENT '讨论内容',
  `pid` varchar(255) DEFAULT NULL COMMENT '关联题目id',
  `uid` varchar(32) NOT NULL COMMENT '发表者id',
  `author` varchar(255) NOT NULL COMMENT '发表者用户名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '发表讨论者头像',
  `role` varchar(25) DEFAULT 'user' COMMENT '发表者角色',
  `view_num` int(11) DEFAULT '0' COMMENT '浏览数量',
  `like_num` int(11) DEFAULT '0' COMMENT '点赞数量',
  `top_priority` tinyint(1) DEFAULT '0' COMMENT '优先级，是否置顶',
  `comment_num` int(11) DEFAULT '0' COMMENT '评论数量',
  `status` int(1) DEFAULT '0' COMMENT '是否封禁该讨论',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `avatar` (`avatar`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`),
  KEY `gid` (`gid`),
  CONSTRAINT `discussion_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `discussion_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `discussion_ibfk_3` FOREIGN KEY (`avatar`) REFERENCES `user_info` (`avatar`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `discussion_ibfk_4` FOREIGN KEY (`pid`) REFERENCES `problem` (`problem_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `discussion_ibfk_5` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `discussion_like`;

CREATE TABLE `discussion_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL,
  `did` int(11) NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `did` (`did`),
  KEY `uid` (`uid`),
  CONSTRAINT `discussion_like_ibfk_1` FOREIGN KEY (`did`) REFERENCES `discussion` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `discussion_like_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `discussion_report`;

CREATE TABLE `discussion_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `did` int(11) DEFAULT NULL COMMENT '讨论id',
  `reporter` varchar(255) DEFAULT NULL COMMENT '举报者的用户名',
  `content` varchar(255) NOT NULL COMMENT '举报内容',
  `status` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `did` (`did`),
  KEY `reporter` (`reporter`),
  CONSTRAINT `discussion_report_ibfk_1` FOREIGN KEY (`did`) REFERENCES `discussion` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `discussion_report_ibfk_2` FOREIGN KEY (`reporter`) REFERENCES `user_info` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `file`;

CREATE TABLE `file` (
  `id` bigint(32) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uid` varchar(32) DEFAULT NULL COMMENT '用户id',
  `name` varchar(255) NOT NULL COMMENT '文件名',
  `suffix` varchar(255) NOT NULL COMMENT '文件后缀格式',
  `folder_path` varchar(255) NOT NULL COMMENT '文件所在文件夹的路径',
  `file_path` varchar(255) DEFAULT NULL COMMENT '文件绝对路径',
  `type` varchar(255) DEFAULT NULL COMMENT '文件所属类型，例如avatar',
  `delete` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `gid` (`gid`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `file_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `judge`;

CREATE TABLE `judge` (
  `submit_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) unsigned NOT NULL COMMENT '题目id',
  `display_pid` varchar(255) NOT NULL COMMENT '题目展示id',
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `submit_time` datetime NOT NULL COMMENT '提交的时间',
  `status` int(11) DEFAULT NULL COMMENT '结果码具体参考文档',
  `share` tinyint(1) DEFAULT '0' COMMENT '0为仅自己可见，1为全部人可见。',
  `error_message` mediumtext COMMENT '错误提醒（编译错误，或者vj提醒）',
  `time` int(11) DEFAULT NULL COMMENT '运行时间(ms)',
  `memory` int(11) DEFAULT NULL COMMENT '运行内存（kb）',
  `score` int(11) DEFAULT NULL COMMENT 'IO判题则不为空',
  `length` int(11) DEFAULT NULL COMMENT '代码长度',
  `code` longtext NOT NULL COMMENT '代码',
  `language` varchar(255) DEFAULT NULL COMMENT '代码语言',
  `cid` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '比赛id，非比赛题目默认为0',
  `cpid` bigint(20) unsigned DEFAULT '0' COMMENT '比赛中题目排序id，非比赛题目默认为0',
  `judger` varchar(20) DEFAULT NULL COMMENT '判题机ip',
  `ip` varchar(20) DEFAULT NULL COMMENT '提交者所在ip',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `oi_rank_score` int(11) NULL DEFAULT '0' COMMENT 'oi排行榜得分',
  `vjudge_submit_id` bigint(20) unsigned NULL  COMMENT 'vjudge判题在其它oj的提交id',
  `vjudge_username` varchar(255) NULL  COMMENT 'vjudge判题在其它oj的提交用户名',
  `vjudge_password` varchar(255) NULL  COMMENT 'vjudge判题在其它oj的提交账号密码',
  `is_public` tinyint(1) DEFAULT '1',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`submit_id`,`pid`,`display_pid`,`uid`,`cid`),
  KEY `pid` (`pid`),
  KEY `uid` (`uid`),
  KEY `gid` (`gid`),
  KEY `username` (`username`),
  CONSTRAINT `judge_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_ibfk_3` FOREIGN KEY (`username`) REFERENCES `user_info` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_ibfk_4` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `judge_case`;

CREATE TABLE `judge_case` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `submit_id` bigint(20) unsigned NOT NULL COMMENT '提交判题id',
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `pid` bigint(20) unsigned NOT NULL COMMENT '题目id',
  `case_id` bigint(20) DEFAULT NULL COMMENT '测试样例id',
  `status` int(11) DEFAULT NULL COMMENT '具体看结果码',
  `time` int(11) DEFAULT NULL COMMENT '测试该样例所用时间ms',
  `memory` int(11) DEFAULT NULL COMMENT '测试该样例所用空间KB',
  `score` int(11) DEFAULT NULL COMMENT 'IO得分',
  `input_data` longtext COMMENT '样例输入，比赛不可看',
  `output_data` longtext COMMENT '样例输出，比赛不可看',
  `user_output` longtext COMMENT '用户样例输出，比赛不可看',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`submit_id`,`uid`,`pid`),
  KEY `case_id` (`case_id`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`),
  KEY `submit_id` (`submit_id`),
  CONSTRAINT `judge_case_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_case_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `judge_case_ibfk_3` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `judge_server`;

CREATE TABLE `judge_server` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '判题服务名字',
  `ip` varchar(255) NOT NULL COMMENT '判题机ip',
  `port` int(11) NOT NULL COMMENT '判题机端口号',
  `url` varchar(255) DEFAULT NULL COMMENT 'ip:port',
  `cpu_core` int(11) DEFAULT '0' COMMENT '判题机所在服务器cpu核心数',
  `task_number` int(11) NOT NULL DEFAULT '0' COMMENT '当前判题数',
  `max_task_number` int(11) NOT NULL COMMENT '判题并发最大数',
  `status` int(11) DEFAULT '0' COMMENT '0可用，1不可用',
  `is_remote` tinyint(1) DEFAULT NULL COMMENT '是否开启远程判题vj',
  `cf_submittable` tinyint(1) DEFAULT 1 NULL COMMENT '是否可提交CF',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `is_remote` (`is_remote`),
  KEY `url` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `language`;

CREATE TABLE `language` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_type` varchar(255) DEFAULT NULL COMMENT '语言类型',
  `description` varchar(255) DEFAULT NULL COMMENT '语言描述',
  `name` varchar(255) DEFAULT NULL COMMENT '语言名字',
  `compile_command` mediumtext COMMENT '编译指令',
  `template` longtext COMMENT '模板',
  `code_template` longtext COMMENT '语言默认代码模板',
  `is_spj` tinyint(1) DEFAULT '0' COMMENT '是否可作为特殊判题的一种语言',
  `oj` varchar(255) DEFAULT NULL COMMENT '该语言属于哪个oj，自身oj用ME',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `problem`;

CREATE TABLE `problem` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `problem_id` varchar(255) NOT NULL COMMENT '问题的自定义ID 例如（OJ-1000）',
  `title` varchar(255) NOT NULL COMMENT '题目',
  `author` varchar(255) DEFAULT '未知' COMMENT '作者',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '0为ACM,1为OI',
  `time_limit` int(11) DEFAULT '1000' COMMENT '单位ms',
  `memory_limit` int(11) DEFAULT '65535' COMMENT '单位kb',
  `stack_limit` int(11) DEFAULT '128' COMMENT '单位mb',
  `description` longtext COMMENT '描述',
  `input` longtext COMMENT '输入描述',
  `output` longtext COMMENT '输出描述',
  `examples` longtext COMMENT '题面样例',
  `is_remote` tinyint(1) DEFAULT '0' COMMENT '是否为vj判题',
  `source` text COMMENT '题目来源',
  `difficulty` int(11) DEFAULT '0' COMMENT '题目难度,0简单，1中等，2困难',
  `hint` longtext COMMENT '备注,提醒',
  `auth` int(11) DEFAULT '1' COMMENT '默认为1公开，2为私有，3为比赛题目',
  `io_score` int(11) DEFAULT '100' COMMENT '当该题目为io题目时的分数',
  `code_share` tinyint(1) DEFAULT '1' COMMENT '该题目对应的相关提交代码，用户是否可用分享',
  `judge_mode` varchar(255) DEFAULT 'default' COMMENT '题目评测模式,default、spj、interactive',
  `user_extra_file` mediumtext DEFAULT NULL COMMENT '题目评测时用户程序的额外文件 json key:name value:content',
  `judge_extra_file` mediumtext DEFAULT NULL COMMENT '题目评测时交互或特殊程序的额外文件 json key:name value:content',
  `spj_code` longtext COMMENT '特判程序或交互程序代码',
  `spj_language` varchar(255) DEFAULT NULL COMMENT '特判程序或交互程序代码的语言',
  `is_remove_end_blank` tinyint(1) DEFAULT '1' COMMENT '是否默认去除用户代码的文末空格',
  `open_case_result` tinyint(1) DEFAULT '1' COMMENT '是否默认开启该题目的测试样例结果查看',
  `is_upload_case` tinyint(1) DEFAULT '1' COMMENT '题目测试数据是否是上传文件的',
  `case_version` varchar(40) DEFAULT '0' COMMENT '题目测试数据的版本号',
  `modified_user` varchar(255) DEFAULT NULL COMMENT '修改题目的管理员用户名',
  `is_public` tinyint(1) DEFAULT '1',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `author` (`author`),
  KEY `problem_id` (`problem_id`),
  KEY `gid` (`gid`),
  CONSTRAINT `problem_ibfk_1` FOREIGN KEY (`author`) REFERENCES `user_info` (`username`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `problem_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `problem_case`;

CREATE TABLE `problem_case` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `pid` bigint(20) unsigned NOT NULL COMMENT '题目id',
  `input` longtext COMMENT '测试样例的输入',
  `output` longtext COMMENT '测试样例的输出',
  `score` int(11) DEFAULT NULL COMMENT '该测试样例的IO得分',
  `status` int(11) DEFAULT '0' COMMENT '0可用，1不可用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`),
  CONSTRAINT `problem_case_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `problem_language`;

CREATE TABLE `problem_language` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) unsigned NOT NULL,
  `lid` bigint(20) unsigned NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`),
  KEY `lid` (`lid`),
  CONSTRAINT `problem_language_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `problem_language_ibfk_2` FOREIGN KEY (`lid`) REFERENCES `language` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `problem_tag`;

CREATE TABLE `problem_tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) unsigned NOT NULL,
  `tid` bigint(20) unsigned NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`),
  KEY `tid` (`tid`),
  CONSTRAINT `problem_tag_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `problem_tag_ibfk_2` FOREIGN KEY (`tid`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `reply`;

CREATE TABLE `reply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) NOT NULL COMMENT '被回复的评论id',
  `from_uid` varchar(32) NOT NULL COMMENT '发起回复的用户id',
  `from_name` varchar(255) NOT NULL COMMENT '发起回复的用户名',
  `from_avatar` varchar(255) DEFAULT NULL COMMENT '发起回复的用户头像地址',
  `from_role` varchar(255) DEFAULT NULL COMMENT '发起回复的用户角色',
  `to_uid` varchar(255) NOT NULL COMMENT '被回复的用户id',
  `to_name` varchar(32) NOT NULL COMMENT '被回复的用户名',
  `to_avatar` varchar(255) DEFAULT NULL COMMENT '被回复的用户头像地址',
  `content` longtext COMMENT '回复的内容',
  `status` int(11) DEFAULT '0' COMMENT '是否封禁或逻辑删除该回复',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `comment_id` (`comment_id`),
  KEY `from_avatar` (`from_avatar`),
  KEY `to_avatar` (`to_avatar`),
  CONSTRAINT `reply_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reply_ibfk_2` FOREIGN KEY (`from_avatar`) REFERENCES `user_info` (`avatar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reply_ibfk_3` FOREIGN KEY (`to_avatar`) REFERENCES `user_info` (`avatar`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` bigint(20) unsigned zerofill NOT NULL,
  `role` varchar(50) NOT NULL COMMENT '角色',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '默认0可用，1不可用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `role_auth`;

CREATE TABLE `role_auth` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `auth_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `auth_id` (`auth_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `role_auth_ibfk_1` FOREIGN KEY (`auth_id`) REFERENCES `auth` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_auth_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `session`;

CREATE TABLE `session` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `user_agent` varchar(512) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `session_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '标签名字',
  `color` varchar(10) DEFAULT NULL COMMENT '标签颜色',
  `oj` varchar(255) DEFAULT 'ME' COMMENT '标签所属oj',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_oj_gid_unique` (`name`, `oj`, `gid`),
  KEY `gid` (`gid`),
  CONSTRAINT `tag_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_acproblem`;

CREATE TABLE `user_acproblem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `pid` bigint(20) unsigned NOT NULL COMMENT 'ac的题目id',
  `submit_id` bigint(20) unsigned NOT NULL COMMENT '提交id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `submit_id` (`submit_id`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`),
  CONSTRAINT `user_acproblem_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_acproblem_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_acproblem_ibfk_3` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `uuid` varchar(32) NOT NULL,
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `school` varchar(100) DEFAULT NULL COMMENT '学校',
  `course` varchar(100) DEFAULT NULL COMMENT '专业',
  `number` varchar(20) DEFAULT NULL COMMENT '学号',
  `realname` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `gender` varchar(20) DEFAULT 'secrecy' NOT NULL  COMMENT '性别',
  `github` varchar(255) DEFAULT NULL COMMENT 'github地址',
  `blog` varchar(255) DEFAULT NULL COMMENT '博客地址',
  `cf_username` varchar(255) DEFAULT NULL COMMENT 'cf的username',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `signature` mediumtext COMMENT '个性签名',
  `title_name` varchar(255) DEFAULT NULL COMMENT '头衔、称号',
  `title_color` varchar(255) DEFAULT NULL COMMENT '头衔、称号的颜色',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0可用，1不可用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `username_unique` (`username`),
  UNIQUE KEY `email_unique` (`email`),
  UNIQUE KEY `avatar_unique` (`avatar`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_record`;

CREATE TABLE `user_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `rating` int(11) DEFAULT NULL COMMENT 'cf得分',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`uid`),
  KEY `uid` (`uid`),
  CONSTRAINT `user_record_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `remote_judge_account`;

CREATE TABLE `remote_judge_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oj` varchar(50) NOT NULL COMMENT '远程oj名字',
  `username` varchar(255) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `version` bigint(20) DEFAULT '0' COMMENT '版本控制',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `admin_sys_notice`;

CREATE TABLE `admin_sys_notice` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '内容',
  `type` varchar(255) DEFAULT NULL COMMENT '发给哪些用户类型',
  `state` tinyint(1) DEFAULT '0' COMMENT '是否已拉取给用户',
  `recipient_id` varchar(32) DEFAULT NULL COMMENT '接受通知的用户id',
  `admin_id` varchar(32) DEFAULT NULL COMMENT '发送通知的管理员id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `recipient_id` (`recipient_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `admin_sys_notice_ibfk_1` FOREIGN KEY (`recipient_id`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `admin_sys_notice_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `msg_remind`;

CREATE TABLE `msg_remind` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL COMMENT '动作类型，如点赞讨论帖Like_Post、点赞评论Like_Discuss、评论Discuss、回复Reply等',
  `source_id` int(10) unsigned DEFAULT NULL COMMENT '消息来源id，讨论id或比赛id',
  `source_type` varchar(255) DEFAULT NULL COMMENT '事件源类型：''Discussion''、''Contest''等',
  `source_content` varchar(255) DEFAULT NULL COMMENT '事件源的内容，比如回复的内容，评论的帖子标题等等',
  `quote_id` int(10) unsigned DEFAULT NULL COMMENT '事件引用上一级评论或回复id',
  `quote_type` varchar(255) DEFAULT NULL COMMENT '事件引用上一级的类型：Comment、Reply',
  `url` varchar(255) DEFAULT NULL COMMENT '事件所发生的地点链接 url',
  `state` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `sender_id` varchar(32) DEFAULT NULL COMMENT '操作者的id',
  `recipient_id` varchar(32) DEFAULT NULL COMMENT '接受消息的用户id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  KEY `recipient_id` (`recipient_id`),
  CONSTRAINT `msg_remind_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `msg_remind_ibfk_2` FOREIGN KEY (`recipient_id`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_sys_notice`;

CREATE TABLE `user_sys_notice` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `sys_notice_id` bigint(20) unsigned DEFAULT NULL COMMENT '系统通知的id',
  `recipient_id` varchar(32) DEFAULT NULL COMMENT '接受通知的用户id',
  `type` varchar(255) DEFAULT NULL COMMENT '消息类型，系统通知sys、我的信息mine',
  `state` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '读取时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sys_notice_id` (`sys_notice_id`),
  KEY `recipient_id` (`recipient_id`),
  CONSTRAINT `user_sys_notice_ibfk_1` FOREIGN KEY (`sys_notice_id`) REFERENCES `admin_sys_notice` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_sys_notice_ibfk_2` FOREIGN KEY (`recipient_id`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `training`;

CREATE TABLE `training` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '训练题单名称',
  `description` longtext COMMENT '训练题单简介',
  `author` varchar(255) NOT NULL COMMENT '训练题单创建者用户名',
  `auth` varchar(255) NOT NULL COMMENT '训练题单权限类型：Public、Private',
  `private_pwd` varchar(255) DEFAULT NULL COMMENT '训练题单权限为Private时的密码',
  `rank` int DEFAULT '0' COMMENT '编号，升序',
  `status` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `is_public` tinyint(1) DEFAULT '1',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `gid` (`gid`),
  CONSTRAINT `training_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `training_category`;

CREATE TABLE `training_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `gid` (`gid`),
  CONSTRAINT `training_category_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `training_problem`;

CREATE TABLE `training_problem` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tid` bigint unsigned NOT NULL COMMENT '训练id',
  `pid` bigint unsigned NOT NULL COMMENT '题目id',
  `rank` int DEFAULT '0',
  `display_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `tid` (`tid`),
  KEY `pid` (`pid`),
  KEY `display_id` (`display_id`),
  CONSTRAINT `training_problem_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_problem_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_problem_ibfk_3` FOREIGN KEY (`display_id`) REFERENCES `problem` (`problem_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `training_record`;

CREATE TABLE `training_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tid` bigint unsigned NOT NULL,
  `tpid` bigint unsigned NOT NULL,
  `pid` bigint unsigned NOT NULL,
  `uid` varchar(32) NOT NULL,
  `submit_id` bigint unsigned NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `tid` (`tid`),
  KEY `tpid` (`tpid`),
  KEY `pid` (`pid`),
  KEY `uid` (`uid`),
  KEY `submit_id` (`submit_id`),
  CONSTRAINT `training_record_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_record_ibfk_2` FOREIGN KEY (`tpid`) REFERENCES `training_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_record_ibfk_3` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_record_ibfk_4` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_record_ibfk_5` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `training_register`;

CREATE TABLE `training_register` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tid` bigint unsigned NOT NULL COMMENT '训练id',
  `uid` varchar(255) NOT NULL COMMENT '用户id',
  `status` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `tid` (`tid`),
  KEY `uid` (`uid`),
  CONSTRAINT `training_register_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `training_register_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mapping_training_category`;

CREATE TABLE `mapping_training_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tid` bigint unsigned NOT NULL,
  `cid` bigint unsigned NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `tid` (`tid`),
  KEY `cid` (`cid`),
  CONSTRAINT `mapping_training_category_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `training` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `mapping_training_category_ibfk_2` FOREIGN KEY (`cid`) REFERENCES `training_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `group`;

CREATE TABLE `group` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `name` varchar(25) DEFAULT NULL COMMENT '团队名称',
  `short_name` varchar(10) DEFAULT NULL COMMENT '团队简称，创建题目时题号自动添加的前缀',
  `brief` varchar(50) COMMENT '团队简介',
  `description` longtext COMMENT '团队介绍',
  `owner` varchar(255) NOT NULL COMMENT '团队拥有者用户名',
  `uid` varchar(32) NOT NULL,
  `auth` int(11) NOT NULL COMMENT '1为Public，2为Protected，3为Private',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见',
  `status` tinyint(1) DEFAULT '0' COMMENT '是否封禁',
  `code` varchar(6) DEFAULT NULL COMMENT '邀请码',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_unique` (`name`),
  UNIQUE KEY `short_name_unique` (`short_name`),
  CONSTRAINT `group_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `group_member`;

CREATE TABLE `group_member` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gid` bigint unsigned NOT NULL COMMENT '团队id',
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `auth` int(11) DEFAULT '1' COMMENT '1未审批，2拒绝，3普通成员，4团队管理员，5团队拥有者',
  `reason` varchar(100) DEFAULT NULL COMMENT '申请理由',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `gid_uid_unique` (`gid`, `uid`),
  KEY `gid` (`gid`),
  KEY `uid` (`uid`),
  CONSTRAINT `group_member_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_member_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `question`;

CREATE TABLE `question` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` varchar(255) NOT NULL COMMENT '问题的自定义ID 例如（OJ-1000）',
  `author` varchar(255) DEFAULT '未知' COMMENT '作者',
  `type` int(11) NOT NULL DEFAULT '1' COMMENT '1选择题，2填空题，3简答题',
  `auth` int(11) DEFAULT '1' COMMENT '默认为1公开，2为私有，3为考试题目',
  `single` tinyint(1) DEFAULT '1',
  `description` longtext COMMENT '描述',
  `answer` longtext COMMENT '答案',
  `choices` longtext COMMENT '选项',
  `radio` int(11) DEFAULT NULL,
  `judge` tinyint(1) DEFAULT '1',
  `share` tinyint(1) DEFAULT '0',
  `modified_user` varchar(255) DEFAULT NULL COMMENT '修改问题的管理员用户名',
  `is_public` tinyint(1) DEFAULT '1',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `author` (`author`),
  KEY `question_id` (`question_id`),
  KEY `gid` (`gid`),
  CONSTRAINT `question_ibfk_1` FOREIGN KEY (`author`) REFERENCES `user_info` (`username`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `question_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exam`;

CREATE TABLE `exam` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL COMMENT '考试创建者id',
  `author` varchar(255) DEFAULT NULL COMMENT '考试创建者的用户名',
  `title` varchar(255) DEFAULT NULL COMMENT '考试标题',
  `description` longtext COMMENT '考试说明',
  `source` int(11) DEFAULT '0' COMMENT '考试来源，原创为0，克隆考试为考试id',
  `auth` int(11) NOT NULL COMMENT '0为公开考试，1为私有考试（访问有密码），2为保护考试（提交有密码）',
  `pwd` varchar(255) DEFAULT NULL COMMENT '考试密码',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` bigint(20) DEFAULT NULL COMMENT '考试时长(s)',
  `real_score` tinyint(1) DEFAULT '1' COMMENT '编程题是否实时出分',
  `auto_real_score` tinyint(1) DEFAULT '1' COMMENT '考试结束是否自动公开成绩以及小题分',
  `status` int(11) DEFAULT NULL COMMENT '-1为未开始，0为进行中，1为已结束',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见',
  `open_account_limit` tinyint(1) DEFAULT '0' COMMENT '是否开启账号限制',
  `account_limit_rule` mediumtext COMMENT '账号限制规则',
  `rank_show_name` varchar(20) DEFAULT 'username' COMMENT '排行榜显示（username、nickname、realname）',
  `rank_score_type` varchar(255) DEFAULT 'Recent' COMMENT '编程题目得分方式，Recent、Highest',
  `is_public` tinyint(1) DEFAULT '1',
  `gid` bigint(20) unsigned DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`uid`),
  KEY `uid` (`uid`),
  KEY `gid` (`gid`),
  CONSTRAINT `exam_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exam_question`;

CREATE TABLE `exam_question` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `display_id` varchar(255) NOT NULL COMMENT '该问题在考试中的顺序id',
  `eid` bigint(20) unsigned NOT NULL COMMENT '考试id',
  `qid` bigint(20) unsigned NOT NULL COMMENT '问题id',
  `score` int(11) NOT NULL COMMENT '问题分数',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`eid`,`qid`),
  UNIQUE KEY `display_id_eid_qid_unique` (`display_id`,`eid`,`qid`),
  KEY `eid` (`eid`),
  KEY `qid` (`qid`),
  CONSTRAINT `exam_question_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_question_ibfk_2` FOREIGN KEY (`qid`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exam_problem`;

CREATE TABLE `exam_problem` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `display_id` varchar(255) NOT NULL COMMENT '该题目在考试中的顺序id',
  `eid` bigint(20) unsigned NOT NULL COMMENT '考试id',
  `pid` bigint(20) unsigned NOT NULL COMMENT '题目id',
  `display_title` varchar(255) NOT NULL COMMENT '该题目在考试中的标题，默认为原名字',
  `score` int(11) NOT NULL COMMENT '题目分数',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`eid`,`pid`),
  UNIQUE KEY `display_id_eid_pid_unique` (`display_id`,`eid`,`pid`),
  KEY `eid` (`eid`),
  KEY `pid` (`pid`),
  CONSTRAINT `exam_problem_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_problem_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exam_record`;

CREATE TABLE `exam_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `eid` bigint(20) unsigned DEFAULT NULL COMMENT '考试id',
  `uid` varchar(255) NOT NULL COMMENT '用户id',
  `pid` bigint(20) unsigned DEFAULT NULL COMMENT '题目id',
  `epid` bigint(20) unsigned DEFAULT NULL COMMENT '考试中的题目的id',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `display_id` varchar(255) DEFAULT NULL COMMENT '考试中展示的id',
  `submit_id` bigint(20) unsigned NOT NULL COMMENT '提交id，用于可重判',
  `submit_time` datetime NOT NULL COMMENT '具体提交时间',
  `time` bigint(20) unsigned DEFAULT NULL COMMENT '提交时间，为提交时间减去比赛时间',
  `score` int(11) DEFAULT NULL COMMENT '得分',
  `use_time` int(11) DEFAULT NULL COMMENT '运行耗时',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`submit_id`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`),
  KEY `epid` (`epid`),
  KEY `submit_id` (`submit_id`),
  KEY `eid` (`eid`),
  KEY `time` (`time`),
  CONSTRAINT `exam_record_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_record_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_record_ibfk_3` FOREIGN KEY (`pid`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_record_ibfk_4` FOREIGN KEY (`epid`) REFERENCES `exam_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_record_ibfk_5` FOREIGN KEY (`submit_id`) REFERENCES `judge` (`submit_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exam_question_record`;

CREATE TABLE `exam_question_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `eid` bigint(20) unsigned DEFAULT NULL COMMENT '考试id',
  `uid` varchar(255) NOT NULL COMMENT '用户id',
  `qid` bigint(20) unsigned DEFAULT NULL COMMENT '问题id',
  `eqid` bigint(20) unsigned DEFAULT NULL COMMENT '考试中的问题的id',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `display_id` varchar(255) DEFAULT NULL COMMENT '考试中展示的id',
  `submit_answer` longtext COMMENT '提交的答案',
  `submit_time` datetime NOT NULL COMMENT '具体提交时间',
  `time` bigint(20) unsigned DEFAULT NULL COMMENT '提交时间，为提交时间减去考试时间',
  `score` int(11) DEFAULT NULL COMMENT '得分',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `qid` (`qid`),
  KEY `eqid` (`eqid`),
  KEY `eid` (`eid`),
  KEY `time` (`time`),
  CONSTRAINT `exam_question_record_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_question_record_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_question_record_ibfk_3` FOREIGN KEY (`qid`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_question_record_ibfk_4` FOREIGN KEY (`eqid`) REFERENCES `exam_question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exam_register`;

CREATE TABLE `exam_register` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `eid` bigint(20) unsigned NOT NULL COMMENT '考试id',
  `uid` varchar(32) NOT NULL COMMENT '用户id',
  `status` int(11) DEFAULT '0' COMMENT '默认为0表示正常，1为失效。',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`eid`,`uid`),
  UNIQUE KEY `eid_uid_unique` (`eid`,`uid`),
  KEY `uid` (`uid`),
  CONSTRAINT `exam_register_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_register_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user_info` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `contest_trigger` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `contest_trigger` BEFORE INSERT ON `contest` FOR EACH ROW BEGIN
set new.status=(
	CASE 
	  WHEN NOW() < new.start_time THEN -1 
	  WHEN NOW() >= new.start_time AND NOW()<new.end_time THEN  0
	  WHEN NOW() >= new.end_time THEN 1
	END);
END */$$

DELIMITER ;

/*!50106 set global event_scheduler = 1*/;

/* Event structure for event `contest_event` */

/*!50106 DROP EVENT IF EXISTS `contest_event`*/;

DELIMITER $$

/*!50106 CREATE DEFINER=`root`@`localhost` EVENT `contest_event` ON SCHEDULE EVERY 1 SECOND STARTS '2021-04-18 19:04:49' ON COMPLETION PRESERVE ENABLE DO CALL contest_status() */$$
DELIMITER ;

/* Procedure structure for procedure `contest_status` */

/*!50003 DROP PROCEDURE IF EXISTS  `contest_status` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `contest_status`()
BEGIN
      UPDATE contest 
	SET STATUS = (
	CASE 
	  WHEN NOW() < start_time THEN -1 
	  WHEN NOW() >= start_time AND NOW()<end_time THEN  0
	  WHEN NOW() >= end_time THEN 1
	END);
    END */$$
DELIMITER ;

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `exam_trigger` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `exam_trigger` BEFORE INSERT ON `exam` FOR EACH ROW BEGIN
set new.status=(
	CASE 
	  WHEN NOW() < new.start_time THEN -1 
	  WHEN NOW() >= new.start_time AND NOW()<new.end_time THEN  0
	  WHEN NOW() >= new.end_time THEN 1
	END);
END */$$

DELIMITER ;

/* Event structure for event `exam_event` */

/*!50106 DROP EVENT IF EXISTS `exam_event`*/;

DELIMITER $$

/*!50106 CREATE DEFINER=`root`@`localhost` EVENT `exam_event` ON SCHEDULE EVERY 1 SECOND STARTS '2022-04-16 21:51:08' ON COMPLETION PRESERVE ENABLE DO CALL exam_status() */$$
DELIMITER ;

/* Procedure structure for procedure `exam_status` */

/*!50003 DROP PROCEDURE IF EXISTS  `exam_status` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `exam_status`()
BEGIN
      UPDATE exam 
	SET STATUS = (
	CASE 
	  WHEN NOW() < start_time THEN -1 
	  WHEN NOW() >= start_time AND NOW()<end_time THEN  0
	  WHEN NOW() >= end_time THEN 1
	END);
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;



/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `oj`;

delete from `auth`;

delete from `category`;

delete from `language`;

delete from `role`;

delete from `role_auth`;

delete from `user_record`;

delete from `user_role`;

delete from `user_info`;

insert  into `auth`(`id`,`name`,`permission`,`status`,`gmt_create`,`gmt_modified`) values (1,'problem','problem_admin',0,NOW(),NOW()),(2,'submit','submit',0,NOW(),NOW()),(3,'contest','contest_admin',0,NOW(),NOW()),(4,'rejudge','rejudge',0,NOW(),NOW()),(5,'announcement','announcement_admin',0,NOW(),NOW()),(6,'user','user_admin',0,NOW(),NOW()),(7,'system_info','system_info_admin',0,NOW(),NOW()),(8,'dicussion','discussion_add',0,NOW(),NOW()),(9,'dicussion','discussion_del',0,NOW(),NOW()),(10,'dicussion','discussion_edit',0,NOW(),NOW()),(11,'comment','comment_add',0,NOW(),NOW()),(12,'reply','reply_add',0,NOW(),NOW()),(13,'group','group_add',0,NOW(),NOW()),(14,'group','group_del',0,NOW(),NOW());

insert  into `category`(`id`,`name`,`gmt_create`,`gmt_modified`) values (1,'闲聊',NOW(),NOW()),(2,'题解',NOW(),NOW()),(3,'求助',NOW(),NOW()),(4,'建议',NOW(),NOW()),(5,'记录',NOW(),NOW());

insert  into `language`(`id`,`content_type`,`description`,`name`,`compile_command`,`template`,`code_template`,`is_spj`,`oj`,`gmt_create`,`gmt_modified`) values (1,'text/x-csrc','GCC 7.5.0','C','/usr/bin/gcc -DONLINE_JUDGE -w -fmax-errors=3 -std=c11 {src_path} -lm -o {exe_path}','#include <stdio.h>\r\nint main() {\r\n    int a,b;\r\n    scanf(\"%d %d\",&a,&b);\r\n    printf(\"%d\",a+b);\r\n    return 0;\r\n}','//PREPEND BEGIN\r\n#include <stdio.h>\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  printf(\"%d\", add(1, 2));\r\n  return 0;\r\n}\r\n//APPEND END',1,'ME',NOW(),NOW()),(2,'text/x-csrc','GCC 9.4.0','C With O2','/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c11 {src_path} -lm -o {exe_path}','#include <stdio.h>\r\nint main() {\r\n    int a,b;\r\n    scanf(\"%d %d\",&a,&b);\r\n    printf(\"%d\",a+b);\r\n    return 0;\r\n}','//PREPEND BEGIN\r\n#include <stdio.h>\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  printf(\"%d\", add(1, 2));\r\n  return 0;\r\n}\r\n//APPEND END',0,'ME',NOW(),NOW()),(3,'text/x-c++src','G++ 9.4.0','C++','/usr/bin/g++ -DONLINE_JUDGE -w -fmax-errors=3 -std=c++14 {src_path} -lm -o {exe_path}','#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}','//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',1,'ME',NOW(),NOW()),(4,'text/x-c++src','G++ 7.5.0','C++ With O2','/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++14 {src_path} -lm -o {exe_path}','#include<iostream>\r\nusing namespace std;\r\nint main()\r\n{\r\n    int a,b;\r\n    cin >> a >> b;\r\n    cout << a + b;\r\n    return 0;\r\n}','//PREPEND BEGIN\r\n#include <iostream>\r\nusing namespace std;\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\nint add(int a, int b) {\r\n  // Please fill this blank\r\n  return ___________;\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nint main() {\r\n  cout << add(1, 2);\r\n  return 0;\r\n}\r\n//APPEND END',0,'ME',NOW(),NOW()),(5,'text/x-java','OpenJDK 1.8','Java','/usr/bin/javac {src_path} -d {exe_dir} -encoding UTF8','import java.util.Scanner;\r\npublic class Main{\r\n    public static void main(String[] args){\r\n        Scanner in=new Scanner(System.in);\r\n        int a=in.nextInt();\r\n        int b=in.nextInt();\r\n        System.out.println((a+b));\r\n    }\r\n}','//PREPEND BEGIN\r\nimport java.util.Scanner;\r\n//PREPEND END\r\n\r\npublic class Main{\r\n    //TEMPLATE BEGIN\r\n    public static Integer add(int a,int b){\r\n        return _______;\r\n    }\r\n    //TEMPLATE END\r\n\r\n    //APPEND BEGIN\r\n    public static void main(String[] args){\r\n        System.out.println(add(a,b));\r\n    }\r\n    //APPEND END\r\n}\r\n',0,'ME',NOW(),NOW()),(6,'text/x-python','Python 3.8.10','Python3','/usr/bin/python3 -m py_compile {src_path}','a, b = map(int, input().split())\r\nprint(a + b)','//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':  \r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print(add(a, b))\r\n    //APPEND END',0,'ME',NOW(),NOW()),(7,'text/x-python','Python 2.7.18','Python2','/usr/bin/python -m py_compile {src_path}','a, b = map(int, raw_input().split())\r\nprint a+b','//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':  \r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print add(a, b)\r\n    //APPEND END',0,'ME',NOW(),NOW()),(8,'text/x-go','Golang 1.18','Golang','/usr/bin/go build -o {exe_path} {src_path}','package main\r\nimport \"fmt\"\r\n\r\nfunc main(){\r\n    var x int\r\n    var y int\r\n    fmt.Scanln(&x,&y)\r\n    fmt.Printf(\"%d\",x+y)  \r\n}\r\n','\r\npackage main\r\n\r\n//PREPEND BEGIN\r\nimport \"fmt\"\r\n//PREPEND END\r\n\r\n\r\n//TEMPLATE BEGIN\r\nfunc add(a,b int)int{\r\n    return ______\r\n}\r\n//TEMPLATE END\r\n\r\n//APPEND BEGIN\r\nfunc main(){\r\n    var x int\r\n    var y int\r\n    fmt.Printf(\"%d\",add(x,y))  \r\n}\r\n//APPEND END\r\n',0,'ME',NOW(),NOW()),(9,'text/x-csharp','C# Mono 6.8.0','C#','/usr/bin/mcs -optimize+ -out:{exe_path} {src_path}','using System;\r\nusing System.Linq;\r\n\r\nclass Program {\r\n    public static void Main(string[] args) {\r\n        Console.WriteLine(Console.ReadLine().Split().Select(int.Parse).Sum());\r\n    }\r\n}','//PREPEND BEGIN\r\nusing System;\r\nusing System.Collections.Generic;\r\nusing System.Text;\r\n//PREPEND END\r\n\r\nclass Solution\r\n{\r\n    //TEMPLATE BEGIN\r\n    static int add(int a,int b){\r\n        return _______;\r\n    }\r\n    //TEMPLATE END\r\n\r\n    //APPEND BEGIN\r\n    static void Main(string[] args)\r\n    {\r\n        int a ;\r\n        int b ;\r\n        Console.WriteLine(add(a,b));\r\n    }\r\n    //APPEND END\r\n}',0,'ME',NOW(),NOW()),(11,'text/x-csrc','GCC','GCC',NULL,NULL,NULL,0,'HDU',NOW(),NOW()),(12,'text/x-c++src','G++','G++',NULL,NULL,NULL,0,'HDU',NOW(),NOW()),(13,'text/x-c++src','C++','C++',NULL,NULL,NULL,0,'HDU',NOW(),NOW()),(14,'text/x-csrc','C','C',NULL,NULL,NULL,0,'HDU',NOW(),NOW()),(15,'text/x-pascal','Pascal','Pascal',NULL,NULL,NULL,0,'HDU',NOW(),NOW()),(16,'text/x-csrc','GNU GCC C11 5.1.0','GNU GCC C11 5.1.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(17,'text/x-c++src','Clang++17 Diagnostics','Clang++17 Diagnostics',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(19,'text/x-c++src','GNU G++14 6.4.0','GNU G++14 6.4.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(20,'text/x-c++src','GNU G++17 7.3.0','GNU G++17 7.3.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(21,'text/x-c++src','GNU G++20 11.2.0 (64 bit, winlibs)','GNU G++20 11.2.0 (64 bit, winlibs)',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(22,'text/x-c++src','Microsoft Visual C++ 2017','Microsoft Visual C++ 2017',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(23,'text/x-csharp','C# Mono 6.8','C# Mono 6.8',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(24,'text/x-d','D DMD32 v2.091.0','D DMD32 v2.091.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(25,'text/x-go','Go 1.15.6','Go 1.15.6',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(26,'text/x-haskell','Haskell GHC 8.10.1','Haskell GHC 8.10.1',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(27,'text/x-java','Java 1.8.0_241','Java 1.8.0_241',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(28,'text/x-java','Kotlin 1.4.0','Kotlin 1.4.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(29,'text/x-ocaml','OCaml 4.02.1','OCaml 4.02.1',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(30,'text/x-pascal','Delphi 7','Delphi 7',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(31,'text/x-pascal','Free Pascal 3.0.2','Free Pascal 3.0.2',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(32,'text/x-pascal','PascalABC.NET 3.4.2','PascalABC.NET 3.4.2',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(33,'text/x-perl','Perl 5.20.1','Perl 5.20.1',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(34,'text/x-php','PHP 7.2.13','PHP 7.2.13',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(35,'text/x-python','Python 2.7.18','Python 2.7.18',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(36,'text/x-python','Python 3.9.1','Python 3.9.1',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(37,'text/x-python','PyPy 2.7 (7.3.0)','PyPy 2.7 (7.3.0)',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(38,'text/x-python','PyPy 3.7 (7.3.0)','PyPy 3.7 (7.3.0)',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(39,'text/x-ruby','Ruby 3.0.0','Ruby 3.0.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(40,'text/x-rustsrc','Rust 1.49.0','Rust 1.49.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(41,'text/x-scala','Scala 2.12.8','Scala 2.12.8',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(42,'text/javascript','JavaScript V8 4.8.0','JavaScript V8 4.8.0',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(43,'text/javascript','Node.js 12.6.3','Node.js 12.6.3',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(44,'text/x-csharp','C# 8, .NET Core 3.1','C# 8, .NET Core 3.1',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(45,'text/x-java','Java 11.0.6','Java 11.0.6',NULL,NULL,NULL,0,'CF',NOW(),NOW()),(46,'text/x-c++src','G++','G++',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(47,'text/x-csrc','GCC','GCC',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(48,'text/x-java','Java','Java',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(49,'text/x-pascal','Pascal','Pascal',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(50,'text/x-c++src','C++','C++',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(51,'text/x-csrc','C','C',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(52,'text/x-fortran','Fortran','Fortran',NULL,NULL,NULL,0,'POJ',NOW(),NOW()),(53,'text/x-php','PHP 7.3.33','PHP','/usr/bin/php {src_path}','<?=array_sum(fscanf(STDIN, \"%d %d\"));','',0,'ME',NOW(),NOW()),(54,'text/javascript','Node.js 14.19.0','JavaScript Node','/usr/bin/node {src_path}','var readline = require(\'readline\');\r\nconst rl = readline.createInterface({\r\n        input: process.stdin,\r\n        output: process.stdout\r\n});\r\nrl.on(\'line\', function(line){\r\n   var tokens = line.split(\' \');\r\n    console.log(parseInt(tokens[0]) + parseInt(tokens[1]));\r\n});','',0,'ME',NOW(),NOW()),(55,'text/javascript','JavaScript V8 8.4.109','JavaScript V8','/usr/bin/jsv8 {src_path}','const [a, b] = readline().split(\' \').map(n => parseInt(n, 10));\r\nprint((a + b).toString());','',0,'ME',NOW(),NOW()),(56,'text/x-python','PyPy 2.7.18 (7.3.8)','PyPy2','/usr/bin/pypy -m py_compile {src_path}','print sum(int(x) for x in raw_input().split(\' \'))','//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':  \r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print add(a, b)\r\n    //APPEND END',0,'ME',NOW(),NOW()),(57,'text/x-python','PyPy 3.8.12 (7.3.8)','PyPy3','/usr/bin/pypy3 -m py_compile {src_path}','print(sum(int(x) for x in input().split(\' \')))','//PREPEND BEGIN\r\n//PREPEND END\r\n\r\n//TEMPLATE BEGIN\r\ndef add(a, b):\r\n    return a + b\r\n//TEMPLATE END\r\n\r\n\r\nif __name__ == \'__main__\':  \r\n    //APPEND BEGIN\r\n    a, b = 1, 1\r\n    print(add(a, b))\r\n    //APPEND END',0,'ME',NOW(),NOW());

insert  into `role`(`id`,`role`,`description`,`status`,`gmt_create`,`gmt_modified`) values (00000000000000001000,'root','超级管理员',0,NOW(),NOW()),(00000000000000001001,'admin','管理员',0,NOW(),NOW()),(00000000000000001002,'default_user','默认用户',0,NOW(),NOW()),(00000000000000001003,'no_subimit_user','禁止提交用户',0,NOW(),NOW()),(00000000000000001004,'no_discuss_user','禁止发贴讨论用户',0,NOW(),NOW()),(00000000000000001005,'mute_user','禁言包括回复讨论发帖用户',0,NOW(),NOW()),(00000000000000001006,'no_submit_no_discuss_user','禁止提交同时禁止发帖用户',0,NOW(),NOW()),(00000000000000001007,'no_submit_mute_user','禁言禁提交用户',0,NOW(),NOW()),(00000000000000001008,'problem_admin','题目管理员',0,NOW(),NOW());

insert  into `role_auth`(`id`,`auth_id`,`role_id`,`gmt_create`,`gmt_modified`) values (1,1,1000,NOW(),NOW()),(2,2,1000,NOW(),NOW()),(3,3,1000,NOW(),NOW()),(4,4,1000,NOW(),NOW()),(5,5,1000,NOW(),NOW()),(6,6,1000,NOW(),NOW()),(7,7,1000,NOW(),NOW()),(8,8,1000,NOW(),NOW()),(9,9,1000,NOW(),NOW()),(10,10,1000,NOW(),NOW()),(11,11,1000,NOW(),NOW()),(12,12,1000,NOW(),NOW()),(13,1,1001,NOW(),NOW()),(14,2,1001,NOW(),NOW()),(15,3,1001,NOW(),NOW()),(16,8,1001,NOW(),NOW()),(17,9,1001,NOW(),NOW()),(18,10,1001,NOW(),NOW()),(19,11,1001,NOW(),NOW()),(20,12,1001,NOW(),NOW()),(21,2,1002,NOW(),NOW()),(22,8,1002,NOW(),NOW()),(23,9,1002,NOW(),NOW()),(24,10,1002,NOW(),NOW()),(25,11,1002,NOW(),NOW()),(26,12,1002,NOW(),NOW()),(27,8,1003,NOW(),NOW()),(28,9,1003,NOW(),NOW()),(29,10,1003,NOW(),NOW()),(30,11,1003,NOW(),NOW()),(31,12,1003,NOW(),NOW()),(32,2,1004,NOW(),NOW()),(33,9,1004,NOW(),NOW()),(34,10,1004,NOW(),NOW()),(35,11,1004,NOW(),NOW()),(36,12,1004,NOW(),NOW()),(37,2,1005,NOW(),NOW()),(38,9,1005,NOW(),NOW()),(39,10,1005,NOW(),NOW()),(40,9,1006,NOW(),NOW()),(41,10,1006,NOW(),NOW()),(42,11,1006,NOW(),NOW()),(43,12,1006,NOW(),NOW()),(44,9,1007,NOW(),NOW()),(45,10,1007,NOW(),NOW()),(46,1,1008,NOW(),NOW()),(47,2,1008,NOW(),NOW()),(48,3,1008,NOW(),NOW()),(49,8,1008,NOW(),NOW()),(50,9,1008,NOW(),NOW()),(51,10,1008,NOW(),NOW()),(52,11,1008,NOW(),NOW()),(53,12,1008,NOW(),NOW()),(54,13,1000,NOW(),NOW()),(55,13,1001,NOW(),NOW()),(56,13,1002,NOW(),NOW()),(57,13,1008,NOW(),NOW()),(58,14,1000,NOW(),NOW()),(59,14,1001,NOW(),NOW()),(60,14,1002,NOW(),NOW()),(61,14,1008,NOW(),NOW());

insert  into `user_info`(`uuid`,`username`,`password`,`gmt_create`,`gmt_modified`) values('1','root','f570bcc861d1b848d4a054716db9a748',NOW(),NOW());

insert  into `user_record`(`uid`,`gmt_create`,`gmt_modified`) values('1',NOW(),NOW());

insert  into `user_role`(`uid`,`role_id`,`gmt_create`,`gmt_modified`) values('1',00000000000000001000,NOW(),NOW());