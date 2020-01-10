package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVO;

public interface OrderService {

    ResponseVO<OrderVo> create(Integer uid, Integer shippingId);

    ResponseVO<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize);

    ResponseVO<OrderVo> detail(Integer uid, Long orderNo);

    ResponseVO cancel(Integer uid, Long orderNo);

    void paid(Long orderNo);

}
