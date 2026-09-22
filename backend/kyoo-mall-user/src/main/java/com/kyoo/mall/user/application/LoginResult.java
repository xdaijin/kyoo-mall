package com.kyoo.mall.user.application;

import com.kyoo.mall.user.domain.model.SysUser;

/**
 * 登录/注册成功的应用层返回对象，由 interfaces 层映射为对外 DTO。
 */
public record LoginResult(SysUser user, String token) {
}
