package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.util.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @program: qingchengAdmin
 * @description:登录成功处理器:管理员登录后，记录管理员名称、登录时间、ip、浏览器类型、所在地区等信息
 * @author: 张梦雅
 * @create: 2020-04-18 21:34
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        LoginLog loginLog = new LoginLog();
        String LoginName = authentication.getName();
        //获取远程地址（ip地址）
        String ip = request.getRemoteAddr();
        String agent = request.getHeader("user-agent");
        //登录用户名
        loginLog.setLoginName(LoginName);
        //获取访问的ip
        loginLog.setIp(ip);
        //浏览器名称
        loginLog.setBrowserName(WebUtil.getBrowserName(agent));
        //通过ip获取城市名称
        loginLog.setLocation(WebUtil.getCityByIP(ip));
        //当前登录时间
        loginLog.setLoginTime(new Date());
        loginLogService.add(loginLog);
        request.getRequestDispatcher("/main.html").forward(request, response);
    }
}
