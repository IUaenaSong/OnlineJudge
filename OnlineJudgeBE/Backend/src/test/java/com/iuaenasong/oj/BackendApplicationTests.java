/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj;

import com.iuaenasong.oj.controller.admin.AdminAccountController;
import com.iuaenasong.oj.controller.oj.PassportController;
import com.iuaenasong.oj.pojo.dto.LoginDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SpringBootTest
public class BackendApplicationTests {

    @Autowired
    private PassportController passportController;

    @Autowired
    private AdminAccountController adminAccountController;

    @Test
    public void test1() {
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        LoginDto loginDto = new LoginDto();

        loginDto.setUsername("root"); loginDto.setPassword("oj123456");
        System.out.println(passportController.login(loginDto, response, request)); // 正确账号

        loginDto.setUsername("problem-admin"); loginDto.setPassword("oj123456");
        System.out.println(passportController.login(loginDto, response, request)); // 正确账号

        loginDto.setUsername("admin"); loginDto.setPassword("oj123456");
        System.out.println(passportController.login(loginDto, response, request)); // 正确账号

        loginDto.setUsername("user"); loginDto.setPassword("oj123456");
        System.out.println(passportController.login(loginDto, response, request)); // 无权限账号

        loginDto.setUsername("root"); loginDto.setPassword("o123456");
        System.out.println(passportController.login(loginDto, response, request)); // 错误密码

        loginDto.setUsername("root1"); loginDto.setPassword("oj123456");
        System.out.println(passportController.login(loginDto, response, request)); // 错误用户名

        loginDto.setUsername(""); loginDto.setPassword("");
        System.out.println(passportController.login(loginDto, response, request)); // 空用户名、空密码

        loginDto.setUsername(""); loginDto.setPassword("oj123456");
        System.out.println(passportController.login(loginDto, response, request)); // 空用户名

        loginDto.setUsername("root"); loginDto.setPassword("");
        System.out.println(passportController.login(loginDto, response, request)); // 空密码
    }

    @Test
    public void test2() {
        LoginDto loginDto = new LoginDto();

        loginDto.setUsername("root"); loginDto.setPassword("oj123456");
        System.out.println(adminAccountController.login(loginDto)); // 正确账号

        loginDto.setUsername("problem-admin"); loginDto.setPassword("oj123456");
        System.out.println(adminAccountController.login(loginDto)); // 正确账号

        loginDto.setUsername("admin"); loginDto.setPassword("oj123456");
        System.out.println(adminAccountController.login(loginDto)); // 正确账号

        loginDto.setUsername("user"); loginDto.setPassword("oj123456");
        System.out.println(adminAccountController.login(loginDto)); // 无权限账号

        loginDto.setUsername("root"); loginDto.setPassword("o123456");
        System.out.println(adminAccountController.login(loginDto)); // 错误密码

        loginDto.setUsername("root1"); loginDto.setPassword("oj123456");
        System.out.println(adminAccountController.login(loginDto)); // 错误用户名

        loginDto.setUsername(""); loginDto.setPassword("");
        System.out.println(adminAccountController.login(loginDto)); // 空用户名、空密码

        loginDto.setUsername(""); loginDto.setPassword("oj123456");
        System.out.println(adminAccountController.login(loginDto)); // 空用户名

        loginDto.setUsername("root"); loginDto.setPassword("");
        System.out.println(adminAccountController.login(loginDto)); // 空密码
    }
}