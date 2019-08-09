package com.yjb.controller;

import com.yjb.mapper.UserMapper;
import com.yjb.model.User;
import com.yjb.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    IndexService indexService;

    @GetMapping("/")
    public String toIndex(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    User user = indexService.findByToken(cookie.getValue());
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                    }
                }
            }
        }
        List questionList = indexService.list();
        model.addAttribute("questionList", questionList);

        return "index";
    }

}
