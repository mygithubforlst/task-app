package com.nrec.service.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表 task_user 映射实体。
 * 主键为 VARCHAR(32) UUID（assign_uuid），Java 类型为 String。
 */
@Data
@Accessors(chain = true)
@TableName("task_user")
public class TaskUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    private String username;

    /** BCrypt 哈希，绝不返回到前端 */
    private String password;

    private String email;

    /** 0-禁用 1-启用 */
    private String enabled;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
