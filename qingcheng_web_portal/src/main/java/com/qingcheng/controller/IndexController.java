package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description: 用@Controller的原因是这个不返回json数据，直接页面跳转
 * @author: 张梦雅
 * @create: 2020-04-23 19:10
 */
@Controller
public class IndexController {

    @Reference
    private AdService adService;

    @Reference
    private CategoryService categoryService;

    @GetMapping("/index")
    public String index(Model model){
        List<Ad> index_lb = adService.findByPosition("index_lb");
        model.addAttribute("lbt",index_lb);

        //得到三级分类
        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryTree",categoryTree);
        return "index";
    }
}
