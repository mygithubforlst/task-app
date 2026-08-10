package com.nrec.service.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器（javax.servlet，非 jakarta）。
 * 解析 Authorization: Bearer &lt;token&gt;，校验通过后将当前用户写入 SecurityContext；
 * 失败则不写入（交由 AuthenticationEntryPoint 对受保护资源返回 401）。
 * 注意：本类不标注 @Component，仅通过 SecurityConfig.addFilterBefore 加入安全链，
 * 避免被 Spring Boot 当成独立 servlet Filter 重复注册。
 */
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.parseToken(token);
                String userId = claims.getSubject();
                String username = claims.get("username", String.class);
                if (StringUtils.hasText(userId)) {
                    JwtUserPrincipal principal = new JwtUserPrincipal(userId, username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                // token 过期/非法/篡改：不设置认证，交由 AuthenticationEntryPoint 返回 401
                log.debug("JWT 校验失败: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
