package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ResponseVO;

public interface ProductService {

    /**
     * 商品列表（分页）
     *
     * @param categoryId 类别id
     * @param pageNum    页数
     * @param pageSize   每页数量
     * @return 分页商品信息
     */
    ResponseVO<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    /**
     * 商品详情
     *
     * @param productId 商品id
     * @return 商品信息
     */
    ResponseVO<ProductDetailVo> detail(Integer productId);
}
