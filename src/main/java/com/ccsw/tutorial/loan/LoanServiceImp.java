package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.criteria.GenericSpecification;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.loan.model.FilterDataModel;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.PageFilterDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
@Transactional
public class LoanServiceImp implements LoanService{
    @Autowired
    LoanRepository loanRepository;

    @Override
    public Page<Loan> findPageFiltered(PageFilterDto dto) {
        FilterDataModel filters = dto.getFilters();
        PageableRequest pageable = dto.getPageable();

        GenericSpecification<Loan> clientSpec = new GenericSpecification<Loan>(new SearchCriteria("client.id",":",filters.getClientId()));
        GenericSpecification<Loan> gameSpec = new GenericSpecification<Loan>(new SearchCriteria("game.id",":",filters.getGameId()));

        //todo -> revisar excepciones posibles en un TryCatch
        Date referenceDate = Date.valueOf(filters.getDate());

        Specification<Loan> spec = clientSpec.and(gameSpec).and(
                DateBetweenLoanSpecification.dateBetween(referenceDate)
        );

        return this.loanRepository.findAll(spec, pageable.getPageable());
    }
}
