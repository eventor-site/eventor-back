-- 기준선: 2026-11-19 운영 DB(eventor) 스키마(MySQL 8.0.32). 이미 있는 DB는 이 버전을 건너뛰고(baseline) V2부터 적용한다.
-- 빈 DB에서만 실행된다. 테이블이 알파벳 순서라 외래 키 검사를 잠시 끈다.
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `ban_nicknames` (
  `ban_nickname_id` bigint NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ban_nickname_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `bookmarks` (
  `bookmark_id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`bookmark_id`),
  KEY `FKr2bb8ww3eg17frxy7pi0u1442` (`category_id`),
  KEY `FKdbsho2e05w5r13fkjqfjmge5f` (`user_id`),
  CONSTRAINT `FKdbsho2e05w5r13fkjqfjmge5f` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKr2bb8ww3eg17frxy7pi0u1442` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `child_count` bigint DEFAULT NULL,
  `depth` bigint DEFAULT NULL,
  `group` bigint DEFAULT NULL,
  `group_order` bigint DEFAULT NULL,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  KEY `FK9il7y6fehxwunjeepq0n7g5rd` (`parent_category_id`),
  CONSTRAINT `FK9il7y6fehxwunjeepq0n7g5rd` FOREIGN KEY (`parent_category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `comment_recommends` (
  `comment_recommend_id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL,
  `recommend_type` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`comment_recommend_id`),
  KEY `FKgj895yuyp5bh4s6ghy6oysmi8` (`comment_id`),
  KEY `FK9ubf9f8gwc8ihlcmbc2pd5sfs` (`recommend_type`),
  KEY `FKlfap67lp3f0iq27kitfshhinn` (`user_id`),
  CONSTRAINT `FK9ubf9f8gwc8ihlcmbc2pd5sfs` FOREIGN KEY (`recommend_type`) REFERENCES `recommend_types` (`recommend_type_id`),
  CONSTRAINT `FKgj895yuyp5bh4s6ghy6oysmi8` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`) ON DELETE CASCADE,
  CONSTRAINT `FKlfap67lp3f0iq27kitfshhinn` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `comment_reports` (
  `comment_report_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_checked` bit(1) DEFAULT NULL,
  `comment_id` bigint NOT NULL,
  `report_type` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`comment_report_id`),
  KEY `FKj9so403o1bquehodqxpyijjma` (`comment_id`),
  KEY `FKlhp07ss5a5acr8y4njg6on7eg` (`report_type`),
  KEY `FKpv6wth1p1osytu7mcriomml8p` (`user_id`),
  CONSTRAINT `FKj9so403o1bquehodqxpyijjma` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`) ON DELETE CASCADE,
  CONSTRAINT `FKlhp07ss5a5acr8y4njg6on7eg` FOREIGN KEY (`report_type`) REFERENCES `report_types` (`report_type_id`),
  CONSTRAINT `FKpv6wth1p1osytu7mcriomml8p` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `comments` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `child_count` bigint DEFAULT NULL,
  `content` varchar(1500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `decommendation_count` bigint DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `depth` bigint DEFAULT NULL,
  `group` bigint DEFAULT NULL,
  `group_order` bigint DEFAULT NULL,
  `recommendation_count` bigint DEFAULT NULL,
  `writer` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `status_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `writer_grade` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK7h839m3lkvhbyv3bcdv7sm4fj` (`parent_comment_id`),
  KEY `FKh4c7lvsc298whoyd4w9ta25cr` (`post_id`),
  KEY `FKq5no5b54q2aou3sxytsu62h71` (`status_id`),
  KEY `FK8omq0tc18jd43bu5tjh6jvraq` (`user_id`),
  CONSTRAINT `FK7h839m3lkvhbyv3bcdv7sm4fj` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`),
  CONSTRAINT `FK8omq0tc18jd43bu5tjh6jvraq` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKh4c7lvsc298whoyd4w9ta25cr` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `FKq5no5b54q2aou3sxytsu62h71` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `crawlers` (
  `crawler_id` bigint NOT NULL AUTO_INCREMENT,
  `last_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`crawler_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `events` (
  `event_id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) DEFAULT NULL,
  `link` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `end_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `favorites` (
  `favorite_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`favorite_id`),
  KEY `FKc03arr824f4f5eaj6wv9bhnoi` (`post_id`),
  KEY `FKk7du8b8ewipawnnpg76d55fus` (`user_id`),
  CONSTRAINT `FKc03arr824f4f5eaj6wv9bhnoi` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `FKk7du8b8ewipawnnpg76d55fus` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `grades` (
  `grade_id` bigint NOT NULL AUTO_INCREMENT,
  `max_amount` bigint DEFAULT NULL,
  `min_amount` bigint DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`grade_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `hotdeals` (
  `hotdeal_id` bigint NOT NULL AUTO_INCREMENT,
  `link` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shopping_mall` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`hotdeal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `images` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_pasted` bit(1) DEFAULT NULL,
  `is_thumbnail` bit(1) DEFAULT NULL,
  `new_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `original_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `size` bigint DEFAULT NULL,
  `url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `extension` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `FKcp0pycisii8ub3q4b7x5mfpn1` (`post_id`),
  CONSTRAINT `FKcp0pycisii8ub3q4b7x5mfpn1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `point_histories` (
  `point_history_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `point_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`point_history_id`),
  KEY `FKi7jy2j3rdbyxg8bvyfl3gxri3` (`point_id`),
  KEY `FKp1n0qput9f88pa5lwy2nsr19f` (`user_id`),
  CONSTRAINT `FKi7jy2j3rdbyxg8bvyfl3gxri3` FOREIGN KEY (`point_id`) REFERENCES `points` (`point_id`),
  CONSTRAINT `FKp1n0qput9f88pa5lwy2nsr19f` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `points` (
  `point_id` bigint NOT NULL AUTO_INCREMENT,
  `amount` bigint DEFAULT NULL,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `post_recommends` (
  `post_recommend_id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `recommend_type` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`post_recommend_id`),
  KEY `FKe93w5dkhhn3t11qpv9iq854in` (`post_id`),
  KEY `FKjrt8w1bvrnm5oqtl2gy8qjfx2` (`recommend_type`),
  KEY `FK4ytwhe2uwwqbjsw4pxtewyyp4` (`user_id`),
  CONSTRAINT `FK4ytwhe2uwwqbjsw4pxtewyyp4` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKe93w5dkhhn3t11qpv9iq854in` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `FKjrt8w1bvrnm5oqtl2gy8qjfx2` FOREIGN KEY (`recommend_type`) REFERENCES `recommend_types` (`recommend_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `post_reports` (
  `post_report_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_checked` bit(1) DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `report_type` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`post_report_id`),
  KEY `FK7ccpkj5jys037f9pq98l31ya2` (`post_id`),
  KEY `FKnobxw90csadspt0axdg48d55m` (`report_type`),
  KEY `FKjbr6kyo7ktru61roraoi7kjtm` (`user_id`),
  CONSTRAINT `FK7ccpkj5jys037f9pq98l31ya2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `FKjbr6kyo7ktru61roraoi7kjtm` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKnobxw90csadspt0axdg48d55m` FOREIGN KEY (`report_type`) REFERENCES `report_types` (`report_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `post_views` (
  `post_view_id` bigint NOT NULL AUTO_INCREMENT,
  `viewed_at` datetime(6) DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `viewer_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`post_view_id`),
  KEY `FKm1fm9hc7487k4j6qd2g1iq0k2` (`post_id`),
  CONSTRAINT `FKm1fm9hc7487k4j6qd2g1iq0k2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `posts` (
  `post_id` bigint NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `recommendation_count` bigint DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `view_count` bigint DEFAULT NULL,
  `writer` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `event_id` bigint DEFAULT NULL,
  `hotdeal_id` bigint DEFAULT NULL,
  `status_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `writer_grade` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_fixed` bit(1) NOT NULL,
  PRIMARY KEY (`post_id`),
  UNIQUE KEY `UKmbwttq8kvkkdfw6mxbs8av3pq` (`event_id`),
  UNIQUE KEY `UK2ktn8d9x2muwr6aeyidk63tx8` (`hotdeal_id`),
  KEY `FKijnwr3brs8vaosl80jg9rp7uc` (`category_id`),
  KEY `FKoy1y7dy92ef5l857fqlsw9r61` (`status_id`),
  KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
  CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKijnwr3brs8vaosl80jg9rp7uc` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `FKosgqxe4wbhy3093ueyeycogck` FOREIGN KEY (`hotdeal_id`) REFERENCES `hotdeals` (`hotdeal_id`),
  CONSTRAINT `FKoy1y7dy92ef5l857fqlsw9r61` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`status_id`),
  CONSTRAINT `FKtnoimyc2wv6tiasoiioxy0rnq` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `recommend_types` (
  `recommend_type_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`recommend_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `report_types` (
  `report_type_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`report_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `roles` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `statistics` (
  `statistic_id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `signup_count` bigint DEFAULT NULL,
  `main_view_count` bigint DEFAULT NULL,
  `visitor_count` bigint DEFAULT NULL,
  `login_count` bigint DEFAULT NULL,
  `post_view_count` bigint DEFAULT NULL,
  PRIMARY KEY (`statistic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `status_types` (
  `status_type_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`status_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `statuses` (
  `status_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status_type_id` bigint NOT NULL,
  PRIMARY KEY (`status_id`),
  KEY `FKsm5kvffbekfr9q6tvuvmyt93m` (`status_type_id`),
  CONSTRAINT `FKsm5kvffbekfr9q6tvuvmyt93m` FOREIGN KEY (`status_type_id`) REFERENCES `status_types` (`status_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_roles` (
  `user_role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_stops` (
  `user_stop_id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `stop_day` bigint DEFAULT NULL,
  `report_type_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_stop_id`),
  KEY `FKra61d0ofhxj2ygifkx5yrblrw` (`report_type_id`),
  KEY `FK10pmxsi0fmabgyuavfo1fud9s` (`user_id`),
  CONSTRAINT `FK10pmxsi0fmabgyuavfo1fud9s` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKra61d0ofhxj2ygifkx5yrblrw` FOREIGN KEY (`report_type_id`) REFERENCES `report_types` (`report_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `birth` date DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `identifier` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_at` datetime(6) DEFAULT NULL,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oauth_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oauth_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `grade_id` bigint NOT NULL,
  `status_id` bigint NOT NULL,
  `nickname_changed_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FKocdpyagwqcwnsytdpie5p0qy2` (`grade_id`),
  KEY `FKpp127ea8ef1s2x9em2o3bhsvo` (`status_id`),
  CONSTRAINT `FKocdpyagwqcwnsytdpie5p0qy2` FOREIGN KEY (`grade_id`) REFERENCES `grades` (`grade_id`),
  CONSTRAINT `FKpp127ea8ef1s2x9em2o3bhsvo` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
