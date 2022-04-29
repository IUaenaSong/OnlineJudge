/**
 * @Author LengYun
 * @Since 2022/04/29 11:57
 * @Description
 */

package com.iuaenasong.oj.service.group.rank;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.OIRankVo;

public interface GroupRankService {
    public CommonResult<IPage<OIRankVo>> getGroupRankList(Integer limit,
                                                          Integer currentPage,
                                                          String searchUser,
                                                          Integer type,
                                                          Long gid);
}
