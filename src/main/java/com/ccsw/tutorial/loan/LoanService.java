package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.PageFilterDto;
import org.springframework.data.domain.Page;

public interface LoanService {

    Page<Loan> findPageFiltered(PageFilterDto dto);
}
