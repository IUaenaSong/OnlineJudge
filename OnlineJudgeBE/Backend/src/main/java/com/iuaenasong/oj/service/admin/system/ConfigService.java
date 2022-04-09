/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.system;

import cn.hutool.json.JSONObject;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.*;

import java.util.List;

public interface ConfigService {

    public CommonResult<JSONObject> getServiceInfo();

    public CommonResult<List<JSONObject>> getJudgeServiceInfo();

    public CommonResult<Void> deleteHomeCarousel(Long id);

    public CommonResult<WebConfigDto> getWebConfig();

    public CommonResult<Void> setWebConfig(WebConfigDto webConfigDto);

    public CommonResult<EmailConfigDto> getEmailConfig();

    public CommonResult<Void> setEmailConfig(EmailConfigDto emailConfigDto);

    public CommonResult<Void> testEmail(TestEmailDto testEmailDto);

    public CommonResult<MobileConfigDto> getMobileConfig();

    public CommonResult<Void> setMobileConfig(MobileConfigDto mobileConfigDto);

    public CommonResult<Void> testMobile(TestMobileDto testMobileDto);

    public CommonResult<DBAndRedisConfigDto> getDBAndRedisConfig();

    public CommonResult<Void> setDBAndRedisConfig(DBAndRedisConfigDto dbAndRedisConfigDto);

}
