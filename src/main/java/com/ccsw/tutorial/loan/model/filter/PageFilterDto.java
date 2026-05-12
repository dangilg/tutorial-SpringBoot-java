package com.ccsw.tutorial.loan.model.filter;

import com.ccsw.tutorial.common.pagination.PageableRequest;

public class PageFilterDto {
    private PageableRequest pageable;

    private FilterDataModel filters;

    public FilterDataModel getFilters(){
        return this.filters;
    }

    public PageableRequest getPageable(){
        return this.pageable;
    }
}
