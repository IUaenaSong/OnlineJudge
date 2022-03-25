/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.group;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.service.group.announcement.GroupAnnouncementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupAnnouncementController {

    @Autowired
    private GroupAnnouncementService groupAnnouncementService;

    @GetMapping("/get-announcement-list")
    public CommonResult<IPage<AnnouncementVo>> getAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                   @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                   @RequestParam(value = "gid", required = true) Long gid) {
        return groupAnnouncementService.getAnnouncementList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-announcement-list")
    public CommonResult<IPage<AnnouncementVo>> getAdminAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                        @RequestParam(value = "gid", required = true) Long gid) {
        return groupAnnouncementService.getAdminAnnouncementList(limit, currentPage, gid);
    }

    @PostMapping("/announcement")
    public CommonResult<Void> addAnnouncement(@RequestBody Announcement announcement) {
        return groupAnnouncementService.addAnnouncement(announcement);
    }

    @PutMapping("/announcement")
    public CommonResult<Void> updateAnnouncement(@RequestBody Announcement announcement) {
        return groupAnnouncementService.updateAnnouncement(announcement);
    }

    @DeleteMapping("/announcement")
    public CommonResult<Void> deleteAnnouncement(@RequestParam(value = "aid", required = true) Long aid) {
        return groupAnnouncementService.deleteAnnouncement(aid);
    }

}
