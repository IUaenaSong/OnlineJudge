/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.shiro;

import lombok.Data;
import com.iuaenasong.oj.pojo.entity.user.Role;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AccountProfile implements Serializable {
    private String uid;

    private String username;

    private String password;

    private String nickname;

    private String school;

    private String course;

    private String number;

    private String gender;

    private String realname;

    private String cfUsername;

    private String email;

    private String avatar;

    private String signature;

    private int status;

    private Date gmtCreate;

    private Date gmtModified;

    private List<Role> roles;

    public String getId(){ //shiro登录用户实体默认主键获取方法要为getId
        return uid;
    }
}