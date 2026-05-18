package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.author.model.AuthorSearchDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckObject;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    GameRepository gameRepository;
    /**
     * {@inheritDoc}
     */
    @Override
    public Author get(Long id) {

        return this.authorRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Author> findPage(AuthorSearchDto dto) {

        return this.authorRepository.findAll(dto.getPageable().getPageable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, AuthorDto data) throws NoIdFoundException {

        Author author;

        if (id == null) {
            author = new Author();
        } else {
            author = this.get(id);
        }

        if (author == null) {
            throw new NoIdFoundException();
        }
        BeanUtils.copyProperties(data, author, "id");

        this.authorRepository.save(author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws NoIdFoundException {

        if (this.get(id) == null) {
            throw new NoIdFoundException();
        }

        this.authorRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Author> findAll() {

        return (List<Author>) this.authorRepository.findAll();
    }

    @Override
    public DeleteCheckResponseDto isDeleteable(Long id){

        List<Game> gamesInConflict = gameRepository.findByAuthorId(id);
        if(!gamesInConflict.isEmpty()){
            List<DeleteCheckObject> list = gamesInConflict.stream().map(g->new DeleteCheckObject(g.getId(),g.getTitle())).toList();
            return new DeleteCheckResponseDto(false,"EN USO", list);
        }
        else{
            return new DeleteCheckResponseDto(true, "",List.of());
        }
    }
}
