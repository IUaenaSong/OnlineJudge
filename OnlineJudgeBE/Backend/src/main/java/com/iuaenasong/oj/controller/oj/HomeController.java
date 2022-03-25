/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.ACMRankVo;
import com.iuaenasong.oj.pojo.vo.AnnouncementVo;;
import com.iuaenasong.oj.pojo.vo.ContestVo;
import com.iuaenasong.oj.service.oj.HomeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/get-recent-contest")
    public CommonResult<List<ContestVo>> getRecentContest() {
        return homeService.getRecentContest();
    }

    
    @GetMapping("/home-carousel")
    public CommonResult<List<HashMap<String, Object>>> getHomeCarousel() {
        return homeService.getHomeCarousel();
    }

    
    @GetMapping("/get-recent-seven-ac-rank")
    public CommonResult<List<ACMRankVo>> getRecentSevenACRank() {
        return homeService.getRecentSevenACRank();
    }

    @GetMapping("/get-recent-other-contest")
    public CommonResult<List<HashMap<String, Object>>> getRecentOtherContest() {
        return homeService.getRecentOtherContest();
    }

    @GetMapping("/get-common-announcement")

    public CommonResult<IPage<AnnouncementVo>> getCommonAnnouncement(@RequestParam(value = "limit", required = false) Integer limit,
                                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        return homeService.getCommonAnnouncement(limit, currentPage);
    }

    @GetMapping("/get-website-config")
    public CommonResult<Map<Object, Object>> getWebConfig() {
        return homeService.getWebConfig();
    }

}