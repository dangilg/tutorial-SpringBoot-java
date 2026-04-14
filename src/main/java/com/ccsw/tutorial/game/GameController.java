package com.ccsw.tutorial.game;

import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidTokenException;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Game", description = "API of Game")
@RequestMapping(value = "/game")
@RestController
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    GameService gameService;

    @Autowired
    ModelMapper mapper;

    @Autowired
    JwtService tokenService;

    /**
     * Método para recuperar una lista de {@link Game}
     *
     * @param title título del juego
     * @param idCategory PK de la categoría
     * @return {@link List} de {@link GameDto}
     */
    @Operation(summary = "Find", description = "Method that return a filtered list of Games")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<GameDto> find(@RequestParam(value = "title", required = false) String title, @RequestParam(value = "idCategory", required = false) Long idCategory) {

        List<Game> games = gameService.find(title, idCategory);

        return games.stream().map(e -> mapper.map(e, GameDto.class)).collect(Collectors.toList());
    }

    /**
     * Método para crear o actualizar un {@link Game}
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     */
    @Operation(summary = "Save or Update", description = "Method that saves or updates a Game")
    @RequestMapping(path = { "", "/{id}" }, method = RequestMethod.PUT)
    public void save(@PathVariable(name = "id", required = false) Long id, @RequestBody GameDto dto, @RequestHeader("Authorization") String authorization) throws NoIdFoundException, NotValidTokenException {
        String token = authorization.replace("Bearer", "");
        tokenService.isTokenValid(token);
        gameService.save(id, dto);
    }

}
