package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidTokenException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.security.JwtService;
import io.jsonwebtoken.Header;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ccsw
 *
 */
@Tag(name = "Category", description = "API of Category")
@RequestMapping(value = "/category")
@RestController
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ModelMapper mapper;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    JwtService tokenService;

    /**
     * Método para recuperar todas las {@link Category}
     *
     * @return {@link List} de {@link CategoryDto}
     */
    @Operation(summary = "Find", description = "Method that return a list of Categories")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<CategoryDto> findAll() {

        List<Category> categories = this.categoryService.findAll();

        return categories.stream().map(e -> mapper.map(e, CategoryDto.class)).collect(Collectors.toList());
    }

    /**
     * Método para crear o actualizar una {@link Category}
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     */
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Category")
    @RequestMapping(path = { "", "/{id}" }, method = RequestMethod.PUT)
    @ApiResponses({ @ApiResponse(responseCode = "404", description = "category doesn't exists"), @ApiResponse(responseCode = "401", description = "invalid token") })
    public CategoryDto save(@PathVariable(name = "id", required = false) Long id, @RequestBody CategoryDto dto, @RequestHeader("Authorization") String authorization) throws NoIdFoundException, NotValidTokenException {
        String token = authorization.substring(7);
        this.tokenService.isTokenValid(token);
        Category category = this.categoryService.save(id, dto);
        return mapper.map(category, CategoryDto.class);
    }

    /**
     * Método para borrar una {@link Category}
     *
     * @param id PK de la entidad
     */
    @Operation(summary = "Delete", description = "Method that deletes a Category")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ApiResponses({ @ApiResponse(responseCode = "404", description = "category doesn't exists"), @ApiResponse(responseCode = "401", description = "invalid token"),
            @ApiResponse(responseCode = "409", description = "cant delete a category in use") })
    public void delete(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) throws NoIdFoundException, NotValidTokenException, NotDeleteableException {

        String token = authorization.substring(7);
        this.tokenService.isTokenValid(token);
        if (!isDeleteable(id).isCanDelete()) {
            throw new NotDeleteableException(isDeleteable(id).getReason());
        }
        this.categoryService.delete(id);
    }

    @Operation(summary = "Can-Delete", description = "Method that check if a Category can be deleted")
    @RequestMapping(path = "/{id}/can-delete", method = RequestMethod.GET)
    public DeleteCheckResponseDto isDeleteable(@PathVariable("id") Long id) {
        if (this.gameRepository.existsByCategoryId(id)) {
            return new DeleteCheckResponseDto(false, "IN_USE");
        } else {
            return new DeleteCheckResponseDto(true, "");
        }
    }
}
