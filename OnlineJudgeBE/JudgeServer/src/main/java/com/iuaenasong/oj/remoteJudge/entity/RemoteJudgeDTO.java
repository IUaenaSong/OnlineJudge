/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge.entity;

import com.iuaenasong.oj.utils.CodeForcesUtils;
import com.iuaenasong.oj.utils.Constants;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

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

    public RemoteJudgeDTO setCookies(List<HttpCookie> cookies) {
        if (cookies != null
                && (Constants.RemoteJudge.CF_JUDGE.getName().equals(this.oj)
                || Constants.RemoteJudge.GYM_JUDGE.getName().equals(this.oj))) {
            HttpCookie rcpc = new HttpCookie("RCPC", CodeForcesUtils.getRCPC());
            rcpc.setVersion(0);
            cookies.add(rcpc);
        }
        this.cookies = cookies;
        return this;
    }

    private String csrfToken;

    private String completeProblemId;

    private String contestId;

    private String problemNum;

    private String language;

    private String userCode;

    private Long pid;

    private String uid;

    private Long cid;

    private Long eid;

    private Long judgeId;

    private Long submitId;

    private Integer testcaseNum;

    private Integer maxTime;

    private Integer maxMemory;

    private Boolean isPublic;

    private Integer loginStatus;

    private Integer submitStatus;

    private String serverIp;

    private Integer serverPort;
}