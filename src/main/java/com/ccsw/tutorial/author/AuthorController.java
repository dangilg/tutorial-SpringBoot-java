package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.author.model.AuthorSearchDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidTokenException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ccsw
 *
 */
@Tag(name = "Author", description = "API of Author")
@RequestMapping(value = "/author")
@RestController
@CrossOrigin(origins = "*")
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @Autowired
    ModelMapper mapper;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    JwtService tokenService;

    /**
     * Método para recuperar un listado paginado de {@link Author}
     *
     * @param dto dto de búsqueda
     * @return {@link Page} de {@link AuthorDto}
     */
    @Operation(summary = "Find Page", description = "Method that return a page of Authors")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public Page<AuthorDto> findPage(@RequestBody AuthorSearchDto dto) {

        Page<Author> page = this.authorService.findPage(dto);

        return new PageImpl<>(page.getContent().stream().map(e -> mapper.map(e, AuthorDto.class)).collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    /**
     * Método para crear o actualizar un {@link Author}
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     */
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Author")
    @RequestMapping(path = { "", "/{id}" }, method = RequestMethod.PUT)
    @ApiResponses({ @ApiResponse(responseCode = "404", description = "category doesn't exists"), @ApiResponse(responseCode = "401", description = "invalid token") })
    public void save(@PathVariable(name = "id", required = false) Long id, @RequestBody AuthorDto dto, @RequestHeader("Authorization") String authorization) throws NoIdFoundException, NotValidTokenException {
        String token = authorization.replace("Bearer", "");
        this.tokenService.isTokenValid(token);
        this.authorService.save(id, dto);
    }

    /**
     * Método para crear o actualizar un {@link Author}
     *
     * @param id PK de la entidad
     */
    @Operation(summary = "Delete", description = "Method that deletes a Author")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ApiResponses({ @ApiResponse(responseCode = "404", description = "category doesn't exists"), @ApiResponse(responseCode = "401", description = "invalid token") })
    public void delete(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) throws NoIdFoundException, NotValidTokenException {
        String token = authorization.replace("Bearer", "");
        this.tokenService.isTokenValid(token);
        this.authorService.delete(id);
    }

    /**
     * Recupera un listado de autores {@link Author}
     *
     * @return {@link List} de {@link AuthorDto}
     */
    @Operation(summary = "Find", description = "Method that return a list of Authors")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<AuthorDto> findAll() {

        List<Author> authors = this.authorService.findAll();

        return authors.stream().map(e -> mapper.map(e, AuthorDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Can-Delete", description = "Method that check if a Category can be deleted")
    @RequestMapping(path = "/{id}/can-delete", method = RequestMethod.GET)
    public DeleteCheckResponseDto isDeleteable(@PathVariable("id") Long id) {
        if (this.gameRepository.existsByAuthorId(id)) {
            return new DeleteCheckResponseDto(false, "IN_USE");
        } else {
            return new DeleteCheckResponseDto(true, "");
        }
    }

}
