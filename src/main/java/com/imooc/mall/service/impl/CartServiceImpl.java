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
import com.imooc.mall.vo.CartProductVO;
import com.imooc.mall.vo.CartVO;
import com.imooc.mall.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        CartVO cartVo = new CartVO();
        List<CartProductVO> cartProductVOList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);

            //TODO 需要优化，使用mysql里的in
            Product product = productDao.selectByPrimaryKey(productId);
            if (product != null) {
                CartProductVO cartProductVo = new CartProductVO(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                );
                cartProductVOList.add(cartProductVo);

                if (!cart.getProductSelected()) {
                    selectAll = false;
                }

                //计算总价(只计算选中的)
                if (cart.getProductSelected()) {
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }
            }

            cartTotalQuantity += cart.getQuantity();
        }

        //有一个没有选中，就不叫全选
        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVOList);
        return ResponseVO.success(cartVo);
    }

    @Override
    public ResponseVO<CartVO> update(Integer uid, Integer productId, CartUpdateForm form) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
            //没有该商品, 报错
            return ResponseVO.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        Cart cart = gson.fromJson(value, Cart.class);
        if (form.getQuantity() != null && form.getQuantity() >= 0) {
            cart.setQuantity(form.getQuantity());
        }
        if (form.getSelected() != null) {
            cart.setProductSelected(form.getSelected());
        }
        opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVO<CartVO> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
            //没有该商品, 报错
            return ResponseVO.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }

        opsForHash.delete(redisKey, String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVO<CartVO> selectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey,
                    String.valueOf(cart.getProductId()),
                    gson.toJson(cart));
        }

        return list(uid);
    }

    @Override
    public ResponseVO<CartVO> unSelectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey,
                    String.valueOf(cart.getProductId()),
                    gson.toJson(cart));
        }

        return list(uid);
    }

    @Override
    public ResponseVO<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream()
                .map(Cart::getQuantity)
                .reduce(0, Integer::sum);
        return ResponseVO.success(sum);
    }

    @Override
    public List<Cart> listForCart(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(), Cart.class));
        }

        return cartList;
    }
}
