package com.imooc.mall.dao;

import com.imooc.mall.BaseTest;
import com.imooc.mall.pojo.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MallCategoryDaoTest extends BaseTest {

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void testSelectById() {
        Category category = categoryDao.findById(100001);
        System.out.println(category);
    }

    @Test
    public void testQueryById() {
        Category category = categoryDao.queryById(100002);
        System.out.println(category);
    }
}
