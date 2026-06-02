package com.ccsw.tutorial.client;


import com.ccsw.tutorial.client.model.ClientDto;
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {

    public static final String LOCALHOST="http://localhost:";
    public static final String SERVICE_PATH = "/client";

    public static final String NEW_CLIENT_NAME ="new Client";
    public static final String MODIFIED_CLIENT_NAME = "Cliente Modificado";
    public static final Long EXISTS_CLIENT_ID = 1L;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService tokenService;

    private HttpHeaders getHeaders(){
        HttpHeaders ret = new HttpHeaders();
        ret.set("Authorization","Bearer "+tokenService.generateToken("admin"));
        return  ret;
    }

    ParameterizedTypeReference<List<ClientDto>> responseType = new ParameterizedTypeReference<List<ClientDto>>() {
    };

    @Test
    public void findAllShouldReturnAllClients(){

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );
        assertNotNull(response.getBody());
        assertEquals(3,response.getBody().size());
    }

    @Test
    public void saveWithoutIdShouldCreateClient(){
        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        ResponseEntity<ClientDto> saveResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                ClientDto.class
        );

        assertNotNull(saveResponse.getBody());
        assertEquals(NEW_CLIENT_NAME, saveResponse.getBody().getName());

        ResponseEntity<List<ClientDto>> response=  restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH ,
                HttpMethod.GET,
                null,
                responseType
        );

        assertNotNull(response.getBody());
        assertEquals(4,response.getBody().size());
    }

    @Test
    public void saveWithExistingIdShouldUpdateClient() {



        ClientDto dto = new ClientDto();
        dto.setName(MODIFIED_CLIENT_NAME);

        restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + EXISTS_CLIENT_ID,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );

        assertNotNull(response.getBody());

        ClientDto client = response.getBody().stream()
                .filter(c -> c.getId().equals(1L))
                .findFirst()
                .orElse(null);

        assertNotNull(client);
        assertEquals(MODIFIED_CLIENT_NAME, client.getName());
    }

    @Test
    public void modifyWithNotExistingIdShouldReturnNotFound() {

        ClientDto dto = new ClientDto();
        dto.setName("Cliente Test");

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/999",
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void saveWithDuplicatedNameShouldReturnConflict() {

        ClientDto dto = new ClientDto();
        dto.setName("Daniel");

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    public void deleteDeleteableClientShouldDeleteClient(){
        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        ResponseEntity<ClientDto> saveResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                ClientDto.class
        );
        assertNotNull(saveResponse.getBody());
        Long deleteableId = saveResponse.getBody().getId();

        ResponseEntity<List<ClientDto>> responseListBeforeDelete = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );
        assertNotNull(responseListBeforeDelete.getBody());


        ClientDto clientBeforeDelete = responseListBeforeDelete.getBody().stream()
                .filter(c -> c.getId().equals(deleteableId))
                .findFirst()
                .orElse(null);

        assertNotNull(clientBeforeDelete);

        restTemplate.exchange(
                LOCALHOST + port+ SERVICE_PATH + "/" + deleteableId,
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );

        ResponseEntity<List<ClientDto>> responseListAfterDelete = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );

        assertNotNull(responseListAfterDelete.getBody());
        ClientDto clientAfterDelete = responseListAfterDelete.getBody().stream()
                .filter(c -> c.getId().equals(deleteableId))
                .findFirst()
                .orElse(null);

        assertNull(clientAfterDelete);

        assertNotNull(responseListAfterDelete.getBody());

        assertEquals(responseListBeforeDelete.getBody().size(), responseListAfterDelete.getBody().size()+1);

    }

    @Test
    public void deleteClientWithConflictShouldReturnConflict() {

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );

        assertNotNull(response.getBody());

        //buscamos un cliente no eliminable dinámicamente
        Long clientId = response.getBody().stream()
                .filter(client -> {
                    ResponseEntity<DeleteCheckResponseDto> canDelete =
                            restTemplate.exchange(
                                    LOCALHOST + port + SERVICE_PATH + "/" + client.getId() + "/can-delete",
                                    HttpMethod.GET,
                                    null,
                                    DeleteCheckResponseDto.class
                            );
                    return !canDelete.getBody().isCanDelete();
                })
                .map(ClientDto::getId)
                .findFirst()
                .orElseThrow();

        ResponseEntity<?> deleteResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + clientId,
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT, deleteResponse.getStatusCode());
    }

    @Test
    public void deleteWithNotExistingIdShouldReturnNotFound() {

        long nonExistingId = 999L;

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + nonExistingId,
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void canDeleteWithDeleteableClientShouldReturnTrue() {

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );

        assertNotNull(response.getBody());

        Long clientId = response.getBody().stream()
                .filter(client -> {
                    ResponseEntity<DeleteCheckResponseDto> canDelete =
                            restTemplate.exchange(
                                    LOCALHOST + port + SERVICE_PATH + "/" + client.getId() + "/can-delete",
                                    HttpMethod.GET,
                                    null,
                                    DeleteCheckResponseDto.class
                            );
                    return canDelete.getBody().isCanDelete();
                })
                .map(ClientDto::getId)
                .findFirst()
                .orElseThrow();

        ResponseEntity<DeleteCheckResponseDto> result = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + clientId + "/can-delete",
                HttpMethod.GET,
                null,
                DeleteCheckResponseDto.class
        );

        assertNotNull(result.getBody());
        assertTrue(result.getBody().isCanDelete());
        assertEquals("", result.getBody().getReason());
        assertTrue(result.getBody().getList().isEmpty());
    }


    @Test
    public void canDeleteWithConflictClientShouldReturnFalse() {

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType
        );

        assertNotNull(response.getBody());

        Long clientId = response.getBody().stream()
                .filter(client -> {
                    ResponseEntity<DeleteCheckResponseDto> canDelete =
                            restTemplate.exchange(
                                    LOCALHOST + port + SERVICE_PATH + "/" + client.getId() + "/can-delete",
                                    HttpMethod.GET,
                                    null,
                                    DeleteCheckResponseDto.class
                            );
                    return !canDelete.getBody().isCanDelete();
                })
                .map(ClientDto::getId)
                .findFirst()
                .orElseThrow();

        ResponseEntity<DeleteCheckResponseDto> result = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + clientId + "/can-delete",
                HttpMethod.GET,
                null,
                DeleteCheckResponseDto.class
        );

        assertNotNull(result.getBody());
        assertFalse(result.getBody().isCanDelete());
    }

    @Test
    public void canDeleteWithNotExistingIdShouldReturnNotFound() {

        long nonExistingId = 999L;

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/" + nonExistingId + "/can-delete",
                HttpMethod.GET,
                null,
                DeleteCheckResponseDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
