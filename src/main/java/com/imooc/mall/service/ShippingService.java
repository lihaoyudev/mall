package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.vo.ResponseVO;

import java.util.Map;

public interface ShippingService {

    /**
     * 新增收货地址
     *
     * @param uid  用户id
     * @param form 收货地址信息
     * @return 收货地址新增信息
     */
    ResponseVO<Map<String, Integer>> add(Integer uid, ShippingForm form);

    ResponseVO delete(Integer uid, Integer shippingId);

    ResponseVO update(Integer uid, Integer shippingId, ShippingForm form);

    ResponseVO<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize);

}
