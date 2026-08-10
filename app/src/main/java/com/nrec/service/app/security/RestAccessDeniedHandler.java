package com.nrec.service.app.security;

import com.alibaba.fastjson.JSON;
import com.nrec.base.common.model.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 已登录但无权限 -> 返回 403 Result。
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = new Result<>();
        result.setSucc(false);
        result.setCode("403");
        result.setMsg("无权限访问该资源");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
