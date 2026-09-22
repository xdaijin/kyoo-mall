package com.kyoo.mall.product.interfaces.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 商品创建/更新请求（HTTP 协议模型）。只暴露客户端允许提交的字段：
 * id、status、createTime/updateTime 等由服务端管理，不接受客户端传入。
 */
public record ProductRequest(
        @NotBlank(message = "商品名称不能为空") String name,
        String description,
        String coverImage,
        @NotNull(message = "价格不能为空") @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于 0") BigDecimal price,
        @NotNull(message = "库存不能为空") @Min(value = 0, message = "库存不能为负数") Integer stock
) {
}
