package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.author.model.AuthorSearchDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import com.ccsw.tutorial.game.model.Game;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author ccsw
 *
 */
public interface AuthorService {

    /**
     * Recupera un {@link Author} a través de su ID
     *
     * @param id PK de la entidad
     * @return {@link Author}
     */
    Author get(Long id);

    /**
     * Método para recuperar un listado paginado de {@link Author}
     *
     * @param dto dto de búsqueda
     * @return {@link Page} de {@link Author}
     */
    Page<Author> findPage(AuthorSearchDto dto);

    /**
     * Método para crear o actualizar un {@link Author}
     *
     * @param id PK de la entidad
     * @param dto datos de la entidad
     * @throws NoIdFoundException si el ID no está en la BD
     */
    void save(Long id, AuthorDto dto) throws NoIdFoundException;

    /**
     * Método para crear o actualizar un {@link Author}
     *
     * @param id PK de la entidad
     * @throws NoIdFoundException si el Id no está en la BD
     * @throws NotDeleteableException si el {@link Author} está en uso en algún {@link Game}
     */
    void delete(Long id) throws NoIdFoundException, NotDeleteableException;

    /**
     * Recupera un listado de autores {@link Author}
     *
     * @return {@link List} de {@link Author}
     */
    List<Author> findAll();

    /**
     * Verifica si el {@link Author} dado por su PK se puede borrar o no
     * @param id PK de la entidad
     * @return {@link DeleteCheckResponseDto} verdadera si se puede borrar.
     * {@link DeleteCheckResponseDto} falsa y la lista de {@link Game} en los que está si no se puede borrar
     * @throws NoIdFoundException si el {@link Author} no existe en la BD
     */
    DeleteCheckResponseDto isDeleteable(Long id) throws NoIdFoundException;
}
