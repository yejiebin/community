package com.yjb.advice;

import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {

        if (e instanceof CustomizeException) {
            model.addAttribute("message", e.getMessage());
        }else {
            model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
        }

        return new ModelAndView("error");
    }

}
