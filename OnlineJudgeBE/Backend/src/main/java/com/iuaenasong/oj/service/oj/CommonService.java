/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.problem.CodeTemplate;
import com.iuaenasong.oj.pojo.entity.problem.Language;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import com.iuaenasong.oj.pojo.entity.training.TrainingCategory;
import com.iuaenasong.oj.pojo.vo.CaptchaVo;

import java.util.Collection;
import java.util.List;

public interface CommonService {

    public CommonResult<CaptchaVo> getCaptcha();

    public CommonResult<List<TrainingCategory>> getTrainingCategory();

    public CommonResult<List<Tag>> getAllProblemTagsList(String oj);

    public CommonResult<Collection<Tag>> getProblemTags(Long pid);

    public CommonResult<List<Language>> getLanguages(Long pid, Boolean all);

    public CommonResult<Collection<Language>> getProblemLanguages(Long pid);

    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(Long pid);
}
