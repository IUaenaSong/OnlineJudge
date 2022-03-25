/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.training;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.vo.TrainingVo;

public interface TrainingEntityService extends IService<Training> {
    public IPage<TrainingVo> getTrainingList(int limit, int currentPage,
                                             Long categoryId, String auth, String keyword);

}
