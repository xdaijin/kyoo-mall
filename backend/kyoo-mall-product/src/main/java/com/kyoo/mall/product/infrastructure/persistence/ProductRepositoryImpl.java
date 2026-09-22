package com.kyoo.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyoo.mall.product.domain.model.Product;
import com.kyoo.mall.product.domain.repository.ProductRepository;
import com.kyoo.mall.product.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;

    @Override
    public Page<Product> pageOnSale(long current, long size) {
        return productMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, Product.STATUS_ON_SALE)
                        .orderByDesc(Product::getCreateTime));
    }

    @Override
    public Product findById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public void save(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void updateById(Product product) {
        productMapper.updateById(product);
    }
}
