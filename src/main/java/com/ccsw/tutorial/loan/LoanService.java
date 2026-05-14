package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import org.springframework.data.domain.Page;

public interface LoanService {

    Page<Loan> findPageFiltered(PageFilterDto dto);

    AvailableResponseDto calculateAvailability(AvailableRequestDto dto);

    void save(Long id, AvailableRequestDto dto);

    long getCount();
}
