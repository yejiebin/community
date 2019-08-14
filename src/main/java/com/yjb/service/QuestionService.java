package com.yjb.service;

import com.yjb.dto.PaginationDTO;
import com.yjb.dto.QuestionDTO;
import com.yjb.dto.QuestionQueryDTO;
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

        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    public PaginationDTO list(String search, String tag, Integer page, Integer size) {
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();

        if (StringUtils.isNotBlank(search)) {
            String[] split = search.split(" ");
            search = Arrays.stream(split)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> t.replace("+", "").replace("*", ""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
            questionQueryDTO.setSearch(search);
        }

        if(StringUtils.isNotBlank(tag)){
            tag = tag.replace("+", "").replace("*", "");
            questionQueryDTO.setTag(tag);
        }


        long totalCount = questionExtMapper.countByQuestionQueryDTO(questionQueryDTO);
        long totalPage = totalCount % size != 0 ? totalCount / size + 1 : totalCount / size;
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>(page, size, totalCount);
        if (totalCount <= 0) {
            return paginationDTO;
        }

        if (page < 1) page = 1;
        if (page > totalPage) page = Math.toIntExact(totalPage);

        questionQueryDTO.setOffset((page-1)*size);
        questionQueryDTO.setSize(size);

        List<Question> questions = questionExtMapper.selectByQuestionQueryDTO(questionQueryDTO);
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOs.add(questionDTO);
        }



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
