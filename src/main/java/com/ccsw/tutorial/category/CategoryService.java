package com.ccsw.tutorial.category;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import com.ccsw.tutorial.game.model.Game;

import java.util.List;

/**
 * @author ccsw
 *
 */
public interface CategoryService {

    /**
     * Recupera una {@link Category} a partir de su ID
     *
     * @param id PK de la entidad
     * @return {@link Category}
     */
    Category get(Long id);

    /**
     * Método para recuperar todas las {@link Category}
     *
     * @return {@link List} de {@link Category}
     */
    List<Category> findAll();

    /**
     * Método para crear o actualizar una {@link Category}
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     * @throws NoIdFoundException si la {@link Category} no existe
     */
    Category save(Long id, CategoryDto dto) throws NoIdFoundException;

    /**
     * Método para borrar una {@link Category}
     *
     * @param id PK de la entidad
     * @throws NoIdFoundException si {@link Category} no existe
     * @throws NotDeleteableException si la {@link Category} está en uso en algún {@link  Game}
     */
    void delete(Long id) throws NoIdFoundException, NotDeleteableException;


    /**
     * Verifica si el {@link Category} dado por su PK se puede borrar o no
     * @param id PK de la entidad
     * @return {@link DeleteCheckResponseDto} verdadera si se puede borrar.
     * {@link DeleteCheckResponseDto} falsa y la lista de {@link Game} en los que está si no se puede borrar
     * @throws NoIdFoundException si no existe la {@link Category} en la BD
     */
    DeleteCheckResponseDto isDeleteable(Long id) throws NoIdFoundException;
}
