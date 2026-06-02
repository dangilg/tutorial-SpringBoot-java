package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void findAllShouldReturnAllCategories() {

        List<Category> list = new ArrayList<>();
        list.add(mock(Category.class));

        when(categoryRepository.findAll()).thenReturn(list);

        List<Category> categories = categoryService.findAll();

        assertNotNull(categories);
        assertEquals(1, categories.size());
    }

    public static final String CATEGORY_NAME = "CAT1";

    @Test
    public void saveNotExistsCategoryIdShouldInsert() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(CATEGORY_NAME);

        ArgumentCaptor<Category> category = ArgumentCaptor.forClass(Category.class);

        categoryService.save(null, categoryDto);

        verify(categoryRepository).save(category.capture());

        assertEquals(CATEGORY_NAME, category.getValue().getName());
    }

    public static final Long EXISTS_CATEGORY_ID = 1L;

    @Test
    public void saveExistsCategoryIdShouldUpdate() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(CATEGORY_NAME);

        Category category = mock(Category.class);
        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(category));

        categoryService.save(EXISTS_CATEGORY_ID, categoryDto);

        verify(categoryRepository).save(category);
    }

    @Test
    public void saveNotExistingIdShouldThrowException() {

        CategoryDto dto = new CategoryDto();
        dto.setName(CATEGORY_NAME);

        when(categoryRepository.findById(NOT_EXISTS_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            categoryService.save(NOT_EXISTS_CATEGORY_ID, dto);
        });

        verify(categoryRepository).findById(NOT_EXISTS_CATEGORY_ID);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void deleteExistsCategoryIdShouldDelete() throws Exception {

        Category category = mock(Category.class);
        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(category));

        categoryService.delete(EXISTS_CATEGORY_ID);

        verify(categoryRepository).deleteById(EXISTS_CATEGORY_ID);
    }

    public static final Long NOT_EXISTS_CATEGORY_ID = 0L;

    @Test
    public void deleteNotExistingIdShouldThrowException() {

        when(categoryRepository.findById(NOT_EXISTS_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            categoryService.delete(NOT_EXISTS_CATEGORY_ID);
        });

        verify(categoryRepository).findById(NOT_EXISTS_CATEGORY_ID);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    public void getExistsCategoryIdShouldReturnCategory() {

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(EXISTS_CATEGORY_ID);
        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(category));

        Category categoryResponse = categoryService.get(EXISTS_CATEGORY_ID);

        assertNotNull(categoryResponse);
        assertEquals(EXISTS_CATEGORY_ID, categoryResponse.getId());
    }

    @Test
    public void getNotExistsCategoryIdShouldReturnNull() {

        when(categoryRepository.findById(NOT_EXISTS_CATEGORY_ID)).thenReturn(Optional.empty());

        Category category = categoryService.get(NOT_EXISTS_CATEGORY_ID);

        assertNull(category);
    }

    @Test
    public void isDeleteableWithNotExistingIdShouldThrowException() {

        when(categoryRepository.findById(NOT_EXISTS_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            categoryService.isDeleteable(NOT_EXISTS_CATEGORY_ID);
        });

        verify(categoryRepository).findById(NOT_EXISTS_CATEGORY_ID);
        verify(gameRepository, never()).findByCategoryId(any());
    }

    @Test
    public void isDeleteableWithNoGamesShouldReturnTrue() {

        Category category = new Category();
        category.setId(EXISTS_CATEGORY_ID);

        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(gameRepository.findByCategoryId(EXISTS_CATEGORY_ID)).thenReturn(List.of());

        DeleteCheckResponseDto result = categoryService.isDeleteable(EXISTS_CATEGORY_ID);

        assertNotNull(result);
        assertTrue(result.isCanDelete());
        assertEquals("", result.getReason());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    public void isDeleteableWithGamesShouldReturnFalse() {

        Category category = new Category();
        category.setId(EXISTS_CATEGORY_ID);

        Game game = new Game();
        game.setId(1L);
        game.setTitle("Juego");

        when(categoryRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(gameRepository.findByCategoryId(EXISTS_CATEGORY_ID)).thenReturn(List.of(game));

        DeleteCheckResponseDto result = categoryService.isDeleteable(EXISTS_CATEGORY_ID);

        assertNotNull(result);
        assertFalse(result.isCanDelete());
        assertEquals("EN USO", result.getReason());
        assertFalse(result.getList().isEmpty());
        assertEquals(1, result.getList().size());
    }

}
