package com.yjb.task;

import com.yjb.cache.HotTagCache;
import com.yjb.dto.HotTagDTO;
import com.yjb.mapper.QuestionMapper;
import com.yjb.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTask {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    HotTagCache hotTagCache;

    @Scheduled(fixedRate = 30000)
    public void hotTagSchedule() {
        log.info("hotTagSchedule start {}", new Date());
        int offset = 0;
        int limit = 20;

        Map<String, HotTagDTO> tags = new HashMap<>();
        List<Question> questions = new ArrayList<>();
        while (offset == 0 || questions.size() == limit) {
            questions = questionMapper.selectByExampleWithRowbounds(null, new RowBounds(offset, limit));
            questions.forEach(question -> {
                String[] split = question.getTag().split(",");
                for (String tag : split) {
                    if (tags.containsKey(tag)) {
                        HotTagDTO hotTagDTO = tags.get(tag);
                        hotTagDTO.setPriority(hotTagDTO.getPriority()+5+question.getCommentCount());
                        hotTagDTO.setQuestionNum(hotTagDTO.getQuestionNum()+1);
                    }else {
                        HotTagDTO hotTagDTO = new HotTagDTO();
                        hotTagDTO.setName(tag);
                        hotTagDTO.setPriority(5+question.getCommentCount());
                        hotTagDTO.setQuestionNum(1);
                        tags.put(tag, hotTagDTO);
                    }
                }
            });
            offset += limit;
        }
        hotTagCache.updateHots(tags);
        log.info("hotTagSchedule end {}", new Date());
    }

}
