package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-04-24 18:36
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SpuService spuService;

    @Reference
    private CategoryService categoryService;

    /**
     * 该注解作用的作用是将我们配置文件的属性读出来
     */
    @Value("${pagePath}")
    private String pagePath;

    @Autowired
    private TemplateEngine templateEngine;

    /**生成商品详细页
     *为什么按照一个sku生成页面传过来却是spuID:
     * 因为实现逻辑是根据spu查出全部sku，这样一次性把spu下的sku全部生成
     * @param spuId
     */
    @GetMapping("/createPage")
    public void createPage(String spuId){
        //查询商品信息
        Goods goods = spuService.findGoodsById(spuId);
        //获取SPU 信息
        Spu spu = goods.getSpu();

        //查询商品分类
        List<String> categoryList = new ArrayList<>();
        categoryList.add( categoryService.findById(spu.getCategory1Id()).getName());
        categoryList.add( categoryService.findById(spu.getCategory2Id()).getName());
        categoryList.add( categoryService.findById(spu.getCategory3Id()).getName());

        //获取sku列表
        List<Sku> skuList = goods.getSkuList();


        //生成SKU地址列表
        Map urlMap=new HashMap();
        for (Sku sku:skuList){
            //Status=0证明这种规格不可选择
            if("1".equals(sku.getStatus())){
                //以规格的JSON字符串作为KEY，以URL地址(是sku的id.html)作为值。
                String specStringNow = JSON.toJSONString(JSON.parseObject(sku.getSpec()), SerializerFeature.MapSortField);
                urlMap.put(specStringNow,sku.getId()+".html");
            }
        }

        //批量创建sku页面（每个SKU为一个页面）
        for (Sku sku:skuList){
            //1、创建上下文和数据模型
            Context context = new Context();
            Map<String,Object> dataModel = new HashMap<>();
            dataModel.put("spu",spu);
            dataModel.put("sku",sku);
            dataModel.put("categoryList",categoryList);
            //SPU图片列表
            dataModel.put("skuImages",spu.getImages().split(","));
            //SKU图片列表
            dataModel.put("spuImages",sku.getImages().split(","));

            //SPU参数列表（显示在下面那块）
            Map paraItems = JSON.parseObject(spu.getParaItems());
            dataModel.put("paraItems",paraItems);
            //当前SKU规格（显示在下面那块）
            Map<String,String> specItems = (Map) JSON.parseObject(sku.getSpec());
            dataModel.put("specItems", specItems);

            //规格选择面板(点击)（显示在上面点击那块）
           // {"颜色":["天空之境","珠光贝母"],"内存":["8GB+64GB","8GB+128GB","8GB+256GB"]}
            //{"颜色":[{'option':'天空之境',checked:true},{'option':'珠光贝母',checked:false}],"
           Map<String,List> specMap = (Map) JSON.parseObject(spu.getSpecItems());
           //map的for循环（集合要for循环的话必须加泛型）
            for (String key:specMap.keySet()){
                //获取value
                List<String> list = specMap.get(key);
                //将"天空之境","珠光贝母"],设置为[{'option':'天空之境',checked:true},{'option':'珠光贝母',checked:false}]
                List<Map> mapList=new ArrayList<>();
                for (String value:list){
                    Map map = new HashMap();
                    map.put("option",value);
                    //判断此规格组合是否是当前SKU的，标记选中状态
                    if(specItems.get(key).equals(value)){
                        map.put("checked",true);
                    }else{
                        map.put("checked",false);
                    }
                    //先查出当前页面spec组合
                   Map<String,String> specMapNow= (Map) JSON.parseObject(sku.getSpec());
                    //点击别的规格时候修改spec,再点击别的就是当前的key和value
                    specMapNow.put(key,value);
                    //将得到的map转换为json字符串
                    String specStringNow = JSON.toJSONString(specMapNow, SerializerFeature.MapSortField);
                    //追加url,因为前面设置以url为value，当get(key)时得到value
                    map.put("url",urlMap.get(specStringNow));
                    mapList.add(map);
                }
                //用新的集合替换原有的集合
                specMap.put(key,mapList);
            }
           dataModel.put("specMap", specMap);

            context.setVariables(dataModel);
            // 2.准备文件
            //先判断文件是否存在,不存在就逐级创建
            File dir = new File(pagePath);
            if (!dir.exists()){
                dir.mkdir();
            }
            //生成目标文件
            File dest = new File(dir,sku.getId()+".html");
            //3.生成页面（用到模板引擎）
            try {
                PrintWriter printWriter = new PrintWriter(dest,"UTF-8");
                templateEngine.process("item",context,printWriter);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
}
