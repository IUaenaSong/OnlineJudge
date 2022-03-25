/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.pojo.entity.problem.Tag;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class ProblemInfoVo {
    private Problem problem;
    private List<Tag> tags;
    private List<String> languages;
    private ProblemCountVo problemCount;
    private HashMap<String, String> codeTemplate;
}