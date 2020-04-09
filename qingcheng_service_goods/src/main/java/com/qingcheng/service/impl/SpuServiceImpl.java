package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private AuditStatusMapper auditStatusMapper;
    private Spu spu;

    /**
     * 返回全部记录
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 保存商品(新增和修改)
     * @param goods 商品组合实体类
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(Goods goods) {
        //保存一个spu的信息:步骤：
        //①get方法得到spu实体  Spu spu = goods.getSpu();
        //②因为不用自增ID用的是（雪花算法）分布式ID spu.setId(idWorker.nextId()+"");
        // ③将值存入数据库
        Spu spu = goods.getSpu();

        if (spu.getId()==null){
            //增加
            spu.setId(idWorker.nextId()+"");
            spuMapper.insert(spu);
        }else{
            //修改
            //①删除原来的(spu下的的sku列表)sku列表（这样修改sku表就相当于再一次增加，主要能使其合并）
            Example example=new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId",spu.getId());
            skuMapper.deleteByExample(example);
            //②对spu执行updateByPrimaryKeySelective操作
            //updateByPrimaryKeySelective:如果修改数据有null，则不会被覆盖，而且展示原来的值
            spuMapper.updateByPrimaryKeySelective(spu);
        }

        List<Sku> skus = goods.getSkuList();
        Date date = new Date();
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //保存sku列表的信息
        for (Sku sku:skus){
            if (sku.getId()==null){
                //增加
                sku.setId(idWorker.nextId()+"");
                //创建日期
                sku.setCreateTime(date);
            }
            sku.setSpuId(spu.getId());

            //sku名称(name)：spu名称(name)+sku的规格（spec）
            String name = spu.getName();

            String skuSpec = sku.getSpec();
            //没有规格（spec）属性，我们要对其进行判断，避免因空值产生
            if(skuSpec==null||"".equals(skuSpec)){
                sku.setSpec("{}");
            }

            //将json转换成map
            //json : {"颜色":"绿","机身内存":"8G"}
            //map:{key:颜色；value：绿}
            Map<String ,String> skuSpecMap = JSON.parseObject(skuSpec, Map.class);
            for (String value:skuSpecMap.values()){
                name+=" "+value;
            }
            //sku名称
            sku.setName(name);
            //修改日期
            sku.setUpdateTime(date);
            //分类ID
            sku.setCategoryId(spu.getCategory3Id());
            //分类名称
            sku.setCategoryName(category.getName());
            //评论数
            sku.setCommentNum(0);
            //销售数量
            sku.setSaleNum(0);
            skuMapper.insert(sku);

            //=======================================================================
            //建立分类和品牌关联
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandId(spu.getBrandId());
            categoryBrand.setCategoryId(spu.getCategory3Id());
            //注意问题：反复添加主键重复，因此加一个判断条件(判断是否有这条数据)
            int count = categoryBrandMapper.selectCount(categoryBrand);
            if (count==0){
                categoryBrandMapper.insert(categoryBrand);
            }
        }
    }


    /**
     * 查询商品id
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public Goods findGoodsById(String id) {
        //查询spu的id
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //查询sku的id
        /*下面四句话相当于这个sql查询
          select *
          from tb_sku
          where spu_id='1247483467168616448'
        */
        Example example=new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        //封装为Goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 商品审核(audit)
     * @param id,status,message
     */
    public void audit(String id, String status, String message) {
        //①修改审核状态，如果审核状态为1则上架状态也修改为1
        //Spu spu = spuMapper.selectByPrimaryKey(id);
        //方法可行但是查询数据量大，效率低。所以new一个对象
        Spu spu = new Spu();
        spu.setId(id);
        spu.setStatus(status);
        if ("1".equals(status)){
            spu.setIsMarketable("1");
        }
        spuMapper.updateByPrimaryKeySelective(spu);

        //2.记录商品审核记录
        Date date = new Date();
        AuditStatus auditStatus = new AuditStatus();
        auditStatus.setSpuId(id);
        auditStatus.setAuditMessage(message);
        auditStatus.setAuditTime(date);
        auditStatus.setAuditStatus(status);
        auditStatus.setAuditUser("admin");

        //3.记录商品日志
    }

    /**
     * 商品下架
     */
    public void pull(String id) {
        //1、商品下架
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
        //2.记录商品日志
    }

    /**
     * 商品上架
     */
    public void put(String id) {
        //1、商品上架
        //需要验证该商品是否审核通过，未审核通过的商品不能上架。
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (!"1".equals(spu.getStatus())){
            throw new RuntimeException("此商品未审核通过，不能上架");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
        //2.记录商品日志
    }

    /**
     * 批量上架
     * 处理后给前端返回处理的条数：返回值int
     * 未审核通过的商品不能上架
     */
    public int putMany(String[] ids) {
        //1.修改状态
        Spu spu = new Spu();
        //相当于：update spu set is_marketable='1' where id=ids and status='1' and ....
        //意思是将满足这些条件的行的is_marketable这一列值全部改为1
        spu.setIsMarketable("1");
        //批量修改
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //数组转换为集合List ：Arrays.asList(ids)
        criteria.andIn("id",Arrays.asList(ids));
        //未审核通过的商品不能上架
        criteria.andEqualTo("status",1);
        //非删除的
        criteria.andEqualTo("isDelete",0);
        //只考虑把下架(0)的商品做上架(1)操作
        criteria.andEqualTo("isMarketable",0);
        //用这个返回值是int
        int i = spuMapper.updateByExampleSelective(spu,example);
        //2.记录商品日志
        return i;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
            }
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
            }
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
            }
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
            }
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
            }
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andLike("isMarketable","%"+searchMap.get("isMarketable")+"%");
            }
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andLike("isEnableSpec","%"+searchMap.get("isEnableSpec")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
