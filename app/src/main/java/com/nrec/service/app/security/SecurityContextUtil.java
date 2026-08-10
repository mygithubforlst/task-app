package com.nrec.service.app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 从 SecurityContext 获取当前登录用户。供 Service / Controller 调用。
 */
public class SecurityContextUtil {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            return ((JwtUserPrincipal) authentication.getPrincipal()).getUserId();
        }
        return null;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            return ((JwtUserPrincipal) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
