package com.yjb.controller;

import com.yjb.dto.PaginationDTO;
import com.yjb.mapper.QuestionMapper;
import com.yjb.mapper.UserMapper;
import com.yjb.model.User;
import com.yjb.service.NotificationService;
import com.yjb.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    /**
     * 根据action参数来判断是获取问题还是通知
     * @param action
     * @param page
     * @param size
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/profile/{action}")
    public String toProfile(@PathVariable("action") String action,
                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                            @RequestParam(name = "size", defaultValue = "10") Integer size,
                            @SessionAttribute(name = "user", required = false) User user,
                            Model model) {

        if (user == null) {
            return "redirect:/";
        }

        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO questionList = questionService.listByUserId(user.getId(), page, size);
            model.addAttribute("pagination", questionList);
        }else if ("replies".equals(action)) {
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            PaginationDTO questionList = notificationService.list(user.getId(), page, size);
            model.addAttribute("pagination", questionList);
        }
        return "profile";
    }
}
