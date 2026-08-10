package com.nrec.service.app.model;

/**
 * 任务状态常量（与数据库 status CHAR(1) 一致，统一使用 String，避免散落魔法数字）。
 */
public final class TaskStatus {

    public static final String TODO = "0";
    public static final String IN_PROGRESS = "1";
    public static final String DONE = "2";

    private TaskStatus() {
    }

    public static boolean isValid(String status) {
        return TODO.equals(status) || IN_PROGRESS.equals(status) || DONE.equals(status);
    }
}
