package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * @author dgilguti
 */
@Tag(name = "Loan", description = "API of Loans")
@RequestMapping(value = "/loan")
@RestController
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    ModelMapper mapper;

    @Autowired
    LoanService loanService;

    /**
     * Metodo para obtener la lista paginada de las {@link Loan} que cumplen los filtros
     * @param dto {@link PageFilterDto} dto con las especificaciones de filtros y paginación
     * @return {@link Page} de {@link LoanDto} que cumplen las especificaciones del dto
     */
    @Operation(summary = "Find", description = "Method that returns a page of filtered Loans")
    @RequestMapping(path="", method = RequestMethod.POST)
    public Page<LoanDto> find(@RequestBody PageFilterDto dto){


        Page<Loan> page = loanService.findPageFiltered(dto);


        Page<LoanDto> ret =new PageImpl<>(page.getContent().stream().map(e->mapper.map(e,LoanDto.class)).collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
        return  ret;
    }

    /**
     * Metodo que devuelve los datos de disponibilidad para las {@link Loan} según los parámetros especificaso en el {@link AvailableRequestDto}
     * @param dto {@link AvailableRequestDto} con los parámetos que especifican la disponibilidad de una {@link Loan}
     * @return {@link AvailableResponseDto} con los datos de disponibilidad según el dto
     */
    @Operation(summary = "Available",description = "Method that returns the availables clients, games and dates due to the data in the dto")
    @RequestMapping(path = "/available",method = RequestMethod.POST)
    public AvailableResponseDto available(@RequestBody AvailableRequestDto dto){

        return  loanService.calculateAvailability(dto);
    }

    /**
     * Metodo que guarda o modifica una {@link Loan} según si tiene id o no.
     * Si la petición no contiene un id, se crea una nueva {@link Loan}
     * @param id PK de la entidad
     * @param dto {@link AvailableRequestDto} con los datos de la {@linkL loan}
     */
    @Operation(summary = "save", description = "Mathod that save or updates a loan")
    @RequestMapping(path = {"/save","/save/{id}"},method = RequestMethod.PUT)
    public void save(@PathVariable (name="id", required = false) Long id, @RequestBody AvailableRequestDto dto){

        loanService.save(id,dto);
    }

    /**
     * Metodo que devuleve el id del último elemento de la BD
     * @return Id
     */
    @Operation(summary = "lastId", description = "Method that returns the lastId in the system")
    @RequestMapping(path = "/lastId",method = RequestMethod.GET)
    public long count(){
        return loanService.getLastId();
    }

    /**
     * Metodo para borrar una {@link Loan} si es posible
     * @param id PK de la entidad
     */
    @Operation(summary = "delete", description = "Method that deletes a Loan")
    @RequestMapping(path = "/{id}",method = RequestMethod.DELETE)
    public void delete(@PathVariable (name="id",required = true)Long id){

        loanService.delete(id);
    }

    /**
     * Metodo que verifica si una {@link Loan} se puede borrar
     * @param id PK de la entidad
     * @return {@link DeleteCheckResponseDto} verdadera si se puede borrar.
     * {@link DeleteCheckResponseDto} falsa si {@link Loan} está activa
     */
    @Operation(summary = "canDelete", description = "Method that check if a Loan can be delete")
    @RequestMapping(path = "/{id}/can-delete",method = RequestMethod.GET)
    public DeleteCheckResponseDto isDeleteable(@PathVariable("id")Long id){
        return loanService.isDeleteable(id);
    }
}
