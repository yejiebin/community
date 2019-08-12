package com.yjb.mapper;

import com.yjb.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    Integer incView(Long id);

    int incCommentCount(Long id);

    List<Question> selectRelated(Question question);
}
