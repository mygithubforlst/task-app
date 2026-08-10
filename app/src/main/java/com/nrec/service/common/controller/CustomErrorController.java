package com.nrec.service.common.controller;

import com.nrec.service.common.properties.ExceptionProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义ErrorController
 *
 * @author chenjia
 */
@Controller
@ApiIgnore
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController extends BasicErrorController {

    @Resource
    private ExceptionProperties exceptionProperties;

    public CustomErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的JSON响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> map = new HashMap<>(5);
        Map<String, Object> originalMsgMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        map.put("succ", false);
        map.put("code", status.value());
        map.put("msg", status.getReasonPhrase());
        map.put("path", originalMsgMap.getOrDefault("path", request.getServletPath()));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(appendExceptionCode(map), status);
    }

    /**
     * 覆盖默认的HTML响应
     */
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        //请求的状态
        HttpStatus status = getStatus(request);
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        //指定自定义的视图
        return (modelAndView == null ? new ModelAndView("error", model) : modelAndView);
    }

    /**
     * 处理追加编码
     */
    private Map<String, Object> appendExceptionCode(Map<String, Object> body) {
        String httpCode = "code";
        if (body.get(httpCode) != null) {
            String code = body.get(httpCode).toString();
            if (code.length() < exceptionProperties.getCodeLength()) {
                body.put(httpCode, exceptionProperties.getCodePrefix() + code);
            }
        }
        return body;
    }
}
