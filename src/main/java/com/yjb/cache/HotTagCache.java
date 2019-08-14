package com.yjb.cache;

import com.yjb.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Component
@Data
public class HotTagCache {
    private List<HotTagDTO> hots;

    private Integer max = 10;

    public void updateHots(Map<String, HotTagDTO> tags) {
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue(max);

        tags.forEach((name, hotTagDTO) -> {
            if (priorityQueue.size() < max) {
                priorityQueue.add(hotTagDTO);
            }else {
                HotTagDTO minHot = priorityQueue.peek();
                if (minHot != null && hotTagDTO.compareTo(minHot) > 0) {
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });

        List<HotTagDTO> tagList = new ArrayList();
        while (priorityQueue.size() > 0) {
            tagList.add(0, priorityQueue.poll());
        }

        hots = tagList;
    }
}
