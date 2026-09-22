package com.kyoo.mall.product.interfaces;

import com.kyoo.mall.common.PageQuery;
import com.kyoo.mall.common.PageResult;
import com.kyoo.mall.common.Result;
import com.kyoo.mall.product.application.command.CreateProductCommand;
import com.kyoo.mall.product.application.command.UpdateProductCommand;
import com.kyoo.mall.product.application.service.ProductAppService;
import com.kyoo.mall.product.domain.model.Product;
import com.kyoo.mall.product.interfaces.dto.ProductRequest;
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
    public Result<PageResult<Product>> page(@RequestParam(defaultValue = "1") long current,
                                            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(productAppService.pageOnSale(PageQuery.of(current, size)));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productAppService.getOnSaleById(id));
    }

    /** 以下接口需要登录 */
    @PostMapping
    public Result<Product> create(@Valid @RequestBody ProductRequest request) {
        return Result.ok(productAppService.create(new CreateProductCommand(
                request.name(), request.description(), request.coverImage(),
                request.price(), request.stock())));
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return Result.ok(productAppService.update(id, new UpdateProductCommand(
                request.name(), request.description(), request.coverImage(),
                request.price(), request.stock())));
    }
}
