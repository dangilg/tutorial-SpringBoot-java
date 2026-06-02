package com.ccsw.tutorial.loan.model.filter;

import com.ccsw.tutorial.common.pagination.PageableRequest;

/**
 * @author dgilguti
 *
 * Clase que espeficifca el cuerpo del mensaje de una petición de busqueda filtrada y paginada
 */
public class PageFilterDto {
    private PageableRequest pageable;

    private FilterDataModel filters;

    /**
     *
     * @return {@link FilterDataModel} filtros
     */
    public FilterDataModel getFilters(){
        return this.filters;
    }

    /**
     *
     * @return {@link PageableRequest} paginación
     */
    public PageableRequest getPageable(){
        return this.pageable;
    }

    public PageFilterDto(PageableRequest pageable, FilterDataModel filters){
        this.pageable = pageable;
        this.filters = filters;
    }

    public PageFilterDto(){
        pageable= null;
        filters = null;
    }
}
