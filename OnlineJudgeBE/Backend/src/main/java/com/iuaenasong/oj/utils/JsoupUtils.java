/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.utils;

import cn.hutool.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import java.io.IOException;
import java.util.Map;

public class JsoupUtils {

    
    public static Connection getConnectionFromUrl(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        // 给url添加参数
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf("?") <= 0) {
                sb.append("?");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            url = sb.toString();
        }
        Connection connection = Jsoup.connect(url);
        // 设置用户代理
        connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
        // 设置超时时间30秒
        connection.timeout(30000);
        // 设置请求头
        if (headers != null) {
            connection.headers(headers);
        }
        return connection;
    }

    
    public static JSONObject getJsonFromConnection(Connection connection) throws IOException {
        // 设置忽略请求类型
        connection.ignoreContentType(true);
        String body = connection.execute().body();
        return new JSONObject(body);
    }

    public static Connection.Response postResponse(Connection connection, Map<String, String> postData) throws IOException {
        connection.data(postData);
        return connection.method(Connection.Method.POST).execute();
    }

    public static Connection.Response getResponse(Connection connection, Map<String, String> getData) throws IOException {
        //添加参数
        if (getData != null) {
            connection.data(getData);
        }
        return connection.method(Connection.Method.GET).execute();
    }
    public static Document getDocument(Connection connection, Map<String, String> getData) throws IOException {
        //添加参数
        if (getData != null) {
            connection.data(getData);
        }
        Document document = connection.get();
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().prettyPrint(false);
        return document;
    }
}
