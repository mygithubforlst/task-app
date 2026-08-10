package com.nrec.service.app.security;

import com.alibaba.fastjson.JSON;
import com.nrec.base.common.model.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录 / Token 无效 / 过期 / 篡改 -> 返回 401 Result（而非跳转登录页）。
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = new Result<>();
        result.setSucc(false);
        result.setCode("401");
        result.setMsg("未登录或Token无效，请先登录");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
