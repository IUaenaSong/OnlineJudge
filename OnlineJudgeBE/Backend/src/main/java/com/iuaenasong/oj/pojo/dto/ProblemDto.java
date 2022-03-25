/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import com.iuaenasong.oj.pojo.entity.problem.*;

import java.util.HashMap;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProblemDto {

    private Problem problem;

    private List<ProblemCase> samples;

    private Boolean isUploadTestCase;

    private String uploadTestcaseDir;

    private String judgeMode;

    private Boolean changeModeCode;

    private List<Language> languages;

    private List<Tag> tags;

    private List<CodeTemplate> codeTemplates;

}