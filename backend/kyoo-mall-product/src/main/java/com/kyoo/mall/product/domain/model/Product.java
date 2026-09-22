package com.kyoo.mall.product.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品领域对象（同 SysUser，领域模型与持久化对象合一的务实做法）。
 */
@Data
@TableName("product")
public class Product {

    public static final int STATUS_ON_SALE = 1;
    public static final int STATUS_OFF_SALE = 0;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String coverImage;

    private BigDecimal price;

    private Integer stock;

    /** 1 上架，0 下架，见 STATUS_* 常量 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public boolean isOnSale() {
        return STATUS_ON_SALE == status;
    }
}
