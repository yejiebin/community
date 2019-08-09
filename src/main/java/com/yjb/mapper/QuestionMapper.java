package com.yjb.mapper;

import com.yjb.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.boot.autoconfigure.session.NonUniqueSessionRepositoryException;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("insert into question(title, description, tag, gmt_create, gmt_modified, creator) values(#{title}, #{description}, #{tag}, #{gmtCreate}, #{gmtModified}, #{creator})")
    void insert(Question question);

    @Select("select * from question")
    List<Question> list();

}
