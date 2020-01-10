package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.ProductDao;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.ProductService;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ProductVo;
import com.imooc.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductDao productDao;

    @Override
    public ResponseVO<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productDao.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVOList = productList.stream().map(e -> {
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(e, productVo);
            return productVo;
        }).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productVOList);
        return ResponseVO.success(pageInfo);
    }

    @Override
    public ResponseVO<ProductDetailVo> detail(Integer productId) {
        Product product = productDao.selectByPrimaryKey(productId);
        if (product.getStatus().equals(ProductStatusEnum.OFF_SALE.getCode()) ||
                product.getStatus().equals(ProductStatusEnum.DELETE.getCode())) {
            return ResponseVO.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);
        //敏感数据处理
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
        return ResponseVO.success(productDetailVo);
    }
}
