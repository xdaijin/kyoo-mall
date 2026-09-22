package com.kyoo.mall.product.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kyoo.mall.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品领域对象（同 SysUser，领域模型与持久化对象合一的务实做法）。
 * 审计字段（createTime/updateTime）在 BaseEntity，由 MetaObjectHandler 统一填充。
 */
@Data
@TableName("product")
public class Product extends BaseEntity {

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

    public boolean isOnSale() {
        return STATUS_ON_SALE == status;
    }

    /**
     * 创建上架商品（工厂方法）：初始状态为上架。
     * 审计时间戳由 MetaObjectHandler 统一填充，此处不设置。
     */
    public static Product createOnSale(String name, String description, String coverImage,
                                       BigDecimal price, Integer stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCoverImage(coverImage);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(STATUS_ON_SALE);
        return product;
    }
}
