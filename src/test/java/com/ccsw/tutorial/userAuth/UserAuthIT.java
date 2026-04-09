package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.model.ErrorResponseDto;
import com.ccsw.tutorial.security.JwtService;
import com.ccsw.tutorial.userAuth.model.AuthResponseDto;
import com.ccsw.tutorial.userAuth.model.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//esto hace q cada vez q se ejecute un test, se carga la DB de nuevo
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserAuthIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/auth";
    public static final String LOGIN = "/logIn";
    public static final String SIGNIN = "/signIn";

    public static final String NEW_USERNAME = "admin1";
    public static final String NEW_PASSWORD = "admin1";

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    ParameterizedTypeReference<AuthResponseDto> responseType = new ParameterizedTypeReference<AuthResponseDto>() {
    };

    @Test
    public void registerANewValidUserShouldCreateNewUser() {
        UserDto dto = new UserDto();
        dto.setUsername(NEW_USERNAME);
        dto.setPassword(NEW_PASSWORD);

        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + SIGNIN, HttpMethod.POST, new HttpEntity<>(dto), AuthResponseDto.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        String token = response.getBody().getToken();
        String username = jwtService.extractUsername(token);
        assertEquals(NEW_USERNAME, username);
    }

    @Test
    public void registerANewValidUserShouldReturnAValidToken() {
        UserDto dto = new UserDto();
        dto.setUsername(NEW_USERNAME);
        dto.setPassword(NEW_PASSWORD);
        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + SIGNIN, HttpMethod.POST, new HttpEntity<>(dto), AuthResponseDto.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(jwtService.isTokenValid(response.getBody().getToken()));
    }

    @Test
    public void registerAnExistingUserShouldNotValidUsernameException() {
        UserDto dto = new UserDto();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        ResponseEntity<ErrorResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + SIGNIN, HttpMethod.POST, new HttpEntity<>(dto), ErrorResponseDto.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals("USER ALREADY EXIST", response.getBody().getMessage());
    }

    @Test
    public void logInValidShouldReturnAValidTokenWithLoggedUser() {
        UserDto dto = new UserDto();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        ResponseEntity<AuthResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + LOGIN, HttpMethod.POST, new HttpEntity<>(dto), responseType);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(jwtService.isTokenValid(response.getBody().getToken()));
        assertEquals(dto.getUsername(), jwtService.extractUsername(response.getBody().getToken()));
    }

    @Test
    public void logInNotValidUsernameShouldNotFoundUserException() {
        UserDto dto = new UserDto();
        dto.setUsername(NEW_USERNAME);
        dto.setPassword(PASSWORD);
        ResponseEntity<ErrorResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + LOGIN, HttpMethod.POST, new HttpEntity<>(dto), ErrorResponseDto.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatus());
        assertEquals("NOT USER FOUND BY THIS USERNAME", response.getBody().getMessage());
    }

    @Test
    public void logInNotValidPasswordShouldWrongPasswordException() {
        UserDto dto = new UserDto();
        dto.setUsername(USERNAME);
        dto.setPassword(NEW_PASSWORD);
        ResponseEntity<ErrorResponseDto> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + LOGIN, HttpMethod.POST, new HttpEntity<>(dto), ErrorResponseDto.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatus());
        assertEquals("USER OR PASSWORD NOT VALID", response.getBody().getMessage());
    }
}
