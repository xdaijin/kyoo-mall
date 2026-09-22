package com.kyoo.mall.product.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyoo.mall.product.domain.model.Product;

/**
 * 商品仓储接口。
 * 注：分页直接暴露 MyBatis-Plus 的 Page 是有意的务实取舍（避免自造分页对象），
 * 拆微服务/换 ORM 时再收敛为领域分页对象。
 */
public interface ProductRepository {

    /** 分页查询上架商品，按创建时间倒序 */
    Page<Product> pageOnSale(long current, long size);

    Product findById(Long id);

    void save(Product product);

    void updateById(Product product);
}
