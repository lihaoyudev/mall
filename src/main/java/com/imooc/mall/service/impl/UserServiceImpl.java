package com.imooc.mall.service.impl;

import cn.hutool.core.lang.Assert;
import com.imooc.mall.dao.UserDao;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public ResponseVO<User> registry(User user) {
        Assert.notNull(user, "用户不能为空");
        Assert.notNull(user.getUsername(), "用户名不能为空");
        Assert.notNull(user.getPassword(), "密码不能为空");
        int countByUsername = userDao.countByUsername(user.getUsername());
        if (countByUsername > 0) {
            return ResponseVO.error(ResponseEnum.USERNAME_EXIST);
        }
        int countByEmail = userDao.countByEmail(user.getEmail());
        if (countByEmail > 0) {
            return ResponseVO.error(ResponseEnum.EMAIL_EXIST);
        }
        user.setRole(RoleEnum.CUSTOMER.getCode());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        int res = userDao.insertSelective(user);
        if (res == 0) {
            return ResponseVO.error(ResponseEnum.ERROR);
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<User> login(String username, String password) {
        Assert.notNull(username, "用户名不能为空");
        Assert.notNull(password, "密码不能为空");
        User user = userDao.selectByUsername(username);
        if (user == null) {
            return ResponseVO.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            //密码错误(返回：用户名或密码错误 )
            return ResponseVO.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVO.success(user);
    }

    private void error() {
        throw new RuntimeException("意外错误");
    }
}
