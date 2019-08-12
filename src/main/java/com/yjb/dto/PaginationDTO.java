package com.yjb.dto;

import lombok.Data;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Long totalPage;
    private Long totalCount;

    public PaginationDTO(Integer page, Integer size, Long totalCount) {
        this.page = page;
        this.totalCount = totalCount;

        totalPage = (totalCount%size == 1 ? totalCount/size+1 : totalCount/size);
        showPrevious = (page <= 1 ? false  : true);
        showNext = (page >= totalPage ? false : true);

        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }

            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        showFirstPage = (pages.contains(1) ? false : true);
        showEndPage = (pages.contains(totalPage) ? false : true);


    }
}
