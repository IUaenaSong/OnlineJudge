/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.advice;

import com.google.protobuf.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.iuaenasong.oj.common.exception.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Set;

@Slf4j(topic = "oj")
@RestControllerAdvice
public class GlobalExceptionAdvice {

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {StatusForbiddenException.class,
            StatusAccessDeniedException.class,
            StatusFailException.class,
            StatusNotFoundException.class,
            StatusSystemErrorException.class})
    public CommonResult<Void> handleCustomException(Exception e) {
        return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AuthenticationException.class)
    public CommonResult<Void> handleAuthenticationException(AuthenticationException e,
                                                            HttpServletRequest httpRequest,
                                                            HttpServletResponse httpResponse) {
        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
        return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
    }

    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = UnauthenticatedException.class)
    public CommonResult<Void> handleUnauthenticatedException(UnauthenticatedException e,
                                                             HttpServletRequest httpRequest,
                                                             HttpServletResponse httpResponse) {
        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
        return CommonResult.errorResponse("请您先登录！", ResultStatus.ACCESS_DENIED);
    }

    
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AuthorizationException.class)
    public CommonResult<Void> handleAuthenticationException(AuthorizationException e,
                                                            HttpServletRequest httpRequest,
                                                            HttpServletResponse httpResponse) {
        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
        return CommonResult.errorResponse("对不起，您无权限进行此操作！", ResultStatus.FORBIDDEN);
    }

    
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = ShiroException.class)
    public CommonResult<Void> handleShiroException(ShiroException e,
                                                   HttpServletRequest httpRequest,
                                                   HttpServletResponse httpResponse) {
        httpResponse.setHeader("Url-Type", httpRequest.getHeader("Url-Type")); // 为了前端能区别请求来源
        return CommonResult.errorResponse("对不起，您无权限进行此操作，请先登录进行授权认证", ResultStatus.FORBIDDEN);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public CommonResult<Void> handler(IllegalArgumentException e) {
        return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<Void> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) throws IOException {
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return CommonResult.errorResponse(objectError.getDefaultMessage(), ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        return CommonResult.errorResponse("The required request parameters are missing：" + e.getMessage(), ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<Void> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        return CommonResult.errorResponse("Failed to parse parameter format!", ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CommonResult<Void> handleBindException(BindException e) {
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return CommonResult.errorResponse(message, ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<Void> handleServiceException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        return CommonResult.errorResponse("[参数验证失败]parameter:" + message, ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public CommonResult<Void> handleValidationException(ValidationException e) {
        return CommonResult.errorResponse("Entity verification failed. The request parameters are incorrect!", ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<Void> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return CommonResult.errorResponse("The request method is not supported!", ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult<Void> handleHttpMediaTypeNotSupportedException(Exception e) {
        return CommonResult.errorResponse("The media type is not supported!", ResultStatus.FAIL);
    }

    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = MessagingException.class)
    public CommonResult<Void> handler(MessagingException e) {
        log.error("邮箱系统异常-------------->{}", getMessage(e));
        return CommonResult.errorResponse("Server Error! Please try Again later!", ResultStatus.SYSTEM_ERROR);
    }

    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public CommonResult<Void> handleServiceException(ServiceException e) {
        log.error("业务逻辑异常-------------->{}", getMessage(e));
        return CommonResult.errorResponse("Server Error! Please try Again later!", ResultStatus.SYSTEM_ERROR);
    }

    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public CommonResult<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("操作数据库出现异常-------------->{}", getMessage(e));
        return CommonResult.errorResponse("Server Error! Please try Again later!", ResultStatus.SYSTEM_ERROR);
    }

    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public CommonResult<Void> handleSQLException(SQLException e) {
        log.error("操作数据库出现异常-------------->{}", getMessage(e));
        return CommonResult.errorResponse("Operation failed! Error message: " + e.getMessage(), ResultStatus.SYSTEM_ERROR);
    }

    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PersistenceException.class)
    public CommonResult<Void> handleBatchUpdateException(PersistenceException e) {
        log.error("操作数据库出现异常-------------->{}", getMessage(e));
        return CommonResult.errorResponse("请检查数据是否准确！可能原因：数据库中已有相同的数据导致重复冲突!", ResultStatus.SYSTEM_ERROR);
    }

    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleException(Exception e) {
        log.error("系统通用异常-------------->{}", getMessage(e));
        return CommonResult.errorResponse("Server Error!", ResultStatus.SYSTEM_ERROR);
    }

    
    public static String getMessage(Exception e) {
        String swStr = null;
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            swStr = sw.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
        return swStr;
    }
}
