package com.imooc.mall.service;

import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.vo.CartVO;
import com.imooc.mall.vo.ResponseVO;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param uid 用户d
     * @param form 商品信息
     * @return 购物车信息
     */
    ResponseVO<CartVO> add(Integer uid, CartAddForm form);

    /**
     * 购物车列表
     * @param uid 用户信息
     * @return 购物车列表
     */
    ResponseVO<CartVO> list(Integer uid);

    /**
     * 更新商品
     * @param uid 用户id
     * @param productId 商品id
     * @param form 购物车信息
     * @return 购物车信息
     */
    ResponseVO<CartVO> update(Integer uid, Integer productId, CartUpdateForm form);

    /**
     * 删除购物车商品
     * @param uid 用户id
     * @param productId 商品id
     * @return 购物车信息
     */
    ResponseVO<CartVO> delete(Integer uid, Integer productId);

    /**
     * 全选
     * @param uid 用户id
     * @return 购物车信息
     */
    ResponseVO<CartVO> selectAll(Integer uid);

    /**
     * 全不选
     * @param uid 用户id
     * @return 购物车信息
     */
    ResponseVO<CartVO> unSelectAll(Integer uid);

    /**
     * 获取购物中所有商品数量总和
     * @param uid 用户id
     * @return 数量总和
     */
    ResponseVO<Integer> sum(Integer uid);
}
