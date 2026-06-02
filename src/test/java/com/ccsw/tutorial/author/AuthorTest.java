package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.Author;
import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.author.model.AuthorSearchDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorTest {

    public static final Long EXISTS_AUTHOR_ID = 1L;
    public static final Long NOT_EXISTS_AUTHOR_ID = 0L;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;


    @Test
    public void getExistsAuthorIdShouldReturnAuthor() {

        Author author = mock(Author.class);
        when(author.getId()).thenReturn(EXISTS_AUTHOR_ID);
        when(authorRepository.findById(EXISTS_AUTHOR_ID)).thenReturn(Optional.of(author));

        Author authorResponse = authorService.get(EXISTS_AUTHOR_ID);

        assertNotNull(authorResponse);

        assertEquals(EXISTS_AUTHOR_ID, authorResponse.getId());
    }

    @Test
    public void getNotExistsAuthorIdShouldReturnNull() {

        when(authorRepository.findById(NOT_EXISTS_AUTHOR_ID)).thenReturn(Optional.empty());

        Author author = authorService.get(NOT_EXISTS_AUTHOR_ID);

        assertNull(author);
    }


    @Test
    public void saveWithoutIdShouldCreateAuthor(){

        AuthorDto dto = new AuthorDto();
        dto.setName("Autor Test");
        dto.setNationality("ES");

        Author savedAuthor = new Author();
        savedAuthor.setId(1L);
        savedAuthor.setName(dto.getName());
        savedAuthor.setNationality(dto.getNationality());

        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        authorService.save(null, dto);

        verify(authorRepository).save(any(Author.class));
    }

    @Test
    public void saveWithExistingIdShouldUpdateAuthor() {

        AuthorDto dto = new AuthorDto();
        dto.setName("Autor Modificado");
        dto.setNationality("FR");

        Author existingAuthor = new Author();
        existingAuthor.setId(EXISTS_AUTHOR_ID);

        when(authorRepository.findById(EXISTS_AUTHOR_ID)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);

        authorService.save(EXISTS_AUTHOR_ID, dto);

        verify(authorRepository).findById(EXISTS_AUTHOR_ID);
        verify(authorRepository).save(existingAuthor);

        assertEquals(dto.getName(), existingAuthor.getName());
        assertEquals(dto.getNationality(), existingAuthor.getNationality());
    }
    @Test
    public void saveWithNotExistingIdShouldThrowException() {

        AuthorDto dto = new AuthorDto();
        dto.setName("Autor Test");

        when(authorRepository.findById(NOT_EXISTS_AUTHOR_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            authorService.save(NOT_EXISTS_AUTHOR_ID, dto);
        });

        verify(authorRepository).findById(NOT_EXISTS_AUTHOR_ID);
        verify(authorRepository, never()).save(any());
    }


    @Test
    public void deleteWithExistingIdShouldDeleteAuthor(){

        Author author = new Author();
        author.setId(EXISTS_AUTHOR_ID);

        when(authorRepository.findById(EXISTS_AUTHOR_ID)).thenReturn(Optional.of(author));

        authorService.delete(EXISTS_AUTHOR_ID);

        verify(authorRepository).deleteById(EXISTS_AUTHOR_ID);
    }

    @Test
    public void deleteWithNotExistingIdShouldThrowException() {

        when(authorRepository.findById(NOT_EXISTS_AUTHOR_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            authorService.delete(NOT_EXISTS_AUTHOR_ID);
        });

        verify(authorRepository).findById(NOT_EXISTS_AUTHOR_ID);
        verify(authorRepository, never()).deleteById(any());
    }

    @Test
    public void findAllShouldReturnAuthorsList() {

        List<Author> authors = List.of(
                new Author(),
                new Author()
        );

        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> result = authorService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(authorRepository).findAll();
    }

    @Test
    public void findAllShouldReturnEmptyList() {

        when(authorRepository.findAll()).thenReturn(List.of());

        List<Author> result = authorService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(authorRepository).findAll();
    }


    @Test
    public void findPageShouldReturnPageWithData() {

        AuthorSearchDto dto = new AuthorSearchDto();
        PageableRequest pageableRequest = new PageableRequest(0, 5);
        dto.setPageable(pageableRequest);

        Page<Author> pageMock = mock(Page.class);

        when(authorRepository.findAll(any())).thenReturn(pageMock);

        Page<Author> result = authorService.findPage(dto);

        assertNotNull(result);
        assertEquals(pageMock, result);

        verify(authorRepository).findAll(any());
    }


    @Test
    public void findPageShouldReturnEmptyPage() {

        AuthorSearchDto dto = new AuthorSearchDto();
        dto.setPageable(new PageableRequest(0, 5));

        Page<Author> emptyPage = Page.empty();

        when(authorRepository.findAll(any())).thenReturn(emptyPage);

        Page<Author> result = authorService.findPage(dto);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(authorRepository).findAll(any());
    }

    @Test
    public void isDeleteableWithNotExistingIdShouldThrowException() {

        when(authorRepository.findById(NOT_EXISTS_AUTHOR_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            authorService.isDeleteable(NOT_EXISTS_AUTHOR_ID);
        });

        verify(authorRepository).findById(NOT_EXISTS_AUTHOR_ID);
        verify(gameRepository, never()).findByAuthorId(any());
    }

    @Test
    public void isDeleteableWithNoGamesShouldReturnTrue() {

        Author author = new Author();
        author.setId(EXISTS_AUTHOR_ID);

        when(authorRepository.findById(EXISTS_AUTHOR_ID)).thenReturn(Optional.of(author));
        when(gameRepository.findByAuthorId(EXISTS_AUTHOR_ID)).thenReturn(List.of());

        DeleteCheckResponseDto result = authorService.isDeleteable(EXISTS_AUTHOR_ID);

        assertNotNull(result);
        assertTrue(result.isCanDelete());
        assertEquals("", result.getReason());
        assertTrue(result.getList().isEmpty());

        verify(authorRepository).findById(EXISTS_AUTHOR_ID);
        verify(gameRepository).findByAuthorId(EXISTS_AUTHOR_ID);
    }

    @Test
    public void isDeleteableWithGamesShouldReturnFalse() {

        Author author = new Author();
        author.setId(EXISTS_AUTHOR_ID);

        Game game = new Game();
        game.setId(10L);
        game.setTitle("Juego Test");

        when(authorRepository.findById(EXISTS_AUTHOR_ID)).thenReturn(Optional.of(author));
        when(gameRepository.findByAuthorId(EXISTS_AUTHOR_ID)).thenReturn(List.of(game));

        DeleteCheckResponseDto result = authorService.isDeleteable(EXISTS_AUTHOR_ID);

        assertNotNull(result);
        assertFalse(result.isCanDelete());
        assertEquals("EN USO", result.getReason());
        assertFalse(result.getList().isEmpty());
        assertEquals(1, result.getList().size());

        verify(authorRepository).findById(EXISTS_AUTHOR_ID);
        verify(gameRepository).findByAuthorId(EXISTS_AUTHOR_ID);
    }
}
