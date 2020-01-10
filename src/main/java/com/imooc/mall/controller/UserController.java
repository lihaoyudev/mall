package com.imooc.mall.controller;

import com.imooc.mall.constant.MallConst;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.UserLoginForm;
import com.imooc.mall.form.UserRegisterForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

import static com.imooc.mall.enums.ResponseEnum.PARAM_ERROR;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/registry")
    public ResponseVO<User> registry(@Valid @RequestBody UserRegisterForm userRegisterForm,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("注册提交的参数有误, {} {}",
                    Objects.requireNonNull(bindingResult.getFieldError()).getField(),
                    bindingResult.getFieldError().getDefaultMessage());
            return ResponseVO.error(PARAM_ERROR, bindingResult);
        }
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user);
        return userService.registry(user);
    }

    @PostMapping("/user/login")
    public ResponseVO<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  BindingResult bindingResult,
                                  HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseVO.error(PARAM_ERROR, bindingResult);
        }

        ResponseVO<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        //设置Session
        session.setAttribute(MallConst.CURRENT_USER, userResponseVo.getData());
        log.info("/login sessionId={}", session.getId());

        return userResponseVo;
    }

    @GetMapping("/user")
    public ResponseVO<User> userInfo(HttpSession session) {
        log.info("/user sessionId={}", session.getId());
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return ResponseVO.success(user);
    }

    @PostMapping("/user/logout")
    public ResponseVO logout(HttpSession session) {
        log.info("/user/logout sessionId={}", session.getId());
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVO.success();
    }
}
