package com.imooc.mall.service;

import com.imooc.mall.BaseTest;
import com.imooc.mall.form.CartAddForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CartServiceTest extends BaseTest {
    @Autowired
    private CartService cartService;

    private Integer uid = 1;

    private Integer productId = 26;

    @Test
    public void addTest() {
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        cartService.add(uid, form);
    }
}
