-- ============================================================
-- 个人任务清单系统 · 测试数据 (虚构，无真实凭据)
-- 依据作业文档 §4.6 / §14（密码列存 BCrypt 哈希，绝不明文）
-- 执行方式：在 task_app 库下 source 本文件（可选，用于本地联调）。
-- 测试账号：user_a / user_b，密码均为 Test@123456（下方哈希即对应该明文）。
-- ============================================================

USE task_app;

-- ---------- 用户（password 为 BCrypt 哈希，前身为 Test@123456）----------
INSERT INTO task_user (id, username, password, email, enabled) VALUES
  ('a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1', 'user_a', '$2a$10$ofdpwzDr6WrhrQbfwRm.Y.SkRDPxGUIhUFvqIqHkJe9W/xlEFeude', 'user_a@example.com', '1'),
  ('b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b', 'user_b', '$2a$10$2.4eDXe2.vJNXs2uX2kMtO16zQQB07lZwgKC5KGJOni64056TA7/6', 'user_b@example.com', '1');

-- ---------- 分类（同一用户下分类名唯一）----------
INSERT INTO task_category (id, name, user_id) VALUES
  ('c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1', '工作',   'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1'),
  ('c2c2c2c2c2c2c2c2c2c2c2c2c2c2c2c2', '学习',   'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1'),
  ('c3c3c3c3c3c3c3c3c3c3c3c3c3c3c3c3', '生活',   'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1'),
  ('d1d1d1d1d1d1d1d1d1d1d1d1d1d1d1d1', '工作',   'b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b');

-- ---------- 任务（status: 0 待办 / 1 进行中 / 2 已完成）----------
-- user_a 的任务覆盖三种状态
INSERT INTO task_item (id, title, description, status, due_date, user_id, category_id) VALUES
  ('t1t1t1t1t1t1t1t1t1t1t1t1t1t1t1t1', '完成周报',     '汇总本周进度',           '0', '2026-08-15 18:00:00', 'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1', 'c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1'),
  ('t2t2t2t2t2t2t2t2t2t2t2t2t2t2t2t2', '阅读 Spring 文档', '熟悉 Security 配置', '1', '2026-08-12 20:00:00', 'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1', 'c2c2c2c2c2c2c2c2c2c2c2c2c2c2c2c2'),
  ('t3t3t3t3t3t3t3t3t3t3t3t3t3t3t3t3', '整理桌面',     '已完成的杂活',          '2', NULL,                   'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a', 'c3c3c3c3c3c3c3c3c3c3c3c3c3c3c3c3'),
  ('t4t4t4t4t4t4t4t4t4t4t4t4t4t4t4t4', '未分类待办',   '暂不归属任何分类',       '0', '2026-08-20 09:00:00', 'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a', NULL),
  ('t5t5t5t5t5t5t5t5t5t5t5t5t5t5t5t5', '提交代码评审', '关联工作分类',          '1', '2026-08-11 17:00:00', 'a1a1a1a1a1a1a1a1a1a1a1a1a1a1a', 'c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1'),
  -- user_b 的任务（少量，验证跨用户数据隔离）
  ('e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1e1', '健身计划',     'user_b 的待办',         '0', '2026-08-14 07:00:00', 'b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b', 'd1d1d1d1d1d1d1d1d1d1d1d1d1d1d1d1'),
  ('e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2', '读书笔记',     'user_b 已完成',         '2', NULL,                   'b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b', NULL);
