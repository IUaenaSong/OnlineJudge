/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.AnnouncementDto;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;

public interface AdminContestAnnouncementService {

    public CommonResult<IPage<AnnouncementVo>> getAnnouncementList(Integer limit, Integer currentPage, Long cid);

    public CommonResult<Void> deleteAnnouncement(Long aid);

    public CommonResult<Void> addAnnouncement(AnnouncementDto announcementDto);

    public CommonResult<Void> updateAnnouncement(AnnouncementDto announcementDto);
}
