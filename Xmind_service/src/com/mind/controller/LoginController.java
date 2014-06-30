package com.mind.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mind.bean.BaseBean;
import com.mind.bean.ErrorBean;
import com.mind.entity.User;
import com.mind.exception.NotLoggedInException;
import com.mind.service.LoginService;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean login(@RequestBody User user, HttpSession session) {
        BaseBean baseBean = new BaseBean();
        try {
            baseBean = loginService.checkUsernameAndPassword(user);
            if (baseBean.isSuccess()) {
                session.setAttribute("username", ((User) baseBean.getResult().get("user")).getUsername());
                session.setAttribute("id", ((User) baseBean.getResult().get("user")).getUserId());
            }
            baseBean.setSuccess(baseBean.isSuccess());
        } catch (Exception e) {
            baseBean.setSuccess(false);
            baseBean.getErrorBeanList().add(new ErrorBean("", "登录失败"));
        }
        return baseBean;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean logout(HttpSession session) {
        BaseBean baseBean = new BaseBean();
        session.invalidate();
        return baseBean;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean register(@RequestBody User user) {
        BaseBean baseBean = new BaseBean();
        try {
            baseBean = loginService.register(user);
        } catch (Exception e) {
            baseBean.setSuccess(false);
            baseBean.getErrorBeanList().add(new ErrorBean("", "注册失败"));
        }
        return baseBean;
    }

    @RequestMapping(value = "/getSessionInfo", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean getSessionInfo(HttpSession session) {
        BaseBean baseBean = new BaseBean();
        try {
            if (session.getAttribute("username") == null) {
                throw new NotLoggedInException();
            } else {
                baseBean.getResult().put("username", session.getAttribute("username"));
            }
        } catch (NotLoggedInException e) {
            throw e;
        } catch (Exception e) {
            baseBean.setSuccess(false);
            baseBean.getErrorBeanList().add(new ErrorBean("", "获取session失败"));
        }
        return baseBean;
    }
}
