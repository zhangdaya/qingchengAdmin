package com.qingcheng.service.goods;

import java.util.Map;

/**
 * @program: qingchengAdmin
 * @description:关键字搜索
 * @author: 张梦雅
 * @create: 2020-05-21 17:02
 */
public interface SkuSearchService {

    public Map search(Map<String,String> searchMap);
}
