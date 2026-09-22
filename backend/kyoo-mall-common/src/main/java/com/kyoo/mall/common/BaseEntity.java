package com.kyoo.mall.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 审计字段基类：所有持久化领域对象继承。
 * 字段值由 app 模块的 AuditMetaObjectHandler 在 insert/update 时统一填充，
 * 业务代码（含聚合工厂方法）不再手动设置时间戳。
 * 注：MP 注解为被动元数据，不破坏 common 零 Spring 依赖的约束。
 */
@Getter
@Setter
public abstract class BaseEntity {

    /** 创建时间（insert 时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（insert/update 时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
