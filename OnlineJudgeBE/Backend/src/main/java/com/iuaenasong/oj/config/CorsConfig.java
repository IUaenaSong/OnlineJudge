/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.iuaenasong.oj.utils.Constants;

import java.io.File;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

    // 前端直接通过/public/img/图片名称即可拿到
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /api/public/img/** /api/public/file/**
        registry.addResourceHandler(Constants.File.IMG_API.getPath() + "**", Constants.File.FILE_API.getPath() + "**")
                .addResourceLocations("file:" + Constants.File.USER_AVATAR_FOLDER.getPath() + File.separator,
                        "file:" + Constants.File.GROUP_AVATAR_FOLDER.getPath() + File.separator,
                        "file:" + Constants.File.MARKDOWN_FILE_FOLDER.getPath() + File.separator,
                        "file:" + Constants.File.HOME_CAROUSEL_FOLDER.getPath() + File.separator,
                        "file:" + Constants.File.PROBLEM_FILE_FOLDER.getPath() + File.separator);
    }
}