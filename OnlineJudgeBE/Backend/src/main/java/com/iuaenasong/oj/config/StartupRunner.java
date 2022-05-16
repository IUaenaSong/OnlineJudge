/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.iuaenasong.oj.crawler.language.LanguageContext;
import com.iuaenasong.oj.dao.judge.RemoteJudgeAccountEntityService;
import com.iuaenasong.oj.dao.problem.LanguageEntityService;
import com.iuaenasong.oj.manager.admin.system.ConfigManager;
import com.iuaenasong.oj.pojo.entity.judge.RemoteJudgeAccount;
import com.iuaenasong.oj.pojo.entity.problem.Language;
import com.iuaenasong.oj.pojo.vo.ConfigVo;
import com.iuaenasong.oj.utils.Constants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j(topic = "oj")
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private ConfigVo configVo;

    @Autowired
    private ConfigManager configManager;

    @Autowired
    private RemoteJudgeAccountEntityService remoteJudgeAccountEntityService;

    @Autowired
    private LanguageEntityService languageEntityService;

    @Value("${OPEN_REMOTE_JUDGE:true}")
    private String openRemoteJudge;

    // jwt配置
    @Value("${JWT_TOKEN_SECRET:default}")
    private String tokenSecret;

    @Value("${JWT_TOKEN_EXPIRE:86400}")
    private String tokenExpire;

    @Value("${JWT_TOKEN_FRESH_EXPIRE:43200}")
    private String checkRefreshExpire;

    // 数据库配置
    @Value("${MYSQL_USERNAME:root}")
    private String mysqlUsername;

    @Value("${MYSQL_ROOT_PASSWORD:oj123456}")
    private String mysqlPassword;

    @Value("${MYSQL_DATABASE_NAME:oj}")
    private String mysqlDBName;

    @Value("${MYSQL_HOST:172.20.0.3}")
    private String mysqlHost;

    @Value("${MYSQL_PUBLIC_HOST:172.20.0.3}")
    private String mysqlPublicHost;

    @Value("${MYSQL_PORT:3306}")
    private Integer mysqlPort;

    // 缓存配置
    @Value("${REDIS_HOST:172.20.0.2}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private Integer redisPort;

    @Value("${REDIS_PASSWORD:oj123456}")
    private String redisPassword;
    // 判题服务token
    @Value("${JUDGE_TOKEN:default}")
    private String judgeToken;

    // 邮箱配置
    @Value("${EMAIL_USERNAME:}")
    private String emailUsername;

    @Value("${EMAIL_PASSWORD:}")
    private String emailPassword;

    @Value("${EMAIL_SERVER_HOST:}")
    private String emailHost;

    @Value("${EMAIL_SERVER_PORT:}")
    private Integer emailPort;

    @Value("${MOBILE_DOMAIN:}")
    private String mobileDomain;

    @Value("${MOBILE_REGION_ID:}")
    private String mobileRegionId;

    @Value("${MOBILE_ACCESS_KEY_ID:}")
    private String mobileAccessKeyId;

    @Value("${MOBILE_SECRET:}")
    private String mobileSecret;

    @Value("${MOBILE_TEMPLATE_CODE:}")
    private String mobileTemplateCode;

    @Value("${HDU_ACCOUNT_USERNAME_LIST:}")
    private List<String> hduUsernameList;

    @Value("${HDU_ACCOUNT_PASSWORD_LIST:}")
    private List<String> hduPasswordList;

    @Value("${CF_ACCOUNT_USERNAME_LIST:}")
    private List<String> cfUsernameList;

    @Value("${CF_ACCOUNT_PASSWORD_LIST:}")
    private List<String> cfPasswordList;

    @Value("${POJ_ACCOUNT_USERNAME_LIST:}")
    private List<String> pojUsernameList;

    @Value("${POJ_ACCOUNT_PASSWORD_LIST:}")
    private List<String> pojPasswordList;

    @Value("${ATCODER_ACCOUNT_USERNAME_LIST:}")
    private List<String> atcoderUsernameList;

    @Value("${ATCODER_ACCOUNT_PASSWORD_LIST:}")
    private List<String> atcoderPasswordList;

    @Value("${SPOJ_ACCOUNT_USERNAME_LIST:}")
    private List<String> spojUsernameList;

    @Value("${SPOJ_ACCOUNT_PASSWORD_LIST:}")
    private List<String> spojPasswordList;

    @Value("${FORCED_UPDATE_REMOTE_JUDGE_ACCOUNT:false}")
    private Boolean forcedUpdateRemoteJudgeAccount;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public void run(String... args) throws Exception {

        if (profile.equals("dev")) {
            return;
        }

        // 动态修改nacos上的配置文件
        if (judgeToken.equals("default")) {
            configVo.setJudgeToken(IdUtil.fastSimpleUUID());
        } else {
            configVo.setJudgeToken(judgeToken);
        }

        if (tokenSecret.equals("default")) {
            configVo.setTokenSecret(IdUtil.fastSimpleUUID());
        } else {
            configVo.setTokenSecret(tokenSecret);
        }
        configVo.setTokenExpire(tokenExpire);
        configVo.setCheckRefreshExpire(checkRefreshExpire);

        configVo.setMysqlUsername(mysqlUsername);
        configVo.setMysqlPassword(mysqlPassword);
        configVo.setMysqlHost(mysqlHost);
        configVo.setMysqlPublicHost(mysqlPublicHost);
        configVo.setMysqlPort(mysqlPort);
        configVo.setMysqlDBName(mysqlDBName);

        configVo.setRedisHost(redisHost);
        configVo.setRedisPort(redisPort);
        configVo.setRedisPassword(redisPassword);

        if (configVo.getEmailHost() == null || !"your_email_host".equals(emailHost)) {
            configVo.setEmailHost(emailHost);
        }
        if (configVo.getEmailPort() == null || emailPort != 456) {
            configVo.setEmailPort(emailPort);
        }
        if (configVo.getEmailUsername() == null || !"your_email_username".equals(emailUsername)) {
            configVo.setEmailUsername(emailUsername);
        }
        if (configVo.getEmailPassword() == null || !"your_email_password".equals(emailPassword)) {
            configVo.setEmailPassword(emailPassword);
        }

        if (configVo.getMobileDomain() == null || !"dysmsapi.aliyuncs.com".equals(mobileDomain)) {
            configVo.setMobileDomain(mobileDomain);
        }
        if (configVo.getMobileRegionId() == null || !"cn-hangzhou".equals(mobileRegionId)) {
            configVo.setMobileRegionId(mobileRegionId);
        }
        if (configVo.getMobileAccessKeyId() == null || !"your_mobile_access_key_id".equals(mobileAccessKeyId)) {
            configVo.setMobileAccessKeyId(mobileAccessKeyId);
        }
        if (configVo.getMobileSecret() == null || !"your_mobile_secret".equals(mobileSecret)) {
            configVo.setMobileSecret(mobileSecret);
        }
        if (configVo.getMobileTemplateCode() == null || !"your_mobile_template_code".equals(mobileTemplateCode)) {
            configVo.setMobileTemplateCode(mobileTemplateCode);
        }

        if (CollectionUtils.isEmpty(configVo.getHduUsernameList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setHduUsernameList(hduUsernameList);
        } else {
            hduUsernameList = configVo.getHduUsernameList();
        }

        if (CollectionUtils.isEmpty(configVo.getHduPasswordList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setHduPasswordList(hduPasswordList);
        } else {
            hduPasswordList = configVo.getHduPasswordList();
        }

        if (CollectionUtils.isEmpty(configVo.getCfUsernameList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setCfUsernameList(cfUsernameList);
        } else {
            cfUsernameList = configVo.getCfUsernameList();
        }

        if (CollectionUtils.isEmpty(configVo.getCfPasswordList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setCfPasswordList(cfPasswordList);
        } else {
            cfPasswordList = configVo.getCfPasswordList();
        }

        if (CollectionUtils.isEmpty(configVo.getPojUsernameList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setPojUsernameList(pojUsernameList);
        } else {
            pojUsernameList = configVo.getPojUsernameList();
        }

        if (CollectionUtils.isEmpty(configVo.getPojPasswordList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setPojPasswordList(pojPasswordList);
        } else {
            pojPasswordList = configVo.getPojPasswordList();
        }

        if (CollectionUtils.isEmpty(configVo.getAtcoderUsernameList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setAtcoderUsernameList(atcoderUsernameList);
        } else {
            atcoderUsernameList = configVo.getAtcoderUsernameList();
        }

        if (CollectionUtils.isEmpty(configVo.getAtcoderPasswordList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setAtcoderPasswordList(atcoderPasswordList);
        } else {
            atcoderPasswordList = configVo.getAtcoderPasswordList();
        }

        if (CollectionUtils.isEmpty(configVo.getSpojUsernameList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setSpojUsernameList(spojUsernameList);
        } else {
            spojUsernameList = configVo.getSpojUsernameList();
        }

        if (CollectionUtils.isEmpty(configVo.getSpojPasswordList()) || forcedUpdateRemoteJudgeAccount) {
            configVo.setSpojPasswordList(spojPasswordList);
        } else {
            spojPasswordList = configVo.getSpojPasswordList();
        }

        configManager.sendNewConfigToNacos();

        if (openRemoteJudge.equals("true")) {
            // 初始化清空表
            remoteJudgeAccountEntityService.remove(new QueryWrapper<>());
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.HDU.getName(), hduUsernameList, hduPasswordList);
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.POJ.getName(), pojUsernameList, pojPasswordList);
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.CODEFORCES.getName(), cfUsernameList, cfPasswordList);
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.SPOJ.getName(), spojUsernameList, spojPasswordList);
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.ATCODER.getName(), atcoderUsernameList, atcoderPasswordList);

            checkRemoteOJLanguage(Constants.RemoteOJ.SPOJ, Constants.RemoteOJ.ATCODER);
        }

    }

    private void addRemoteJudgeAccountToMySQL(String oj, List<String> usernameList, List<String> passwordList) {

        if (CollectionUtils.isEmpty(usernameList) || CollectionUtils.isEmpty(passwordList) || usernameList.size() != passwordList.size()) {
            log.error("[Init System Config] [{}]: There is no account or password configured for remote judge, " +
                            "username list:{}, password list:{}", oj, Arrays.toString(usernameList.toArray()),
                    Arrays.toString(passwordList.toArray()));
        }

        List<RemoteJudgeAccount> remoteAccountList = new LinkedList<>();
        for (int i = 0; i < usernameList.size(); i++) {

            remoteAccountList.add(new RemoteJudgeAccount()
                    .setUsername(usernameList.get(i))
                    .setPassword(passwordList.get(i))
                    .setStatus(true)
                    .setVersion(0L)
                    .setOj(oj));

        }

        if (remoteAccountList.size() > 0) {
            boolean addOk = remoteJudgeAccountEntityService.saveOrUpdateBatch(remoteAccountList);
            if (!addOk) {
                log.error("[Init System Config] Remote judge initialization failed. Failed to add account for: [{}]. Please check the configuration file and restart!", oj);
            }
        }
    }

    private void checkRemoteOJLanguage(Constants.RemoteOJ... remoteOJList) {
        for (Constants.RemoteOJ remoteOJ : remoteOJList) {
            QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
            languageQueryWrapper.eq("oj", remoteOJ.getName());
            int count = languageEntityService.count(languageQueryWrapper);
            if (count == 0) {
                List<Language> languageList = new LanguageContext(remoteOJ).buildLanguageList();
                boolean isOk = languageEntityService.saveBatch(languageList);
                if (!isOk) {
                    log.error("[Init System Config] [{}] Failed to initialize language list! Please check whether the language table corresponding to the database has the OJ language!", remoteOJ.getName());
                }
            }
        }
    }

}

