package com.nrec.service.app.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JWT 解析后的当前用户身份，作为 Authentication 的 principal 存入 SecurityContext。
 */
@Getter
@AllArgsConstructor
public class JwtUserPrincipal {
    private final String userId;
    private final String username;
}
