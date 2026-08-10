package com.nrec.service.common.aop;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.nrec.base.common.model.PageBean;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 排序字段驼峰转下划线处理
 * @author zhuyf
 * @date 2020/8/3 19:51
 */
@Component
@Aspect
@Slf4j
public class OrderFieldCamelToUnderLineAspect {

    /**
     * 定义切点,拦截controller
     */
    private final String POINT_CUT = "execution(public * com.nrec.service.app.controller.*.*(..))";

    @Pointcut(POINT_CUT)
    public void pointCut() {
    }

    /**
     * 前置拦截，处理驼峰排序字段转下划线
     *
     * @param joinPoint
     */
    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null&&args[i] instanceof PageBean) {
                PageBean pageBean=(PageBean)args[i];
                if(StringUtils.isNotBlank(pageBean.getSortField())){
                    pageBean.setSortField(StringUtils.camelToUnderline(pageBean.getSortField()));
                    log.debug("处理驼峰排序字段转下划线：" + pageBean.getSortField());
                }
            }
        }
    }
}
