package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuSearchService;
import com.qingcheng.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description:用Thymeleaf模板技术
 * @author: 张梦雅
 * @create: 2020-05-28 17:09
 */
@Controller
public class SearchController {

    @Reference
    private SkuSearchService skuSearchService;

    /**
     * themleaf模板渲染技术
     * get请求，为了防止传过来的参数有中文乱码，所以要进行字符集处理
     * */
    @GetMapping("/search")
    public String search(Model model,@RequestParam Map<String, String> searchMap) throws Exception {
        //字符集处理
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);
        Map result = skuSearchService.search(searchMap);
        model.addAttribute("result",result);
        return "search";
    }
}
