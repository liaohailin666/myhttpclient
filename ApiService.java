package com.mytaotao.web.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mytaotao.web.httpclient.HttpResult;

@Service
public class ApiService implements BeanFactoryAware{

	//注入http实例对象  由于 httpclient是多例.所以不能直接注入
	//@Autowired
	//private CloseableHttpClient httpClient;
	//注入beanfactory在spring容器中取 httpclient
	@Autowired
	private BeanFactory beanFactory;
	
	//注入请求参数对象
	@Autowired
	private RequestConfig requestConfig;
	
	/**
	 * 没有参数的doGet请求
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String doGet(String url) throws ClientProtocolException, IOException{

        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        //设置参数对象
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getBeanFactory().execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                //执行成功,返回数据
                return content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        //如果失败就返回null
        return null;

	}
	
	/**
	 * 带有参数的doGet请求
	 * @param url
	 * @param params
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws URISyntaxException 
	 */
	public String doGet(String url,Map<String,String> params) throws ClientProtocolException, IOException, URISyntaxException{
        // 定义请求的参数
        URIBuilder uri = new URIBuilder(url);
        for (Map.Entry<String, String> param : params.entrySet()) {
			uri.setParameter(param.getKey(), param.getValue());
		}
        return this.doGet(uri.build().toString());

	}
	
	/**
	 * 没有带入参数 的doPost
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public HttpResult doPost(String url) throws ClientProtocolException, IOException{
	  return  doPost(url, null);	
	}
	
	/**
	 * 带入参数 的doPost
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public HttpResult doPost(String url,Map<String,String> params) throws ClientProtocolException, IOException{

        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        //判断params是否为null
        if(params!=null){
        	 // 设置post参数
        	List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        	for (Map.Entry<String, String> param : params.entrySet()) {
        		parameters.add(new BasicNameValuePair(param.getKey(),param.getValue()));
			}
        	 // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);

        }
        CloseableHttpResponse response = null;
        try {
        	response = getBeanFactory().execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                    response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
	}
	//获取httpclient实例
	public CloseableHttpClient  getBeanFactory(){
		return this.beanFactory.getBean(CloseableHttpClient.class);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		 this.beanFactory = beanFactory;
	}
}
