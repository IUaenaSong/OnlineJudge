/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@RefreshScope
@Data
@Component
public class ConfigVo {
    // 数据库配置
    @Value("${oj.db.username}")
    private String mysqlUsername;

    @Value("${oj.db.password}")
    private String mysqlPassword;

    @Value("${oj.db.name}")
    private String mysqlDBName;

    @Value("${oj.db.host}")
    private String mysqlHost;

    @Value("${oj.db.public-host}")
    private String mysqlPublicHost;

    @Value("${oj.db.port}")
    private Integer mysqlPort;

    // 判题服务token
    @Value("${oj.judge.token}")
    private String judgeToken;

    // 缓存配置
    @Value("${oj.redis.host}")
    private String redisHost;

    @Value("${oj.redis.port}")
    private Integer redisPort;

    @Value("${oj.redis.password}")
    private String redisPassword;

    // jwt配置
    @Value("${oj.jwt.secret}")
    private String tokenSecret;

    @Value("${oj.jwt.expire}")
    private String tokenExpire;

    @Value("${oj.jwt.checkRefreshExpire}")
    private String checkRefreshExpire;

    // 邮箱配置
    @Value("${oj.mail.username}")
    private String emailUsername;

    @Value("${oj.mail.password}")
    private String emailPassword;

    @Value("${oj.mail.host}")
    private String emailHost;

    @Value("${oj.mail.port}")
    private Integer emailPort;

    @Value("${oj.mail.ssl}")
    private Boolean emailSsl;

    @Value("${oj.mail.background-img}")
    private String emailBGImg;

    @Value("${oj.mobile.regionId}")
    public String mobileRegionId;

    @Value("${oj.mobile.accessKeyId}")
    public String mobileAccessKeyId;

    @Value("${oj.mobile.secret}")
    public String mobileSecret;

    @Value("${oj.mobile.domain}")
    public String mobileDomain;

    @Value("${oj.mobile.signName}")
    public String mobileSignName;

    @Value("${oj.mobile.templateCode}")
    public String mobileTemplateCode;

    // 网站前端显示配置
    @Value("${oj.web-config.base-url}")
    private String baseUrl;

    @Value("${oj.web-config.name}")
    private String name;

    @Value("${oj.web-config.short-name}")
    private String shortName;

    @Value("${oj.web-config.description}")
    private String description;

    @Value("${oj.web-config.register}")
    private Boolean register;

    @Value("${oj.web-config.footer.record.name}")
    private String recordName;

    @Value("${oj.web-config.footer.record.url}")
    private String recordUrl;

    @Value("${oj.web-config.footer.project.name}")
    private String projectName;

    @Value("${oj.web-config.footer.project.url}")
    private String projectUrl;

    @Value("${oj.hdu.account.username:}")
    private List<String> hduUsernameList;

    @Value("${oj.hdu.account.password:}")
    private List<String> hduPasswordList;

    @Value("${oj.cf.account.username:}")
    private List<String> cfUsernameList;

    @Value("${oj.cf.account.password:}")
    private List<String> cfPasswordList;

    @Value("${oj.poj.account.username:}")
    private List<String> pojUsernameList;

    @Value("${oj.poj.account.password:}")
    private List<String> pojPasswordList;

    @Value("${oj.atcoder.account.username:}")
    private List<String> atcoderUsernameList;

    @Value("${oj.atcoder.account.password:}")
    private List<String> atcoderPasswordList;

    @Value("${oj.spoj.account.username:}")
    private List<String> spojUsernameList;

    @Value("${oj.spoj.account.password:}")
    private List<String> spojPasswordList;

    @Value("${oj.switch.discussion.public:true}")
    private Boolean openPublicDiscussion;

    @Value("${oj.switch.discussion.group:true}")
    private Boolean openGroupDiscussion;

    @Value("${oj.switch.comment.contest:true}")
    private Boolean openContestComment;

    @Value("${oj.switch.judge.public:true}")
    private Boolean openPublicJudge;

    @Value("${oj.switch.judge.group:true}")
    private Boolean openGroupJudge;

    @Value("${oj.switch.judge.contest:true}")
    private Boolean openContestJudge;

    @Value("${oj.switch.judge.submit-interval:8}")
    private Integer defaultSubmitInterval;

    @Value("${oj.switch.group.create-daily:2}")
    private Integer defaultCreateGroupDailyLimit;

    @Value("${oj.switch.group.create-total:5}")
    private Integer defaultCreateGroupLimit;

    @Value("${oj.switch.group.ac-initial-value:20}")
    private Integer defaultCreateGroupACInitValue;

    @Value("${oj.switch.discussion.create-daily:5}")
    private Integer defaultCreateDiscussionDailyLimit;

    @Value("${oj.switch.discussion.ac-initial-value:10}")
    private Integer defaultCreateDiscussionACInitValue;

    @Value("${oj.switch.comment.ac-initial-value:10}")
    private Integer defaultCreateCommentACInitValue;

}
