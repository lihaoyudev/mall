package com.imooc.mall.service.impl;

import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.ShippingMapper;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.pojo.Shipping;
import com.imooc.mall.service.ShippingService;
import com.imooc.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVO<Map<String, Integer>> add(Integer uid, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form, shipping);
        shipping.setUserId(uid);
        int row = shippingMapper.insertSelective(shipping);
        if (row == 0) {
            return ResponseVO.error(ResponseEnum.ERROR);
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("shippingId", shipping.getId());
        return ResponseVO.success(map);
    }

    @Override
    public ResponseVO delete(Integer uid, Integer shippingId) {
        Assert.notNull(uid, "用户id不能为空");
        Assert.notNull(shippingId, "收货地址id不能为空");
        int row = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if (row == 0) {
            return ResponseVO.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }

        return ResponseVO.success();
    }

    @Override
    public ResponseVO update(Integer uid, Integer shippingId, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form, shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (row == 0) {
            return ResponseVO.error(ResponseEnum.ERROR);
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo(shippings);
        return ResponseVO.success(pageInfo);
    }
}
