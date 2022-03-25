/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.judge.JudgeServer;

import java.util.HashMap;

public interface JudgeServerEntityService extends IService<JudgeServer> {

    public HashMap<String, Object> getJudgeServerInfo();
}
