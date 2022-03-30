/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.iuaenasong.oj.remoteJudge.task.RemoteJudgeStrategy;

import java.io.Serializable;
import java.net.HttpCookie;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@ToString
public class RemoteJudgeDTO implements Serializable {
    private static final long serialVersionUID = 888L;

    private String oj;

    private String username;

    private String password;

    private List<HttpCookie> cookies;

    private String csrfToken;

    private String completeProblemId;

    private String contestId;

    private String problemNum;

    private String language;

    private String userCode;

    private Long pid;

    private String uid;

    private Long cid;

    private Long judgeId;

    private Long submitId;

    private Boolean isPublic;

    private Integer loginStatus;

    private Integer submitStatus;

    private String serverIp;

    private Integer serverPort;
}