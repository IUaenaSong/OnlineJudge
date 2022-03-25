/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.email;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.UnicodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.iuaenasong.oj.utils.Constants;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Component
@RefreshScope
@Slf4j(topic = "oj")
public class EmailManager {

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${oj.web-config.base-url}")
    public String ojAddr;

    @Value("${oj.web-config.name}")
    public String ojName;

    @Value("${oj.web-config.short-name}")
    public String ojShortName;

    @Value("${oj.mail.background-img}")
    public String ojEmailBg;

    @Value("${oj.mail.username}")
    public String ojEmailFrom;

    @Value("${oj.mail.password}")
    public String ojEmailPassword;

    @Value("${oj.mail.host}")
    public String ojEmailHost;

    @Value("${oj.mail.port}")
    public Integer ojEmailPort;

    @Value("${oj.mail.ssl}")
    public String ojEmailSsl;

    
    private JavaMailSenderImpl getMailSender() {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(ojEmailHost);
        sender.setPort(ojEmailPort);
        sender.setDefaultEncoding("UTF-8");
        sender.setUsername(ojEmailFrom);
        sender.setPassword(ojEmailPassword);

        Properties p = new Properties();
        p.setProperty("mail.smtp.ssl.enable", ojEmailSsl);
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", ojEmailSsl);
        sender.setJavaMailProperties(p);
        return sender;
    }

    
    public boolean isOk() {
        return ojEmailFrom != null
                && ojEmailPassword != null
                && !ojEmailFrom.equals("your_email_username")
                && !ojEmailPassword.equals("your_email_password")
                && Validator.isEmail(ojEmailFrom);
    }

    @Async
    public void sendCode(String email, String code) {
        DateTime expireTime = DateUtil.offsetMinute(new Date(), 10);
        JavaMailSenderImpl mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                    true);
            // 设置渲染到html页面对应的值
            Context context = new Context();
            context.setVariable(Constants.Email.OJ_NAME.name(), UnicodeUtil.toString(ojName));
            context.setVariable(Constants.Email.OJ_SHORT_NAME.name(), UnicodeUtil.toString(ojShortName).toUpperCase());
            context.setVariable(Constants.Email.OJ_URL.name(), ojAddr);
            context.setVariable(Constants.Email.EMAIL_BACKGROUND_IMG.name(), ojEmailBg);
            context.setVariable("CODE", code);
            context.setVariable("EXPIRE_TIME", expireTime.toString());

            //利用模板引擎加载html文件进行渲染并生成对应的字符串
            String emailContent = templateEngine.process("emailTemplate_registerCode", context);

            // 设置邮件标题
            mimeMessageHelper.setSubject(UnicodeUtil.toString(ojShortName).toUpperCase() + "的注册邮件");
            mimeMessageHelper.setText(emailContent, true);
            // 收件人
            mimeMessageHelper.setTo(email);
            // 发送人
            mimeMessageHelper.setFrom(ojEmailFrom);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("用户注册的邮件任务发生异常------------>{}", e.getMessage());
        }
    }

    
    @Async
    public void sendResetPassword(String username, String code, String email) {
        DateTime expireTime = DateUtil.offsetMinute(new Date(), 10);
        JavaMailSenderImpl mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                    true);
            // 设置渲染到html页面对应的值
            Context context = new Context();
            context.setVariable(Constants.Email.OJ_NAME.name(), UnicodeUtil.toString(ojName));
            context.setVariable(Constants.Email.OJ_SHORT_NAME.name(), UnicodeUtil.toString(ojShortName).toUpperCase());
            context.setVariable(Constants.Email.OJ_URL.name(), ojAddr);
            context.setVariable(Constants.Email.EMAIL_BACKGROUND_IMG.name(), ojEmailBg);

            String resetUrl;
            if (ojAddr.endsWith("/")) {
                resetUrl = ojAddr + "reset-password?username=" + username + "&code=" + code;
            } else {
                resetUrl = ojAddr + "/reset-password?username=" + username + "&code=" + code;
            }

            context.setVariable("RESET_URL", resetUrl);
            context.setVariable("EXPIRE_TIME", expireTime.toString());
            context.setVariable("USERNAME", username);

            //利用模板引擎加载html文件进行渲染并生成对应的字符串
            String emailContent = templateEngine.process("emailTemplate_resetPassword", context);

            mimeMessageHelper.setSubject(UnicodeUtil.toString(ojShortName).toUpperCase() + "的重置密码邮件");

            mimeMessageHelper.setText(emailContent, true);
            // 收件人
            mimeMessageHelper.setTo(email);
            // 发送人
            mimeMessageHelper.setFrom(ojEmailFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("用户重置密码的邮件任务发生异常------------>{}", e.getMessage());
        }
    }

    
    @Async
    public void testEmail(String email) {
        JavaMailSenderImpl mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                    true);
            // 设置渲染到html页面对应的值
            Context context = new Context();
            context.setVariable(Constants.Email.OJ_NAME.name(), UnicodeUtil.toString(ojName));
            context.setVariable(Constants.Email.OJ_SHORT_NAME.name(), UnicodeUtil.toString(ojShortName).toUpperCase());
            context.setVariable(Constants.Email.OJ_URL.name(), ojAddr);
            context.setVariable(Constants.Email.EMAIL_BACKGROUND_IMG.name(), ojEmailBg);
            //利用模板引擎加载html文件进行渲染并生成对应的字符串
            String emailContent = templateEngine.process("emailTemplate_testEmail", context);

            mimeMessageHelper.setSubject(UnicodeUtil.toString(ojShortName).toUpperCase() + "的测试邮件");

            mimeMessageHelper.setText(emailContent, true);
            // 收件人
            mimeMessageHelper.setTo(email);
            // 发送人
            mimeMessageHelper.setFrom(ojEmailFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("超级管理员重置邮件系统配置的测试邮箱可用性的任务发生异常------------>{}", e.getMessage());
        }
    }
}