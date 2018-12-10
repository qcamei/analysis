package net.mofancy.analysis.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

public class WebUtils {
	
    private static SSLConnectionSocketFactory SSL_SF = null;
    
    static {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(
                new TrustStrategy() {
                    //信任所有
                    public boolean isTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        return true;
                    }
                }).build();
        } catch (KeyManagementException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
        }
        SSL_SF = new SSLConnectionSocketFactory(sslContext);
    }
    
    public static String get(String url) throws IOException {
    	if (isHttps(url)) {
    		return httpsGet(url);
    	} else {
    		return httpGet(url);
    	}
    }
    
    private static String httpGet(String url) throws IOException {
    	String respStr = null;
    	try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
    		HttpGet get = new HttpGet(url);
    		try (CloseableHttpResponse response = httpclient.execute(get)) {
    			HttpEntity entity = response.getEntity();
		        respStr = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
    		}
    	}
    	return respStr;
    }
    
    private static String httpsGet(String url) throws IOException {
    	String respStr = null;
    	try (CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(SSL_SF).build()) {
    		HttpGet get = new HttpGet(url);
    		try (CloseableHttpResponse response = httpclient.execute(get)) {
    			HttpEntity entity = response.getEntity();
		        respStr = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
    		}
    	}
    	return respStr;
    }
    
    public static String postForm(String url, Map<String, String> params) throws IOException {
    	if (isHttps(url)) {
    		return httpsPostForm(url, params, null);
    	} else {
    		return httpPostForm(url, params, null);
    	}
    }
    
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
    	if (isHttps(url)) {
    		return httpsPostForm(url, params, headers);
    	} else {
    		return httpPostForm(url, params, headers);
    	}
    }
    
    private static String httpPostForm(String url, Map<String, String> mapParam, Map<String, String> headers) throws IOException {
        String respStr = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            if ((headers != null) && (!headers.isEmpty())) {
            	for (Entry<String, String> entry : headers.entrySet()) {
                	post.setHeader(entry.getKey(), entry.getValue()); // 定义头部信息
                }
            }
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            Set<String> keys = mapParam.keySet();
            for (String key : keys) {
                nvps.add(new BasicNameValuePair(key, mapParam.get(key)));
            }
            post.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = httpclient.execute(post)) {
            	HttpEntity entity = response.getEntity();
            	respStr = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            }
        }
        return respStr;
    }
    
    private static String httpsPostForm(String url, Map<String, String> mapParam, Map<String, String> headers) throws IOException {
        String respStr = null;
        try (CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(SSL_SF).build()) {
            HttpPost post = new HttpPost(url);
            if ((headers != null) && (!headers.isEmpty())) {
            	for (Entry<String, String> entry : headers.entrySet()) {
                	post.setHeader(entry.getKey(), entry.getValue()); // 定义头部信息
                }
            }
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            Set<String> keys = mapParam.keySet();
            for (String key : keys) {
                nvps.add(new BasicNameValuePair(key, mapParam.get(key)));
            }
            post.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = httpclient.execute(post)) {
            	HttpEntity entity = response.getEntity();
            	respStr = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            }
        }
        return respStr;
    }
    
    public static String postXml(String url, String xml) throws IOException {
    	Map<String, String> headers = new HashMap<>();
    	headers.put("Content-Type", "text/xml");
        return postString(url, xml, headers);
    }
    
    public static String postJson(String url, String json) throws IOException {
    	Map<String, String> headers = new HashMap<>();
    	headers.put("Content-Type", "application/json");
        return postString(url, json, headers);
    }
    
    /**
     * @param url 
     * @param content 发送的文本内容
     * @param headers 自定义的头部信息, 如 Content-Type
     * @return
     * @throws IOException 
     * @throws UnsupportedOperationException 
     */
    public static String postString(String url, String content, Map<String, String> headers) throws IOException {
    	CloseableHttpClient httpclient = null;
    	if (isHttps(url)) {
    		httpclient = HttpClients.custom().setSSLSocketFactory(SSL_SF).build();
    	} else {
    		httpclient = HttpClients.createDefault();
    	}
    	HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "text/plain");
        if ((headers != null) && (!headers.isEmpty())) {
        	for (Entry<String, String> entry : headers.entrySet()) {
        		post.setHeader(entry.getKey(), entry.getValue()); // 定义头部信息
        	}
        }
        // 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(content, StandardCharsets.UTF_8);
        post.setEntity(postEntity);
        String respStr = null;
    	CloseableHttpResponse response = null;
    	try {
	        response = httpclient.execute(post);
	        HttpEntity entity = response.getEntity();
	        respStr = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
    	} finally {
    	    try {
    	        response.close();
    	    } catch (IOException e) {}
    	    try {
                httpclient.close();
            } catch (IOException e) {}
    	}
    	return respStr;
    }
    
    private static Boolean isHttps(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        if (url.length()<8) {
            return false;
        }
        if ("https://".equalsIgnoreCase(url.substring(0, 8))) {
            return true;
        } else {
            return false;
        }
    }
    
    public static Map<String, String> getParameterMap(HttpServletRequest request) {  
        // 参数Map  
        Map<String,String[]> properties = request.getParameterMap();  
        // 返回值Map  
        Map<String, String>  returnMap = new HashMap<String, String>();  
        Iterator<Entry<String, String[]>>  entries = properties.entrySet().iterator();  
        Map.Entry<String, String[]>  entry;  
        String name = "";  
        String value = "";  
        while (entries.hasNext()) {  
            entry = (Map.Entry<String, String[]> ) entries.next();  
            name = (String) entry.getKey();  
            Object valueObj = entry.getValue();  
            if(null == valueObj){  
                value = "";  
            }else if(valueObj instanceof String[]){  
                String[] values = (String[])valueObj;  
                for(int i=0;i<values.length;i++){  
                    value = values[i] + ",";  
                }  
                value = value.substring(0, value.length()-1);  
            }else{  
                value = valueObj.toString();  
            }  
            returnMap.put(name, value);  
        }  
        return returnMap;  
    }  
    
}
