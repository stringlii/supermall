package com.litianyi.supermall.thirdparty.component;

import com.litianyi.supermall.thirdparty.util.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/9 11:57 AM
 */
@ConfigurationProperties(prefix = "alibaba.sms")
@Component
@Getter
@Setter
public class SmsComponent {

    private String host;
    private String path;
    private String method;
    private String appcode;
    private Integer expiration;
    private String smsSignId;
    private String templateId;

    public void sendCode(String phone, String code) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", "**code**:" + code + ",**minute**:" + expiration);
        querys.put("smsSignId", smsSignId);
        querys.put("templateId", templateId);
        Map<String, String> bodys = new HashMap<String, String>();

        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        System.out.println(response.toString());
        //获取response的body
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
