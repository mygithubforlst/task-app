-- ============================================================
-- 个人任务清单系统 · H2 内存库初始化脚本（仅集成测试用）
-- 与 schema-mysql.sql 结构保持一致，仅适配 H2 语法（ark 列类型、TIMESTAMP 默认值）。
-- 由 application-integrationtest.yml 的 spring.datasource.schema 自动加载。
-- ============================================================

CREATE TABLE IF NOT EXISTS task_user (
  id          VARCHAR(32)   NOT NULL,
  username    VARCHAR(50)   NOT NULL,
  password    VARCHAR(100)  NOT NULL,
  email       VARCHAR(100)  DEFAULT NULL,
  enabled     CHAR(1)       NOT NULL DEFAULT '1',
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username)
);

CREATE TABLE IF NOT EXISTS task_category (
  id          VARCHAR(32)   NOT NULL,
  name        VARCHAR(50)   NOT NULL,
  user_id     VARCHAR(32)   NOT NULL,
  version     BIGINT        NOT NULL DEFAULT 0,
  deleted     TINYINT       NOT NULL DEFAULT 0,
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_user_name (user_id, name),
  KEY idx_category_user (user_id)
);

CREATE TABLE IF NOT EXISTS task_item (
  id          VARCHAR(32)   NOT NULL,
  title       VARCHAR(200)  NOT NULL,
  description VARCHAR(1000) DEFAULT NULL,
  status      CHAR(1)       NOT NULL DEFAULT '0',
  due_date    TIMESTAMP     DEFAULT NULL,
  user_id     VARCHAR(32)   NOT NULL,
  category_id VARCHAR(32)   DEFAULT NULL,
  version     BIGINT        NOT NULL DEFAULT 0,
  deleted     TINYINT       NOT NULL DEFAULT 0,
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_task_user (user_id),
  KEY idx_task_user_status (user_id, status),
  KEY idx_task_category (category_id)
);
