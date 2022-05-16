/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.SystemError;
import com.iuaenasong.oj.dao.JudgeEntityService;
import com.iuaenasong.oj.dao.ProblemEntityService;
import com.iuaenasong.oj.judge.JudgeContext;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.ToJudge;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.remoteJudge.RemoteJudgeContext;
import com.iuaenasong.oj.service.JudgeService;
import com.iuaenasong.oj.utils.Constants;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
@RefreshScope
public class JudgeServiceImpl implements JudgeService {

    @Value("${oj-judge-server.name}")
    private String name;

    @Resource
    private JudgeEntityService judgeEntityService;

    @Resource
    private ProblemEntityService problemEntityService;

    @Resource
    private JudgeContext judgeContext;

    @Autowired
    private RemoteJudgeContext remoteJudgeContext;

    @Override
    public void judge(Judge judge) {
        judge.setStatus(Constants.Judge.STATUS_COMPILING.getStatus()); // 标志该判题过程进入编译阶段
        // 写入当前判题服务的名字
        judge.setJudger(name);
        judgeEntityService.updateById(judge);
        // 进行判题操作
        Problem problem = problemEntityService.getById(judge.getPid());
        Judge finalJudge = judgeContext.Judge(problem, judge);

        // 更新该次提交
        judgeEntityService.updateById(finalJudge);

        if (finalJudge.getStatus().intValue() != Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus()) {
            // 更新其它表
            judgeContext
                    .updateOtherTable(finalJudge.getSubmitId(),
                    finalJudge.getStatus(),
                    finalJudge.getCid(),
                    finalJudge.getEid(),
                    finalJudge.getUid(),
                    finalJudge.getPid(),
                    finalJudge.getIsPublic(),
                    finalJudge.getScore(),
                    finalJudge.getTime());
        }
    }

    @Override
    public void remoteJudge(ToJudge toJudge) {
        remoteJudgeContext.judge(toJudge);
    }

    @Override
    public Boolean compileSpj(String code, Long pid, String spjLanguage, HashMap<String, String> extraFiles) throws SystemError {
        return judgeContext.compileSpj(code, pid, spjLanguage, extraFiles);
    }

    @Override
    public Boolean compileInteractive(String code, Long pid, String interactiveLanguage, HashMap<String, String> extraFiles) throws SystemError {
        return judgeContext.compileInteractive(code, pid, interactiveLanguage, extraFiles);
    }
}