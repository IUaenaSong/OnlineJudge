/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iuaenasong.oj.pojo.vo.ACMRankVo;
import com.iuaenasong.oj.pojo.entity.user.UserRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.vo.OIRankVo;
import com.iuaenasong.oj.pojo.vo.UserHomeVo;

import java.util.List;

public interface UserRecordEntityService extends IService<UserRecord> {

    List<ACMRankVo> getRecent7ACRank();

    UserHomeVo getUserHomeInfo(String uid, String username);

    IPage<OIRankVo> getOIRankList(Page<OIRankVo> page, List<String> uidList);

    IPage<ACMRankVo> getACMRankList(Page<ACMRankVo> page, List<String> uidList);

}
