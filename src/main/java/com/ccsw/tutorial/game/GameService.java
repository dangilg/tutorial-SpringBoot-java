package com.ccsw.tutorial.game;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidDtoException;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;

import java.util.List;

/**
 * @author ccsw
 *
 */
public interface GameService {

    /**
     * Recupera los juegos filtrando opcionalmente por título y/o categoría
     *
     * @param title título del juego
     * @param idCategory PK de la categoría
     * @return {@link List} de {@link Game}
     */
    List<Game> find(String title, Long idCategory);

    /**
     * Guarda o modifica un juego, dependiendo de si el identificador está o no informado
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     * @throws NoIdFoundException si no existe el {@link Game}
     * @throws com.ccsw.tutorial.exceptions.NotValidDtoException si alguno de los valores del dto son null
     */
    void save(Long id, GameDto dto) throws NoIdFoundException, NotValidDtoException;

}
