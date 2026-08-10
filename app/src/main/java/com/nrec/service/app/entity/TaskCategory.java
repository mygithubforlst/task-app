package com.nrec.service.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类表 task_category 映射实体。
 */
@Data
@Accessors(chain = true)
@TableName("task_category")
public class TaskCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    private String name;

    @TableField("user_id")
    private String userId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
