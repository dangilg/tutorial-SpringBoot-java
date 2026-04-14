package com.ccsw.tutorial.game;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.author.AuthorService;
import com.ccsw.tutorial.category.CategoryService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    AuthorService authorService;

    @Autowired
    CategoryService categoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Game> find(String title, Long idCategory) {
        System.out.println("titulo:" + title);
        System.out.println("idCategory:" + idCategory);
        GameSpecification titleSpec = new GameSpecification(new SearchCriteria("title", ":", title));
        GameSpecification categorySpec = new GameSpecification(new SearchCriteria("category.id", ":", idCategory));

        //Specification<Game> spec = Specification.where(titleSpec).and(categorySpec);
        // Desde la versión 3.5.0 de Spring Boot, la nueva manera es
        Specification<Game> spec = titleSpec.and(categorySpec);

        return (List<Game>) this.gameRepository.findAll(spec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, GameDto dto) {

        Game game;

        if (id == null) {
            game = new Game();
        } else {
            game = this.gameRepository.findById(id).orElse(null);
        }

        if (game == null) {
            throw new NoIdFoundException();
        }
        BeanUtils.copyProperties(dto, game, "id", "author", "category");
        Author author = authorService.get(dto.getAuthor().getId());
        //para evitar tener q esperar en caso de q el author ya sea null
        if (author == null) {
            throw new NoIdFoundException();
        }
        Category category = categoryService.get(dto.getCategory().getId());
        if (category == null) {
            throw new NoIdFoundException();
        }
        game.setAuthor(author);
        game.setCategory(category);

        this.gameRepository.save(game);
    }

}
