package com.kyoo.mall.user.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户领域对象。
 * 务实做法：领域模型直接带 MyBatis-Plus 注解兼任持久化对象，避免 DO/DTO 多层转换样板代码；
 * 表结构复杂化后如需拆分，在 infrastructure 层引入独立 PO 即可。
 */
@Data
@TableName("sys_user")
public class SysUser {

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public boolean isEnabled() {
        return STATUS_ENABLED == status;
    }
}
