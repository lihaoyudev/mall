package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.imooc.mall.dao.ProductDao;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.pojo.Cart;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.CartService;
import com.imooc.mall.vo.CartVO;
import com.imooc.mall.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    private Gson gson = new Gson();

    @Autowired
    private ProductDao productDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseVO<CartVO> add(Integer uid, CartAddForm form) {
        Integer quantity = 1;
        Product product = productDao.selectByPrimaryKey(form.getProductId());
        if (product == null) {
            return ResponseVO.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            return ResponseVO.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        if (product.getStock() <= 0) {
            return ResponseVO.error(ResponseEnum.PROODUCT_STOCK_ERROR);
        }
        // 写入redis
        // key: cart_1
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        if (StringUtils.isEmpty(value)) {
            cart = new Cart(product.getId(), quantity, form.getSelected());
        } else {
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
        }
        opsForHash.put(redisKey, String.valueOf(product.getId()), gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVO<CartVO> list(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        return null;
    }

    @Override
    public ResponseVO<CartVO> update(Integer uid, Integer productId, CartUpdateForm form) {
        return null;
    }

    @Override
    public ResponseVO<CartVO> delete(Integer uid, Integer productId) {
        return null;
    }

    @Override
    public ResponseVO<CartVO> selectAll(Integer uid) {
        return null;
    }

    @Override
    public ResponseVO<CartVO> unSelectAll(Integer uid) {
        return null;
    }

    @Override
    public ResponseVO<Integer> sum(Integer uid) {
        return null;
    }
}
