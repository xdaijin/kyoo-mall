package com.kyoo.mall.app.security;

/**
 * 登录用户主体，由 JWT claims 直接构建（无状态，不查库）。
 * 业务代码需要当前登录人时：@AuthenticationPrincipal LoginUser loginUser。
 * 注：token 有效期内用户被禁用不会即时生效（无角色/踢人需求下的有意取舍），
 * 将来需要即时失效时引入短时效 + refresh token 或 Redis 令牌黑名单。
 */
public record LoginUser(Long userId, String username) {
}
