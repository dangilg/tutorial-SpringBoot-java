package com.ccsw.tutorial.game;

import com.ccsw.tutorial.author.AuthorService;
import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.category.CategoryService;
import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidDtoException;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private GameServiceImpl gameService;

    public static final Long EXISTS_GAME_ID = 1L;
    public static final Long NOT_EXISTS_GAME_ID = 0L;
    public static final String GAME_TITLE = "Game Test";
    public static final String NEW_TITLE = "Nuevo juego";
    public static final Long EXISTS_AUTHOR_ID = 1L;
    public static final Long EXISTS_CATEGORY_ID=1L;
    public static final Long NOT_EXISTS_AUTHOR_ID = 0L;
    public static final Long NOT_EXISTS_CATEGORY_ID=0L;


    //NO testeamos find, ya que no tiene lógica interna.

    private GameDto buildValidDto() {

        GameDto dto = new GameDto();

        dto.setTitle(GAME_TITLE);
        dto.setAge("18");

        AuthorDto author = new AuthorDto();
        author.setId(EXISTS_AUTHOR_ID);

        CategoryDto category = new CategoryDto();
        category.setId(EXISTS_CATEGORY_ID);

        dto.setAuthor(author);
        dto.setCategory(category);

        return dto;
    }


    @Test
    void shouldFindGames() {
        List<Game> games = List.of(new Game(), new Game());

        when(gameRepository.findAll(any())).thenReturn(games);

        List<Game> result = gameService.find("test", 1L);

        assertEquals(2, result.size());
        verify(gameRepository).findAll(any());
    }


    @Test
    void shouldFindGamesWithNullFilters() {
        when(gameRepository.findAll(any())).thenReturn(List.of());

        List<Game> result = gameService.find(null, null);

        assertNotNull(result);
        verify(gameRepository).findAll(any());
    }


    @Test
    public void saveWithoutIdShouldCreateGame(){
        GameDto game = buildValidDto();

        Author author = new Author();
        author.setId(EXISTS_AUTHOR_ID);

        Category category = new Category();
        category.setId(EXISTS_CATEGORY_ID);

        when(authorService.get(EXISTS_AUTHOR_ID)).thenReturn(author);
        when(categoryService.get(EXISTS_CATEGORY_ID)).thenReturn(category);

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        gameService.save(null,game);

        verify(gameRepository).save((captor.capture()));

        Game saved = captor.getValue();

        assertEquals(GAME_TITLE,saved.getTitle());
        assertEquals("18",saved.getAge());
        assertEquals(author,saved.getAuthor());
        assertEquals(category,saved.getCategory());
    }


    @Test
    public void saveWithExistingIdShouldUpdateGame(){

        Game game = new Game();
        game.setId(EXISTS_GAME_ID);
        game.setTitle(GAME_TITLE);

        GameDto dto = buildValidDto();

        Author author = new Author();
        author.setId(EXISTS_AUTHOR_ID);

        Category category = new Category();
        category.setId(EXISTS_CATEGORY_ID);

        dto.setTitle(NEW_TITLE);


        when(gameRepository.findById(EXISTS_GAME_ID)).thenReturn(Optional.of(game));
        when(authorService.get(EXISTS_AUTHOR_ID)).thenReturn(author);
        when(categoryService.get(EXISTS_CATEGORY_ID)).thenReturn(category);

        gameService.save(EXISTS_GAME_ID,dto);

        verify(gameRepository).save(game);
    }

    @Test
    public void saveWithNonExistingGameShouldThrowException(){
        GameDto dto = buildValidDto();

        when(gameRepository.findById(NOT_EXISTS_GAME_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class,()->{
            gameService.save(NOT_EXISTS_GAME_ID,dto);
        });

        verify(gameRepository).findById(NOT_EXISTS_GAME_ID);
    }


    @Test
    public void saveWithNullAuthorShouldThrowException(){
        GameDto dto = buildValidDto();
        dto.setAuthor(null);

        assertThrows(NotValidDtoException.class,()->{
            gameService.save(null,dto);
        });
    }

    @Test
    public void saveWithNullCategoryShouldThrowException(){
        GameDto dto = buildValidDto();
        dto.setCategory(null);

        assertThrows(NotValidDtoException.class,()->{
            gameService.save(null,dto);
        });
    }

    @Test
    public void saveWithNoExistingAuthorShouldThrowException(){
        GameDto dto = buildValidDto();
        AuthorDto author =dto.getAuthor();
        author.setId(NOT_EXISTS_AUTHOR_ID);
        dto.setAuthor(author);

        when(authorService.get(NOT_EXISTS_AUTHOR_ID)).thenReturn(null);


        assertThrows(NoIdFoundException.class,()->{
            gameService.save(null,dto);
        });

        verify(authorService).get(NOT_EXISTS_AUTHOR_ID);
    }

    @Test
    public void saveWithNoExistingCategoryShouldThrowException(){
        GameDto dto = buildValidDto();
        CategoryDto category = dto.getCategory();
        category.setId(NOT_EXISTS_CATEGORY_ID);
        dto.setCategory(category);

        when(authorService.get(EXISTS_AUTHOR_ID)).thenReturn(new Author());
        when(categoryService.get(NOT_EXISTS_CATEGORY_ID)).thenReturn(null);


        assertThrows(NoIdFoundException.class,()->{
            gameService.save(null,dto);
        });

        verify(categoryService).get(NOT_EXISTS_CATEGORY_ID);
    }
}
