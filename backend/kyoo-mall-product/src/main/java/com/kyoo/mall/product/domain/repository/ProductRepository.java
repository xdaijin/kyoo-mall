package com.kyoo.mall.product.domain.repository;

import com.kyoo.mall.common.PageQuery;
import com.kyoo.mall.common.PageResult;
import com.kyoo.mall.product.domain.model.Product;

/**
 * 商品仓储接口。
 * 分页契约使用 common 的 PageQuery/PageResult（技术无关），
 * MyBatis-Plus 的 Page 只允许出现在 infrastructure 的仓储实现内部。
 */
public interface ProductRepository {

    /** 分页查询上架商品，按创建时间倒序 */
    PageResult<Product> pageOnSale(PageQuery query);

    Product findById(Long id);

    void save(Product product);

    void updateById(Product product);
}
