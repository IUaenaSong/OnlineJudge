/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.problem;

import com.iuaenasong.oj.pojo.entity.problem.ProblemCount;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProblemCountEntityService extends IService<ProblemCount> {
    ProblemCount getContestProblemCount(Long pid, Long cpid, Long cid);
}
