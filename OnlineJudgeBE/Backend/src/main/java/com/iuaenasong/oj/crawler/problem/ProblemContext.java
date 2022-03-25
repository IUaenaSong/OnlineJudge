/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.crawler.problem;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "oj")
public class ProblemContext {

    ProblemStrategy problemStrategy;

    public ProblemContext(ProblemStrategy problemStrategy) {
        this.problemStrategy = problemStrategy;
    }

    //上下文接口
    public ProblemStrategy.RemoteProblemInfo getProblemInfo(String problemId, String author) throws Exception {

        try {
            return problemStrategy.getProblemInfo(problemId, author);
        } catch (IllegalArgumentException e){
            throw e;
        } catch (Exception e) {
            log.error("获取题目详情失败---------------->{}", e);
        }
        return null;
    }
}