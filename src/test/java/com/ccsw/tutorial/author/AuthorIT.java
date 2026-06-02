package com.ccsw.tutorial.author;

import com.ccsw.tutorial.author.model.AuthorDto;
import com.ccsw.tutorial.author.model.AuthorSearchDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
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
public class AuthorIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/author";

    public static final Long DELETEABLE_AUTHOR_ID = 6L;
    public static final Long NOT_DELETEABLE_AUTHOR_ID = 1L;
    public static final Long MODIFY_AUTHOR_ID = 3L;
    public static final String NEW_AUTHOR_NAME = "Nuevo Autor";
    public static final String NEW_NATIONALITY = "Nueva Nacionalidad";

    private static final int TOTAL_AUTHORS = 7;
    private static final int PAGE_SIZE = 5;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService tokenService;

    ParameterizedTypeReference<ResponsePage<AuthorDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<AuthorDto>>() {
    };

    private HttpHeaders getHeaders() {
        HttpHeaders ret = new HttpHeaders();
        ret.set("Authorization", "Bearer " + tokenService.generateToken("admin"));
        return ret;
    }

    @Test
    public void findFirstPageWithFiveSizeShouldReturnFirstFiveResults() {

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<AuthorDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    public void findSecondPageWithFiveSizeShouldReturnLastResult() {

        int elementsCount = TOTAL_AUTHORS - PAGE_SIZE;

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(1, PAGE_SIZE));

        ResponseEntity<ResponsePage<AuthorDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getBody().getTotalElements());
        assertEquals(elementsCount, response.getBody().getContent().size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewAuthor() {
        HttpHeaders headers = getHeaders();
        long newAuthorId = TOTAL_AUTHORS + 1;
        long newAuthorSize = TOTAL_AUTHORS + 1;

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);
        dto.setNationality(NEW_NATIONALITY);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, (int) newAuthorSize));

        ResponseEntity<ResponsePage<AuthorDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newAuthorSize, response.getBody().getTotalElements());

        AuthorDto author = response.getBody().getContent().stream().filter(item -> item.getId().equals(newAuthorId)).findFirst().orElse(null);
        assertNotNull(author);
        assertEquals(NEW_AUTHOR_NAME, author.getName());
        assertEquals(NEW_NATIONALITY, author.getNationality());
    }

    @Test
    public void saveWithNotValidTokenShouldThrowException() {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.generateToken("admin");
        String badToken = token.replace("e", "l");
        headers.set("Authorization", "Bearer " + badToken);

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);
        dto.setNationality(NEW_NATIONALITY);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void modifyWithExistIdShouldModifyAuthor() {
        HttpHeaders headers = getHeaders();
        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);
        dto.setNationality(NEW_NATIONALITY);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_AUTHOR_ID, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<AuthorDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getBody().getTotalElements());

        AuthorDto author = response.getBody().getContent().stream().filter(item -> item.getId().equals(MODIFY_AUTHOR_ID)).findFirst().orElse(null);
        assertNotNull(author);
        assertEquals(NEW_AUTHOR_NAME, author.getName());
        assertEquals(NEW_NATIONALITY, author.getNationality());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {
        HttpHeaders headers = getHeaders();
        long authorId = TOTAL_AUTHORS + 1;

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + authorId, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void modifyWithNotValidTokenShouldThrowException() {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.generateToken("admin");
        String badToken = token.replace("e", "l");
        headers.set("Authorization", "Bearer " + badToken);

        long authorId = TOTAL_AUTHORS + 1;

        AuthorDto dto = new AuthorDto();
        dto.setName(NEW_AUTHOR_NAME);
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + authorId, HttpMethod.PUT, new HttpEntity<>(dto, headers), Void.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    public void deleteWithExistsIdAndDeleteableAuthorShouldDeleteAuthor() {
        HttpHeaders headers = getHeaders();
        long newAuthorsSize = TOTAL_AUTHORS - 1;

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETEABLE_AUTHOR_ID, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_AUTHORS));

        ResponseEntity<ResponsePage<AuthorDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newAuthorsSize, response.getBody().getTotalElements());
    }

    @Test
    public void deleteWithNotExistsIdShouldThrowException() {
        HttpHeaders headers = getHeaders();
        long deleteAuthorId = TOTAL_AUTHORS + 1;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + deleteAuthorId, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    ParameterizedTypeReference<List<AuthorDto>> responseTypeList = new ParameterizedTypeReference<List<AuthorDto>>() {
    };

    @Test
    public void deleteANonDeleteableAuthorShouldThrowException() {
        HttpHeaders headers = getHeaders();

        ResponseEntity<?> respone = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NOT_DELETEABLE_AUTHOR_ID, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

        assertEquals(HttpStatus.CONFLICT, respone.getStatusCode());

    }

    @Test
    public void findAllShouldReturnAllAuthor() {

        ResponseEntity<List<AuthorDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseTypeList);

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getBody().size());
    }

    @Test
    public void canDeleteWithDeletableAuthorShouldReturnTrue(){
        ResponseEntity<DeleteCheckResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETEABLE_AUTHOR_ID + "/can-delete", HttpMethod.GET,null, DeleteCheckResponseDto.class);

        assertNotNull(response.getBody());

        assertEquals(true, response.getBody().isCanDelete());

        assertEquals(0,response.getBody().getList().size());

        assertEquals("",response.getBody().getReason());
    }
    @Test
    public void canDeleteWithNotDeleteableAuthorShouldReturnFalse(){
        ResponseEntity<DeleteCheckResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NOT_DELETEABLE_AUTHOR_ID + "/can-delete", HttpMethod.GET,null, DeleteCheckResponseDto.class);

        assertNotNull(response.getBody());

        assertEquals(false, response.getBody().isCanDelete());

        assertEquals("EN USO", response.getBody().getReason());
        assertNotNull(response.getBody().getList());
        assertEquals(true,!response.getBody().getList().isEmpty());
    }

    @Test
    public void canDeleteWithNotValidIdShouldThrowNoIdFoundException(){
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + -1 + "/can-delete", HttpMethod.GET,null, DeleteCheckResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void canDeleteWithNonExistingAuthorShoulThrowNoIdFoundException(){
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + 37 + "/can-delete", HttpMethod.GET,null, DeleteCheckResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findPageWithOutOfRangePageShouldReturnEmptyContent() {

        AuthorSearchDto searchDto = new AuthorSearchDto();
        searchDto.setPageable(new PageableRequest(100, PAGE_SIZE));

        ResponseEntity<ResponsePage<AuthorDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(searchDto),
                responseTypePage
        );

        assertNotNull(response);
        assertEquals(TOTAL_AUTHORS, response.getBody().getTotalElements());
        assertEquals(0, response.getBody().getContent().size());
    }
}
