package com.kyoo.mall.user.domain.service;

/**
 * 令牌签发能力（领域服务接口，infrastructure 层实现）。
 * 应用层只依赖此接口，不感知 JWT 实现细节。
 */
public interface TokenProvider {

    String generateToken(Long userId, String username);
}
