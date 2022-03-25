/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.vo.ProblemVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroupProblemEntityService extends IService<Problem> {

    IPage<ProblemVo> getProblemList(int limit, int currentPage, Long gid);

    IPage<Problem> getAdminProblemList(int limit, int currentPage, Long gid);

}
