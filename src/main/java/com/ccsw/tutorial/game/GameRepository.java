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

    @Override
    @EntityGraph(attributePaths = { "category", "author" })
    List<Game> findAll(Specification<Game> spec);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByAuthorId(Long authorId);


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


    List<Game> findByCategoryId(Long categoryId);

    List<Game> findByAuthorId(Long authorId);
}
