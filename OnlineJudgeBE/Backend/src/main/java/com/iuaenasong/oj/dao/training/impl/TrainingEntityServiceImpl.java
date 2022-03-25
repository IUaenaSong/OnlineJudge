/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.training.impl;

;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.mapper.TrainingMapper;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.vo.TrainingVo;
import com.iuaenasong.oj.dao.training.TrainingEntityService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingEntityServiceImpl extends ServiceImpl<TrainingMapper, Training> implements TrainingEntityService {

    @Resource
    private TrainingMapper trainingMapper;

    @Override
    public IPage<TrainingVo> getTrainingList(int limit, int currentPage, Long categoryId, String auth, String keyword) {
        List<TrainingVo> trainingList = trainingMapper.getTrainingList(categoryId, auth, keyword);
        Page<TrainingVo> page = new Page<>(currentPage, limit);
        int count = trainingList.size();
        List<TrainingVo> pageList = new ArrayList<>();
        //计算当前页第一条数据的下标
        int currId = currentPage > 1 ? (currentPage - 1) * limit : 0;
        for (int i = 0; i < limit && i < count - currId; i++) {
            pageList.add(trainingList.get(currId + i));
        }
        page.setSize(limit);
        page.setCurrent(currentPage);
        page.setTotal(count);
        page.setRecords(pageList);
        return page;
    }

}