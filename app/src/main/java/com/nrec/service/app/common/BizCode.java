package com.nrec.service.app.common;

/**
 * 业务异常 code 常量，配合 {@code new ServiceException(code, msg)} 实现异常语义化区分。
 * <p>覆盖作业要求的异常分类：参数错误 / 数据不存在 / 用户名重复 / 分类名重复 / 原密码错误 / 状态非法。
 * 未登录(401) 与无权限(403) 由 Security 的 EntryPoint / AccessDeniedHandler 直接写 Result。</p>
 */
public final class BizCode {

    private BizCode() {
    }

    /** 参数校验错误 */
    public static final String PARAM_ERROR = "PARAM_ERROR";

    /** 数据不存在（任务 / 分类 / 用户） */
    public static final String NOT_FOUND = "NOT_FOUND";

    /** 用户名重复 */
    public static final String DUPLICATE_USER = "DUPLICATE_USER";

    /** 分类名重复 */
    public static final String DUPLICATE_CATEGORY = "DUPLICATE_CATEGORY";

    /** 原密码错误 */
    public static final String PWD_WRONG = "PWD_WRONG";

    /** 任务状态非法 */
    public static final String INVALID_STATUS = "INVALID_STATUS";
}
