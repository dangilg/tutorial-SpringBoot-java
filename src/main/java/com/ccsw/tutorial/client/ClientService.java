package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.loan.model.Loan;

import java.util.List;

public interface ClientService {

    /**
     * Obtiene un {@link Client} según su id
     * @param id PK de la entidad
     * @return {@link Client} según el id dado
     */
    Client getById(Long id);

    /**
     * Devuelve la lista de todos los {@link Client} de la BD
     * @return {@link List} de {@link Client}
     */
    List<Client> findAll();

    /**
     * Guarda o modifica un {@link Client}.
     * Si id==null, se crea y guarda un nuevo {@link Client}
     * @param id Pk de la entidad a  mofificar
     * @param dto {@link ClientDto} nuevos datos del cliente
     * @return {@link Client} con los nuevos datos
     * @throws NoIdFoundException si al modificar, el {@link Client} no existe en la BD
     */
    Client save(Long id, ClientDto dto) throws NoIdFoundException;

    /**
     * Borra un {@link Client} de la BD
     * @param id PK de la entidad
     * @throws NoIdFoundException si el {@link Client} a borrar no existe en la BD
     */
    void delete(Long id)throws NoIdFoundException;

    /**
     * Comprueba si un {@link Client} se puede borrar o no
     * @param id PK de la entidad
     * @return {@link DeleteCheckResponseDto} verdadera si se puede borrar.
     * {@link DeleteCheckResponseDto} falsa y lista de {@link Loan} activos en los que está
     */
    DeleteCheckResponseDto isDeleteable(Long id);
}
