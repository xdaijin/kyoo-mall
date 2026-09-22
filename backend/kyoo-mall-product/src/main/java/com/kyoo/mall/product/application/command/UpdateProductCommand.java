package com.kyoo.mall.product.application.command;

import java.math.BigDecimal;

/**
 * 更新商品用例入参。不含 id（路径参数由应用服务方法签名携带）、不含 status（上下架是独立用例）。
 */
public record UpdateProductCommand(
        String name,
        String description,
        String coverImage,
        BigDecimal price,
        Integer stock
) {
}
