package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClientRepository extends CrudRepository<Client,Long> {

    /**
     * Verifica si un {@link Client} existe en la BD dado su name
     * @param name Nombre del {@link Client} a buscar
     * @return True si existe.
     * False si no existe
     */
    boolean existsByName(String name);

    /**
     * Devuelve la lista de {@link Client} que son válidos para un {@link com.ccsw.tutorial.loan.model.Loan} si se cumplen las reglas de negocio:
     * Un {@link Client} no puede tener más de 2 {@link com.ccsw.tutorial.loan.model.Loan} el mismo dia
     * @param loanId PK del {@link com.ccsw.tutorial.loan.model.Loan} a verificar
     * @param startDate {@link LocalDate} fecha de inicio del {@link com.ccsw.tutorial.loan.model.Loan}
     * @param endDate {@link LocalDate} fecha de fin del {@link com.ccsw.tutorial.loan.model.Loan}
     * @return {@link List} de {@link Client} que son válidos para obtener un {@link com.ccsw.tutorial.loan.model.Loan} según las reglas de negocio
     */
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
