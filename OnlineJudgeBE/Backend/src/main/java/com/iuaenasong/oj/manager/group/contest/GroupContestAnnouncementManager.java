/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.group.contest;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.common.AnnouncementEntityService;
import com.iuaenasong.oj.dao.contest.ContestAnnouncementEntityService;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.pojo.dto.AnnouncementDto;
import com.iuaenasong.oj.pojo.entity.common.Announcement;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestAnnouncement;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupContestAnnouncementManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private AnnouncementEntityService announcementEntityService;

    @Autowired
    private ContestAnnouncementEntityService contestAnnouncementEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public IPage<AnnouncementVo> getContestAnnouncementList(Integer limit, Integer currentPage, Long cid) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        return announcementEntityService.getContestAnnouncement(cid, false, limit, currentPage);
    }

    public void addContestAnnouncement(AnnouncementDto announcementDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = announcementDto.getCid();

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        announcementDto.getAnnouncement().setGid(gid);

        boolean isOk = announcementEntityService.save(announcementDto.getAnnouncement());
        if (isOk) {
            contestAnnouncementEntityService.saveOrUpdate(new ContestAnnouncement()
                    .setAid(announcementDto.getAnnouncement().getId())
                    .setCid(announcementDto.getCid()));
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void updateContestAnnouncement(AnnouncementDto announcementDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = announcementDto.getCid();

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = announcementEntityService.updateById(announcementDto.getAnnouncement());
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteContestAnnouncement(Long aid, Long cid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = contest.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Announcement announcement = announcementEntityService.getById(aid);

        if (announcement == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !userRolesVo.getUid().equals(contest.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = announcementEntityService.removeById(aid);
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }
}
