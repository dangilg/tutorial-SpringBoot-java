package com.ccsw.tutorial.author.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;

/**
 * @author ccsw
 *
 */
public class AuthorSearchDto {

    private PageableRequest pageable;

    /**
     *
     * @return PageableRequest pageable
     */
    public PageableRequest getPageable() {
        return pageable;
    }

    /**
     *
     * @param pageable nuevo valor de pagebale
     */
    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }
}
