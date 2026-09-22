package com.kyoo.mall.product.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyoo.mall.common.Result;
import com.kyoo.mall.product.application.ProductAppService;
import com.kyoo.mall.product.domain.model.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductAppService productAppService;

    /** 商品分页列表（游客可访问，仅返回上架商品） */
    @GetMapping
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") long current,
                                      @RequestParam(defaultValue = "10") long size) {
        return Result.ok(productAppService.pageOnSale(current, size));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productAppService.getOnSaleById(id));
    }

    /** 以下接口需要登录 */
    @PostMapping
    public Result<Product> create(@Valid @RequestBody Product product) {
        return Result.ok(productAppService.create(product));
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return Result.ok(productAppService.update(id, product));
    }
}
