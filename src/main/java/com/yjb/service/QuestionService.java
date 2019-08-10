package com.yjb.service;

import com.yjb.dto.PaginationDTO;
import com.yjb.dto.QuestionDTO;
import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import com.yjb.mapper.QuestionMapper;
import com.yjb.mapper.UserMapper;
import com.yjb.model.Question;
import com.yjb.model.QuestionExample;
import com.yjb.model.User;
import com.yjb.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;

    public User findByToken(String token) {
        UserExample example = new UserExample();
        example.createCriteria()
                .andTokenEqualTo(token);
        return userMapper.selectByExample(example).get(0);
    }

    public PaginationDTO list(Integer page, Integer size) {
        long totalCount = questionMapper.countByExample(null);
        Integer totalPage = Math.toIntExact(totalCount % size != 0 ? totalCount / size + 1 : totalCount / size);
        if (page < 1) page = 1;
        if (page > totalPage) page = totalPage;

        RowBounds rowBounds = new RowBounds((page-1)*size, size);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(null, rowBounds);
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOs.add(questionDTO);
        }


        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>(page, size, (int) totalCount);

        paginationDTO.setData(questionDTOs);
        return paginationDTO;
    }

    public PaginationDTO listByUserId(Integer userId, Integer page, Integer size) {
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Long totalCount = questionMapper.countByExample(example);
        Integer totalPage = Math.toIntExact(totalCount % size != 0 ? totalCount / size + 1 : totalCount / size);
        if (page < 1) page = 1;
        if (page > totalPage) page = totalPage;

        example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        RowBounds rowBounds = new RowBounds((page-1)*size, size);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, rowBounds);
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOs.add(questionDTO);
        }


        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<QuestionDTO>(page, size, Math.toIntExact(totalCount));

        paginationDTO.setData(questionDTOs);
        return paginationDTO;
    }

    public QuestionDTO findById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;

    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        }else {
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.updateByPrimaryKeySelective(question);
        }
    }
}
