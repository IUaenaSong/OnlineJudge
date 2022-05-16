/**
 * @Author LengYun
 * @Since 2022/05/14 10:43
 * @Description
 */

package com.iuaenasong.oj.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuaenasong.oj.annotation.OJAccess;
import com.iuaenasong.oj.annotation.OJAccessEnum;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.exception.AccessException;
import com.iuaenasong.oj.utils.ServiceContextUtils;
import com.iuaenasong.oj.validator.AccessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    private AccessValidator accessValidator;

    @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = HandlerMethod.class.cast(handler);
            OJAccess ojAccess = ServiceContextUtils.getAnnotation(handlerMethod.getMethod(), handlerMethod.getBeanType(), OJAccess.class);
            if (ojAccess == null || ojAccess.value().length == 0) {
                return true;
            }
            for (OJAccessEnum value : ojAccess.value()) {
                try {
                    accessValidator.validateAccess(value);
                } catch (AccessException e) {
                    response.setStatus(200);
                    response.setCharacterEncoding("utf-8");
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getWriter(), CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED));
                    return false;
                }
            }
            return true;
        } else if (handler instanceof ResourceHttpRequestHandler){
            // 静态资源的请求不处理
            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
