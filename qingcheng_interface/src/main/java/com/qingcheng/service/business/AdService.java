package com.qingcheng.service.business;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.business.Ad;

import java.util.*;

/**
 * ad业务逻辑层
 */
public interface AdService {


    public List<Ad> findAll();


    public PageResult<Ad> findPage(int page, int size);


    public List<Ad> findList(Map<String,Object> searchMap);


    public PageResult<Ad> findPage(Map<String,Object> searchMap,int page, int size);


    public Ad findById(Integer id);

    public void add(Ad ad);


    public void update(Ad ad);


    public void delete(Integer id);

    /**
     * 根据广告位置查询广告列表,将广告列表返回给前端
     * @param position
     * @return
     */
    public List<Ad> findByPosition(String position);

    /**
     * 将某个位置的广告存入缓存
     * @param position
     * 将广告轮播图放入缓存(缓存预热)
     * 这种初始化时候调用很麻烦，因此初始化时应该调用将所有广告存入缓存的方法
     */
    public void saveAdToRedisByPosition(String position);

    /**
     * 将全部广告数据存入缓存
     */
    public void saveAllAdToRedis();


}
