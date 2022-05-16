/**
 * @Author LengYun
 * @Since 2022/05/14 10:43
 * @Description
 */

package com.iuaenasong.oj.validator;

import com.iuaenasong.oj.annotation.OJAccessEnum;
import com.iuaenasong.oj.exception.AccessException;
import com.iuaenasong.oj.pojo.vo.ConfigVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

    @Autowired
    private ConfigVo configVo;

    public void validateAccess(OJAccessEnum hojAccessEnum) throws AccessException {
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        if (isRoot)  {
            return;
        }
        switch (hojAccessEnum) {
            case PUBLIC_DISCUSSION:
                if (!configVo.getOpenPublicDiscussion()) {
                    throw new AccessException("网站当前未开启公共讨论区的功能，不可访问！");
                }
                break;
            case GROUP_DISCUSSION:
                if (!configVo.getOpenGroupDiscussion()) {
                    throw new AccessException("网站当前未开启团队讨论区的功能，不可访问！");
                }
                break;
            case CONTEST_COMMENT:
                if (!configVo.getOpenContestComment()) {
                    throw new AccessException("网站当前未开启比赛评论区的功能，不可访问！");
                }
                break;
            case PUBLIC_JUDGE:
                if (!configVo.getOpenPublicJudge()) {
                    throw new AccessException("网站当前未开启公共题目评测的功能，不可访问！");
                }
                break;
            case GROUP_JUDGE:
                if (!configVo.getOpenGroupJudge()) {
                    throw new AccessException("网站当前未开启团队题目评测的功能，不可访问！");
                }
                break;
            case CONTEST_JUDGE:
                if (!configVo.getOpenContestJudge()) {
                    throw new AccessException("网站当前未开启比赛题目评测的功能，不可访问！");
                }
                break;
        }
    }
}