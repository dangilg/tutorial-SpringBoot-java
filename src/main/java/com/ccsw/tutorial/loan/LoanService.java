package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidLoanException;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import org.springframework.data.domain.Page;

public interface LoanService {

    /**
     * Devuelve la lista paginada de {@link Loan} que cumplan los filtros
     * @param dto {@link PageFilterDto} con los filtros a cumplir y las especificaciones de la paginación
     * @return {@link Page} de {@link Loan} que cumplen los filtros
     */
    Page<Loan> findPageFiltered(PageFilterDto dto);

    /**
     * Metodo que devuleve los valores disponibles para una {@link Loan} según los que ya tiene en su dto
     * @param dto {@link AvailableRequestDto} datos actuales de la {@link Loan}
     * @return {@link AvailableResponseDto} respuesta con los valores dispobibles
     */
    AvailableResponseDto calculateAvailability(AvailableRequestDto dto);

    /**
     * Metodo que guarda o modifica una {@link Loan}.
     * Si id == null, se crea y se guarda una nueva {@link Loan}
     * Para que se pueda guardar debe cumplir las siguientes reglas de negocio:
     * - Un {@link com.ccsw.tutorial.client.model.Client} puede tener  como maximo 2 {@link Loan} el mismo dia
     * - Un {@link com.ccsw.tutorial.game.model.Game} solo puede estar en un {@link Loan} el mismo dia.
     * - Una {@link Loan} puede durar 14 días como máximo.
     * @param id PK de la entidad
     * @param dto {@link AvailableRequestDto} datos de la {@link Loan}
     * @throws NotValidLoanException si no existe la {@link Loan}
     * @throws NotValidLoanException si el dto es null
     * @throws NotValidLoanException si alguno de los valores del dto es null
     * @throws NotValidLoanException si el {@link com.ccsw.tutorial.client.model.Client} o el {@link com.ccsw.tutorial.game.model.Game} dados en el dto no existen en la BD
     * @throws NotValidLoanException si la fecha de fin es anterior a la de inicio, o si es mayor de 14 días desde la de inicio
     * @throws NotValidLoanException si las fechas no son válidas para el {@link com.ccsw.tutorial.client.model.Client} o para el {@link com.ccsw.tutorial.game.model.Game} dados
     */
    void save(Long id, AvailableRequestDto dto) throws NotValidLoanException;

    /**
     * Metodo que obtiene le id el último elemento de la tabla de {@link Loan}
     * @return PK
     */
    long getLastId();

    /**
     * Metodo que elimina una {@link Loan} de la BD si es posible}
     * @param id
     */
    void delete(Long id);

    /**
     * Metodo que verifica si una {@link Loan} está activa o no, para poder eliminarla.
     * Si está activa no puede ser eliminada.
     * @param id PK de la entidad
     * @return {@link DeleteCheckResponseDto} verdadera si no está activa.
     * {@link DeleteCheckResponseDto} falsa si está activa con las fechas en las que está activa.
     */
    DeleteCheckResponseDto isDeleteable(Long id);
}
