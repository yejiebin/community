package com.yjb.service;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.yjb.dto.QuestionDTO;
import com.yjb.mapper.QuestionMapper;
import com.yjb.mapper.UserMapper;
import com.yjb.model.Question;
import com.yjb.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;


    public User findByToken(String token) {
        return userMapper.findByToken(token);
    }

    public List list() {
        List<Question> questions = questionMapper.list();
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOs.add(questionDTO);
        }
        return questionDTOs;
    }
}
