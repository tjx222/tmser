-- ----------------------------
-- Table structure for attachments
-- ----------------------------
DROP TABLE IF EXISTS `attachments`;
CREATE TABLE `attachments`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `create_time` datetime(6) NULL DEFAULT NULL,
                                `update_time` datetime(6) NULL DEFAULT NULL,
                                `file_key` varchar(2047) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `height` int(11) NULL DEFAULT 0,
                                `media_type` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                `path` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                `size` bigint(20) NOT NULL,
                                `suffix` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `thumb_path` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `type` int(11) NULL DEFAULT 0,
                                `width` int(11) NULL DEFAULT 0,
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `attachments_media_type`(`media_type`) USING BTREE,
                                INDEX `attachments_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `create_time` datetime(6) NULL DEFAULT NULL,
                               `update_time` datetime(6) NULL DEFAULT NULL,
                               `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                               `parent_id` int(11) NULL DEFAULT 0,
                               `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                               `priority` int(11) NULL DEFAULT 0,
                               `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                               `slug_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                               `thumbnail` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `UK_oul14ho7bctbefv8jywp5v3i2`(`slug`) USING BTREE,
                               INDEX `categories_name`(`name`) USING BTREE,
                               INDEX `categories_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment_black_list
-- ----------------------------
DROP TABLE IF EXISTS `comment_black_list`;
CREATE TABLE `comment_black_list`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `create_time` datetime(6) NULL DEFAULT NULL,
                                       `update_time` datetime(6) NULL DEFAULT NULL,
                                       `ban_time` datetime(6) NULL DEFAULT NULL,
                                       `ip_address` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
                             `type` int(11) NOT NULL DEFAULT 0,
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `create_time` datetime(6) NULL DEFAULT NULL,
                             `update_time` datetime(6) NULL DEFAULT NULL,
                             `allow_notification` bit(1) NULL DEFAULT b'1',
                             `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                             `author_url` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                             `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                             `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                             `gravatar_md5` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                             `ip_address` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                             `is_admin` bit(1) NULL DEFAULT b'0',
                             `parent_id` bigint(20) NULL DEFAULT 0,
                             `post_id` int(11) NOT NULL,
                             `status` int(11) NULL DEFAULT 1,
                             `top_priority` int(11) NULL DEFAULT 0,
                             `user_agent` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `comments_post_id`(`post_id`) USING BTREE,
                             INDEX `comments_type_status`(`type`, `status`) USING BTREE,
                             INDEX `comments_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for content_patch_logs
-- ----------------------------
DROP TABLE IF EXISTS `content_patch_logs`;
CREATE TABLE `content_patch_logs`  (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `create_time` datetime(6) NULL DEFAULT NULL,
                                       `update_time` datetime(6) NULL DEFAULT NULL,
                                       `content_diff` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                                       `original_content_diff` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                                       `post_id` int(11) NULL DEFAULT NULL,
                                       `publish_time` datetime(6) NULL DEFAULT NULL,
                                       `source_id` int(11) NOT NULL,
                                       `status` int(11) NULL DEFAULT 1,
                                       `version` int(11) NOT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `idx_post_id`(`post_id`) USING BTREE,
                                       INDEX `idx_status`(`status`) USING BTREE,
                                       INDEX `idx_version`(`version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for contents
-- ----------------------------
DROP TABLE IF EXISTS `contents`;
CREATE TABLE `contents`  (
                             `post_id` int(11) NOT NULL,
                             `create_time` datetime(6) NULL DEFAULT NULL,
                             `update_time` datetime(6) NULL DEFAULT NULL,
                             `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                             `head_patch_log_id` int(11) NULL DEFAULT NULL,
                             `original_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                             `patch_log_id` int(11) NULL DEFAULT NULL,
                             `status` int(11) NULL DEFAULT 1,
                             PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for journals
-- ----------------------------
DROP TABLE IF EXISTS `journals`;
CREATE TABLE `journals`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `create_time` datetime(6) NULL DEFAULT NULL,
                             `update_time` datetime(6) NULL DEFAULT NULL,
                             `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                             `likes` bigint(20) NULL DEFAULT 0,
                             `source_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                             `type` int(11) NULL DEFAULT 0,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for links
-- ----------------------------
DROP TABLE IF EXISTS `links`;
CREATE TABLE `links`  (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `create_time` datetime(6) NULL DEFAULT NULL,
                          `update_time` datetime(6) NULL DEFAULT NULL,
                          `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `logo` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          `priority` int(11) NULL DEFAULT 0,
                          `team` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `url` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          PRIMARY KEY (`id`) USING BTREE,
                          INDEX `links_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `create_time` datetime(6) NULL DEFAULT NULL,
                         `update_time` datetime(6) NULL DEFAULT NULL,
                         `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                         `ip_address` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                         `log_key` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                         `type` int(11) NOT NULL,
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `logs_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for menus
-- ----------------------------
DROP TABLE IF EXISTS `menus`;
CREATE TABLE `menus`  (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `create_time` datetime(6) NULL DEFAULT NULL,
                          `update_time` datetime(6) NULL DEFAULT NULL,
                          `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          `parent_id` int(11) NULL DEFAULT 0,
                          `priority` int(11) NULL DEFAULT 0,
                          `target` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '_self',
                          `team` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `url` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          PRIMARY KEY (`id`) USING BTREE,
                          INDEX `menus_parent_id`(`parent_id`) USING BTREE,
                          INDEX `menus_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for metas
-- ----------------------------
DROP TABLE IF EXISTS `metas`;
CREATE TABLE `metas`  (
                          `type` int(11) NOT NULL DEFAULT 0,
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `create_time` datetime(6) NULL DEFAULT NULL,
                          `update_time` datetime(6) NULL DEFAULT NULL,
                          `meta_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          `post_id` int(11) NOT NULL,
                          `meta_value` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for options
-- ----------------------------
DROP TABLE IF EXISTS `options`;
CREATE TABLE `options`  (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `create_time` datetime(6) NULL DEFAULT NULL,
                            `update_time` datetime(6) NULL DEFAULT NULL,
                            `option_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                            `type` int(11) NULL DEFAULT 0,
                            `option_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of options
-- ----------------------------
INSERT INTO `options` VALUES (1, '2022-03-15 13:42:09.025000', '2022-03-15 13:42:09.025000', 'blog_title', 0, 'tmser');
INSERT INTO `options` VALUES (2, '2022-03-15 13:42:09.053000', '2022-03-15 13:42:09.053000', 'global_absolute_path_enabled', 0, 'false');
INSERT INTO `options` VALUES (3, '2022-03-15 13:42:09.054000', '2022-03-15 13:42:09.054000', 'blog_locale', 0, 'zh');
INSERT INTO `options` VALUES (4, '2022-03-15 13:42:09.055000', '2022-03-15 13:42:09.055000', 'blog_url', 0, 'http://localhost:8090');
INSERT INTO `options` VALUES (5, '2022-03-15 13:42:09.056000', '2022-03-15 13:42:09.056000', 'is_installed', 0, 'true');
INSERT INTO `options` VALUES (6, '2022-03-15 13:42:19.983000', '2022-03-15 13:42:19.983000', 'birthday', 0, '1647322939976');
INSERT INTO `options` VALUES (7, '2022-03-15 14:23:13.248000', '2022-03-15 14:23:13.248000', 'theme', 0, 'weicarus');
INSERT INTO `options` VALUES (8, '2022-08-05 10:58:20.331000', '2022-08-05 10:58:20.331000', 'api_enabled', 0, 'true');
INSERT INTO `options` VALUES (9, '2022-08-05 10:58:20.344000', '2022-08-05 10:58:20.344000', 'api_access_key', 0, 'apikey_');
INSERT INTO `options` VALUES (10, '2022-08-05 15:23:49.640000', '2022-08-05 15:23:49.640000', 'gravatar_source', 0, '//gravatar.com/avatar/');
INSERT INTO `options` VALUES (11, '2022-08-05 15:23:49.647000', '2022-08-05 15:23:49.647000', 'post_index_sort', 0, 'create_time');
INSERT INTO `options` VALUES (12, '2022-08-05 15:23:49.650000', '2022-08-05 15:23:49.650000', 'email_ssl_port', 0, '465');
INSERT INTO `options` VALUES (13, '2022-08-05 15:23:49.656000', '2022-08-05 15:23:49.656000', 'oss_qiniu_zone', 0, 'auto');
INSERT INTO `options` VALUES (14, '2022-08-05 15:23:49.659000', '2022-08-05 15:23:49.659000', 'recycled_post_retention_time', 0, '30');
INSERT INTO `options` VALUES (15, '2022-08-05 15:23:49.663000', '2022-08-05 15:23:49.663000', 'email_enabled', 0, 'false');
INSERT INTO `options` VALUES (16, '2022-08-05 15:23:49.666000', '2022-08-05 15:23:49.666000', 'attachment_upload_max_files', 0, '50');
INSERT INTO `options` VALUES (17, '2022-08-05 15:23:49.668000', '2022-08-05 15:23:49.668000', 'email_protocol', 0, 'smtp');
INSERT INTO `options` VALUES (18, '2022-08-05 15:23:49.671000', '2022-08-05 15:23:49.671000', 'comment_api_enabled', 0, 'true');
INSERT INTO `options` VALUES (19, '2022-08-05 15:23:49.673000', '2022-08-05 15:23:49.673000', 'developer_mode', 0, 'false');
INSERT INTO `options` VALUES (20, '2022-08-05 15:23:49.677000', '2022-08-05 15:23:49.677000', 'default_editor', 0, 'MARKDOWN');
INSERT INTO `options` VALUES (21, '2022-08-05 15:23:49.680000', '2022-08-05 15:23:49.680000', 'photos_title', 0, '图库');
INSERT INTO `options` VALUES (22, '2022-08-05 15:23:49.684000', '2022-08-05 15:23:49.684000', 'comment_range', 0, '30');
INSERT INTO `options` VALUES (23, '2022-08-05 15:23:49.687000', '2022-08-05 15:23:49.687000', 'seo_spider_disabled', 0, 'false');
INSERT INTO `options` VALUES (24, '2022-08-05 15:23:49.690000', '2022-08-05 15:23:49.690000', 'journals_page_size', 0, '10');
INSERT INTO `options` VALUES (25, '2022-08-05 15:23:49.693000', '2022-08-05 15:23:49.693000', 'archives_prefix', 0, 'archives');
INSERT INTO `options` VALUES (26, '2022-08-05 15:23:49.696000', '2022-08-05 15:23:49.696000', 'journals_prefix', 0, 'journals');
INSERT INTO `options` VALUES (27, '2022-08-05 15:23:49.698000', '2022-08-05 15:23:49.698000', 'comment_page_size', 0, '10');
INSERT INTO `options` VALUES (28, '2022-08-05 15:23:49.701000', '2022-08-05 15:23:49.701000', 'journals_title', 0, '日志');
INSERT INTO `options` VALUES (29, '2022-08-05 15:23:49.703000', '2022-08-05 15:32:13.467000', 'attachment_upload_image_preview_enable', 0, 'false');
INSERT INTO `options` VALUES (30, '2022-08-05 15:23:49.705000', '2022-08-05 15:23:49.705000', 'attachment_type', 0, 'TENCENTCOS');
INSERT INTO `options` VALUES (31, '2022-08-05 15:23:49.707000', '2022-08-05 15:23:49.707000', 'comment_reply_notice', 0, 'false');
INSERT INTO `options` VALUES (32, '2022-08-05 15:23:49.711000', '2022-08-05 15:23:49.711000', 'comment_ban_time', 0, '10');
INSERT INTO `options` VALUES (33, '2022-08-05 15:23:49.714000', '2022-08-05 15:23:49.714000', 'oss_qiniu_domain_protocol', 0, 'https://');
INSERT INTO `options` VALUES (34, '2022-08-05 15:23:49.715000', '2022-08-05 15:23:49.715000', 'oss_upyun_domain_protocol', 0, 'https://');
INSERT INTO `options` VALUES (35, '2022-08-05 15:23:49.718000', '2022-08-05 15:23:49.718000', 'comment_new_notice', 0, 'false');
INSERT INTO `options` VALUES (36, '2022-08-05 15:23:49.720000', '2022-08-05 15:23:49.720000', 'photos_page_size', 0, '10');
INSERT INTO `options` VALUES (37, '2022-08-05 15:23:49.723000', '2022-08-05 15:23:49.723000', 'attachment_upload_max_parallel_uploads', 0, '3');
INSERT INTO `options` VALUES (38, '2022-08-05 15:23:49.725000', '2022-08-05 15:23:49.725000', 'links_prefix', 0, 'links');
INSERT INTO `options` VALUES (39, '2022-08-05 15:23:49.728000', '2022-08-05 15:23:49.728000', 'sheet_prefix', 0, 's');
INSERT INTO `options` VALUES (40, '2022-08-05 15:23:49.729000', '2022-08-05 15:23:49.729000', 'tags_prefix', 0, 'tags');
INSERT INTO `options` VALUES (41, '2022-08-05 15:23:49.731000', '2022-08-05 15:23:49.731000', 'recycled_post_retention_timeunit', 0, 'DAY');
INSERT INTO `options` VALUES (42, '2022-08-05 15:23:49.734000', '2022-08-05 15:23:49.734000', 'rss_page_size', 0, '20');
INSERT INTO `options` VALUES (43, '2022-08-05 15:23:49.737000', '2022-08-05 15:23:49.737000', 'post_permalink_type', 0, 'DEFAULT');
INSERT INTO `options` VALUES (44, '2022-08-05 15:23:49.740000', '2022-08-05 15:23:49.740000', 'sheet_permalink_type', 0, 'SECONDARY');
INSERT INTO `options` VALUES (45, '2022-08-05 15:23:49.743000', '2022-08-05 15:23:49.743000', 'rss_content_type', 0, 'full');
INSERT INTO `options` VALUES (46, '2022-08-05 15:23:49.746000', '2022-08-05 15:23:49.746000', 'categories_prefix', 0, 'categories');
INSERT INTO `options` VALUES (47, '2022-08-05 15:23:49.748000', '2022-08-05 15:23:49.748000', 'photos_prefix', 0, 'photos');
INSERT INTO `options` VALUES (48, '2022-08-05 15:23:49.750000', '2022-08-05 15:23:49.750000', 'comment_internal_plugin_js', 0, '//cdn.jsdelivr.net/npm/halo-comment@latest/dist/halo-comment.min.js');
INSERT INTO `options` VALUES (49, '2022-08-05 15:23:49.753000', '2022-08-05 15:23:49.753000', 'post_index_page_size', 0, '10');
INSERT INTO `options` VALUES (50, '2022-08-05 15:23:49.756000', '2022-08-05 15:23:49.756000', 'oss_ali_domain_protocol', 0, 'https://');
INSERT INTO `options` VALUES (51, '2022-08-05 15:23:49.758000', '2022-08-05 15:23:49.758000', 'post_archives_page_size', 0, '10');
INSERT INTO `options` VALUES (52, '2022-08-05 15:23:49.761000', '2022-08-05 15:23:49.761000', 'comment_new_need_check', 0, 'true');
INSERT INTO `options` VALUES (53, '2022-08-05 15:23:49.762000', '2022-08-05 15:23:49.762000', 'links_title', 0, '友情链接');
INSERT INTO `options` VALUES (54, '2022-08-05 15:23:49.764000', '2022-08-05 15:23:49.764000', 'recycled_post_cleaning_enabled', 0, 'false');
INSERT INTO `options` VALUES (55, '2022-08-05 15:23:49.766000', '2022-08-05 15:23:49.766000', 'post_summary_length', 0, '150');
INSERT INTO `options` VALUES (56, '2022-08-05 15:23:49.767000', '2022-08-05 15:23:49.767000', 'cos_tencent_region', 0, 'ap-beijing');
INSERT INTO `options` VALUES (57, '2022-08-05 15:23:49.769000', '2022-08-05 15:23:49.769000', 'cos_tencent_secret_id', 0, 'AKIDjHaeT3OUM1RbWCdptgEWZNxINDoAOWP3');
INSERT INTO `options` VALUES (58, '2022-08-05 15:23:49.772000', '2022-08-05 15:23:49.772000', 'cos_tencent_secret_key', 0, 'SfeOt9AwTlocBVbS4RZS6VfbIkxve8NM');
INSERT INTO `options` VALUES (59, '2022-08-05 15:23:49.776000', '2022-08-05 15:23:49.776000', 'cos_tencent_bucket_name', 0, 'testoss-1309636105');
INSERT INTO `options` VALUES (60, '2022-08-05 15:32:13.481000', '2022-08-05 15:32:13.481000', 'cos_tencent_source', 0, 'test');
INSERT INTO `options` VALUES (61, '2022-08-05 15:57:53.936000', '2022-08-05 15:57:53.936000', 'cos_tencent_domain_protocol', 0, 'https://');

-- ----------------------------
-- Table structure for photos
-- ----------------------------
DROP TABLE IF EXISTS `photos`;
CREATE TABLE `photos`  (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `create_time` datetime(6) NULL DEFAULT NULL,
                           `update_time` datetime(6) NULL DEFAULT NULL,
                           `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                           `likes` bigint(20) NOT NULL DEFAULT 0,
                           `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                           `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                           `take_time` datetime(6) NULL DEFAULT NULL,
                           `team` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                           `thumbnail` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                           `url` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `photos_team`(`team`) USING BTREE,
                           INDEX `photos_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_categories
-- ----------------------------
DROP TABLE IF EXISTS `post_categories`;
CREATE TABLE `post_categories`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `create_time` datetime(6) NULL DEFAULT NULL,
                                    `update_time` datetime(6) NULL DEFAULT NULL,
                                    `category_id` int(11) NULL DEFAULT NULL,
                                    `post_id` int(11) NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `post_categories_post_id`(`post_id`) USING BTREE,
                                    INDEX `post_categories_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_tags
-- ----------------------------
DROP TABLE IF EXISTS `post_tags`;
CREATE TABLE `post_tags`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `create_time` datetime(6) NULL DEFAULT NULL,
                              `update_time` datetime(6) NULL DEFAULT NULL,
                              `post_id` int(11) NOT NULL,
                              `tag_id` int(11) NOT NULL,
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `post_tags_post_id`(`post_id`) USING BTREE,
                              INDEX `post_tags_tag_id`(`tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
                          `type` int(11) NOT NULL DEFAULT 0,
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `create_time` datetime(6) NULL DEFAULT NULL,
                          `update_time` datetime(6) NULL DEFAULT NULL,
                          `disallow_comment` bit(1) NULL DEFAULT b'0',
                          `edit_time` datetime(6) NULL DEFAULT NULL,
                          `editor_type` int(11) NULL DEFAULT 0,
                          `format_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                          `likes` bigint(20) NULL DEFAULT 0,
                          `meta_description` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `meta_keywords` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `original_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                          `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `status` int(11) NULL DEFAULT 1,
                          `summary` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
                          `template` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `thumbnail` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          `top_priority` int(11) NULL DEFAULT 0,
                          `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `version` int(11) NULL DEFAULT 1,
                          `visits` bigint(20) NULL DEFAULT 0,
                          `word_count` bigint(20) NULL DEFAULT 0,
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE INDEX `UK_qmmso8qxjpbxwegdtp0l90390`(`slug`) USING BTREE,
                          INDEX `posts_type_status`(`type`, `status`) USING BTREE,
                          INDEX `posts_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for share_info
-- ----------------------------
DROP TABLE IF EXISTS `share_info`;
CREATE TABLE `share_info`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
                               `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '说明',
                               `start_time` datetime(0) NOT NULL COMMENT '生效时间',
                               `end_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
                               `valid_days` int(11) NOT NULL COMMENT '有效天数',
                               `create_id` int(11) NOT NULL COMMENT '创建用户id',
                               `sign` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '签名',
                               `res_ids` json NULL COMMENT '允许访问资源id列表',
                               `total_visit` int(11) NULL DEFAULT NULL COMMENT '总访问次数',
                               `create_time` datetime(0) NULL DEFAULT NULL,
                               `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
                               `update_time` datetime(0) NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `sign_index`(`sign`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `create_time` datetime(6) NULL DEFAULT NULL,
                         `update_time` datetime(6) NULL DEFAULT NULL,
                         `color` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                         `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                         `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                         `slug_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                         `thumbnail` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                         PRIMARY KEY (`id`) USING BTREE,
                         UNIQUE INDEX `UK_sn0d91hxu700qcw0n4pebp5vc`(`slug`) USING BTREE,
                         INDEX `tags_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for theme_settings
-- ----------------------------
DROP TABLE IF EXISTS `theme_settings`;
CREATE TABLE `theme_settings`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `create_time` datetime(6) NULL DEFAULT NULL,
                                   `update_time` datetime(6) NULL DEFAULT NULL,
                                   `setting_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                   `theme_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                   `setting_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `theme_settings_setting_key`(`setting_key`) USING BTREE,
                                   INDEX `theme_settings_theme_id`(`theme_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `create_time` datetime(6) NULL DEFAULT NULL,
                          `update_time` datetime(6) NULL DEFAULT NULL,
                          `avatar` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `description` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `email` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `expire_time` datetime(6) NULL DEFAULT NULL,
                          `mfa_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                          `mfa_type` int(11) NOT NULL DEFAULT 0,
                          `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for visit_log
-- ----------------------------
DROP TABLE IF EXISTS `visit_log`;
CREATE TABLE `visit_log`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `ip_address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '访问ip',
                              `create_time` datetime(0) NOT NULL COMMENT '访问时间',
                              `content_id` int(11) NULL DEFAULT NULL COMMENT '内容id',
                              `content_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '',
                              `share_id` int(11) NOT NULL COMMENT '分享id',
                              `share_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `index_share_id`(`share_id`) USING BTREE,
                              INDEX `index_content_id`(`content_id`) USING BTREE,
                              INDEX `index_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;


