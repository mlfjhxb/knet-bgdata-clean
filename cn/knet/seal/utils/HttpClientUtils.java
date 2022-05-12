package cn.knet.seal.utils;

import cn.knet.seal.vo.ResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HttpClientUtils {

    private static PoolingHttpClientConnectionManager connectionManager = null;

    private static HttpClientBuilder httpBuilder = null;

    private static RequestConfig requestConfig = null;

    private static int MAXCONNECTION = 10;

    private static int DEFAULTMAXCONNECTION = 5;

    private static String IP = "cnivi.com.cn";
    private static int PORT = 80;

    private static CloseableHttpClient httpClient = null;

    static {
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                //一、连接目标服务器超时时间：ConnectionTimeout-->指的是连接一个url的连接等待时间
                .setConnectTimeout(5000)
                //二、读取目标服务器数据超时时间：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
                .setSocketTimeout(5000)
                //三、从连接池获取连接的超时时间:ConnectionRequestTimeout
                .setConnectionRequestTimeout(5000)
                .build();

        HttpHost httpHost = new HttpHost(IP, PORT);
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAXCONNECTION);//客户端总并行链接最大数
        connectionManager.setDefaultMaxPerRoute(DEFAULTMAXCONNECTION);//每个主机的最大并行链接数
        connectionManager.setMaxPerRoute(new HttpRoute(httpHost), 20);
        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager);
    }

    public void requestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                //一、连接目标服务器超时时间：ConnectionTimeout-->指的是连接一个url的连接等待时间
                .setConnectTimeout(5000)
                //二、读取目标服务器数据超时时间：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
                .setSocketTimeout(5000)
                //三、从连接池获取连接的超时时间:ConnectionRequestTimeout
                .setConnectionRequestTimeout(5000)
                .build();
        //这个超时可以设置为客户端级别,作为所有请求的默认值：
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        //       httpclient.execute(httppost);的时候可以让httppost直接享受到httpclient中的默认配置.

        /**
         * httpget可以单独地使用新copy的requestConfig请求配置,不会对别的request请求产生影响
         *
         *  Request不会继承客户端级别的请求配置，所以在自定义Request的时候，需要将客户端的默认配置拷贝过去：
         */
        HttpGet httpget = new HttpGet("http://www.apache.org/");
        RequestConfig httpgetRequestConfig = RequestConfig.copy(requestConfig)
                .setProxy(new HttpHost("myotherproxy", 8080))
                .build();
        httpget.setConfig(httpgetRequestConfig);

    }

    public static CloseableHttpClient getConnection() {
        if (httpClient == null) {
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        }
        return httpClient;
    }


    public static HttpUriRequest getRequestMethod(Map<String, Object> mapParams, String url, String method) {
        url = "http://" + url;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (mapParams != null && mapParams.size() > 0) {
            for (String key : mapParams.keySet()) {
                if (mapParams.get(key) instanceof List) {
                    List<String> valueList = (List<String>) mapParams.get(key);
                    for (String tmpValue : valueList) {
                        params.add(new BasicNameValuePair(key, tmpValue));
                    }
                } else {
                    params.add(new BasicNameValuePair(key, mapParams.get(key).toString()));
                }
            }
        }
        HttpUriRequest reqMethod = null;
        if ("post".equals(method)) {
            reqMethod = RequestBuilder.post().setUri(url).setCharset(Consts.UTF_8)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        } else if ("get".equals(method)) {
            reqMethod = RequestBuilder.get().setUri(url).setCharset(Consts.UTF_8)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        }
        return reqMethod;
    }

    public static void main(String args[]) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();

        HttpClientUtils httpClientUtils = new HttpClientUtils();
        httpClientUtils.get("www.e-maxdent.com", map, "utf-8");


    }

    private String getContentByMatcher(String oldStr) {
        oldStr = oldStr.toLowerCase();
        //1.0 正则提取
        if (StringUtils.isNotBlank(oldStr)) {
            Matcher matcher = Pattern.compile("(content.+)", Pattern.CASE_INSENSITIVE).matcher(oldStr);
            while (matcher.find()) { //表示往下遍历
                //截取出来的字符串 [user:name]
                oldStr = matcher.group(0);
            }
        }
        //2.0 内容提取
        oldStr = oldStr.replace(">", "").replace("content", "").replace("=", "").replace("\"", "").replace("'", "");
        return oldStr;
    }

    private String getCharset(String oldStr) {
        oldStr = oldStr.toLowerCase();
        //1.0 正则提取
        if (StringUtils.isNotBlank(oldStr)) {
            Matcher matcher = Pattern.compile("(charset.+)", Pattern.CASE_INSENSITIVE).matcher(oldStr);
            while (matcher.find()) { //表示往下遍历
                //截取出来的字符串 [user:name]
                oldStr = matcher.group(0);
                String x1 = matcher.group(1);
//                System.out.println("matcher1:"+x1 );
            }
        }
        //2.0 内容提取
        oldStr = oldStr.replace(">", "").replace("content", "").replace("=", "").replace("\"", "").replace("'", "").replace("charset", "");
        return oldStr;
    }

    /**
     * 发送 get请求
     */
    public String get(String uriPath, Map<String, Object> mapParams, String readerChcarset) {
        CloseableHttpResponse response = null;
        BufferedReader reader = null;
        CloseableHttpClient httpClient = null;
        ResultMsg resultMsg = null;
        try {
            /**
             *  1.0 创建httpclient
             */

            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            /**
             *  2.0 创建httpget.
             */
            HttpUriRequest httpget = getRequestMethod(mapParams, uriPath, "get");
            System.out.println("<<<" + uriPath + ">>> executing request " + httpget.getURI());
            /**
             * 3.0  执行get请求.
             */
            response = httpClient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 打印响应状态
            if (entity != null) {
                resultMsg = new ResultMsg();
                String page_cnt = "";
                //提前 关键字段
                InputStream inputStream = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(inputStream, readerChcarset));
                StringBuilder sb_title = new StringBuilder();
                StringBuilder sb_cnt = new StringBuilder();
                String line;
                String paget_charset = "utf-8";
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("content-type")) {
                        paget_charset = getCharset(line);
                        if (StringUtils.isBlank(resultMsg.getPageCharset())) {
                            resultMsg.setPageCharset(paget_charset);
                        }
                    }
                    if (line.toLowerCase().contains("<title>")) {
                        if (StringUtils.isBlank(resultMsg.getTitle())) {
                            resultMsg.setTitle(line.toLowerCase().replace("<title>", "").replace("</title>", ""));
                        }
                    }
                    if (line.toLowerCase().contains("kyewords")||line.toLowerCase().contains("keywords")) {
                        if (StringUtils.isBlank(resultMsg.getKeyword())) {
                            resultMsg.setKeyword(getContentByMatcher(line));
                        }
                    }
                    if (line.toLowerCase().contains("description")) {
                        if (StringUtils.isBlank(resultMsg.getDescription())) {
                            resultMsg.setDescription(getContentByMatcher(line));
                        }
                    }
                    sb_cnt.append(line).append("\n");
                }
                if (StringUtils.isNotBlank(sb_cnt.toString())) {
                    if (sb_cnt.toString().length() > 3000) {
                        page_cnt = sb_cnt.toString().substring(0, 3000);
                    } else {
                        page_cnt = sb_cnt.toString();
                    }
                }
                resultMsg.setPageCnt(page_cnt);
                resultMsg.setCode(200);
                if (StringUtils.isBlank(resultMsg.getTitle()) && StringUtils.isBlank(resultMsg.getKeyword()) && StringUtils.isBlank(resultMsg.getDescription())) {
                    resultMsg.setCode(300);
                }
                resultMsg.setMsg("success");
                log.info(String.format("<<<%s>>> code:[%s] getContentType:[%s] getContentEncoding:[%s]  resultMsg:[%s]", uriPath, response.getStatusLine(), entity.getContentType(), entity.getContentEncoding(), resultMsg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMsg = new ResultMsg(500, e.getMessage(), "", "", "", "", "");
        } finally {
            // 关闭连接,释放资源
            try {
                if (reader != null) {
                    reader.close();
                }
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                    httpClient = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject(resultMsg).toString();
    }


    /**
     * post方式提交表单（模拟用户登录请求）
     */
    public void postForm(Map<String, Object> mapParams) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://localhost:8080/myDemo/Ajax/serivceJ.action");
//        httppost.addHeader("Content-Type", "”application/json;charset=UTF-8");
        // 创建参数队列
        //设置参数对 以key-value的方式进行保存
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (String key : mapParams.keySet()) {
            if (mapParams.get(key) instanceof List) {
                List<String> valueList = (List<String>) mapParams.get(key);
                for (String tmpValue : valueList) {
                    nvps.add(new BasicNameValuePair(key, tmpValue));
                }
            } else {
                nvps.add(new BasicNameValuePair(key, mapParams.get(key).toString()));
            }
        }
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");

            StringEntity stringEntity = new StringEntity(uefEntity.toString(), ContentType.APPLICATION_JSON);// 第二个参数，设置后才会对内容进行编码

            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
