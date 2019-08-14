package com.yjb.mapper;

import com.yjb.dto.QuestionQueryDTO;
import com.yjb.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    Integer incView(Long id);

    int incCommentCount(Long id);

    List<Question> selectRelated(Question question);

    long countByQuestionQueryDTO(QuestionQueryDTO questionQueryDTO);

    List<Question> selectByQuestionQueryDTO(QuestionQueryDTO questionQueryDTO);
}
