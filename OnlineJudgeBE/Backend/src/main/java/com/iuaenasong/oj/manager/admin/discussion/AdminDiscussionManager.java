/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.admin.discussion;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.DiscussionReport;
import com.iuaenasong.oj.dao.discussion.DiscussionReportEntityService;
import com.iuaenasong.oj.dao.discussion.DiscussionEntityService;

import java.util.List;

@Component
public class AdminDiscussionManager {

    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private DiscussionReportEntityService discussionReportEntityService;

    public void updateDiscussion(Discussion discussion) throws StatusFailException {
        boolean isOk = discussionEntityService.updateById(discussion);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void removeDiscussion(List<Integer> didList) throws StatusFailException {
        boolean isOk = discussionEntityService.removeByIds(didList);
        if (!isOk) {
            throw new StatusFailException("删除失败");
        }
    }

    public IPage<DiscussionReport> getDiscussionReport(Integer limit, Integer currentPage) {
        QueryWrapper<DiscussionReport> discussionReportQueryWrapper = new QueryWrapper<>();
        discussionReportQueryWrapper.orderByAsc("status");
        IPage<DiscussionReport> iPage = new Page<>(currentPage, limit);
        return discussionReportEntityService.page(iPage, discussionReportQueryWrapper);
    }

    public void updateDiscussionReport(DiscussionReport discussionReport) throws StatusFailException {
        boolean isOk = discussionReportEntityService.updateById(discussionReport);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

}