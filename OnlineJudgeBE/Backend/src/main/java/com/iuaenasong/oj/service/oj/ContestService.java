/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ContestPrintDto;
import com.iuaenasong.oj.pojo.dto.ContestRankDto;
import com.iuaenasong.oj.pojo.dto.RegisterContestDto;
import com.iuaenasong.oj.pojo.dto.UserReadContestAnnouncementDto;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.*;

import java.util.List;

public interface ContestService {

    public CommonResult<IPage<ContestVo>> getContestList(Integer limit, Integer currentPage, Integer status, Integer type, String keyword);

    public CommonResult<ContestVo> getContestInfo(Long cid);

    public CommonResult<Void> toRegisterContest(RegisterContestDto registerContestDto);

    public CommonResult<AccessVo> getContestAccess(Long cid);

    public CommonResult<List<ContestProblemVo>> getContestProblem(Long cid);

    public CommonResult<ProblemInfoVo> getContestProblemDetails(Long cid, String displayId);

    public CommonResult<IPage<JudgeVo>> getContestSubmissionList(Integer limit,
                                                                 Integer currentPage,
                                                                 Boolean onlyMine,
                                                                 String displayId,
                                                                 Integer searchStatus,
                                                                 String searchUsername,
                                                                 Long searchCid,
                                                                 Boolean completeProblemID);

    public CommonResult<IPage> getContestRank(ContestRankDto contestRankDto);

    public CommonResult<IPage<AnnouncementVo>> getContestAnnouncement(Long cid, Integer limit, Integer currentPage);

    public CommonResult<List<Announcement>> getContestUserNotReadAnnouncement(UserReadContestAnnouncementDto userReadContestAnnouncementDto);

    public CommonResult<Void> submitPrintText(ContestPrintDto contestPrintDto);

}
