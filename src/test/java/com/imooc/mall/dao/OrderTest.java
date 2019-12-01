package com.imooc.mall.dao;

import com.imooc.mall.BaseTest;
import com.imooc.mall.pojo.Order;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderTest extends BaseTest {

    @Autowired
    private OrderDao orderDao;

    @Test
    public void testSelect() {
        Order order = orderDao.selectByPrimaryKey(1);
        System.out.println(order);
    }

}
