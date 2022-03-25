/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.system;

import cn.hutool.json.JSONObject;
import com.iuaenasong.oj.common.result.CommonResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ConfigService {

    public CommonResult<JSONObject> getServiceInfo();

    public CommonResult<List<JSONObject>> getJudgeServiceInfo();

    public CommonResult<Void> deleteHomeCarousel(Long id);

    public CommonResult<Map<Object,Object>> getWebConfig();

    public CommonResult<Void> setWebConfig(HashMap<String, Object> params);

    public CommonResult<Map<Object,Object>> getEmailConfig();

    public CommonResult<Void> setEmailConfig(HashMap<String, Object> params);

    public CommonResult<Void> testEmail(HashMap<String, Object> params);

    public CommonResult<Map<Object,Object>> getMobileConfig();

    public CommonResult<Void> setMobileConfig(HashMap<String, Object> params);

    public CommonResult<Void> testMobile(HashMap<String, Object> params);

    public CommonResult<Map<Object,Object>> getDBAndRedisConfig();

    public CommonResult<Void> setDBAndRedisConfig(HashMap<String, Object> params);

}
