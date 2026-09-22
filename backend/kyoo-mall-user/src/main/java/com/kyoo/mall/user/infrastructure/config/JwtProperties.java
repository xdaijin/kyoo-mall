package com.kyoo.mall.user.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性，归属 user 上下文（TokenProvider 实现的技术细节）。
 * 注：哪些路径匿名可访问是部署关注点，仍只在 app 模块 SecurityConfig 配置，不在此处。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HMAC 签名密钥 */
    private String secret;

    /** 令牌有效期（毫秒） */
    private long expiration;
}
