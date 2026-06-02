package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

/**
 * @author dgilguti
 * Clase que genera una especificación para la búsqueda de {@link Loan} cuya fecha de inicio y de fin contenga a la fecha de referencia
 */
public class DateBetweenLoanSpecification {

    /**
     *
     * @param referenceDate {@link Date} fecha de referencia
     * @return {@link Specification} con la {@link Loan} cuyas fechas de inicio y fin contenga la fecha de referencia
     */
    public static Specification<Loan> dateBetween(Date referenceDate){
        return (Root<Loan> root,CriteriaQuery<?> query, CriteriaBuilder cb)->{
            if(referenceDate==null){
                return null;
            }

            Predicate startBeforeOrEqual =
                    cb.lessThanOrEqualTo(root.get("startDate"),referenceDate);
            Predicate endAfterOrEqual =
                    cb.greaterThanOrEqualTo(root.get("endDate"),referenceDate);

            return cb.and(startBeforeOrEqual,endAfterOrEqual);
        };
    }
}
