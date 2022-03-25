/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.contest;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.AnnouncementDto;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupContestAnnouncementService {

    public CommonResult<IPage<AnnouncementVo>> getContestAnnouncementList(Integer limit, Integer currentPage, Long cid);

    public CommonResult<Void> addContestAnnouncement(AnnouncementDto announcementDto);

    public CommonResult<Void> updateContestAnnouncement(AnnouncementDto announcementDto);

    public CommonResult<Void> deleteContestAnnouncement(Long aid, Long cid);

}
