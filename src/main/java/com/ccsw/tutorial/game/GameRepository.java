package com.ccsw.tutorial.game;

import com.ccsw.tutorial.game.model.Game;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ccsw
 *
 */
public interface GameRepository extends CrudRepository<Game, Long>, JpaSpecificationExecutor<Game> {

    /**
     * Metodo que devuelve todos los {@link Game} según la {@link GameSpecification} dada
     * @param spec {@link GameSpecification}
     * @return {@link List} de {@link Game} que cumplen la especificacion.
     */
    @Override
    @EntityGraph(attributePaths = { "category", "author" })
    List<Game> findAll(Specification<Game> spec);


    /**
     * Devuelve la {@link List} de {@link Game} válidos para un {@link com.ccsw.tutorial.loan.model.Loan} según las reglas de negocio.
     * Un {@link Game} no puede estar en más de un {@link com.ccsw.tutorial.loan.model.Loan} el mismo dia
     * @param loanId PK del {@link com.ccsw.tutorial.loan.model.Loan}
     * @param startDate {@link LocalDate} fecha de inico de {@link com.ccsw.tutorial.loan.model.Loan}
     * @param endDate {@link LocalDate} fecha de fin de {@link com.ccsw.tutorial.loan.model.Loan}
     * @return {@link List} de {@link Game} que son válidos según las reglas de negocio y los parametros dados
     */
    @Query("""
            SELECT g FROM Game g
            WHERE NOT EXISTS (
                SELECT 1
                FROM Loan l
                WHERE l.game.id = g.id
                AND (:loanId IS NULL OR l.id <> :loanId)
                AND NOT(
                    l.endDate< :startDate OR
                    l.startDate > :endDate
                )    
            )
            
            OR g.id = (
                    SELECT l2.game.id
                    FROM Loan l2
                    WHERE l2.id = :loanId
                )
            
            """)
    List<Game> findAvailableGames(
            @Param("loanId") Long loanId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Devuelve una {@link List} de {@link Game} con el  mismo {@link com.ccsw.tutorial.category.model.Category}
     * @param categoryId PK de {@link com.ccsw.tutorial.category.model.Category}
     * @return {@link List} de {@link Game}
     */
    List<Game> findByCategoryId(Long categoryId);

    /**
     * Devuelve una {@link List} de {@link Game} con el  mismo {@link com.ccsw.tutorial.author.model.Author}
     * @param authorId PK de {@link com.ccsw.tutorial.author.model.Author}
     * @return {@link List} de {@link Game}
     */
    List<Game> findByAuthorId(Long authorId);
}
