package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

//todo-> a lo mejor hay q cambiar de CrudRepository a JpaRepository por la paginacion
public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    @Override
    @EntityGraph(attributePaths = {
            "game",
            "client"
    })
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);

    @Query("""
            SELECT l FROM Loan l
            WHERE l.game.id = :gameId
            AND NOT (
            l.endDate < :startDate OR 
            l.startDate> :endDate
            )
            AND (:loanId IS NULL OR l.id <>:loanId)
            """)
    List<Loan> findOverlappingLoansByGame(
            @Param("loanId") Long loanId,
            @Param("gameId") long gameId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COUNT(l)
            FROM Loan l
            WHERE l.client.id = :clientId
            AND NOT(
            l.endDate< :startDate OR
            l.startDate>:endDate
            )
            AND (:loanId IS NULL OR l.id<> :loanId)
            """)
    long countOverlappingLoansByClient(
            @Param("loanId") Long loanId,
            @Param("clientId") long clientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT l FROM Loan l
            WHERE l.game.id =:gameId
            ORDER BY l.startDate
            """)
    List<Loan> findLoansByGameOrdered(
            @Param("gameId") long gameId
    );


    @Query("""
            SELECT MAX(l.id) FROM Loan l
            """)
    Long getLastId();


    List<Loan> findByClientId(Long id);
}
