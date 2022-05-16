/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.remoteJudge.entity;

import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class RemoteJudgeRes implements Serializable {
    private static final long serialVersionUID = 999L;

    private Integer status;

    private Integer time;

    private Integer memory;

    private String errorInfo;

    private List<JudgeCase> judgeCaseList;
}