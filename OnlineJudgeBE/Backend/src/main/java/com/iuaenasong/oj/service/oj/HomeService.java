/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.ACMRankVo;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.pojo.vo.ContestVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HomeService {

    public CommonResult<List<ContestVo>> getRecentContest();

    public CommonResult<List<HashMap<String, Object>>> getHomeCarousel();

    public CommonResult<List<ACMRankVo>> getRecentSevenACRank();

    public CommonResult<List<HashMap<String, Object>>> getRecentOtherContest();

    public CommonResult<IPage<AnnouncementVo>> getCommonAnnouncement(Integer limit, Integer currentPage);

    public CommonResult<Map<Object, Object>> getWebConfig();

}