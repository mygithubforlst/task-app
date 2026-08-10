package com.nrec.service.app.model;

import net.sf.oval.constraint.CheckWithCheck;

/**
 * OVal 自定义校验：任务状态只允许 "0"、"1"、"2"。
 * 用于状态字段（String 类型）的参数校验。
 */
public class TaskStatusCheck implements CheckWithCheck.SimpleCheck {

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        if (value == null) {
            // 允许为空（如更新时状态可选）
            return true;
        }
        String status = value instanceof String ? (String) value : String.valueOf(value);
        return TaskStatus.isValid(status);
    }
}
