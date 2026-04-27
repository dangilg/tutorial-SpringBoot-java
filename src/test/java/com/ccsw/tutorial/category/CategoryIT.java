package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/category";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService tokenService;
    ParameterizedTypeReference<List<CategoryDto>> responseType = new ParameterizedTypeReference<List<CategoryDto>>() {
    };

    private HttpHeaders getHeaders() {
        HttpHeaders ret = new HttpHeaders();
        ret.set("Authorization", "Bearer " + tokenService.generateToken("admin"));
        return ret;
    }

    @Test
    public void findAllShouldReturnAllCategories() {

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        assertNotNull(response);
        assertEquals(4, response.getBody().size());
    }

    public static final Long NEW_CATEGORY_ID = 5L;
    public static final String NEW_CATEGORY_NAME = "CAT4";

    @Test
    public void saveWithoutIdShouldCreateNewCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto, getHeaders()), Void.class);

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(5, response.getBody().size());

        CategoryDto categorySearch = response.getBody().stream().filter(item -> item.getId().equals(NEW_CATEGORY_ID)).findFirst().orElse(null);
        assertNotNull(categorySearch);
        assertEquals(NEW_CATEGORY_NAME, categorySearch.getName());
    }

    public static final Long MODIFY_CATEGORY_ID = 3L;

    @Test
    public void modifyWithExistIdShouldModifyCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_CATEGORY_ID, HttpMethod.PUT, new HttpEntity<>(dto, getHeaders()), Void.class);

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(4, response.getBody().size());

        CategoryDto categorySearch = response.getBody().stream().filter(item -> item.getId().equals(MODIFY_CATEGORY_ID)).findFirst().orElse(null);
        assertNotNull(categorySearch);
        assertEquals(NEW_CATEGORY_NAME, categorySearch.getName());
    }

    @Test
    public void modifyWithNotExistIdShouldNotFoundError() {

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CATEGORY_ID, HttpMethod.PUT, new HttpEntity<>(dto, getHeaders()), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void saveWithNotValidTokenShouldThrowException() {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.generateToken("admin");
        String badToken = token.replace("e", "l");
        headers.set("Authorization", "Bearer " + badToken);

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void modifyWithNotValidTokenShouldThrowException() {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.generateToken("admin");
        String badToken = token.replace("e", "l");
        headers.set("Authorization", "Bearer " + badToken);

        CategoryDto dto = new CategoryDto();
        dto.setName(NEW_CATEGORY_NAME);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CATEGORY_ID, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    public static final Long DELETE_CATEGORY_ID = 4L;

    @Test
    public void deleteDeleteableCategoryWithExistsIdShouldDeleteCategory() {
        ResponseEntity<DeleteCheckResponseDto> responseCanDelete = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_CATEGORY_ID + "/" + "can-delete", HttpMethod.GET, null, DeleteCheckResponseDto.class);
        assertNotNull(responseCanDelete);
        assertTrue(responseCanDelete.getBody().isCanDelete());

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_CATEGORY_ID, HttpMethod.DELETE, new HttpEntity<>(getHeaders()), Void.class);

        ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(3, response.getBody().size());
    }

    @Test
    public void deleteDeleteableCategoryWithNotExistsIdShouldNotFoundError() {

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CATEGORY_ID, HttpMethod.DELETE, new HttpEntity<>(getHeaders()), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteNotDeleteableCategoryShouldThrowException() {
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + "1", HttpMethod.DELETE, new HttpEntity<>(getHeaders()), Void.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    }

    @Test
    public void deleteDeleteableCategoryWithInvalidTokenShouldThrowException() {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.generateToken("admin");
        String badToken = token.replace("e", "l");
        headers.set("Authorization", "Bearer " + badToken);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + "1", HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
