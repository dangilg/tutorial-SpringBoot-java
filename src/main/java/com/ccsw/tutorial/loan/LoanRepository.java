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

/**
 * @author dgilguti
 */
public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    /**
     * Petición a la BD que devuelve todas las {@link Loan} que cumplen las especificaciones, en el formato que especifica la paginación
     * @param spec Especificaciones que filtran las {@link Loan}
     * @param pageable Define como devolver la lista según la paginación
     * @return {@link Page} de {@link Loan} con aquellas que cumplen las especificaciones, en el formato de paginación dado
     */
    @Override
    @EntityGraph(attributePaths = {
            "game",
            "client"
    })
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);

    /**
     * Petición a la BD que devuelve las {@link Loan} que tienen el mismo {@link com.ccsw.tutorial.game.model.Game} y se solapan en el tiempo
     * @param loanId id de la {@link Loan}, para excluirla de la busqueda
     * @param gameId id del {@link  com.ccsw.tutorial.game.model.Game}
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return {@link List} de {@link Loan} que cumplen la query
     */
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

    /**
     * Petición a la BD que devuelve las {@link Loan} que tengan el mismo {@link com.ccsw.tutorial.client.model.Client} y se solapan en el tiempo.
     * @param loanId id de la {@link Loan} para excluirla de la busqueda
     * @param clientId id del {@link com.ccsw.tutorial.client.model.Client}
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return {@link List} de {@link Loan} que cumplen la query.
     */
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

    /**
     * Metodo que obtine las {@link Loan} con el mismo {@link com.ccsw.tutorial.game.model.Game}
     * @param gameId PK de {@link com.ccsw.tutorial.game.model.Game}
     * @return {@link List} de {@link Loan} con el mismo {@link com.ccsw.tutorial.game.model.Game}
     */
    @Query("""
            SELECT l FROM Loan l
            WHERE l.game.id =:gameId
            ORDER BY l.startDate
            """)
    List<Loan> findLoansByGameOrdered(
            @Param("gameId") long gameId
    );

    /**
     * Metodo que obtiene el id del último elemento en la tabla de {@link Loan}
     * @return id
     */
    @Query("""
            SELECT MAX(l.id) FROM Loan l
            """)
    Long getLastId();

    /**
     * Metodo que devuelve una {@link Loan} según su {@link com.ccsw.tutorial.client.model.Client}
     * @param id PK de {@link com.ccsw.tutorial.client.model.Client}
     * @return {@link List} de {@link Loan} con ese {@link com.ccsw.tutorial.client.model.Client}
     */
    List<Loan> findByClientId(Long id);
}
