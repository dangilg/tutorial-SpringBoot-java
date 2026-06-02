package com.ccsw.tutorial.tokenAuth;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TokenAuthIt {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/authToken";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    private HttpHeaders getHeaders() {
        HttpHeaders ret = new HttpHeaders();
        ret.set("Authorization", "Bearer " + jwtService.generateToken("admin"));
        return ret;
    }

    @Test
    public void validateTokenWithValidTokenShouldReturnOk(){


        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/validateToken",
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                Void.class
        );
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void validateTokenWithNotValidTokenShouldReturnUnauthorized(){
        HttpHeaders header = getHeaders();
        header.set("Authorization","Bearer asdfghjk");

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/validateToken",
                HttpMethod.GET,
                new HttpEntity<>(header),
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void callToEndpointNotPublicRouteWithValidTokenShouldReturnOK(){

        ClientDto dto = new ClientDto();
        dto.setName("nuevoCliente");

        ResponseEntity<ClientDto> response = restTemplate.exchange(
                LOCALHOST + port + "/client",
                HttpMethod.PUT,
                new HttpEntity<>(dto,getHeaders()),
                ClientDto.class
        );

        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void callToEndpointNotPublicWithNotValidTokenShouldReturnUnauthorized(){
        ClientDto dto = new ClientDto();
        dto.setName("nuevoCliente");
        HttpHeaders headers = getHeaders();
        headers.set("Authorization","Bearer dfghjkl");
        ResponseEntity<ClientDto> response = restTemplate.exchange(
                LOCALHOST + port + "/client/save",
                HttpMethod.PUT,
                new HttpEntity<>(dto,headers),
                ClientDto.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void callToEndpointPublicWithNoTokenShouldReturnOk(){

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + "/client",
                HttpMethod.GET,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK,response.getStatusCode());
    }
}
