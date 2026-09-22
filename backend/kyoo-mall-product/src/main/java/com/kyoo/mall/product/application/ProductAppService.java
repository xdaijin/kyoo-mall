package com.kyoo.mall.product.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyoo.mall.common.BusinessException;
import com.kyoo.mall.product.domain.model.Product;
import com.kyoo.mall.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public Product create(Product product) {
        product.setId(null);
        if (product.getStatus() == null) {
            product.setStatus(Product.STATUS_ON_SALE);
        }
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productRepository.save(product);
        return product;
    }

    public Product update(Long id, Product product) {
        product.setId(id);
        product.setUpdateTime(LocalDateTime.now());
        productRepository.updateById(product);
        return productRepository.findById(id);
    }
}
