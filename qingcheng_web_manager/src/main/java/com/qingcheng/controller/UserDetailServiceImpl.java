package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.service.system.AdminService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * @program: qingchengAdmin
 * @description:访问数据库实现用户认证
 * @author: 张梦雅
 * @create: 2020-04-18 16:01
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Reference
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询管理员
        Map map = new HashMap();
        map.put("loginName",username);
        map.put("status",1);
        List<Admin> list = adminService.findList(map);
        //等于0就是没有查到相应用户
        if(list.size()==0){
            return null;
        }
        //实际项目中此处应该从数据库中根据用户名查询用户的角色列表
        List<GrantedAuthority> grantedAuths =new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        //list.get(0).getPassword() : 更加username查询，然后查出第一条数据
        return new User(username,list.get(0).getPassword(),grantedAuths);
    }
}
