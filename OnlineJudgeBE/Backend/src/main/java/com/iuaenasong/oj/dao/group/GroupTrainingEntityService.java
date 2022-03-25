/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.iuaenasong.oj.pojo.entity.training.Training;
import com.iuaenasong.oj.pojo.vo.TrainingVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroupTrainingEntityService extends IService<Training> {

    IPage<TrainingVo> getTrainingList(int limit, int currentPage, Long gid);

    IPage<Training> getAdminTrainingList(int limit, int currentPage, Long gid);

}
