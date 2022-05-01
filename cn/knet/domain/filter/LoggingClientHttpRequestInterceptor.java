package cn.knet.domain.filter;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    int i = 0;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {


        long l = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        long time = System.currentTimeMillis() - l;

        try {
            String className = Thread.currentThread().getStackTrace()[9].getClassName();//调用的类名
            String methodName = Thread.currentThread().getStackTrace()[9].getMethodName();//调用的方法名
            int lineNumber = Thread.currentThread().getStackTrace()[9].getLineNumber();
            String bodystr = new String(body, StandardCharsets.UTF_8);
            StringBuilder inputStringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    inputStringBuilder.append(line);
                    inputStringBuilder.append('\n');
                    line = bufferedReader.readLine();
                }
            }

            String logstr = "\n\n===========================请求开始：================================================" +
                    "\n= Service      : {}" +
                    "\n= URI          : {}" +
                    "\n= Request body : {}" +
                    "\n= Method       : {}" +
                    "\n= Status code  : {}" +
                    "\n= Times        : {}" +
                    "\n= Response body: {}" +
                    "===========================请求结束：================================================\n\n";
            log.info(logstr, className + "." + methodName + ":" + lineNumber, request.getURI(), getJsonStrByQueryUrl(bodystr),
                    request.getMethod(), response.getStatusCode(), time, inputStringBuilder.toString());
        } catch (Exception e) {
            log.info("请求参数处理异常：" + e.getMessage() + ",URI:" + request.getURI());

        }
        return response;
    }


    public String getJsonStrByQueryUrl(String paramStr) {
        //String paramStr = "a=a1&b=b1&c=c1";
        String[] params = paramStr.split("&");
        JSONObject obj = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                try {
                    obj.put(key, URLDecoder.decode(value,"UTF-8"));
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj.toString();
    }
}
