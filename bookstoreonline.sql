/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : bookstoreonline

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 23/12/2025 21:26:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for authors
-- ----------------------------
DROP TABLE IF EXISTS `authors`;
CREATE TABLE `authors`  (
  `author_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `author_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nationality` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `birth_date` date NULL DEFAULT NULL,
  `biography` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`author_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of authors
-- ----------------------------
INSERT INTO `authors` VALUES ('A001', '路遥', '中国', '1949-12-03', '中国当代作家，代表作《平凡的世界》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A002', '余华', '中国', '1960-04-03', '中国当代作家，代表作《活着》《许三观卖血记》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A003', '刘慈欣', '中国', '1963-06-23', '中国科幻小说作家，代表作《三体》系列', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A004', '钱钟书', '中国', '1910-11-21', '中国现代作家、文学研究家，代表作《围城》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A005', '杨绛', '中国', '1911-07-17', '中国作家、翻译家，钱钟书夫人', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A006', '曹雪芹', '中国', '1715-06-04', '清代小说家，代表作《红楼梦》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A007', 'J.K.罗琳', '英国', '1965-07-31', '英国作家，代表作《哈利·波特》系列', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A008', '村上春树', '日本', '1949-01-12', '日本现代作家，代表作《挪威的森林》《1Q84》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A009', '东野圭吾', '日本', '1958-02-04', '日本推理小说作家，代表作《白夜行》《解忧杂货店》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A010', '玛格丽特·米切尔', '美国', '1900-11-08', '美国作家，代表作《飘》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A011', '斯蒂芬·金', '美国', '1947-09-21', '美国作家，代表作《肖申克的救赎》《闪灵》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A012', '托马斯·索维尔', '美国', '1930-06-30', '美国经济学家、社会评论家', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A013', '埃德加·斯诺', '美国', '1905-07-19', '美国记者，代表作《西行漫记》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A014', '戴尔·卡耐基', '美国', '1888-11-24', '美国人际关系学大师', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A015', '尤瓦尔·赫拉利', '以色列', '1976-02-24', '以色列历史学家，代表作《人类简史》', '2025-12-22 21:11:53');
INSERT INTO `authors` VALUES ('A91d623475', '加鹿鸣', NULL, NULL, NULL, '2025-12-22 11:49:25');
INSERT INTO `authors` VALUES ('AUTH001', 'Bruce Eckel', 'US', '1960-01-01', '', '2025-12-20 18:42:34');
INSERT INTO `authors` VALUES ('AUTH002', 'Randal E. Bryant', 'US', '1952-01-01', '', '2025-12-20 18:42:34');
INSERT INTO `authors` VALUES ('AUTH003', 'Thomas H. Cormen', 'US', '1956-01-01', '', '2025-12-20 18:42:34');
INSERT INTO `authors` VALUES ('AUTH004', 'Robert C. Martin', 'US', '1952-01-01', '', '2025-12-20 18:42:34');

-- ----------------------------
-- Table structure for book_authors
-- ----------------------------
DROP TABLE IF EXISTS `book_authors`;
CREATE TABLE `book_authors`  (
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `author_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `author_order` tinyint(4) NOT NULL DEFAULT 1,
  `author_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'author',
  PRIMARY KEY (`isbn`, `author_id`) USING BTREE,
  UNIQUE INDEX `uk_isbn_author_order`(`isbn` ASC, `author_order` ASC) USING BTREE,
  INDEX `fk_book_authors_author`(`author_id` ASC) USING BTREE,
  CONSTRAINT `fk_book_authors_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`author_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_book_authors_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_authors
-- ----------------------------
INSERT INTO `book_authors` VALUES ('9787020024759', 'A002', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787020049295', 'A001', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787020090006', 'A004', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787101003048', 'A006', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787101003055', 'A006', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787101003062', 'A006', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787101003079', 'A006', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787108009821', 'A012', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787108009821', 'A013', 2, 'translator');
INSERT INTO `book_authors` VALUES ('9787111370529', 'A011', 1, 'translator');
INSERT INTO `book_authors` VALUES ('9787115351531', 'A007', 1, 'translator');
INSERT INTO `book_authors` VALUES ('9787208061644', 'A010', 1, 'translator');
INSERT INTO `book_authors` VALUES ('9787229030933', 'A003', 1, 'author');
INSERT INTO `book_authors` VALUES ('978730000001', 'AUTH001', 1, 'author');
INSERT INTO `book_authors` VALUES ('978730000002', 'AUTH002', 1, 'author');
INSERT INTO `book_authors` VALUES ('978730000003', 'AUTH003', 1, 'author');
INSERT INTO `book_authors` VALUES ('978730000004', 'AUTH004', 1, 'author');
INSERT INTO `book_authors` VALUES ('978730000008', 'A91d623475', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787302455106', 'A003', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787508647357', 'A012', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787512501003', 'A002', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787532731074', 'A008', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787544253994', 'A008', 1, 'translator');
INSERT INTO `book_authors` VALUES ('9787544285148', 'A009', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787806570920', 'A005', 1, 'author');
INSERT INTO `book_authors` VALUES ('9787807667452', 'A015', 1, 'author');

-- ----------------------------
-- Table structure for book_categories
-- ----------------------------
DROP TABLE IF EXISTS `book_categories`;
CREATE TABLE `book_categories`  (
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `category_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`isbn`, `category_id`) USING BTREE,
  INDEX `fk_book_categories_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_book_categories_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_book_categories_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_categories
-- ----------------------------
INSERT INTO `book_categories` VALUES ('9787111370529', 'CS_ALG');
INSERT INTO `book_categories` VALUES ('978730000003', 'CS_ALG');
INSERT INTO `book_categories` VALUES ('9787115351531', 'CS_PROG');
INSERT INTO `book_categories` VALUES ('978730000001', 'CS_PROG');
INSERT INTO `book_categories` VALUES ('9787302455106', 'CS_PROG');
INSERT INTO `book_categories` VALUES ('978730000002', 'CS_SYS');
INSERT INTO `book_categories` VALUES ('978730000004', 'CS_SYS');
INSERT INTO `book_categories` VALUES ('9787508647357', 'HIS_ECO');
INSERT INTO `book_categories` VALUES ('9787108009821', 'HIS_HIST');
INSERT INTO `book_categories` VALUES ('9787512501003', 'HIS_SOC');
INSERT INTO `book_categories` VALUES ('9787807667452', 'HIS_SOC');
INSERT INTO `book_categories` VALUES ('9787101003048', 'LIT_CN_CL');
INSERT INTO `book_categories` VALUES ('9787101003055', 'LIT_CN_CL');
INSERT INTO `book_categories` VALUES ('9787101003062', 'LIT_CN_CL');
INSERT INTO `book_categories` VALUES ('9787101003079', 'LIT_CN_CL');
INSERT INTO `book_categories` VALUES ('9787020024759', 'LIT_CN_MOD');
INSERT INTO `book_categories` VALUES ('9787020049295', 'LIT_CN_MOD');
INSERT INTO `book_categories` VALUES ('9787020090006', 'LIT_CN_MOD');
INSERT INTO `book_categories` VALUES ('9787229030933', 'LIT_CN_MOD');
INSERT INTO `book_categories` VALUES ('9787806570920', 'LIT_CN_MOD');
INSERT INTO `book_categories` VALUES ('9787208061644', 'LIT_FOR_NOV');
INSERT INTO `book_categories` VALUES ('9787532731074', 'LIT_FOR_NOV');
INSERT INTO `book_categories` VALUES ('9787544253994', 'LIT_FOR_NOV');
INSERT INTO `book_categories` VALUES ('9787544285148', 'LIT_FOR_NOV');
INSERT INTO `book_categories` VALUES ('978730000008', 'OTH');

-- ----------------------------
-- Table structure for book_keywords
-- ----------------------------
DROP TABLE IF EXISTS `book_keywords`;
CREATE TABLE `book_keywords`  (
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `keyword_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `relevance` decimal(3, 2) NOT NULL DEFAULT 1.00,
  PRIMARY KEY (`isbn`, `keyword_id`) USING BTREE,
  INDEX `fk_book_keywords_keyword`(`keyword_id` ASC) USING BTREE,
  CONSTRAINT `fk_book_keywords_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_book_keywords_keyword` FOREIGN KEY (`keyword_id`) REFERENCES `keywords` (`keyword_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_keywords
-- ----------------------------

-- ----------------------------
-- Table structure for book_series
-- ----------------------------
DROP TABLE IF EXISTS `book_series`;
CREATE TABLE `book_series`  (
  `series_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `series_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `publisher_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `total_books` int(11) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`series_id`) USING BTREE,
  INDEX `fk_book_series_publisher`(`publisher_id` ASC) USING BTREE,
  CONSTRAINT `fk_book_series_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`publisher_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_series
-- ----------------------------
INSERT INTO `book_series` VALUES ('BS001', '计算机科学经典译丛', 'PUB001', 12, '介绍计算机科学领域的经典著作中文译本系列，涵盖算法、系统、编程等多个方向');
INSERT INTO `book_series` VALUES ('BS002', '图灵程序设计丛书', 'PUB002', 20, '人民邮电出版社图灵公司出版的经典程序设计书籍系列');
INSERT INTO `book_series` VALUES ('BS003', '数据科学与人工智能系列', 'PUB003', 8, '清华大学出版社出版的数据科学与AI领域前沿技术书籍');
INSERT INTO `book_series` VALUES ('BS004', '世界文学名著系列', 'P001', 50, '人民文学出版社精选世界各国文学经典作品');
INSERT INTO `book_series` VALUES ('BS005', '中国当代文学精品', 'P001', 30, '收录中国当代优秀作家代表作品');
INSERT INTO `book_series` VALUES ('BS006', '商务印书馆学术经典', 'P002', 100, '商务印书馆百年积累的学术经典著作集');

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `edition` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `publisher_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `publish_date` date NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `page_count` int(11) NULL DEFAULT 0,
  `format` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'zh',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `table_of_contents` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `cover_image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `series_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `book_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'normal',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`isbn`) USING BTREE,
  INDEX `fk_books_publisher`(`publisher_id` ASC) USING BTREE,
  INDEX `fk_books_series`(`series_id` ASC) USING BTREE,
  CONSTRAINT `fk_books_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`publisher_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_books_series` FOREIGN KEY (`series_id`) REFERENCES `book_series` (`series_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES ('9787020024759', '活着', '第二版', 'P001', '2004-01-01', 28.00, 194, '32开', 'zh', '余华的代表作，讲述了一个人历尽世间沧桑和磨难的一生', '序言,第一章-第十二章...', 'https://image12.bookschina.com/2022/20220127/1/8697828.jpg', 'BS005', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787020049295', '平凡的世界', '第一版', 'P001', '2005-01-01', 88.00, 1230, '16开', 'zh', '路遥的代表作，描写中国当代城乡社会生活的长篇小说', '第一部:第一章-第十章...', 'https://image12.bookschina.com/2021/20210721/1/8625510.jpg', 'BS005', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787020090006', '围城', '第一版', 'P001', '1991-02-01', 39.80, 359, '32开', 'zh', '钱钟书的代表作，中国现代文学史上的经典之作', '序,第一章-第九章...', 'https://image12.bookschina.com/2022/20221215/1/8906357.jpg', 'BS005', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787101003048', '红楼梦', '第一版', 'P002', '1996-10-01', 59.70, 1606, '32开', 'zh', '中国古典四大名著之首', '第一回-第一百二十回...', 'https://image12.bookschina.com/2018/20180702/1/7728249.jpg', 'BS006', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787101003055', '西游记', '第一版', 'P002', '2004-07-01', 49.80, 1198, '32开', 'zh', '中国古典四大名著之一', '第一回-第一百回...', 'https://image12.bookschina.com/2024/20241217/1/5925650.jpg', 'BS006', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787101003062', '水浒传', '第一版', 'P002', '1997-01-01', 48.00, 1310, '32开', 'zh', '中国古典四大名著之一', '引首,第一回-第一百回...', 'https://image31.bookschina.com/2025/zuo/9/5925647.jpg', 'BS006', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787101003079', '三国演义', '第一版', 'P002', '1998-07-01', 46.00, 1024, '32开', 'zh', '中国古典四大名著之一', '第一回-第一百二十回...', 'https://image12.bookschina.com/2018/20180702/1/7728263.jpg', 'BS006', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787108009821', '史记', '第一版', 'P002', '1982-11-01', 198.00, 3326, '16开', 'zh', '西汉史学家司马迁撰写的纪传体史书', '本纪十二篇,表十篇...', 'https://image12.bookschina.com/2015/20150409/6947506.jpg', 'BS006', 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787111370529', '算法导论', '第三版', 'P004', '2012-12-01', 128.00, 780, '16开', 'zh', '经典的算法教材', '第一部分:基础知识...', 'https://image12.bookschina.com/2013/20130310/5743846.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787115351531', 'JavaScript高级程序设计', '第三版', 'P004', '2012-03-01', 99.00, 748, '16开', 'zh', 'JavaScript经典教材', '第1章:JavaScript简介...', 'https://image31.bookschina.com/2024/zuo/12/9472241.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787208061644', '追风筝的人', '第一版', 'P006', '2006-05-01', 29.00, 362, '32开', 'zh', '卡勒德·胡赛尼的代表作', '第一章-第二十五章...', 'https://image12.bookschina.com/2024/20240607/7/9300908.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787229030933', '三体', '第一版', 'P003', '2008-01-01', 68.00, 302, '16开', 'zh', '刘慈欣的科幻小说，亚洲首部获得雨果奖的作品', '第一章:科学边界...', 'https://image31.bookschina.com/2018/zuo/11/7861182.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('978730000001', 'Java 编程思想', '4', 'PUB001', '2019-01-01', 88.00, 1200, '16K', 'zh', 'Everythin you want to know about java', 'Contents', 'https://image12.bookschina.com/2024/20240306/3/8550689.jpg', 'BS001', 'normal', 'active', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `books` VALUES ('978730000002', '深入理解计算机系统', '3', 'PUB002', '2021-06-01', 99.00, 900, '16K', 'zh', 'The must-read book if you want to learn computer science', 'Contents', 'https://image12.bookschina.com/2016/20161223/7340367.jpg', 'BS002', 'normal', 'active', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `books` VALUES ('978730000003', '算法导论', '3', 'PUB003', '2020-03-01', 108.00, 800, '16K', 'zh', 'Algorithm is all you need', 'Contents', 'https://image12.bookschina.com/2013/20130310/5743846.jpg', 'BS001', 'normal', 'active', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `books` VALUES ('978730000004', '代码整洁之道', '1', 'PUB004', '2018-10-01', 66.00, 320, '16K', 'zh', 'How to code gracefully', 'Contents', 'https://image12.bookschina.com/2020/20200909/1/8271133.jpg', 'BS002', 'normal', 'active', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `books` VALUES ('978730000008', '我的2026', NULL, 'PUB002', NULL, 99.00, NULL, NULL, 'zh', NULL, NULL, 'blob:https://scn8yly0vkck.feishu.cn/8aa22083-1120-4b6b-b62e-495fe564dca8', NULL, 'normal', 'active', '2025-12-22 11:49:25', '2025-12-22 11:49:25');
INSERT INTO `books` VALUES ('9787302455106', 'Python编程从入门到实践', '第一版', 'P005', '2016-07-01', 89.00, 459, '16开', 'zh', 'Python编程入门教程', '第一部分:基础知识...', 'https://image12.bookschina.com/2023/20230210/30/8529619.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787508647357', '经济学通识', '第一版', 'P003', '2015-08-01', 58.00, 392, '16开', 'zh', '薛兆丰的经济学普及读物', '第一章:稀缺...', 'https://image12.bookschina.com/2025/20251120/2/9929133.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787512501003', '看见', '第一版', 'P003', '2013-01-01', 39.80, 424, '16开', 'zh', '柴静的个人传记作品', '第一章-第二十章...', 'https://image12.bookschina.com/2025/20250120/1/9492301.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787532731074', '挪威的森林', '第一版', 'P006', '2001-02-01', 27.30, 350, '32开', 'zh', '村上春树的代表作', '第一章-第十一章...', 'https://image12.bookschina.com/2023/20230615/1/9101890.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787544253994', '百年孤独', '第一版', 'P006', '2011-06-01', 39.50, 360, '32开', 'zh', '加西亚·马尔克斯的代表作', '第一章-第二十章...', 'https://image31.bookschina.com/2025/zuo/10/9469083.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787544285148', '解忧杂货店', '第一版', 'P006', '2014-05-01', 39.50, 291, '32开', 'zh', '东野圭吾的温情推理小说', '第一章-第五章...', 'https://image12.bookschina.com/2021/20210315/1/8398159.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787806570920', '我们仨', '第一版', 'P001', '2003-07-01', 23.00, 165, '32开', 'zh', '杨绛撰写的家庭生活回忆录', '第一部:我们俩老了...', 'https://image12.bookschina.com/2023/20231210/2/9131384.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `books` VALUES ('9787807667452', '人类简史', '第一版', 'P003', '2014-11-01', 68.00, 440, '16开', 'zh', '尤瓦尔·赫拉利的历史学著作', '第一部分:认知革命...', 'https://image12.bookschina.com/2020/20200702/1/8177755.jpg', NULL, 'normal', 'active', '2025-12-22 21:11:53', '2025-12-22 21:11:53');

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `category_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `parent_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `level` int(11) NOT NULL DEFAULT 1,
  `display_order` int(11) NOT NULL DEFAULT 1,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `fk_categories_parent`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES ('CS', '计算机科学', NULL, 1, 2, '计算机与编程相关书籍');
INSERT INTO `categories` VALUES ('CS_ALG', '算法与数据结构', 'CS', 2, 2, '算法、数据结构相关');
INSERT INTO `categories` VALUES ('CS_PROG', '编程语言', 'CS', 2, 1, '编程语言学习与开发');
INSERT INTO `categories` VALUES ('CS_SYS', '系统与架构', 'CS', 2, 3, '计算机系统、架构、工程');
INSERT INTO `categories` VALUES ('HIS', '历史与社科', NULL, 1, 3, '历史、经济、社会类书籍');
INSERT INTO `categories` VALUES ('HIS_ECO', '经济学', 'HIS', 2, 2, '经济学相关');
INSERT INTO `categories` VALUES ('HIS_HIST', '历史', 'HIS', 2, 1, '历史类书籍');
INSERT INTO `categories` VALUES ('HIS_SOC', '社会学/人类学', 'HIS', 2, 3, '社会、人类学相关');
INSERT INTO `categories` VALUES ('LIT', '文学', NULL, 1, 1, '文学作品');
INSERT INTO `categories` VALUES ('LIT_CN', '中国文学', 'LIT', 2, 1, '中国现当代及古典文学作品');
INSERT INTO `categories` VALUES ('LIT_CN_CL', '古典文学', 'LIT_CN', 3, 1, '中国古代文学经典');
INSERT INTO `categories` VALUES ('LIT_CN_MOD', '现代小说', 'LIT_CN', 3, 2, '中国现当代小说');
INSERT INTO `categories` VALUES ('LIT_FOR', '外国文学', 'LIT', 2, 2, '外国文学作品');
INSERT INTO `categories` VALUES ('LIT_FOR_NOV', '外国小说', 'LIT_FOR', 3, 1, '外国小说作品');
INSERT INTO `categories` VALUES ('OTH', '其他', NULL, 1, 4, '其他类别');

-- ----------------------------
-- Table structure for credit_rules
-- ----------------------------
DROP TABLE IF EXISTS `credit_rules`;
CREATE TABLE `credit_rules`  (
  `level` int(11) NOT NULL,
  `level_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `discount_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000,
  `overdraft_enabled` tinyint(1) NOT NULL DEFAULT 0,
  `overdraft_limit` decimal(10, 2) NULL DEFAULT NULL,
  `min_balance` decimal(10, 2) NULL DEFAULT NULL,
  `min_total_purchase` decimal(12, 2) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`level`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of credit_rules
-- ----------------------------
INSERT INTO `credit_rules` VALUES (1, '一级会员', 0.1000, 0, NULL, NULL, 0.00, '10%折扣，不能透支');
INSERT INTO `credit_rules` VALUES (2, '二级会员', 0.1500, 0, NULL, NULL, 1000.00, '15%折扣，不能透支');
INSERT INTO `credit_rules` VALUES (3, '三级会员', 0.1500, 1, 500.00, NULL, 2000.00, '15%折扣，可先发书再付款，透支额度500元');
INSERT INTO `credit_rules` VALUES (4, '四级会员', 0.2000, 1, 1000.00, NULL, 5000.00, '20%折扣，可先发书再付款，透支额度1000元');
INSERT INTO `credit_rules` VALUES (5, '五级会员', 0.2500, 1, 999999.00, NULL, 10000.00, '25%折扣，可先发书再付款，透支无限制');

-- ----------------------------
-- Table structure for customers
-- ----------------------------
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers`  (
  `customer_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `account_balance` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `credit_level` int(11) NOT NULL DEFAULT 1,
  `total_purchase` decimal(12, 2) NOT NULL DEFAULT 0.00,
  `registration_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` datetime NULL DEFAULT NULL,
  `account_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `is_admin` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为管理员: 0-否, 1-是',
  PRIMARY KEY (`customer_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customers
-- ----------------------------
-- 管理员账户: admin / Admin123
INSERT INTO `customers` VALUES ('ADMIN001', 'admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', '系统管理员', 'admin@easybook.com', '13800000000', '管理员地址', 10000.00, 5, 50000.00, '2025-12-20 18:00:00', '2025-12-24 01:00:00', 'active', 1);
-- 普通用户
INSERT INTO `customers` VALUES ('1', 'ja', '123456', 'ja', 'ja@123.com', '1231235566', 'Wuhan', 0.00, 1, 0.00, '2025-12-20 14:42:04', NULL, 'active', 0);
INSERT INTO `customers` VALUES ('C000000001', 'testuser', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', '测试用户', 'testuser@example.com', '13800000001', '北京市海淀区', 500.00, 3, 2500.00, '2025-12-20 18:42:35', '2025-12-20 18:42:35', 'active', 0);
INSERT INTO `customers` VALUES ('C000000002', 'customer2', 'd3dec3f35387156495cbc21471313f87155f878f3435b693f50077c2be479033', '普通用户', 'customer2@example.com', '13800000002', '上海市浦东新区', 100.00, 1, 0.00, '2025-12-20 18:42:35', '2025-12-20 18:42:35', 'active', 0);
INSERT INTO `customers` VALUES ('CF26BF77E-', 'jaluming', 'de0f32c08f79f762787fd0dfbbfa56f1ff51190786bad2e2c127dcbef6ab75cb', '鹿鸣', 'jaluming@hust.edu.cn', '18966668888', '华中科技大学软件学院', 40.10, 1, 283.50, '2025-12-20 16:44:06', '2025-12-23 17:28:54', 'active', 0);

-- ----------------------------
-- Table structure for delivery_details
-- ----------------------------
DROP TABLE IF EXISTS `delivery_details`;
CREATE TABLE `delivery_details`  (
  `detail_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `delivery_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `order_detail_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `package_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`detail_id`) USING BTREE,
  INDEX `fk_delivery_details_delivery`(`delivery_id` ASC) USING BTREE,
  INDEX `fk_delivery_details_order_detail`(`order_detail_id` ASC) USING BTREE,
  CONSTRAINT `fk_delivery_details_delivery` FOREIGN KEY (`delivery_id`) REFERENCES `delivery_orders` (`delivery_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_delivery_details_order_detail` FOREIGN KEY (`order_detail_id`) REFERENCES `order_details` (`detail_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery_details
-- ----------------------------

-- ----------------------------
-- Table structure for delivery_orders
-- ----------------------------
DROP TABLE IF EXISTS `delivery_orders`;
CREATE TABLE `delivery_orders`  (
  `delivery_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `delivery_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `delivery_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tracking_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `delivery_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `delivery_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'preparing',
  `shipping_fee` decimal(8, 2) NOT NULL DEFAULT 0.00,
  `weight` decimal(8, 2) NULL DEFAULT NULL,
  `package_count` int(11) NOT NULL DEFAULT 1,
  `prepare_time` datetime NULL DEFAULT NULL,
  `ship_time` datetime NULL DEFAULT NULL,
  `expected_arrival` date NULL DEFAULT NULL,
  `actual_arrival` date NULL DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`delivery_id`) USING BTREE,
  UNIQUE INDEX `uk_delivery_no`(`delivery_no` ASC) USING BTREE,
  INDEX `fk_delivery_orders_order`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_delivery_orders_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery_orders
-- ----------------------------
INSERT INTO `delivery_orders` VALUES ('Dc95a9d84-b0f', 'O457d82d3-ba7', 'DEL1766319599661', '中通', '456', '华中科技大学软件学院', 'shipped', 0.00, NULL, 1, '2025-12-21 20:20:00', '2025-12-21 20:20:00', NULL, NULL, NULL);
INSERT INTO `delivery_orders` VALUES ('Df80c5ed3-124', 'Oc8365667-60f', 'DEL1766321073263', 'gg', '55', '华中科技大学软件学院', 'shipped', 0.00, NULL, 1, '2025-12-21 20:44:33', '2025-12-21 20:44:33', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory`  (
  `inventory_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `safety_stock` int(11) NOT NULL DEFAULT 10,
  `reserved_quantity` int(11) NOT NULL DEFAULT 0,
  `location_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `warehouse` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'main',
  `last_restock` datetime NULL DEFAULT NULL,
  `last_check` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`) USING BTREE,
  UNIQUE INDEX `uk_isbn`(`isbn` ASC) USING BTREE,
  CONSTRAINT `fk_inventory_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES ('INV001', '978730000001', 90, 10, 5, 'A-01', 'main', '2025-12-20 20:52:57', '2025-12-23 16:50:51');
INSERT INTO `inventory` VALUES ('INV002', '978730000002', 9, 10, 6, 'A-02', 'main', '2025-12-22 20:29:58', '2025-12-21 20:20:00');
INSERT INTO `inventory` VALUES ('INV003', '978730000003', 1, 10, 2, 'A-03', 'main', '2025-12-21 21:08:51', '2025-12-21 20:44:33');
INSERT INTO `inventory` VALUES ('INV004', '978730000004', 25, 10, 21, 'A-04', 'main', '2025-12-22 20:59:55', '2025-12-20 18:42:35');
INSERT INTO `inventory` VALUES ('INV005', '9787101003048', 95, 20, 8, 'A-03-01', 'main', '2024-01-12 16:40:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV006', '9787108009821', 45, 10, 3, 'A-03-02', 'main', '2024-01-05 13:25:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV007', '9787806570920', 120, 15, 10, 'A-01-08', 'main', '2024-01-18 10:10:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV008', '9787544253994', 180, 25, 15, 'B-02-02', 'main', '2024-01-22 15:30:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV009', '9787544285148', 260, 30, 22, 'B-02-03', 'main', '2024-01-28 09:20:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV010', '9787208061644', 140, 20, 11, 'B-02-04', 'main', '2024-01-16 14:50:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV011', '9787532731074', 110, 15, 9, 'B-02-05', 'main', '2024-01-14 11:35:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV012', '9787101003055', 75, 15, 6, 'A-03-03', 'main', '2024-01-08 10:05:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV013', '9787101003062', 80, 15, 7, 'A-03-04', 'main', '2024-01-09 13:15:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV014', '9787101003079', 78, 15, 6, 'A-03-05', 'main', '2024-01-11 15:45:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV015', '9787512501003', 190, 25, 16, 'A-01-09', 'main', '2024-01-24 16:20:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV016', '9787807667452', 210, 28, 19, 'B-02-06', 'main', '2024-01-30 10:40:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV017', '9787508647357', 130, 20, 13, 'B-02-07', 'main', '2024-01-17 14:25:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV018', '9787111370529', 65, 12, 4, 'C-04-01', 'main', '2024-01-07 09:30:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV019', '9787302455106', 330, 40, 30, 'C-04-02', 'main', '2025-12-23 16:53:27', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV020', '9787115351531', 92, 15, 8, 'C-04-03', 'main', '2024-01-13 13:40:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV021', '9787020049295', 150, 20, 12, 'A-01-05', 'main', '2024-01-15 10:30:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV022', '9787020024759', 230, 25, 18, 'A-01-06', 'main', '2024-01-20 14:15:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV023', '9787020090006', 85, 15, 5, 'A-01-07', 'main', '2024-01-10 09:45:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV024', '9787229030933', 320, 30, 25, 'B-02-01', 'main', '2024-01-25 11:20:00', '2025-12-22 21:14:20');
INSERT INTO `inventory` VALUES ('INV42e1f7ee-a94', '978730000008', 15, 10, 0, 'B-01', 'main', '2025-12-22 20:25:07', '2025-12-22 20:25:07');

-- ----------------------------
-- Table structure for keywords
-- ----------------------------
DROP TABLE IF EXISTS `keywords`;
CREATE TABLE `keywords`  (
  `keyword_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `keyword` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `search_count` int(11) NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`keyword_id`) USING BTREE,
  UNIQUE INDEX `uk_keyword`(`keyword` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of keywords
-- ----------------------------

-- ----------------------------
-- Table structure for order_details
-- ----------------------------
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details`  (
  `detail_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `unit_price` decimal(10, 2) NOT NULL,
  `discount_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000,
  `subtotal` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`detail_id`) USING BTREE,
  INDEX `fk_order_details_order`(`order_id` ASC) USING BTREE,
  INDEX `fk_order_details_isbn`(`isbn` ASC) USING BTREE,
  CONSTRAINT `fk_order_details_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_details_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_details
-- ----------------------------
INSERT INTO `order_details` VALUES ('OD01bc8d40-67c', 'Oad68fdcf-f98', '978730000003', 1, 108.00, 0.1000, 97.20);
INSERT INTO `order_details` VALUES ('OD0d8a5125-87c', 'O9d3d3ee0-a21', '978730000002', 1, 99.00, 0.1000, 89.10);
INSERT INTO `order_details` VALUES ('OD199fd74b-fa2', 'Oc8365667-60f', '978730000003', 1, 108.00, 0.1000, 97.20);
INSERT INTO `order_details` VALUES ('OD1e8f9fcc-731', 'Oa73126ec-3d5', '978730000003', 1, 108.00, 0.1000, 97.20);
INSERT INTO `order_details` VALUES ('OD208af903-644', 'O8c4922ee-221', '978730000003', 1, 108.00, 0.1000, 97.20);
INSERT INTO `order_details` VALUES ('OD3202522f-e5d', 'O457d82d3-ba7', '978730000002', 1, 99.00, 0.1000, 89.10);
INSERT INTO `order_details` VALUES ('OD4b8e701d-888', 'O3e674884-345', '978730000004', 1, 66.00, 0.1000, 59.40);
INSERT INTO `order_details` VALUES ('OD6ac36c39-10f', 'O0c0cf028-273', '978730000003', 1, 108.00, 0.1000, 97.20);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `customer_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `total_amount` decimal(12, 2) NOT NULL DEFAULT 0.00,
  `discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `actual_amount` decimal(12, 2) NOT NULL DEFAULT 0.00,
  `shipping_fee` decimal(8, 2) NOT NULL DEFAULT 0.00,
  `order_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pending',
  `shipping_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `recipient_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `recipient_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `payment_time` datetime NULL DEFAULT NULL,
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `shipping_time` datetime NULL DEFAULT NULL,
  `delivery_time` datetime NULL DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `fk_orders_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_orders_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES ('O0c0cf028-273', 'NO1766321521836', 'CF26BF77E-', 108.00, 10.80, 97.20, 0.00, 'pending', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', NULL, '2025-12-21 20:52:02', NULL, NULL, '');
INSERT INTO `orders` VALUES ('O3e674884-345', 'NO1766372521822', 'CF26BF77E-', 66.00, 6.60, 59.40, 0.00, 'pending', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', NULL, '2025-12-22 11:02:02', NULL, NULL, '');
INSERT INTO `orders` VALUES ('O457d82d3-ba7', 'NO1766319503697', 'CF26BF77E-', 99.00, 9.90, 89.10, 0.00, 'delivered', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', '2025-12-21 20:19:27', '2025-12-21 20:18:24', '2025-12-21 20:20:00', '2025-12-21 20:20:11', '');
INSERT INTO `orders` VALUES ('O8c4922ee-221', 'NO1766321246982', 'CF26BF77E-', 108.00, 10.80, 97.20, 0.00, 'cancelled', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', '2025-12-21 20:47:43', '2025-12-21 20:47:27', NULL, NULL, '');
INSERT INTO `orders` VALUES ('O9d3d3ee0-a21', 'NO1766319743510', 'CF26BF77E-', 99.00, 9.90, 89.10, 0.00, 'paid', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', '2025-12-21 20:22:37', '2025-12-21 20:22:24', NULL, NULL, '');
INSERT INTO `orders` VALUES ('Oa73126ec-3d5', 'NO1766318263676', 'CF26BF77E-', 108.00, 10.80, 97.20, 0.00, 'cancelled', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', NULL, '2025-12-21 19:57:44', NULL, NULL, '');
INSERT INTO `orders` VALUES ('Oad68fdcf-f98', 'NO1766322256486', 'CF26BF77E-', 108.00, 10.80, 97.20, 0.00, 'paid', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', '2025-12-22 11:02:37', '2025-12-21 21:04:16', NULL, NULL, '');
INSERT INTO `orders` VALUES ('Oc8365667-60f', 'NO1766320936643', 'CF26BF77E-', 108.00, 10.80, 97.20, 0.00, 'delivered', '华中科技大学软件学院', '鹿鸣', '18966668888', 'balance', '2025-12-21 20:42:40', '2025-12-21 20:42:17', '2025-12-21 20:44:33', '2025-12-21 20:44:45', '');

-- ----------------------------
-- Table structure for out_of_stock_records
-- ----------------------------
DROP TABLE IF EXISTS `out_of_stock_records`;
CREATE TABLE `out_of_stock_records`  (
  `record_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `book_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `publisher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `required_quantity` int(11) NOT NULL DEFAULT 1,
  `customer_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `customer_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `customer_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'manual',
  `priority` int(11) NOT NULL DEFAULT 1,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pending',
  `notify_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'not_required',
  `notify_time` datetime NULL DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resolve_time` datetime NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_out_of_stock_isbn`(`isbn` ASC) USING BTREE,
  INDEX `fk_out_of_stock_records_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_out_of_stock_records_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
  -- 注意：isbn 不设置外键，因为客户可能询价系统中尚未录入的新书
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of out_of_stock_records
-- ----------------------------
INSERT INTO `out_of_stock_records` VALUES ('OS0c922b7c841941d2aa', '9787020024759', '活着', '人民文学出版社', 1, 'C000000001', 'testuser@example.com', '13800000001', 'manual', 2, 'resolved', 'not_required', NULL, '2025-12-22 21:26:43', '2025-12-23 16:52:00', 'supplier:Sb37481a95');
INSERT INTO `out_of_stock_records` VALUES ('OS2d81ab525b3e43a6a8', '978730000003', '算法导论', '清华大学出版社', 10, NULL, NULL, NULL, 'auto', 2, 'processing', 'sent', '2025-12-22 19:38:09', '2025-12-22 19:37:05', NULL, NULL);
INSERT INTO `out_of_stock_records` VALUES ('OS47a9c874a08e4498b9', '978730000004', '代码整洁之道', '电子工业出版社', 10, NULL, NULL, NULL, 'auto', 2, 'resolved', 'sent', '2025-12-22 19:38:05', '2025-12-22 19:37:05', '2025-12-22 20:37:41', NULL);
INSERT INTO `out_of_stock_records` VALUES ('OS7b17c65647a24c9b87', '978730000002', '深入理解计算机系统', '人民邮电出版社', 5, NULL, NULL, NULL, 'auto', 2, 'resolved', 'not_required', NULL, '2025-12-22 19:37:05', '2025-12-22 20:37:42', NULL);
INSERT INTO `out_of_stock_records` VALUES ('OS7ee8343a9d0c476182', '978730000002', '深入理解计算机系统', '人民邮电出版社', 1, NULL, NULL, NULL, 'auto', 2, 'pending', 'not_required', NULL, '2025-12-23 16:52:00', NULL, NULL);
INSERT INTO `out_of_stock_records` VALUES ('OSb9459dc5796f454889', '978730000008', 'new book', '人民邮电出版社', 5, NULL, NULL, NULL, 'auto', 2, 'resolved', 'not_required', NULL, '2025-12-22 19:37:05', '2025-12-22 20:37:43', NULL);
INSERT INTO `out_of_stock_records` VALUES ('OSf04e22f181354ccbb2', '9787020049295', '平凡的世界', '人民文学出版社', 1, 'C000000001', 'testuser@example.com', '13800000001', 'customer', 3, 'resolved', 'pending', NULL, '2025-12-22 21:27:05', '2025-12-23 16:52:00', '无');

-- ----------------------------
-- Table structure for publishers
-- ----------------------------
DROP TABLE IF EXISTS `publishers`;
CREATE TABLE `publishers`  (
  `publisher_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `publisher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`publisher_id`) USING BTREE,
  UNIQUE INDEX `uk_publisher_name`(`publisher_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of publishers
-- ----------------------------
INSERT INTO `publishers` VALUES ('P001', '人民文学出版社', '北京市东城区朝阳门内大街166号', '010-65221920', '张伟', 'service@rw-cn.com', 'www.rw-cn.com', '中国最大的专业文学出版机构', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `publishers` VALUES ('P002', '商务印书馆', '北京市东城区王府井大街36号', '010-65258899', '李明', 'service@cp.com.cn', 'www.cp.com.cn', '中国历史最悠久的现代出版机构', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `publishers` VALUES ('P003', '中信出版社', '北京市朝阳区惠新东街甲4号富盛大厦2座', '010-84849525', '王芳', 'service@citicpub.com', 'www.citicpub.com', '综合性商业出版社', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `publishers` VALUES ('P004', '机械工业出版社.', '北京市西城区百万庄大街22号', '010-88379833', '赵刚', 'service@cmpbook.com', 'www.cmpbook.com', '中国知名的科技出版社', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `publishers` VALUES ('P005', '清华大学出版社.', '北京市海淀区清华园', '010-62776969', '刘洋', 'service@tup.tsinghua.edu.cn', 'www.tup.tsinghua.edu.cn', '中国著名大学出版社', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `publishers` VALUES ('P006', '上海译文出版社', '上海市福建中路193号', '021-63914557', '陈静', 'service@yiwen.com.cn', 'www.yiwen.com.cn', '中国最大的综合性翻译出版社', '2025-12-22 21:11:53', '2025-12-22 21:11:53');
INSERT INTO `publishers` VALUES ('PUB001', '机械工业出版社', '北京市海淀区', '010-00000001', '张编辑', 'pub001@example.com', 'https://pub001.example.com', '', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `publishers` VALUES ('PUB002', '人民邮电出版社', '北京市东城区', '010-00000002', '李编辑', 'pub002@example.com', 'https://pub002.example.com', '', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `publishers` VALUES ('PUB003', '清华大学出版社', '北京市海淀区', '010-00000003', '王编辑', 'pub003@example.com', 'https://pub003.example.com', '', '2025-12-20 18:42:34', '2025-12-20 18:42:34');
INSERT INTO `publishers` VALUES ('PUB004', '电子工业出版社', '北京市海淀区', '010-00000004', '赵编辑', 'pub004@example.com', 'https://pub004.example.com', '', '2025-12-20 18:42:34', '2025-12-20 18:42:34');

-- ----------------------------
-- Table structure for purchase_details
-- ----------------------------
DROP TABLE IF EXISTS `purchase_details`;
CREATE TABLE `purchase_details`  (
  `detail_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `purchase_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `unit_price` decimal(10, 2) NOT NULL,
  `received_quantity` int(11) NOT NULL DEFAULT 0,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`detail_id`) USING BTREE,
  INDEX `fk_purchase_details_purchase`(`purchase_id` ASC) USING BTREE,
  INDEX `fk_purchase_details_isbn`(`isbn` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_details_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_purchase_details_purchase` FOREIGN KEY (`purchase_id`) REFERENCES `purchase_orders` (`purchase_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_details
-- ----------------------------
INSERT INTO `purchase_details` VALUES ('PDb437782469bc47ccb4', 'P5a397bba8bd34b608ff', '978730000008', 10, 55.00, 10, 'complete');

-- ----------------------------
-- Table structure for purchase_orders
-- ----------------------------
DROP TABLE IF EXISTS `purchase_orders`;
CREATE TABLE `purchase_orders`  (
  `purchase_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `purchase_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `supplier_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `total_amount` decimal(12, 2) NOT NULL DEFAULT 0.00,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'draft',
  `order_date` date NULL DEFAULT NULL,
  `expected_delivery` date NULL DEFAULT NULL,
  `actual_delivery` date NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `approve_time` datetime NULL DEFAULT NULL,
  `approve_by` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`purchase_id`) USING BTREE,
  UNIQUE INDEX `uk_purchase_no`(`purchase_no` ASC) USING BTREE,
  INDEX `fk_purchase_orders_supplier`(`supplier_id` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_orders_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_orders
-- ----------------------------
INSERT INTO `purchase_orders` VALUES ('P5a397bba8bd34b608ff', 'PO1766406229570', 'S8e1ed02de', 550.00, 'received', NULL, NULL, '2025-12-22', '2025-12-22 20:23:50', 'C000000001', '2025-12-22 20:25:02', 'C000000001');
INSERT INTO `purchase_orders` VALUES ('P866ced904b7e45548c0', 'PO1766404560516', 'S8e1ed02de', 550.00, 'cancelled', NULL, NULL, NULL, '2025-12-22 19:56:01', 'C000000001', '2025-12-22 20:24:59', 'C000000001');

-- ----------------------------
-- Table structure for purchase_shortage_map
-- ----------------------------
DROP TABLE IF EXISTS `purchase_shortage_map`;
CREATE TABLE `purchase_shortage_map`  (
  `purchase_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `record_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `mapped_quantity` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`purchase_id`, `record_id`) USING BTREE,
  INDEX `fk_purchase_shortage_map_record`(`record_id` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_shortage_map_purchase` FOREIGN KEY (`purchase_id`) REFERENCES `purchase_orders` (`purchase_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_purchase_shortage_map_record` FOREIGN KEY (`record_id`) REFERENCES `out_of_stock_records` (`record_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_shortage_map
-- ----------------------------

-- ----------------------------
-- Table structure for supplier_books
-- ----------------------------
DROP TABLE IF EXISTS `supplier_books`;
CREATE TABLE `supplier_books`  (
  `supplier_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `supply_price` decimal(10, 2) NOT NULL,
  `min_order_quantity` int(11) NOT NULL DEFAULT 1,
  `delivery_days` int(11) NOT NULL DEFAULT 7,
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  `last_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`, `isbn`) USING BTREE,
  INDEX `fk_supplier_books_isbn`(`isbn` ASC) USING BTREE,
  CONSTRAINT `fk_supplier_books_isbn` FOREIGN KEY (`isbn`) REFERENCES `books` (`isbn`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_supplier_books_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier_books
-- ----------------------------
INSERT INTO `supplier_books` VALUES ('S8e1ed02de', '978730000001', 55.00, 1, 7, 1, '2025-12-22 19:48:16');
INSERT INTO `supplier_books` VALUES ('S8e1ed02de', '978730000002', 99.00, 10, 14, 1, '2025-12-22 19:49:18');
INSERT INTO `supplier_books` VALUES ('S8e1ed02de', '978730000008', 0.00, 1, 7, 1, '2025-12-22 19:46:53');

-- ----------------------------
-- Table structure for suppliers
-- ----------------------------
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers`  (
  `supplier_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tax_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `cooperation_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `rating` decimal(3, 2) NULL DEFAULT 5.00,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of suppliers
-- ----------------------------
INSERT INTO `suppliers` VALUES ('S8e1ed02de', 'supplier1', 'Somebody', '13344556677', '123@email.com', '华中科技大学', NULL, NULL, 'active', NULL, '2025-12-22 19:35:08', '2025-12-22 19:35:08');
INSERT INTO `suppliers` VALUES ('Sb37481a95', 'supplier2', 'Somebody', '13344556677', '123@email.com', '华中科技大学', NULL, NULL, 'suspended', NULL, '2025-12-22 19:49:41', '2025-12-22 19:49:41');

-- ----------------------------
-- 存储过程定义
-- ----------------------------

-- 1. 充值存储过程
DELIMITER //
DROP PROCEDURE IF EXISTS sp_recharge//
CREATE PROCEDURE sp_recharge(
    IN p_customer_id VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN p_amount DECIMAL(10,2),
    IN p_method VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    OUT p_new_balance DECIMAL(10,2)
)
BEGIN
    DECLARE v_current_balance DECIMAL(10,2);

    -- 参数校验
    IF p_amount <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '充值金额必须大于0';
    END IF;

    -- 获取当前余额
    SELECT account_balance INTO v_current_balance
    FROM customers
    WHERE customer_id = p_customer_id;

    IF v_current_balance IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '客户不存在';
    END IF;

    -- 更新余额
    UPDATE customers
    SET account_balance = account_balance + p_amount
    WHERE customer_id = p_customer_id;

    -- 使用OUT参数返回新余额
    SELECT account_balance INTO p_new_balance
    FROM customers
    WHERE customer_id = p_customer_id;
END//

-- 2. 订单支付确认存储过程（只更新状态，不扣款）
DROP PROCEDURE IF EXISTS sp_pay_order//
CREATE PROCEDURE sp_pay_order(
    IN p_order_id VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN p_payment_method VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
)
BEGIN
    DECLARE v_order_status VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_customer_id VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_actual_amount DECIMAL(12,2);
    DECLARE v_account_balance DECIMAL(10,2);
    DECLARE v_credit_level INT;
    DECLARE v_overdraft_enabled BOOLEAN;
    DECLARE v_overdraft_limit DECIMAL(10,2);
    DECLARE v_available_balance DECIMAL(10,2);

    -- 获取订单信息
    SELECT order_status, customer_id, actual_amount
    INTO v_order_status, v_customer_id, v_actual_amount
    FROM orders
    WHERE order_id = p_order_id;

    IF v_order_status IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '订单不存在';
    END IF;

    IF v_order_status != 'pending' AND v_order_status != 'processing' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '订单状态不允许支付';
    END IF;

    -- 检查客户信用和可用余额
    SELECT account_balance, credit_level
    INTO v_account_balance, v_credit_level
    FROM customers
    WHERE customer_id = v_customer_id;

    -- 获取信用等级信息
    SELECT overdraft_enabled, IFNULL(overdraft_limit, 0)
    INTO v_overdraft_enabled, v_overdraft_limit
    FROM credit_rules
    WHERE level = v_credit_level;

    -- 如果没有信用规则，使用默认值
    IF v_overdraft_enabled IS NULL THEN
        SET v_overdraft_enabled = FALSE;
        SET v_overdraft_limit = 0;
    END IF;

    -- 计算可用余额
    SET v_available_balance = v_account_balance + IF(v_overdraft_enabled, v_overdraft_limit, 0);

    -- 检查余额是否足够（只检查，不扣款）
    IF v_available_balance < v_actual_amount THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '可用余额不足，无法支付';
    END IF;

    -- 更新订单状态为已支付（扣款在发货时进行）
    UPDATE orders
    SET order_status = 'paid',
        payment_method = p_payment_method,
        payment_time = NOW()
    WHERE order_id = p_order_id;

    -- 返回结果
    SELECT
        'paid' as order_status,
        NOW() as payment_time,
        v_actual_amount as actual_amount;
END//

-- 3. 处理发货存储过程（发货时扣款）
DROP PROCEDURE IF EXISTS sp_process_delivery//
CREATE PROCEDURE sp_process_delivery(
    IN p_order_id VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN p_delivery_company VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN p_tracking_no VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
)
BEGIN
    DECLARE v_delivery_id VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_delivery_no VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_customer_id VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_order_status VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_actual_amount DECIMAL(12,2);
    DECLARE v_shipping_address VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_account_balance DECIMAL(10,2);
    DECLARE v_credit_level INT;
    DECLARE v_overdraft_enabled BOOLEAN;
    DECLARE v_overdraft_limit DECIMAL(10,2);
    DECLARE v_total_purchase DECIMAL(12,2);
    DECLARE done INT DEFAULT 0;
    DECLARE v_isbn VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_quantity INT;

    -- 游标声明
    DECLARE cur_details CURSOR FOR
        SELECT isbn, quantity
        FROM order_details
        WHERE order_id = p_order_id;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    -- 事务异常处理
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 检查订单状态
    SELECT customer_id, order_status, actual_amount, shipping_address
    INTO v_customer_id, v_order_status, v_actual_amount, v_shipping_address
    FROM orders
    WHERE order_id = p_order_id;

    IF v_order_status IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '订单不存在';
    END IF;

    IF v_order_status != 'paid' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '订单未支付，无法发货';
    END IF;

    -- 检查客户信用
    SELECT account_balance, credit_level, total_purchase
    INTO v_account_balance, v_credit_level, v_total_purchase
    FROM customers
    WHERE customer_id = v_customer_id;

    -- 获取信用规则
    SELECT overdraft_enabled, IFNULL(overdraft_limit, 0)
    INTO v_overdraft_enabled, v_overdraft_limit
    FROM credit_rules
    WHERE level = v_credit_level;

    IF v_overdraft_enabled IS NULL THEN
        SET v_overdraft_enabled = FALSE;
        SET v_overdraft_limit = 0;
    END IF;

    -- 检查余额是否足够
    IF NOT v_overdraft_enabled AND v_account_balance < v_actual_amount THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '账户余额不足，无法发货';
    END IF;

    IF v_overdraft_enabled AND (v_account_balance - v_actual_amount) < -v_overdraft_limit THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '超过透支额度，无法发货';
    END IF;

    -- 扣款
    UPDATE customers
    SET account_balance = account_balance - v_actual_amount,
        total_purchase = total_purchase + v_actual_amount
    WHERE customer_id = v_customer_id;

    -- 扣减库存并释放预留
    OPEN cur_details;
    read_loop: LOOP
        FETCH cur_details INTO v_isbn, v_quantity;
        IF done THEN
            LEAVE read_loop;
        END IF;

        UPDATE inventory
        SET quantity = quantity - v_quantity,
            reserved_quantity = reserved_quantity - v_quantity,
            last_check = NOW()
        WHERE isbn = v_isbn;
    END LOOP;
    CLOSE cur_details;

    -- 生成发货单ID和单号
    SET v_delivery_id = CONCAT('D', SUBSTRING(UUID(), 1, 12));
    SET v_delivery_no = CONCAT('DEL', UNIX_TIMESTAMP(NOW()) * 1000);

    -- 创建发货单
    INSERT INTO delivery_orders (
        delivery_id, order_id, delivery_no,
        delivery_company, tracking_no, delivery_address,
        delivery_status, ship_time
    ) VALUES (
        v_delivery_id, p_order_id, v_delivery_no,
        p_delivery_company, p_tracking_no, v_shipping_address,
        'shipped', NOW()
    );

    -- 更新订单状态
    UPDATE orders
    SET order_status = 'shipped',
        shipping_time = NOW()
    WHERE order_id = p_order_id;

    COMMIT;

    -- 返回发货信息
    SELECT
        v_delivery_id as delivery_id,
        v_delivery_no as delivery_no,
        'shipped' as order_status,
        NOW() as shipping_time;
END//

DELIMITER ;

-- ----------------------------
-- 触发器定义
-- ----------------------------

-- 1. 订单取消触发器（释放预留库存）
DELIMITER //
DROP TRIGGER IF EXISTS tr_order_cancelled//
CREATE TRIGGER tr_order_cancelled
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    -- 当订单状态从非取消状态变为取消状态时
    IF OLD.order_status != 'cancelled' AND NEW.order_status = 'cancelled' THEN
        -- 释放预留库存
        UPDATE inventory i
        INNER JOIN order_details od ON i.isbn = od.isbn
        SET i.reserved_quantity = i.reserved_quantity - od.quantity
        WHERE od.order_id = NEW.order_id
        AND i.reserved_quantity >= od.quantity;
    END IF;
END//

-- 2. 库存不足触发器（自动生成缺书记录）
DROP TRIGGER IF EXISTS tr_inventory_low_stock//
CREATE TRIGGER tr_inventory_low_stock
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    DECLARE v_book_title VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_publisher_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    DECLARE v_record_id VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

    -- 当库存低于安全库存时触发
    IF NEW.quantity < NEW.safety_stock AND OLD.quantity >= OLD.safety_stock THEN
        -- 获取图书信息
        SELECT b.title, p.publisher_name
        INTO v_book_title, v_publisher_name
        FROM books b
        INNER JOIN publishers p ON b.publisher_id = p.publisher_id
        WHERE b.isbn = NEW.isbn;

        -- 生成缺书记录ID（缩短到18字符，加上'OS'前缀共20字符）
        SET v_record_id = CONCAT('OS', SUBSTRING(MD5(CONCAT(NEW.isbn, NOW())), 1, 18));

        -- 插入缺书记录
        INSERT INTO out_of_stock_records (
            record_id, isbn, book_title, publisher_name,
            required_quantity, source_type, priority, status
        ) VALUES (
            v_record_id, NEW.isbn, v_book_title, v_publisher_name,
            NEW.safety_stock - NEW.quantity, 'auto', 2, 'pending'
        );
    END IF;
END//

-- 3. 订单完成触发器（更新cumit累计消费 - 已在sp_process_delivery中处理）
DROP TRIGGER IF EXISTS tr_order_completed//
CREATE TRIGGER tr_order_completed
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    -- 当订单状态变为delivered时，记录日志或执行其他业务逻辑
    -- 注意：累计消费已经在sp_process_delivery中更新，这里可以做其他处理
    IF OLD.order_status != 'delivered' AND NEW.order_status = 'delivered' THEN
        -- 可以在这里添加其他业务逻辑，例如发送通知等
        -- 当前版本保持简单
        SET @order_completed = NEW.order_id;
    END IF;
END//

-- 4. 图书状态自动更新触发器（库存为0时标记为缺货）
DROP TRIGGER IF EXISTS tr_book_status_update//
CREATE TRIGGER tr_book_status_update
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    -- 如果库存为0且没有预留，将图书标记为缺货
    IF NEW.quantity = 0 AND NEW.reserved_quantity = 0 THEN
        UPDATE books
        SET status = 'out_stock'
        WHERE isbn = NEW.isbn AND status = 'active';

    -- 如果恢复库存，将图书标记为在售
    ELSEIF NEW.quantity > 0 AND OLD.quantity = 0 THEN
        UPDATE books
        SET status = 'active'
        WHERE isbn = NEW.isbn AND status = 'out_of_stock';
    END IF;
END//

-- 5. 采购收货触发器（自动更新库存和采购单状态）
DROP TRIGGER IF EXISTS tr_purchase_received//
CREATE TRIGGER tr_purchase_received
AFTER UPDATE ON purchase_details
FOR EACH ROW
BEGIN
    DECLARE v_total_received INT;
    DECLARE v_total_quantity INT;

    -- 检查采购明细收货数量是否增加
    IF NEW.received_quantity > OLD.received_quantity THEN
        -- 更新库存（只加增量，避免重复累加）
        UPDATE inventory
        SET quantity = quantity + (NEW.received_quantity - OLD.received_quantity),
            last_restock = NOW()
        WHERE isbn = NEW.isbn;

        -- 自动标记该ISBN的缺书记录为"已解决"
        UPDATE out_of_stock_records
        SET status = 'resolved',
            resolve_time = NOW(),
            remark = CONCAT(IFNULL(remark, ''), ' [自动解决：采购到货]')
        WHERE isbn = NEW.isbn
          AND status IN ('pending', 'processing');

        -- 检查采购单是否全部完成
        SELECT SUM(received_quantity), SUM(quantity)
        INTO v_total_received, v_total_quantity
        FROM purchase_details
        WHERE purchase_id = NEW.purchase_id;

        IF v_total_received = v_total_quantity THEN
            UPDATE purchase_orders
            SET status = 'received', actual_delivery = CURDATE()
            WHERE purchase_id = NEW.purchase_id AND status = 'ordered';
        END IF;
    END IF;
END//

-- 6. 关键字数量约束触发器（限制每本书最多10个关键字）
DROP TRIGGER IF EXISTS tr_limit_book_keywords//
CREATE TRIGGER tr_limit_book_keywords
BEFORE INSERT ON book_keywords
FOR EACH ROW
BEGIN
    DECLARE keyword_count INT;

    SELECT COUNT(*) INTO keyword_count
    FROM book_keywords
    WHERE isbn = NEW.isbn;

    IF keyword_count >= 10 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '每本书最多只能有10个关键字';
    END IF;
END//

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;
