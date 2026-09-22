package com.kyoo.mall.product.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyoo.mall.common.BusinessException;
import com.kyoo.mall.product.application.command.CreateProductCommand;
import com.kyoo.mall.product.application.command.UpdateProductCommand;
import com.kyoo.mall.product.domain.model.Product;
import com.kyoo.mall.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商品应用服务：用例编排。入参为 Command（由 interfaces 层从 Request 转换），
 * 不直接接收领域对象——id、status、时间戳等由服务端管理。
 */
@Service
@RequiredArgsConstructor
public class ProductAppService {

    private final ProductRepository productRepository;

    public Page<Product> pageOnSale(long current, long size) {
        return productRepository.pageOnSale(current, size);
    }

    public Product getOnSaleById(Long id) {
        Product product = productRepository.findById(id);
        if (product == null || !product.isOnSale()) {
            throw new BusinessException("商品不存在或已下架");
        }
        return product;
    }

    public Product create(CreateProductCommand command) {
        Product product = Product.createOnSale(command.name(), command.description(),
                command.coverImage(), command.price(), command.stock());
        productRepository.save(product);
        return product;
    }

    public Product update(Long id, UpdateProductCommand command) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setName(command.name());
        product.setDescription(command.description());
        product.setCoverImage(command.coverImage());
        product.setPrice(command.price());
        product.setStock(command.stock());
        productRepository.updateById(product);
        return productRepository.findById(id);
    }
}
