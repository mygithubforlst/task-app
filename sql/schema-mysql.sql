-- ============================================================
-- 个人任务清单系统 · 数据库初始化脚本 (MySQL 5.7 / 8.x 通用)
-- 依据作业文档 §4 / §12
-- 执行方式：在 MySQL 客户端中 source 本文件，或复制内容执行。
--   建库 task_app + 三张表 + 唯一约束 + 四组索引，一次完成。
-- ============================================================

CREATE DATABASE IF NOT EXISTS task_app
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE task_app;

-- 用户表：存放注册账号，password 列存 BCrypt 哈希（绝不明文）
CREATE TABLE task_user (
  id          VARCHAR(32)   NOT NULL,
  username    VARCHAR(50)   NOT NULL,
  password    VARCHAR(100)  NOT NULL,
  email       VARCHAR(100)  DEFAULT NULL,
  enabled     CHAR(1)       NOT NULL DEFAULT '1',
  created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类表：同一用户下分类名唯一
CREATE TABLE task_category (
  id          VARCHAR(32)   NOT NULL,
  name        VARCHAR(50)   NOT NULL,
  user_id     VARCHAR(32)   NOT NULL,
  version     BIGINT        NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删 1-已删',
  created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_user_name (user_id, name),
  KEY idx_category_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 任务表：status 取值 0/1/2（待办/进行中/已完成），category_id 可空
CREATE TABLE task_item (
  id          VARCHAR(32)   NOT NULL,
  title       VARCHAR(200)  NOT NULL,
  description VARCHAR(1000) DEFAULT NULL,
  status      CHAR(1)       NOT NULL DEFAULT '0',
  due_date    DATETIME      DEFAULT NULL,
  user_id     VARCHAR(32)   NOT NULL,
  category_id VARCHAR(32)   DEFAULT NULL,
  version     BIGINT        NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删 1-已删',
  created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_task_user (user_id),
  KEY idx_task_user_status (user_id, status),
  KEY idx_task_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
