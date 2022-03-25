/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroupContestEntityService extends IService<Contest> {

    IPage<ContestVo> getContestList(int limit, int currentPage, Long gid);

    IPage<Contest> getAdminContestList(int limit, int currentPage, Long gid);

}
