package com.nrec.service.app.common;

import com.nrec.base.common.exception.ServiceException;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import java.util.List;

/**
 * OVal 参数校验工具：手动触发 QO 上的 net.sf.oval.constraint 注解。
 * <p>校验失败抛 {@link ServiceException}({@link BizCode#PARAM_ERROR}, 中文消息)，
 * 由 ExceptionInterceptor 统一转 Result，避免暴露堆栈。</p>
 */
public final class ValidationUtils {

    private static final Validator VALIDATOR = new Validator();

    private ValidationUtils() {
    }

    /**
     * 校验目标对象，失败抛业务异常。
     *
     * @param target 待校验对象（一般为 *Qo）
     */
    public static void validate(Object target) {
        if (target == null) {
            return;
        }
        List<ConstraintViolation> violations = VALIDATOR.validate(target);
        if (violations != null && !violations.isEmpty()) {
            throw new ServiceException(BizCode.PARAM_ERROR, violations.get(0).getMessage());
        }
    }
}
