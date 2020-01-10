package com.imooc.mall.service.impl;

import com.imooc.mall.constant.MallConst;
import com.imooc.mall.dao.CategoryDao;
import com.imooc.mall.pojo.Category;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ResponseVO<List<CategoryVo>> selectAll() {
        List<Category> categories = categoryDao.selectAll();

        List<CategoryVo> categoryVOList = categories.stream().filter(e -> e.getParentId().equals(MallConst.ROOT_PARENT_ID))
                .map(this::category2CategoryVo)
                .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect(Collectors.toList());

        // 查询子目录
        findSubCategory(categoryVOList, categories);
        return null;
    }

    private void findSubCategory(List<CategoryVo> categoryVOList, List<Category> categories) {
        for (CategoryVo categoryVo : categoryVOList) {
            List<CategoryVo> subCategoryVoList = new ArrayList<>();

            for (Category category : categories) {
                //如果查到内容，设置subCategory, 继续往下查
                if (categoryVo.getId().equals(category.getParentId())) {
                    CategoryVo subCategoryVo = category2CategoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }

                subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
                categoryVo.setSubCategories(subCategoryVoList);

                findSubCategory(subCategoryVoList, categories);
            }
        }
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryDao.selectAll();
        findSubCategoryId(id, resultSet, categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)) {
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(), resultSet, categories);
            }
        }
    }

    private CategoryVo category2CategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }
}
