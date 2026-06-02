package com.ccsw.tutorial.game;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/game";

    public static final Long EXISTS_GAME_ID = 1L;
    public static final Long NOT_EXISTS_GAME_ID = 0L;
    private static final String NOT_EXISTS_TITLE = "NotExists";
    private static final String EXISTS_TITLE = "Aventureros al tren";
    private static final String NEW_TITLE = "Nuevo juego";
    private static final Long NOT_EXISTS_CATEGORY = 0L;
    private static final Long EXISTS_CATEGORY = 3L;

    private static final String TITLE_PARAM = "title";
    private static final String CATEGORY_ID_PARAM = "idCategory";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    JwtService tokenService;

    ParameterizedTypeReference<List<GameDto>> responseType = new ParameterizedTypeReference<List<GameDto>>() {
    };

    private String getUrlWithParams() {
        return UriComponentsBuilder.fromHttpUrl(LOCALHOST + port + SERVICE_PATH).queryParam(TITLE_PARAM, "{" + TITLE_PARAM + "}").queryParam(CATEGORY_ID_PARAM, "{" + CATEGORY_ID_PARAM + "}").encode().toUriString();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders ret = new HttpHeaders();
        ret.set("Authorization", "Bearer " + tokenService.generateToken("admin"));
        return ret;
    }

    @Test
    public void findWithoutFiltersShouldReturnAllGamesInDB() {

        int GAMES_WITH_FILTER = 6;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(GAMES_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findExistsTitleShouldReturnFilteredGames() {

        int GAMES_WITH_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(GAMES_WITH_FILTER, response.getBody().size());
        assertTrue(response.getBody().get(0).getTitle().toLowerCase().contains(EXISTS_TITLE.toLowerCase()));
    }

    @Test
    public void findExistsCategoryShouldReturnFilteredGames() {

        int GAMES_WITH_FILTER = 2;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(GAMES_WITH_FILTER, response.getBody().size());
        for(int i=0;i<response.getBody().size();i++){
            assertEquals(EXISTS_CATEGORY,response.getBody().get(i).getCategory().getId());
        }
    }

    @Test
    public void findExistsTitleAndCategoryShouldReturnGames() {

        int GAMES_WITH_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(GAMES_WITH_FILTER, response.getBody().size());
        GameDto responseDto = response.getBody().get(0);
        assertEquals(EXISTS_CATEGORY,responseDto.getCategory().getId());
        assertEquals(EXISTS_TITLE,responseDto.getTitle());
    }

    @Test
    public void findNotExistsTitleShouldReturnEmpty() {


        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NOT_EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void findNotExistsCategoryShouldReturnEmpty() {


        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, NOT_EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

    }

    @Test
    public void findNotExistsTitleOrCategoryShouldReturnEmpty() {

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NOT_EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, NOT_EXISTS_CATEGORY);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

    }

    @Test
    public void saveWithoutIdShouldCreateNewGame() {
        HttpHeaders headers = getHeaders();

        GameDto dto = new GameDto();
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NEW_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        GameDto gameSaved = response.getBody().get(0);
        assertEquals(NEW_TITLE,gameSaved.getTitle());
        assertEquals("18",gameSaved.getAge());
        assertEquals(1L, gameSaved.getAuthor().getId());
        assertEquals(1L,gameSaved.getCategory().getId());
    }

    @Test
    public void saveWithExistIdShouldUpdateGame() {
        HttpHeaders headers = getHeaders();

        GameDto dto = new GameDto();
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NEW_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + EXISTS_GAME_ID, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(EXISTS_GAME_ID, response.getBody().get(0).getId());
        assertEquals(NEW_TITLE,response.getBody().get(0).getTitle());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {
        HttpHeaders headers = getHeaders();
        GameDto dto = new GameDto();
        dto.setTitle(NEW_TITLE);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NOT_EXISTS_GAME_ID, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void saveWithNotExistingCategoryShouldThrowException(){

        GameDto dto = new GameDto();

        AuthorDto author = new AuthorDto();
        author.setId(1L);

        CategoryDto category = new CategoryDto();
        category.setId(NOT_EXISTS_CATEGORY);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(author);
        dto.setCategory(category);

        ResponseEntity<?> response=restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void saveWithNoExistingAuthorShouldThrowException(){
        GameDto dto = new GameDto();

        AuthorDto author = new AuthorDto();
        author.setId(NOT_EXISTS_GAME_ID);

        CategoryDto category = new CategoryDto();
        category.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(author);
        dto.setCategory(category);

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
    @Test
    public void saveWithNullAuthorShouldThrowException(){
        GameDto dto = new GameDto();



        CategoryDto category = new CategoryDto();
        category.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(null);
        dto.setCategory(category);

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }@Test
    public void saveWithNullCategoryShouldThrowException(){
        GameDto dto = new GameDto();

        AuthorDto author = new AuthorDto();
        author.setId(NOT_EXISTS_GAME_ID);



        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(author);
        dto.setCategory(null);

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }
}
