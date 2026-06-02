package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import com.ccsw.tutorial.loan.model.Loan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author dgilguti
 */
@Tag(name = "Client", description = "API of Client")
@RequestMapping(value = "/client")
@RestController
@CrossOrigin(origins = "*")
public class ClientController {
    @Autowired ModelMapper mapper;
    @Autowired ClientService clientService;

    /**
     * Metodo para obtener todos los {@link Client} de la BD
     * @return {@link List} de {@link ClientDto}
     */
    @Operation(summary = "find", description = "method that returns all Clients")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<ClientDto> findAll() {
        List<Client> clientList = clientService.findAll();
        return clientList.stream().map(e -> mapper.map(e, ClientDto.class)).collect(Collectors.toList());
    }

    /**
     * Metodo para guardar o modificar un {@link Client}.
     * Si no tiene id, creará y guardará un nuevo {@link Client}
     * @param id PK de la entidad a modificar
     * @param dto {@link ClientDto} con los nuevos datos del {@link Client}
     * @return {@link ClientDto} nuevo
     */
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Client")
    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.PUT)
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "client doesn't exists"),
            @ApiResponse(responseCode = "409", description = "Client name Already exists")
    })

    public ClientDto save(@PathVariable(name = "id", required = false) Long id, @RequestBody ClientDto dto){

        Client client = this.clientService.save(id, dto);
        return mapper.map(client, ClientDto.class);
    }

    /**
     * Metodo para borrar un {@link Client} si se puede.
     * @param id PK de la entidad a borrar
     */
    @Operation(summary = "Delete", description = "Method that deletes a Client")
    @RequestMapping(path = "{id}",method = RequestMethod.DELETE)
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "client not found"),
            @ApiResponse(responseCode = "409", description = "cant delete a client in a loan")
    })
    public void delete(@PathVariable("id")Long id){
        this.clientService.delete(id);
    }

    /**
     * Metodo para verificar si un {@link Client} se puede borrar o no
     * @param id PK de la entidad a verificar
     * @return {@link DeleteCheckResponseDto} verdadera si se puede borrar
     * {@link DeleteCheckResponseDto} falsa y lista de {@link Loan} Activos si no se puede borrar
     */
    @Operation(summary = "Can-Delete",description = "Method thar checks if a Client is deleteable")
    @RequestMapping(path = "/{id}/can-delete",method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "client not Found")
    })
    public DeleteCheckResponseDto isDeleteable(@PathVariable("id")Long id){
        return clientService.isDeleteable(id);
    }


}
