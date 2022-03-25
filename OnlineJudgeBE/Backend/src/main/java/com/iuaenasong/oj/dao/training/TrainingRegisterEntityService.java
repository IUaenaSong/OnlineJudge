/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.training;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.training.TrainingRegister;
import java.util.List;

public interface TrainingRegisterEntityService extends IService<TrainingRegister> {

    public List<String> getAlreadyRegisterUidList(Long tid);

}
