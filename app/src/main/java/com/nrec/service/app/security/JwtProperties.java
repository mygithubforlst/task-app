package com.nrec.service.app.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置，从 application(-test).yml 的 nrec.task.jwt.* 读取。
 * secret 为开发环境固定密钥（非生产真实密钥）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "nrec.task.jwt")
public class JwtProperties {
    private String secret;
    private Long expiration;
}
