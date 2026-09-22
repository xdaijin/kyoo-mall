package com.kyoo.mall.user.domain.repository;

import com.kyoo.mall.user.domain.model.SysUser;

/**
 * 用户仓储接口（领域层定义，infrastructure 层实现）。
 * 领域/应用层只依赖此接口，不感知 MyBatis-Plus。
 */
public interface UserRepository {

    SysUser findById(Long id);

    SysUser findByUsername(String username);

    boolean existsByUsername(String username);

    void save(SysUser user);
}
