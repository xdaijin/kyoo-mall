package com.kyoo.mall.product.application.command;

import java.math.BigDecimal;

/**
 * 创建商品用例入参。与 UpdateProductCommand 有意不复用（用例独立演化）。
 */
public record CreateProductCommand(
        String name,
        String description,
        String coverImage,
        BigDecimal price,
        Integer stock
) {
}
