package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Spu spu){
        spuService.add(spu);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu){
        spuService.update(spu);
        return new Result();
    }

    /**
     *  真删除商品
     * @param id
     */
    @GetMapping("/realdelete")
    public Result realdelete(String id){
        spuService.realdelete(id);
        return new Result();
    }

    /**
     *  假删除商品
     * @param id
     */
    @GetMapping("/falsedelete")
    public Result falsedelete(String id){
        spuService.falsedelete(id);
        return new Result();
    }

    /**
     *  回收商品，修改spu表is_delete字段为0
     * @param id
     */
    @GetMapping("/restore")
    public Result restore(String id){
        spuService.restore(id);
        return new Result();
    }

    @PostMapping("/saveGoods")
    public Result saveGoods(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result();
    }

    @GetMapping("/findGoodsById")
    public Goods findGoodsById(String id){
        Goods goodsById = spuService.findGoodsById(id);
        return goodsById;
    }

    /**
     *因为message值可能过长，所以用一个map传这三个值
     * @param map
     */
    @PostMapping("/audit")
    public Result audit(Map<String,String> map){
        spuService.audit(map.get("id"),map.get("status"),map.get("message"));
        return new Result();
    }

    /**
     * 商品下架
     */
    @GetMapping("/pull")
    public Result pull(String id){
        spuService.pull(id);
        return new Result();
    }

    /**
     * 商品下架
     */
    @GetMapping("/put")
    public Result put(String id){
        spuService.put(id);
        return new Result();
    }

    /**
     * 批量上架
     * 处理后给前端返回处理的条数：返回值int
     */
    @PostMapping("/putMany")
    public Result  putMany(@RequestBody String[] ids){
        int count = spuService.putMany(ids);
        return new Result(0,"上架"+count+"个商品");
    }
}
