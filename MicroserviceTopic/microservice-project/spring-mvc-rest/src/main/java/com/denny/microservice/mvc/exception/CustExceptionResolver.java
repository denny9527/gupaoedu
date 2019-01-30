package com.denny.microservice.mvc.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CustExceptionResolver {

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody String exceptionHandler(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod){
        return handlerMethod.getBeanType().getName() + "." + handlerMethod.getMethod().getName() + "处理发生异常！";
    }
}
