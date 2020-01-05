package com.imooc.mall.service;

import com.imooc.mall.pojo.User;
import com.imooc.mall.vo.ResponseVO;

public interface UserService {
    /**
     * 注册
     * @param user 用户信息
     * @return 注册信息
     */
    ResponseVO<User> registry(User user);

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录信息
     */
    ResponseVO<User> login(String username, String password);
}
