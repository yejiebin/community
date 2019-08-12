package com.yjb.service;

import com.yjb.dto.PaginationDTO;
import com.yjb.dto.QuestionDTO;
import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import com.yjb.mapper.QuestionExtMapper;
import com.yjb.mapper.QuestionMapper;
import com.yjb.mapper.UserMapper;
import com.yjb.model.Question;
import com.yjb.model.QuestionExample;
import com.yjb.model.User;
import com.yjb.model.UserExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    QuestionExtMapper questionExtMapper;

    public User findByToken(String token) {
        UserExample example = new UserExample();
        example.createCriteria()
                .andTokenEqualTo(token);
        return userMapper.selectByExample(example).get(0);
    }

    public PaginationDTO list(Integer page, Integer size) {

        long totalCount = questionMapper.countByExample(null);
        long totalPage = totalCount % size != 0 ? totalCount / size + 1 : totalCount / size;
        if (page < 1) page = 1;
        if (page > totalPage) page = Math.toIntExact(totalPage);

        QuestionExample example = new QuestionExample();
        example.setOrderByClause("gmt_create desc");
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

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>(page, size, totalCount);

        paginationDTO.setData(questionDTOs);
        return paginationDTO;
    }

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Long totalCount = questionMapper.countByExample(example);
        Long totalPage = totalCount % size != 0 ? totalCount / size + 1 : totalCount / size;
        if (page < 1) page = 1;
        if (page > totalPage) page = Math.toIntExact(totalPage);

        example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
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


        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<QuestionDTO>(page, size, totalCount);

        paginationDTO.setData(questionDTOs);
        return paginationDTO;
    }

    public QuestionDTO findById(Long id) {
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

    public Integer incView(Long id) {
        return questionExtMapper.incView(id);
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

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        //正则表达式
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays
                .stream(tags)
                .filter(StringUtils::isNotBlank)
                .map(t -> t.replace("+", "").replace("*", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("|"));

        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOs = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return  questionDTOs;
    }
}
