/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.validator;

import org.springframework.stereotype.Component;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.pojo.dto.ToJudgeDto;

import java.util.Arrays;
import java.util.List;

@Component
public class JudgeValidator {

    private final static List<String> OJ_LANGUAGE_LIST = Arrays.asList("C++", "C++ With O2",
            "C", "C With O2", "Python3", "Python2", "Java", "Golang", "C#", "PHP", "PyPy2", "PyPy3",
            "JavaScript Node", "JavaScript V8");

    public void validateSubmissionInfo(ToJudgeDto toJudgeDto) throws StatusFailException {

        if (!toJudgeDto.getIsRemote() && !OJ_LANGUAGE_LIST.contains(toJudgeDto.getLanguage())) {
            throw new StatusFailException("提交的代码的语言错误！请使用" + OJ_LANGUAGE_LIST + "中之一的语言！");
        }

        if (toJudgeDto.getCode().length() < 50
                && !toJudgeDto.getLanguage().contains("Py")
                && !toJudgeDto.getLanguage().contains("PHP")
                && !toJudgeDto.getLanguage().contains("JavaScript")) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要低于50！");
        }

        if (toJudgeDto.getCode().length() > 65535) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要超过65535！");
        }
    }
}