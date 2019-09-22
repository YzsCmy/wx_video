/*
Navicat MySQL Data Transfer

Source Server         : yzs
Source Server Version : 50536
Source Host           : localhost:3306
Source Database       : wx-video

Target Server Type    : MYSQL
Target Server Version : 50536
File Encoding         : 65001

Date: 2019-09-22 16:15:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bgm
-- ----------------------------
DROP TABLE IF EXISTS `bgm`;
CREATE TABLE `bgm` (
  `id` varchar(64) NOT NULL,
  `author` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL COMMENT '播放地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bgm
-- ----------------------------
INSERT INTO `bgm` VALUES ('1909228NF5CZ7X1P', 'ava', 'salt', '\\bgm\\Salt.mp3');
INSERT INTO `bgm` VALUES ('1909228NTRWSH9S8', 'blaze u', 'home', '\\bgm\\Home.mp3');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` varchar(20) NOT NULL,
  `father_comment_id` varchar(20) DEFAULT '',
  `to_user_id` varchar(20) DEFAULT NULL,
  `video_id` varchar(20) NOT NULL COMMENT '视频id',
  `from_user_id` varchar(20) NOT NULL COMMENT '留言者，评论的用户id',
  `comment` text NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL,
  `has_child` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评论表';

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES ('19091992SAF30K8H', '', '190913F1G8MCY23C', '190918DNPC223SCH', '1909136SKS6H9BTC', 'fahsjfbs', '2019-09-19 12:45:02', '0');
INSERT INTO `comments` VALUES ('19091992YPR6CDAW', '', '190913F1G8MCY23C', '190918DNPC223SCH', '1909136SKS6H9BTC', 'fhfsd', '2019-09-19 12:45:30', '0');
INSERT INTO `comments` VALUES ('19091992ZNCN33TC', '', '190913F1G8MCY23C', '190918DNPC223SCH', '1909136SKS6H9BTC', 'dscvbsdfghjd', '2019-09-19 12:45:36', '1');
INSERT INTO `comments` VALUES ('190919AF2909XCSW', '19091992ZNCN33TC', '1909136SKS6H9BTC', '190918DNPC223SCH', '180425CFA4RB6T0H', '呵呵', '2019-09-19 14:42:59', '0');
INSERT INTO `comments` VALUES ('190919C90SHGZC94', '', '190913F1G8MCY23C', '190918DNPC223SCH', '180425CFA4RB6T0H', 'jsdkjksd', '2019-09-19 17:15:56', '0');
INSERT INTO `comments` VALUES ('190919C91NW8A4PH', '', '190913F1G8MCY23C', '190918DNPC223SCH', '180425CFA4RB6T0H', 'fdsfsd', '2019-09-19 17:16:02', '0');
INSERT INTO `comments` VALUES ('190919C923CMWBR4', '', '190913F1G8MCY23C', '190918DNPC223SCH', '180425CFA4RB6T0H', 'dsfsd', '2019-09-19 17:16:05', '1');
INSERT INTO `comments` VALUES ('190919C95F8TP754', '19091992ZNCN33TC', '1909136SKS6H9BTC', '190918DNPC223SCH', '180425CFA4RB6T0H', 'fgdf', '2019-09-19 17:16:27', '0');
INSERT INTO `comments` VALUES ('190919C994H86KD4', '19091992ZNCN33TC', '1909136SKS6H9BTC', '190918DNPC223SCH', '180425CFA4RB6T0H', 'fhdf', '2019-09-19 17:16:50', '0');
INSERT INTO `comments` VALUES ('190919C99N8M4280', '19091992ZNCN33TC', '1909136SKS6H9BTC', '190918DNPC223SCH', '180425CFA4RB6T0H', 'ukuk', '2019-09-19 17:16:54', '0');
INSERT INTO `comments` VALUES ('190919CF1FRD1YFW', '', '190913F1G8MCY23C', '190918DNPC223SCH', '180425CFA4RB6T0H', 'fdsfd', '2019-09-19 17:31:02', '1');
INSERT INTO `comments` VALUES ('190919HY3H900S5P', '190919C923CMWBR4', '180425CFA4RB6T0H', '190918DNPC223SCH', '180425CFA4RB6T0H', 'fsfsfs', '2019-09-19 23:43:34', '0');
INSERT INTO `comments` VALUES ('190919K22HR4GDKP', '19091992ZNCN33TC', '180425CFA4RB6T0H', '190918DNPC223SCH', '180425CFA4RB6T0H', '哈哈', '2019-09-19 23:55:28', '0');
INSERT INTO `comments` VALUES ('1909200195DZ88SW', '190919C923CMWBR4', '180425CFA4RB6T0H', '190918DNPC223SCH', '180425CFA4RB6T0H', '嘻嘻', '2019-09-20 00:03:59', '0');
INSERT INTO `comments` VALUES ('19092001DHW4R680', '19091992ZNCN33TC', '1909136SKS6H9BTC', '190918DNPC223SCH', '180425CFA4RB6T0H', '啊速达三百', '2019-09-20 00:04:27', '0');
INSERT INTO `comments` VALUES ('190920730NNTSCH0', '190919CF1FRD1YFW', '180425CFA4RB6T0H', '190918DNPC223SCH', '180425CFA4RB6T0H', '哇哇哇', '2019-09-20 09:57:34', '0');
INSERT INTO `comments` VALUES ('190920736D5A5FRP', '190919CF1FRD1YFW', '180425CFA4RB6T0H', '190918DNPC223SCH', '180425CFA4RB6T0H', 'gg', '2019-09-20 09:58:11', '0');
INSERT INTO `comments` VALUES ('1909207Y9YB3K7HH', '', '', '190914B3XBD5Y32W', '180425CFA4RB6T0H', 'hh', '2019-09-20 11:07:37', '1');
INSERT INTO `comments` VALUES ('1909207YPB80N63C', '', '190913F1G8MCY23C', '190918DNPC223SCH', '1909136SKS6H9BTC', 'aabbcc得到', '2019-09-20 11:08:44', '1');
INSERT INTO `comments` VALUES ('190920847RB5KC4H', '', '', '190914B3XBD5Y32W', '1909136SKS6H9BTC', 'hello', '2019-09-20 11:25:24', '0');
INSERT INTO `comments` VALUES ('190920849NR4B5D4', '1909207Y9YB3K7HH', '180425CFA4RB6T0H', '190914B3XBD5Y32W', '1909136SKS6H9BTC', 'xx', '2019-09-20 11:25:36', '0');
INSERT INTO `comments` VALUES ('190922B152NWH7TC', '', '190913F1G8MCY23C', '190918DNPC223SCH', '180425CFA4RB6T0H', '你们好啊', '2019-09-22 15:28:19', '0');
INSERT INTO `comments` VALUES ('190922B184T6X77C', '1909207YPB80N63C', '1909136SKS6H9BTC', '190918DNPC223SCH', '180425CFA4RB6T0H', 'jjyy', '2019-09-22 15:28:38', '0');

-- ----------------------------
-- Table structure for search_records
-- ----------------------------
DROP TABLE IF EXISTS `search_records`;
CREATE TABLE `search_records` (
  `id` varchar(64) NOT NULL,
  `content` varchar(255) NOT NULL COMMENT '搜索的内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频搜索的记录表';

-- ----------------------------
-- Records of search_records
-- ----------------------------
INSERT INTO `search_records` VALUES ('18051309YBCMHYRP', '风景');
INSERT INTO `search_records` VALUES ('1805130DAXX58ARP', '风景');
INSERT INTO `search_records` VALUES ('1805130DMG6P0ZC0', '风景');
INSERT INTO `search_records` VALUES ('180513C94W152Z7C', 'dwqdwq');
INSERT INTO `search_records` VALUES ('180513DXNT7SY04H', '风景');
INSERT INTO `search_records` VALUES ('19091587543BFY80', 'aa');
INSERT INTO `search_records` VALUES ('19091587DCBPGH6W', 'bb');
INSERT INTO `search_records` VALUES ('1909167ANPS29YW0', '风景');
INSERT INTO `search_records` VALUES ('1909167AR5Z8N7C0', 'aa');
INSERT INTO `search_records` VALUES ('190916HMWR9T7C00', 'aa');
INSERT INTO `search_records` VALUES ('190918DPN3ADAH4H', 'aa');
INSERT INTO `search_records` VALUES ('190918GD61G3CBR4', 'aa');
INSERT INTO `search_records` VALUES ('7', 'zookeeper');
INSERT INTO `search_records` VALUES ('8', 'zookeeper');
INSERT INTO `search_records` VALUES ('9', 'zookeeper');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` varchar(64) NOT NULL,
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `face_image` varchar(255) DEFAULT NULL COMMENT '我的头像，如果没有默认给一张',
  `nickname` varchar(20) NOT NULL COMMENT '昵称',
  `fans_counts` int(11) DEFAULT '0' COMMENT '我的粉丝数量',
  `follow_counts` int(11) DEFAULT '0' COMMENT '我关注的人总数',
  `receive_like_counts` int(11) DEFAULT '0' COMMENT '我接受到的赞美/收藏 的数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1001', 'test-imooc-1111', '9999', null, 'egdsgsd', '123456', '111', '222');
INSERT INTO `users` VALUES ('180425B0B3N6B25P', 'imooc11', 'wzNncBURtPYCDsYd7TUgWQ==', null, 'afsaf', '0', '0', '0');
INSERT INTO `users` VALUES ('180425BNSR1CG0H0', 'abc', '4QrcOUm6Wau+VuBX8g+IPg==', null, 'abc', '0', '0', '0');
INSERT INTO `users` VALUES ('180425CFA4RB6T0H', 'imooc', 'kU8h64TG/bK2Y91vRT9lyg==', '180425CFA4RB6T0H/face/tmp_441b2f74995c28dd95c4632f0ee8d87af12222476090ef37.jpg', 'asfasf', '1', '4', '8');
INSERT INTO `users` VALUES ('180426F4D35R04X4', 'aaa', 'R7zlx09Yn0hn29V+nKn4CA==', null, 'aaa', '0', '0', '0');
INSERT INTO `users` VALUES ('180426F55CZPA9YW', 'aaaa', 'R7zlx09Yn0hn29V+nKn4CA==', null, 'aaaa', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GA3SBB4DP0', '1001', 'bfw1xHdW6WLvBV0QSfH47A==', null, '1001', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GAK87AB0X4', '10401', 'bfw1xHdW6WLvBV0QSfH47A==', null, '10401', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GAWCC17KWH', '104701', 'bfw1xHdW6WLvBV0QSfH47A==', null, '104701', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GBDFKDG5D4', '10re4701', 'bfw1xHdW6WLvBV0QSfH47A==', null, '10re4701', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GBKN0YRFRP', '10rwee4701', 'bfw1xHdW6WLvBV0QSfH47A==', null, '10rwee4701', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GH49XRZHX4', '390213890', 'H9V/tnfgt6nniqH5bDZ0hQ==', null, '390213890', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GHH12WXPZC', '390ewqewq213890', 'H9V/tnfgt6nniqH5bDZ0hQ==', null, '390ewqewq213890', '0', '0', '0');
INSERT INTO `users` VALUES ('180426GHHPZ7NTC0', '390ewqewq21ewqe3890', 'H9V/tnfgt6nniqH5bDZ0hQ==', null, '390ewqewq21ewqe3890', '0', '0', '0');
INSERT INTO `users` VALUES ('180426H0GWP0C3HH', 'jdiowqjodij', 'wEmTz54sy+StEzB+TrtGSQ==', null, 'jdiowqjodij', '0', '0', '0');
INSERT INTO `users` VALUES ('180518CHS7SXMCX4', 'aaaaa', 'WU+AOzgKQTlu1j3KOVA1Qg==', null, 'aaaaa', '0', '0', '0');
INSERT INTO `users` VALUES ('180518CKMAAM5TXP', 'abc123', '6ZoYxCjLONXyYIU2eJIuAw==', null, 'abc123', '0', '0', '1');
INSERT INTO `users` VALUES ('190912K29GZRPHZC', '张三', 'ICy5YqxZB1uWSwcVLSNLcA==', null, '张三', '0', '0', '0');
INSERT INTO `users` VALUES ('1909136SKS6H9BTC', 'zs', 'ICy5YqxZB1uWSwcVLSNLcA==', null, 'zs', '1', '1', '2');
INSERT INTO `users` VALUES ('1909137A5D27XH28', '李四', 'ICy5YqxZB1uWSwcVLSNLcA==', null, '李四', '0', '0', '0');
INSERT INTO `users` VALUES ('1909137FSXR3KXWH', 'imooc123', 'ICy5YqxZB1uWSwcVLSNLcA==', null, 'imooc123', '0', '0', '0');
INSERT INTO `users` VALUES ('1909138RTXZT60SW', '发顺丰', 'CL4nrfZWfnO+7+iMAnaNfA==', null, '发顺丰', '0', '0', '0');
INSERT INTO `users` VALUES ('1909138S91MXB540', '发射点发射点', 'c37yWVHKSoO6wW0DChTcqw==', null, '发射点发射点', '0', '0', '0');
INSERT INTO `users` VALUES ('190913CR11HYNMFW', 'dgfsdf', 'hNnPwvOVzog6Qdf/wbvPTg==', null, 'dgfsdf', '0', '0', '0');
INSERT INTO `users` VALUES ('190913F1G8MCY23C', 'ls', 'ICy5YqxZB1uWSwcVLSNLcA==', '190913F1G8MCY23C/face/tmp_1bc9a6848c496223fb9f3fae61dfb89f46412199b5f73ae8.jpg', 'ls', '1', '0', '1');

-- ----------------------------
-- Table structure for users_fans
-- ----------------------------
DROP TABLE IF EXISTS `users_fans`;
CREATE TABLE `users_fans` (
  `id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL COMMENT '用户',
  `fan_id` varchar(64) NOT NULL COMMENT '粉丝',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`fan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户粉丝关联关系表';

-- ----------------------------
-- Records of users_fans
-- ----------------------------
INSERT INTO `users_fans` VALUES ('190916FH8NHY0KKP', '180425CFA4RB6T0H', '1909136SKS6H9BTC');
INSERT INTO `users_fans` VALUES ('190916H4KMBNCHZC', '1909136SKS6H9BTC', '180425CFA4RB6T0H');
INSERT INTO `users_fans` VALUES ('190918DPCDTYZT2W', '190913F1G8MCY23C', '180425CFA4RB6T0H');

-- ----------------------------
-- Table structure for users_like_videos
-- ----------------------------
DROP TABLE IF EXISTS `users_like_videos`;
CREATE TABLE `users_like_videos` (
  `id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL COMMENT '用户',
  `video_id` varchar(64) NOT NULL COMMENT '视频',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_video_rel` (`user_id`,`video_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户喜欢的/赞过的视频';

-- ----------------------------
-- Records of users_like_videos
-- ----------------------------
INSERT INTO `users_like_videos` VALUES ('190916G1G3BWT540', '180425CFA4RB6T0H', '190914B3XBD5Y32W');
INSERT INTO `users_like_videos` VALUES ('190916FHHD6DDPZC', '180425CFA4RB6T0H', '1909167DDCDFMPBC');
INSERT INTO `users_like_videos` VALUES ('190918DPKT66KGR4', '180425CFA4RB6T0H', '190918DNPC223SCH');
INSERT INTO `users_like_videos` VALUES ('190916FH8G90NKWH', '1909136SKS6H9BTC', '190915AD100HRCSW');
INSERT INTO `users_like_videos` VALUES ('1909168X6AT70K8H', '1909136SKS6H9BTC', '1909167DDCDFMPBC');

-- ----------------------------
-- Table structure for users_report
-- ----------------------------
DROP TABLE IF EXISTS `users_report`;
CREATE TABLE `users_report` (
  `id` varchar(64) NOT NULL,
  `deal_user_id` varchar(64) NOT NULL COMMENT '被举报用户id',
  `deal_video_id` varchar(64) NOT NULL,
  `title` varchar(128) NOT NULL COMMENT '类型标题，让用户选择，详情见 枚举',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `userid` varchar(64) NOT NULL COMMENT '举报人的id',
  `create_date` datetime NOT NULL COMMENT '举报时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报用户表';

-- ----------------------------
-- Records of users_report
-- ----------------------------
INSERT INTO `users_report` VALUES ('180521FZ501ABDYW', '180425CFA4RB6T0H', '180510CD0A6K3SRP', '引人不适', '不合时宜的视频', '180518CKMAAM5TXP', '2018-05-21 20:58:35');
INSERT INTO `users_report` VALUES ('180521FZK44ZRWX4', '180425CFA4RB6T0H', '180510CD0A6K3SRP', '引人不适', '不合时宜的视频', '180518CKMAAM5TXP', '2018-05-21 20:59:53');
INSERT INTO `users_report` VALUES ('180521FZR1TYRTXP', '180425CFA4RB6T0H', '180510CD0A6K3SRP', '辱骂谩骂', 't4t43', '180518CKMAAM5TXP', '2018-05-21 21:00:18');
INSERT INTO `users_report` VALUES ('190918F8YS0PSFW0', '190913F1G8MCY23C', '190918DNPC223SCH', '色情低俗', '呵呵', '180425CFA4RB6T0H', '2019-09-18 20:03:52');

-- ----------------------------
-- Table structure for videos
-- ----------------------------
DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos` (
  `id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL COMMENT '发布者id',
  `audio_id` varchar(64) DEFAULT NULL COMMENT '用户使用音频的信息',
  `video_desc` varchar(128) DEFAULT NULL COMMENT '视频描述',
  `video_path` varchar(255) NOT NULL COMMENT '视频存放的路径',
  `video_seconds` float(6,2) DEFAULT NULL COMMENT '视频秒数',
  `video_width` int(6) DEFAULT NULL COMMENT '视频宽度',
  `video_height` int(6) DEFAULT NULL COMMENT '视频高度',
  `cover_path` varchar(255) DEFAULT NULL COMMENT '视频封面图',
  `like_counts` bigint(20) NOT NULL DEFAULT '0' COMMENT '喜欢/赞美的数量',
  `status` int(1) NOT NULL COMMENT '视频状态：\r\n1、发布成功\r\n2、禁止播放，管理员操作',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频信息表';

-- ----------------------------
-- Records of videos
-- ----------------------------
INSERT INTO `videos` VALUES ('190914B3XBD5Y32W', '180425CFA4RB6T0H', '', '红thus你明明您', '180425CFA4RB6T0H/video/tmp_61db50ec11e8642bf4665b20635cfad6376a76113aa1a0bf.mp4', '15.00', '720', '1280', '/180425CFA4RB6T0H/video/tmp_61db50ec11e8642bf4665b20635cfad6376a76113aa1a0bf.jpg', '1', '1', '2019-09-14 15:36:30');
INSERT INTO `videos` VALUES ('190914D1KXYHF7MW', '180425CFA4RB6T0H', '', '恶犬', '180425CFA4RB6T0H/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.q1Jy9rCgktoOc839c8829339a03af32135e18eb41327.mp4', '13.12', '544', '960', '/180425CFA4RB6T0H/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35oq1Jy9rCgktoOc839c8829339a03af32135e18eb41327.jpg', '0', '1', '2019-09-14 18:17:50');
INSERT INTO `videos` VALUES ('190914D1R92C8P6W', '180425CFA4RB6T0H', '', 'aa', '180425CFA4RB6T0H/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.sVYycJTd88UZ30ded04fbfd43122e0625b06e867058d.mp4', '17.07', '540', '960', '/180425CFA4RB6T0H/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35osVYycJTd88UZ30ded04fbfd43122e0625b06e867058d.jpg', '0', '1', '2019-09-14 18:18:11');
INSERT INTO `videos` VALUES ('190914D1T1ZTAF5P', '180425CFA4RB6T0H', '', 'cc', '180425CFA4RB6T0H/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.jSnf8KWiPKoZ5998643d94315bb7fbdcd190597827bb.mp4', '15.74', '720', '1280', '/180425CFA4RB6T0H/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35ojSnf8KWiPKoZ5998643d94315bb7fbdcd190597827bb.jpg', '0', '1', '2019-09-14 18:18:23');
INSERT INTO `videos` VALUES ('190914D1XF3YF0DP', '180425CFA4RB6T0H', '', 'bb', '180425CFA4RB6T0H/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.Er3bDnVok4vu8879471d50e0fe78de348776da37a7c4.mp4', '34.97', '720', '1280', '/180425CFA4RB6T0H/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35oEr3bDnVok4vu8879471d50e0fe78de348776da37a7c4.jpg', '0', '1', '2019-09-14 18:18:38');
INSERT INTO `videos` VALUES ('190914D1YYFF62CH', '180425CFA4RB6T0H', '', 'aa', '180425CFA4RB6T0H/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.Gq55LiDGKxUNa768bf83b50d1b5cf13454bd9c15b457.mp4', '13.82', '1280', '720', '/180425CFA4RB6T0H/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35oGq55LiDGKxUNa768bf83b50d1b5cf13454bd9c15b457.jpg', '0', '1', '2019-09-14 18:18:48');
INSERT INTO `videos` VALUES ('190915AD100HRCSW', '180425CFA4RB6T0H', '47847823234', '合法解放后受打击啊发顺丰吧', '180425CFA4RB6T0H/video/7358d976-60c1-4062-adca-d1b5e4aeba02.mp4', '34.97', '720', '1280', '/180425CFA4RB6T0H/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35oiVaUAI2kxi888879471d50e0fe78de348776da37a7c4.jpg', '1', '1', '2019-09-15 14:39:50');
INSERT INTO `videos` VALUES ('1909167DDCDFMPBC', '1909136SKS6H9BTC', '', '张三的视频', '1909136SKS6H9BTC/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.Etc3gGuqiwqkc839c8829339a03af32135e18eb41327.mp4', '13.12', '544', '960', '/1909136SKS6H9BTC/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35oEtc3gGuqiwqkc839c8829339a03af32135e18eb41327.jpg', '2', '1', '2019-09-16 10:28:57');
INSERT INTO `videos` VALUES ('190918DNPC223SCH', '190913F1G8MCY23C', '', '李四啊', '190913F1G8MCY23C/video/wxc79364d6461497fd.o6zAJs_IeZGoCzyK7nJz2Ud6s35o.MicOp0WjWiqY620cf50a17f9513b822b289bf89f6b7a.mp4', '15.30', '720', '1280', '/190913F1G8MCY23C/video/wxc79364d6461497fdo6zAJs_IeZGoCzyK7nJz2Ud6s35oMicOp0WjWiqY620cf50a17f9513b822b289bf89f6b7a.jpg', '1', '1', '2019-09-18 19:12:08');
