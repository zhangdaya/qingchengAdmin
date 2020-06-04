package com.qingcheng.service.impl;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @program: qingchengAdmin
 * @description:
 * @author: 张梦雅
 * @create: 2020-05-21 16:11
 */
public class RestClientFactory {

   public static RestHighLevelClient getRestHighLevelClient(String hostname,int port){
      HttpHost httpHost = new HttpHost(hostname,port,"http");
      RestClientBuilder builder = RestClient.builder(httpHost);
      return new RestHighLevelClient(builder);
   }
}
