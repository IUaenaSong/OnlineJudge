/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.contest;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.vo.AdminContestVo;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupContestService {

    public CommonResult<IPage<ContestVo>> getContestList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Contest>> getAdminContestList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<AdminContestVo> getContest(Long cid);

    public CommonResult<Void> addContest(AdminContestVo adminContestVo);

    public CommonResult<Void> updateContest(AdminContestVo adminContestVo);

    public CommonResult<Void> deleteContest(Long cid);

    public CommonResult<Void> changeContestVisible(Long cid, Boolean visible);

}
