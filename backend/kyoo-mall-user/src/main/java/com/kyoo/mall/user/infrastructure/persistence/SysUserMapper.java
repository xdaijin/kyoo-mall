package com.kyoo.mall.user.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kyoo.mall.user.domain.model.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
