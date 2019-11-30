package com.imooc.mall.dao;

import com.imooc.mall.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CategoryDao {

    @Select("select * from mall_category where id = #{id}")
    Category findById(@Param("id") Integer id);

    Category queryById(@Param("id") Integer id);

}
