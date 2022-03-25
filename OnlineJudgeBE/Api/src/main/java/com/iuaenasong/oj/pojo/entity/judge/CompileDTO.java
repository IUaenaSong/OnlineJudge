/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.entity.judge;

import lombok.Data;

import java.util.HashMap;

@Data
public class CompileDTO {

    private String code;

    private Long pid;

    private String language;

    private String token;

    private HashMap<String,String> extraFiles;

}