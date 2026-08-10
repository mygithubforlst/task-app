package com.nrec.service.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务表 task_item 映射实体。
 * status 为 CHAR(1)，Java 端统一使用 String（"0"/"1"/"2"）。
 */
@Data
@Accessors(chain = true)
@TableName("task_item")
public class TaskItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    private String title;

    private String description;

    /** 状态：0-待办 1-进行中 2-已完成 */
    private String status;

    @TableField("due_date")
    private LocalDateTime dueDate;

    @TableField("user_id")
    private String userId;

    @TableField("category_id")
    private String categoryId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
