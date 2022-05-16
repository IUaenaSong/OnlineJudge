/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.judge.entity;

import cn.hutool.json.JSONObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.iuaenasong.oj.utils.Constants;

import java.io.Serializable;
import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class JudgeGlobalDTO implements Serializable {

    private static final long serialVersionUID = 888L;

    
    private Long problemId;

    
    private Constants.JudgeMode judgeMode;

    
    private String userFileId;

    
    private String userFileSrc;

    
    private String runDir;

    
    private Long testTime;

    
    private Long maxTime;

    
    private Long maxMemory;

    
    private Integer maxStack;

    
    private JSONObject testCaseInfo;

    
    private HashMap<String,String> judgeExtraFiles;

    
    Constants.RunConfig runConfig;

    
    Constants.RunConfig spjRunConfig;

    
    Constants.RunConfig interactiveRunConfig;

    
    private Boolean needUserOutputFile;

    
    private Boolean removeEOLBlank;

}