package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Client", description = "API of Client")
@RequestMapping(value = "/client")
@RestController
@CrossOrigin(origins = "*")
public class ClientController {
    @Autowired ModelMapper mapper;
    @Autowired ClientService clientService;

    @Operation(summary = "find", description = "method that returns all Clients")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<ClientDto> findAll() {
        List<Client> clientList = clientService.findAll();
        return clientList.stream().map(e -> mapper.map(e, ClientDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Save or Update", description = "Method that saves or updates a Client")
    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.PUT)
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "client doesn't exists"),
            @ApiResponse(responseCode = "401", description = "invalid token"),
            @ApiResponse(responseCode = "409", description = "Client name Already exists")
    })

    public ClientDto save(@PathVariable(name = "id", required = false) Long id, @RequestBody ClientDto dto) throws NoIdFoundException {
        System.out.println("controllerClientSave");
        Client client = this.clientService.save(id, dto);
        return mapper.map(client, ClientDto.class);
    }

    @Operation(summary = "Delete", description = "Method that deletes a Client")
    @RequestMapping(path = "{id}",method = RequestMethod.DELETE)
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "client not found"),
            @ApiResponse(responseCode = "409", description = "cant delete a client in a loan")
    })
    public void delete(@PathVariable("id")Long id) throws NoIdFoundException, NotDeleteableException{

        DeleteCheckResponseDto deleteable = isDeleteable(id);
        if(!deleteable.isCanDelete()){
            throw new NotDeleteableException(deleteable.getReason());
        }
        this.clientService.delete(id);
    }


    @Operation(summary = "Can-Delete",description = "Method thar checks if a Client is deleteable")
    @RequestMapping(path = "/{id}/can-delete",method = RequestMethod.GET)
    public DeleteCheckResponseDto isDeleteable(@PathVariable("id")Long id){
        if(id==3){
            return new DeleteCheckResponseDto(true,"");
        }
        else {
            return new DeleteCheckResponseDto(false,"IN_USE");
        }

        /*
        if (this.loanRepository.existsByClientId(id)) {
            return new DeleteCheckResponseDto(false, "IN_USE");
        } else {
            return new DeleteCheckResponseDto(true, "");
        }

         */
    }


}
