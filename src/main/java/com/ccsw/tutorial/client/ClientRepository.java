package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClientRepository extends CrudRepository<Client,Long> {

    boolean existsByName(String name);

    @Query("""
            SELECT c FROM Client c
            WHERE(
                SELECT COUNT(l)
                FROM Loan l
                WHERE l.client.id = c.id
                AND (:loanId IS NULL OR l.id <> :loanId)
                AND NOT (
                    l.endDate < :startDate OR 
                    l.startDate > :endDate
                )
            ) < 2
            """)
    List<Client> findAvailableClients(
            @Param("loanId") Long loanId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
