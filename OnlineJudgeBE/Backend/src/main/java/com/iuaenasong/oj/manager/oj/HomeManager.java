/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.UnicodeUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.pojo.entity.common.File;
import com.iuaenasong.oj.pojo.vo.ACMRankVo;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;
import com.iuaenasong.oj.pojo.vo.ConfigVo;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.iuaenasong.oj.dao.common.AnnouncementEntityService;
import com.iuaenasong.oj.dao.common.FileEntityService;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.user.UserRecordEntityService;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.RedisUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HomeManager {

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private ConfigVo configVo;

    @Autowired
    private AnnouncementEntityService announcementEntityService;

    @Autowired

    private UserRecordEntityService userRecordEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private FileEntityService fileEntityService;

    
    public List<ContestVo> getRecentContest() {
        return contestEntityService.getWithinNext14DaysContests();
    }

    
    public List<HashMap<String, Object>> getHomeCarousel() {
        List<File> fileList = fileEntityService.queryCarouselFileList();
        List<HashMap<String, Object>> apiList = fileList.stream().map(f -> {
            HashMap<String, Object> param = new HashMap<>(2);
            param.put("id", f.getId());
            param.put("url", Constants.File.IMG_API.getPath() + f.getName());
            return param;
        }).collect(Collectors.toList());
        return apiList;
    }

    
    public List<ACMRankVo> getRecentSevenACRank() {
        return userRecordEntityService.getRecent7ACRank();
    }

    
    public List<HashMap<String, Object>> getRecentOtherContest() {
        String redisKey = Constants.Schedule.RECENT_OTHER_CONTEST.getCode();
        // 从redis获取比赛列表
        return (ArrayList<HashMap<String, Object>>) redisUtils.get(redisKey);
    }

    
    public IPage<AnnouncementVo> getCommonAnnouncement(Integer limit, Integer currentPage) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        return announcementEntityService.getAnnouncementList(limit, currentPage, true);
    }

    
    public Map<Object, Object> getWebConfig() {

        return MapUtil.builder().put("baseUrl", UnicodeUtil.toString(configVo.getBaseUrl()))
                .put("name", UnicodeUtil.toString(configVo.getName()))
                .put("shortName", UnicodeUtil.toString(configVo.getShortName()))
                .put("register", configVo.getRegister())
                .put("recordName", UnicodeUtil.toString(configVo.getRecordName()))
                .put("recordUrl", UnicodeUtil.toString(configVo.getRecordUrl()))
                .put("description", UnicodeUtil.toString(configVo.getDescription()))
                .put("email", UnicodeUtil.toString(configVo.getEmailUsername()))
                .put("projectName", UnicodeUtil.toString(configVo.getProjectName()))
                .put("projectUrl", UnicodeUtil.toString(configVo.getProjectUrl()))
                .put("openPublicDiscussion", configVo.getOpenPublicDiscussion())
                .put("openGroupDiscussion", configVo.getOpenGroupDiscussion())
                .put("openContestComment", configVo.getOpenContestComment())
                .put("openPublicJudge", configVo.getOpenPublicJudge())
                .put("openGroupJudge", configVo.getOpenGroupJudge())
                .put("openContestJudge", configVo.getOpenContestJudge())
                .map();
    }
}