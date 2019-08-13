package com.yjb.controller;

import com.yjb.dto.NotificationDTO;
import com.yjb.enums.NotificationTypeEnum;
import com.yjb.model.User;
import com.yjb.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    /**
     * 根据id获取具体通知
     * @param user
     * @param id
     * @return
     */
    @GetMapping("/notification/{id}")
    public String profile(@SessionAttribute(name = "user", required = false) User user,
                          @PathVariable("id") Long id) {
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);
        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
            || NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()) {
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else {
            return "redirect:/";
        }
    }

}
