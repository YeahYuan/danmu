package com.allensu.danmu.utils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTools {
    public static String get(String url) {
        String resultContent = null;
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                // 获取状态码
//                System.out.println(response.getVersion()); // HTTP/1.1
//                System.out.println(response.getCode()); // 200
//                System.out.println(response.getReasonPhrase()); // OK
                HttpEntity entity = response.getEntity();
                // 获取响应信息
                resultContent = EntityUtils.toString(entity);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return resultContent;
    }

    public static String post(String url) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        // 表单参数
        List<NameValuePair> nvps = new ArrayList<>();
        // POST 请求参数
        nvps.add(new BasicNameValuePair("pp_id", "8303"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
//                System.out.println(response.getVersion()); // HTTP/1.1
//                System.out.println(response.getCode()); // 200
//                System.out.println(response.getReasonPhrase()); // OK

                HttpEntity entity = response.getEntity();
                // 获取响应信息
                result = EntityUtils.toString(entity);
                // 确保流被完全消费
                EntityUtils.consume(entity);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
