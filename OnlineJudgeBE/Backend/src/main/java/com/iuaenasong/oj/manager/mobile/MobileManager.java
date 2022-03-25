/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.mobile;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Slf4j(topic = "oj")
public class MobileManager {

    @Value("${oj.mobile.regionId}")
    public String regionId;

    @Value("${oj.mobile.accessKeyId}")
    public String accessKeyId;

    @Value("${oj.mobile.secret}")
    public String secret;

    @Value("${oj.mobile.domain}")
    public String domain;

    @Value("${oj.mobile.signName}")
    public String signName;

    @Value("${oj.mobile.templateCode}")
    public String templateCode;

    private CommonRequest getMobileRequest() {

        CommonRequest request = new CommonRequest();//组装请求对象
        request.setMethod(MethodType.POST);
        request.setDomain(domain);
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("SignName", signName);//短信签名
        request.putQueryParameter("TemplateCode", templateCode);

        return request;
    }

    public boolean isOk() {
        return accessKeyId != null && secret != null && templateCode != null
                &&!accessKeyId.equals("your_mobile_access_key_id") && !secret.equals("your_mobile_secret")
                && !templateCode.equals("your_mobile_template_code");
    }

    @Async
    public void sendCode(String mobile, String code) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = getMobileRequest();
        try {
            request.putQueryParameter("PhoneNumbers", mobile);
            request.putQueryParameter("TemplateParam", "{code:"+code+"}");
            client.getCommonResponse(request);
        } catch (ServerException e) {
            log.error("用户绑定手机号的信息任务发生异常------------>{}", e.getMessage());
        } catch (ClientException e) {
            log.error("用户绑定手机号的信息任务发生异常------------>{}", e.getMessage());
        }
    }

    @Async
    public void testMobile(String mobile) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = getMobileRequest();
        try {
            request.putQueryParameter("PhoneNumbers", mobile);
            request.putQueryParameter("TemplateParam", "{code: 测试短信}");
            client.getCommonResponse(request);
        } catch (ServerException e) {
            log.error("超级管理员重置信息系统配置的测试手机号可用性的任务发生异常------------>{}", e.getMessage());
        } catch (ClientException e) {
            log.error("超级管理员重置信息系统配置的测试手机号可用性的任务发生异常------------>{}", e.getMessage());
        }
    }
}