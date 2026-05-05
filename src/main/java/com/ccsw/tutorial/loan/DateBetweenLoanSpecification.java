package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class DateBetweenLoanSpecification {

    public static Specification<Loan> dateBetween(Date referenceDate){
        return (Root<Loan> root,CriteriaQuery<?> query, CriteriaBuilder cb)->{
            if(referenceDate==null){
                return null;
            }

            Predicate startBeforeOrEqual =
                    cb.lessThanOrEqualTo(root.get("start_date"),referenceDate);
            Predicate endAfterOrEqual =
                    cb.greaterThanOrEqualTo(root.get("end_date"),referenceDate);

            return cb.and(startBeforeOrEqual,endAfterOrEqual);
        };
    }
}
