/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.system;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.dao.judge.RemoteJudgeAccountEntityService;
import com.iuaenasong.oj.manager.mobile.MobileManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.iuaenasong.oj.pojo.dto.*;
import com.iuaenasong.oj.pojo.entity.judge.RemoteJudgeAccount;
import com.iuaenasong.oj.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.manager.email.EmailManager;
import com.iuaenasong.oj.pojo.entity.common.File;
import com.iuaenasong.oj.pojo.vo.ConfigVo;
import com.iuaenasong.oj.dao.common.FileEntityService;
import com.iuaenasong.oj.utils.ConfigUtils;

import java.util.*;

@Component
@Slf4j(topic = "oj")
public class ConfigManager {
    @Autowired
    private ConfigVo configVo;

    @Autowired
    private EmailManager emailManager;

    @Autowired
    private MobileManager mobileManager;

    @Autowired
    private FileEntityService fileEntityService;

    @Autowired
    private RemoteJudgeAccountEntityService remoteJudgeAccountEntityService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${service-url.name}")
    private String judgeServiceName;

    @Value("${spring.application.name}")
    private String currentServiceName;

    @Value("${spring.cloud.nacos.url}")
    private String NACOS_URL;

    @Value("${spring.cloud.nacos.config.prefix}")
    private String prefix;

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${spring.cloud.nacos.config.file-extension}")
    private String fileExtension;

    @Value("${spring.cloud.nacos.config.group}")
    private String GROUP;

    @Value("${spring.cloud.nacos.config.type}")
    private String TYPE;

    @Value("${spring.cloud.nacos.config.username}")
    private String nacosUsername;

    @Value("${spring.cloud.nacos.config.password}")
    private String nacosPassword;

    public JSONObject getServiceInfo() {

        JSONObject result = new JSONObject();

        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(currentServiceName);

        // 获取nacos中心配置所在的机器环境
        String response = restTemplate.getForObject(NACOS_URL + "/nacos/v1/ns/operator/metrics", String.class);

        JSONObject jsonObject = JSONUtil.parseObj(response);
        // 获取当前数据后台所在机器环境
        int cores = OshiUtil.getCpuInfo().getCpuNum(); // 当前机器的cpu核数
        double cpuLoad = 100 - OshiUtil.getCpuInfo().getFree();
        String percentCpuLoad = String.format("%.2f", cpuLoad) + "%"; // 当前服务所在机器cpu使用率

        double totalVirtualMemory = OshiUtil.getMemory().getTotal(); // 当前服务所在机器总内存
        double freePhysicalMemorySize = OshiUtil.getMemory().getAvailable(); // 当前服务所在机器空闲内存
        double value = freePhysicalMemorySize / totalVirtualMemory;
        String percentMemoryLoad = String.format("%.2f", (1 - value) * 100) + "%"; // 当前服务所在机器内存使用率

        result.put("nacos", jsonObject);
        result.put("backupCores", cores);
        result.put("backupService", serviceInstances);
        result.put("backupPercentCpuLoad", percentCpuLoad);
        result.put("backupPercentMemoryLoad", percentMemoryLoad);
        return result;
    }

    public List<JSONObject> getJudgeServiceInfo() {
        List<JSONObject> serviceInfoList = new LinkedList<>();
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(judgeServiceName);
        for (ServiceInstance serviceInstance : serviceInstances) {
            String result = restTemplate.getForObject(serviceInstance.getUri() + "/get-sys-config", String.class);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            jsonObject.put("service", serviceInstance);
            serviceInfoList.add(jsonObject);
        }
        return serviceInfoList;
    }

    public WebConfigDto getWebConfig() {

        return WebConfigDto.builder()
                .baseUrl(UnicodeUtil.toString(configVo.getBaseUrl()))
                .name(UnicodeUtil.toString(configVo.getName()))
                .shortName(UnicodeUtil.toString(configVo.getShortName()))
                .description(UnicodeUtil.toString(configVo.getDescription()))
                .register(configVo.getRegister())
                .recordName(UnicodeUtil.toString(configVo.getRecordName()))
                .recordUrl(UnicodeUtil.toString(configVo.getRecordUrl()))
                .projectName(UnicodeUtil.toString(configVo.getProjectName()))
                .projectUrl(UnicodeUtil.toString(configVo.getProjectUrl()))
                .build();
    }

    public void deleteHomeCarousel(Long id) throws StatusFailException {

        File imgFile = fileEntityService.getById(id);
        if (imgFile == null) {
            throw new StatusFailException("文件id错误，图片不存在");
        }
        boolean isOk = fileEntityService.removeById(id);
        if (isOk) {
            FileUtil.del(imgFile.getFilePath());
        } else {
            throw new StatusFailException("删除失败！");
        }
    }

    public void setWebConfig(WebConfigDto webConfigDto) throws StatusFailException {

        if (!StringUtils.isEmpty(webConfigDto.getBaseUrl())) {
            configVo.setBaseUrl(webConfigDto.getBaseUrl());
        }
        if (!StringUtils.isEmpty(webConfigDto.getName())) {
            configVo.setName(webConfigDto.getName());
        }
        if (!StringUtils.isEmpty(webConfigDto.getShortName())) {
            configVo.setShortName(webConfigDto.getShortName());
        }
        if (!StringUtils.isEmpty(webConfigDto.getDescription())) {
            configVo.setDescription(webConfigDto.getDescription());
        }
        if (webConfigDto.getRegister() != null) {
            configVo.setRegister(webConfigDto.getRegister());
        }
        if (!StringUtils.isEmpty(webConfigDto.getRecordName())) {
            configVo.setRecordName(webConfigDto.getRecordName());
        }
        if (!StringUtils.isEmpty(webConfigDto.getRecordUrl())) {
            configVo.setRecordUrl(webConfigDto.getRecordUrl());
        }
        if (!StringUtils.isEmpty(webConfigDto.getProjectName())) {
            configVo.setProjectName(webConfigDto.getProjectName());
        }
        if (!StringUtils.isEmpty(webConfigDto.getProjectUrl())) {
            configVo.setProjectUrl(webConfigDto.getProjectUrl());
        }
        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public EmailConfigDto getEmailConfig() {
        return EmailConfigDto.builder()
                .emailUsername(configVo.getEmailUsername())
                .emailPassword(configVo.getEmailPassword())
                .emailHost(configVo.getEmailHost())
                .emailPort(configVo.getEmailPort())
                .emailBGImg(configVo.getEmailBGImg())
                .emailSsl(configVo.getEmailSsl())
                .build();
    }

    public void setEmailConfig(EmailConfigDto emailConfigDto) throws StatusFailException {

        if (!StringUtils.isEmpty(emailConfigDto.getEmailHost())) {
            configVo.setEmailHost(emailConfigDto.getEmailHost());
        }
        if (!StringUtils.isEmpty(emailConfigDto.getEmailPassword())) {
            configVo.setEmailPassword(emailConfigDto.getEmailPassword());
        }
        if (emailConfigDto.getEmailPort() != null) {
            configVo.setEmailPort(emailConfigDto.getEmailPort());
        }
        if (!StringUtils.isEmpty(emailConfigDto.getEmailUsername())) {
            configVo.setEmailUsername(emailConfigDto.getEmailUsername());
        }
        if (!StringUtils.isEmpty(emailConfigDto.getEmailBGImg())) {
            configVo.setEmailBGImg(emailConfigDto.getEmailBGImg());
        }
        if (emailConfigDto.getEmailSsl() != null) {
            configVo.setEmailSsl(emailConfigDto.getEmailSsl());
        }
        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void testEmail(TestEmailDto testEmailDto) throws StatusFailException {
        String email = testEmailDto.getEmail();
        if (StringUtils.isEmpty(email)) {
            throw new StatusFailException("测试的邮箱不能为空！");
        }
        boolean isEmail = Validator.isEmail(email);
        if (isEmail) {
            emailManager.testEmail(email);
        } else {
            throw new StatusFailException("测试的邮箱格式不正确！");
        }
    }

    public MobileConfigDto getMobileConfig() {
        return MobileConfigDto.builder()
                .mobileDomain(configVo.getMobileDomain())
                .mobileRegionId(configVo.getMobileRegionId())
                .mobileAccessKeyId(configVo.getMobileAccessKeyId())
                .mobileSecret(configVo.getMobileSecret())
                .mobileSignName(configVo.getMobileSignName())
                .mobileTemplateCode(configVo.getMobileTemplateCode())
                .build();
    }

    public void setMobileConfig(MobileConfigDto mobileConfigDto) throws StatusFailException {

        if (!StringUtils.isEmpty(mobileConfigDto.getMobileDomain())) {
            configVo.setMobileDomain(mobileConfigDto.getMobileDomain());
        }
        if (!StringUtils.isEmpty(mobileConfigDto.getMobileRegionId())) {
            configVo.setMobileDomain(mobileConfigDto.getMobileRegionId());
        }
        if (!StringUtils.isEmpty(mobileConfigDto.getMobileAccessKeyId())) {
            configVo.setMobileDomain(mobileConfigDto.getMobileAccessKeyId());
        }
        if (!StringUtils.isEmpty(mobileConfigDto.getMobileSecret())) {
            configVo.setMobileDomain(mobileConfigDto.getMobileSecret());
        }
        if (!StringUtils.isEmpty(mobileConfigDto.getMobileSignName())) {
            configVo.setMobileDomain(mobileConfigDto.getMobileSignName());
        }
        if (!StringUtils.isEmpty(mobileConfigDto.getMobileTemplateCode())) {
            configVo.setMobileDomain(mobileConfigDto.getMobileTemplateCode());
        }
        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void testMobile(TestMobileDto testMobileDto) throws StatusFailException {
        String mobile = testMobileDto.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            throw new StatusFailException("测试的手机号不能为空！");
        }
        boolean isMobile = Validator.isMobile(mobile);
        if (isMobile) {
            mobileManager.testMobile(mobile);
        } else {
            throw new StatusFailException("测试的手机号格式不正确！");
        }
    }

    public DBAndRedisConfigDto getDBAndRedisConfig() {
        return DBAndRedisConfigDto.builder()
                .dbName(configVo.getMysqlDBName())
                .dbHost(configVo.getMysqlHost())
                .dbPort(configVo.getMysqlPort())
                .dbUsername(configVo.getMysqlUsername())
                .dbPassword(configVo.getMysqlPassword())
                .redisHost(configVo.getRedisHost())
                .redisPort(configVo.getRedisPort())
                .redisPassword(configVo.getRedisPassword())
                .build();
    }

    public void setDBAndRedisConfig(DBAndRedisConfigDto dbAndRedisConfigDto) throws StatusFailException {
        if (!StringUtils.isEmpty(dbAndRedisConfigDto.getDbName())) {
            configVo.setMysqlDBName(dbAndRedisConfigDto.getDbName());
        }
        if (!StringUtils.isEmpty(dbAndRedisConfigDto.getDbHost())) {
            configVo.setMysqlHost(dbAndRedisConfigDto.getDbHost());
        }
        if (dbAndRedisConfigDto.getDbPort() != null) {
            configVo.setMysqlPort(dbAndRedisConfigDto.getDbPort());
        }
        if (!StringUtils.isEmpty(dbAndRedisConfigDto.getDbUsername())) {
            configVo.setMysqlUsername(dbAndRedisConfigDto.getDbUsername());
        }
        if (!StringUtils.isEmpty(dbAndRedisConfigDto.getDbPassword())) {
            configVo.setMysqlPassword(dbAndRedisConfigDto.getDbPassword());
        }
        if (!StringUtils.isEmpty(dbAndRedisConfigDto.getRedisHost())) {
            configVo.setRedisHost(dbAndRedisConfigDto.getRedisHost());
        }
        if (dbAndRedisConfigDto.getRedisPort() != null) {
            configVo.setRedisPort(dbAndRedisConfigDto.getRedisPort());
        }
        if (!StringUtils.isEmpty(dbAndRedisConfigDto.getRedisPassword())) {
            configVo.setRedisPassword(dbAndRedisConfigDto.getRedisPassword());
        }
        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public SwitchConfigDto getSwitchConfig() {
        return SwitchConfigDto.builder()
                .openPublicDiscussion(configVo.getOpenPublicDiscussion())
                .openGroupDiscussion(configVo.getOpenGroupDiscussion())
                .openContestComment(configVo.getOpenContestComment())
                .openPublicJudge(configVo.getOpenPublicJudge())
                .openContestJudge(configVo.getOpenContestJudge())
                .openGroupJudge(configVo.getOpenGroupJudge())
                .defaultCreateCommentACInitValue(configVo.getDefaultCreateCommentACInitValue())
                .defaultCreateDiscussionACInitValue(configVo.getDefaultCreateDiscussionACInitValue())
                .defaultCreateDiscussionDailyLimit(configVo.getDefaultCreateDiscussionDailyLimit())
                .defaultCreateGroupACInitValue(configVo.getDefaultCreateGroupACInitValue())
                .defaultSubmitInterval(configVo.getDefaultSubmitInterval())
                .defaultCreateGroupDailyLimit(configVo.getDefaultCreateGroupDailyLimit())
                .defaultCreateGroupLimit(configVo.getDefaultCreateGroupLimit())
                .hduUsernameList(configVo.getHduUsernameList())
                .hduPasswordList(configVo.getHduPasswordList())
                .cfUsernameList(configVo.getCfUsernameList())
                .cfPasswordList(configVo.getCfPasswordList())
                .pojUsernameList(configVo.getPojUsernameList())
                .pojPasswordList(configVo.getPojPasswordList())
                .atcoderUsernameList(configVo.getAtcoderUsernameList())
                .atcoderPasswordList(configVo.getAtcoderPasswordList())
                .spojUsernameList(configVo.getSpojUsernameList())
                .spojPasswordList(configVo.getSpojPasswordList())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void setSwitchConfig(SwitchConfigDto config) throws StatusFailException {
        if (config.getOpenPublicDiscussion() != null) {
            configVo.setOpenPublicDiscussion(config.getOpenPublicDiscussion());
        }
        if (config.getOpenGroupDiscussion() != null) {
            configVo.setOpenGroupDiscussion(config.getOpenGroupDiscussion());
        }
        if (config.getOpenContestComment() != null) {
            configVo.setOpenContestComment(config.getOpenContestComment());
        }
        if (config.getOpenPublicJudge() != null) {
            configVo.setOpenPublicJudge(config.getOpenPublicJudge());
        }
        if (config.getOpenGroupJudge() != null) {
            configVo.setOpenGroupJudge(config.getOpenGroupJudge());
        }
        if (config.getOpenContestJudge() != null) {
            configVo.setOpenContestJudge(config.getOpenContestJudge());
        }

        if (config.getDefaultCreateDiscussionACInitValue() != null) {
            configVo.setDefaultCreateDiscussionACInitValue(config.getDefaultCreateDiscussionACInitValue());
        }

        if (config.getDefaultCreateDiscussionDailyLimit() != null) {
            configVo.setDefaultCreateDiscussionDailyLimit(config.getDefaultCreateDiscussionDailyLimit());
        }

        if (config.getDefaultCreateCommentACInitValue() != null) {
            configVo.setDefaultCreateCommentACInitValue(config.getDefaultCreateCommentACInitValue());
        }

        if (config.getDefaultSubmitInterval() != null) {
            if (config.getDefaultSubmitInterval() >= 0) {
                configVo.setDefaultSubmitInterval(config.getDefaultSubmitInterval());
            } else {
                configVo.setDefaultSubmitInterval(0);
            }
        }

        if (config.getDefaultCreateGroupACInitValue() != null) {
            configVo.setDefaultCreateGroupACInitValue(config.getDefaultCreateGroupACInitValue());
        }

        if (config.getDefaultCreateGroupDailyLimit() != null) {
            configVo.setDefaultCreateGroupDailyLimit(config.getDefaultCreateGroupDailyLimit());
        }

        if (config.getDefaultCreateGroupLimit() != null) {
            configVo.setDefaultCreateGroupLimit(config.getDefaultCreateGroupLimit());
        }

        if (checkListDiff(config.getCfUsernameList(), configVo.getCfUsernameList()) ||
                checkListDiff(config.getCfPasswordList(), configVo.getCfPasswordList())) {
            configVo.setCfUsernameList(config.getCfUsernameList());
            configVo.setCfPasswordList(config.getCfPasswordList());
            changeRemoteJudgeAccount(config.getCfUsernameList(),
                    config.getCfPasswordList(),
                    Constants.RemoteOJ.CODEFORCES.getName());
        }

        if (checkListDiff(config.getHduUsernameList(), configVo.getHduUsernameList()) ||
                checkListDiff(config.getHduPasswordList(), configVo.getHduPasswordList())) {
            configVo.setHduUsernameList(config.getHduUsernameList());
            configVo.setHduPasswordList(config.getHduPasswordList());
            changeRemoteJudgeAccount(config.getHduUsernameList(),
                    config.getHduPasswordList(),
                    Constants.RemoteOJ.HDU.getName());
        }

        if (checkListDiff(config.getPojUsernameList(), configVo.getPojUsernameList()) ||
                checkListDiff(config.getPojPasswordList(), configVo.getPojPasswordList())) {
            configVo.setPojUsernameList(config.getPojUsernameList());
            configVo.setPojPasswordList(config.getPojPasswordList());
            changeRemoteJudgeAccount(config.getPojUsernameList(),
                    config.getPojPasswordList(),
                    Constants.RemoteOJ.POJ.getName());
        }

        if (checkListDiff(config.getSpojUsernameList(), configVo.getSpojUsernameList()) ||
                checkListDiff(config.getSpojPasswordList(), configVo.getSpojPasswordList())) {
            configVo.setSpojUsernameList(config.getSpojUsernameList());
            configVo.setSpojPasswordList(config.getSpojPasswordList());
            changeRemoteJudgeAccount(config.getSpojUsernameList(),
                    config.getSpojPasswordList(),
                    Constants.RemoteOJ.SPOJ.getName());
        }

        if (checkListDiff(config.getAtcoderUsernameList(), configVo.getAtcoderUsernameList()) ||
                checkListDiff(config.getAtcoderPasswordList(), configVo.getAtcoderPasswordList())) {
            configVo.setAtcoderUsernameList(config.getAtcoderUsernameList());
            configVo.setAtcoderPasswordList(config.getAtcoderPasswordList());
            changeRemoteJudgeAccount(config.getAtcoderUsernameList(),
                    config.getAtcoderPasswordList(),
                    Constants.RemoteOJ.ATCODER.getName());
        }

        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    private boolean checkListDiff(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return true;
        }
        list1.sort(Comparator.comparing(String::hashCode));
        list2.sort(Comparator.comparing(String::hashCode));
        return !list1.toString().equals(list2.toString());
    }

    private void changeRemoteJudgeAccount(List<String> usernameList,
                                          List<String> passwordList,
                                          String oj) {

        if (CollectionUtils.isEmpty(usernameList) || CollectionUtils.isEmpty(passwordList) || usernameList.size() != passwordList.size()) {
            log.error("[Change by Switch] [{}]: There is no account or password configured for remote judge, " +
                            "username list:{}, password list:{}", oj, Arrays.toString(usernameList.toArray()),
                    Arrays.toString(passwordList.toArray()));
        }

        QueryWrapper<RemoteJudgeAccount> remoteJudgeAccountQueryWrapper = new QueryWrapper<>();
        remoteJudgeAccountQueryWrapper.eq("oj", oj);
        remoteJudgeAccountEntityService.remove(remoteJudgeAccountQueryWrapper);

        List<RemoteJudgeAccount> newRemoteJudgeAccountList = new ArrayList<>();

        for (int i = 0; i < usernameList.size(); i++) {
            newRemoteJudgeAccountList.add(new RemoteJudgeAccount()
                    .setUsername(usernameList.get(i))
                    .setPassword(passwordList.get(i))
                    .setStatus(true)
                    .setVersion(0L)
                    .setOj(oj));
        }

        if (newRemoteJudgeAccountList.size() > 0) {
            boolean addOk = remoteJudgeAccountEntityService.saveOrUpdateBatch(newRemoteJudgeAccountList);
            if (!addOk) {
                log.error("Remote judge initialization failed. Failed to add account for: [{}]. Please check the configuration file and restart!", oj);
            }
        }
    }

    public boolean sendNewConfigToNacos() {

        Properties properties = new Properties();
        properties.put("serverAddr", NACOS_URL);

        // if need username and password to login
        properties.put("username", nacosUsername);
        properties.put("password", nacosPassword);

        com.alibaba.nacos.api.config.ConfigService configService = null;
        boolean isOK = false;
        try {
            configService = NacosFactory.createConfigService(properties);
            isOK = configService.publishConfig(prefix + "-" + active + "." + fileExtension, GROUP, configUtils.getConfigContent(), TYPE);
        } catch (NacosException e) {
            log.error("通过nacos修改网站配置异常--------------->{}", e.getMessage());
        }
        return isOK;
    }
}