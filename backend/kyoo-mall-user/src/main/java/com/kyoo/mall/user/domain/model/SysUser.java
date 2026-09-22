package com.kyoo.mall.user.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyoo.mall.common.BaseEntity;
import lombok.Data;

/**
 * 用户领域对象。
 * 务实做法：领域模型直接带 MyBatis-Plus 注解兼任持久化对象，避免 DO/DTO 多层转换样板代码；
 * 表结构复杂化后如需拆分，在 infrastructure 层引入独立 PO 即可。
 * 审计字段（createTime/updateTime）在 BaseEntity，由 MetaObjectHandler 统一填充。
 */
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {

    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DISABLED = 0;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String nickname;

    private String email;

    /** 1 正常，0 禁用，见 STATUS_* 常量 */
    private Integer status;

    public boolean isEnabled() {
        return STATUS_ENABLED == status;
    }

    /**
     * 注册新用户（工厂方法）：昵称缺省取用户名，初始状态为正常。
     * 审计时间戳由 MetaObjectHandler 统一填充，此处不设置。
     */
    public static SysUser register(String username, String encodedPassword, String nickname) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setNickname(nickname == null || nickname.isBlank() ? username : nickname);
        user.setStatus(STATUS_ENABLED);
        return user;
    }
}
