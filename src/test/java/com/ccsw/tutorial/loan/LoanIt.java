package com.ccsw.tutorial.loan;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.available.Interval;
import com.ccsw.tutorial.loan.model.filter.FilterDataModel;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import com.ccsw.tutorial.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIt {
    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    private static final Long CLIENT_ID = 1L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    JwtService tokenService;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responsePageType = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };
    ParameterizedTypeReference<AvailableResponseDto> responseAvailableType = new ParameterizedTypeReference<AvailableResponseDto>() {
    };

    private HttpHeaders getHeaders() {
        HttpHeaders ret = new HttpHeaders();
        ret.set("Authorization", "Bearer " + tokenService.generateToken("admin"));
        return ret;
    }

    private PageableRequest getPageableRequest(){
        return new PageableRequest(0,5);
    }
    private FilterDataModel getEmptyFilter(){
        FilterDataModel filter =  new FilterDataModel();
        filter.setClientId(null);
        filter.setGameId(null);
        filter.setDate(null);
        return filter;
    }

    private AvailableRequestDto getEmptyAvailableRequest(){
        return new AvailableRequestDto();
    }

    @Test
    public void findWithNoFiltersShouldReturnFirstPageOfLoansWith4Elements(){
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),getEmptyFilter());

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto), responsePageType
        );

        assertNotNull(response.getBody());
        assertEquals(4,response.getBody().getTotalElements());
    }

    @Test
    public void findWithClientFilteredShouldReturnFirstPageOfLoansOfClient(){
        FilterDataModel filter = getEmptyFilter();

        filter.setClientId(1L);
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filter);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto), responsePageType
        );

        assertNotNull(response.getBody());
        List<LoanDto> result = response.getBody().getContent();
        List<LoanDto> filtered = result.stream().filter(loanDto -> {
            return loanDto.getClient().getId().equals(1L);
        }).toList();
        assertEquals(result.size(),filtered.size());
    }

    @Test
    public void findWithGameFilteredShouldReturnFirstPageOfLoansOfGame(){
        FilterDataModel filter = getEmptyFilter();

        filter.setGameId(1L);
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filter);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto), responsePageType
        );

        assertNotNull(response.getBody());
        List<LoanDto> result = response.getBody().getContent();
        List<LoanDto> filtered = result.stream().filter(loanDto -> {
            return loanDto.getGame().getId().equals(1L);
        }).toList();

        assertEquals(result.size(),filtered.size());
    }

    @Test
    public void findWithDateFilteredShouldReturnFirstPageOfLoansWithDateBetweenLoanStartDateAndLoanEndDate(){
        FilterDataModel filter = getEmptyFilter();
        String filterDateStr ="2026-05-25";
        LocalDate filterDate = LocalDate.parse(filterDateStr);
        filter.setDate(filterDateStr);
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filter);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto), responsePageType
        );

        assertNotNull(response.getBody());
        List<LoanDto> result = response.getBody().getContent();

        List<LoanDto> filtered = result.stream().filter(loanDto -> {
            LocalDate start = loanDto.getStartDate();
            LocalDate end = loanDto.getEndDate();
            return (
                    (
                            start.isEqual(filterDate)||
                            start.isBefore(filterDate)
                    ) &&
                    (
                            end.isEqual(filterDate)||
                            end.isAfter(filterDate)
                    )
            );
        }).toList();

        assertEquals(result.size(),filtered.size());
    }

    @Test
    public void findWithNullFilterDataModelShouldThrowException(){
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),null);
        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    @Test
    public void findWithNullPageableRequestShouldThrowException(){
        PageFilterDto dto = new PageFilterDto(null,getEmptyFilter());
        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    @Test
    public void calculateAvailabilityWithNoDataShouldReturnAllClientsAndGamesAndAFullInterval(){
        AvailableRequestDto request = getEmptyAvailableRequest();

        String url =LOCALHOST + port + SERVICE_PATH + "/available";
        ResponseEntity<AvailableResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        AvailableResponseDto body = response.getBody();
        assertEquals(6,body.getGames().size());
        assertEquals(3,body.getClients().size());
        assertEquals(1,body.getValidStartDates().size());
        Interval interval = body.getValidStartDates().get(0);
        assertTrue(interval.getStart().isBefore(interval.getEnd()));
        //Se establece un máximo de 60 días entre fecha de inicio y fecha de fin, por lo q la resta de end menos start son 59 días
        assertEquals(interval.getEnd(),interval.getStart().plusDays(59));
        assertNull(body.getValidEndDates());
    }

    @Test

    public void calculateAvailabilityWithClientSelectedShouldReturnAllGamesAndClientsButFilterStartDatesByClient(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setClientId(1L);

        ResponseEntity<AvailableResponseDto> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        AvailableResponseDto body = response.getBody();
        assertEquals(6,body.getGames().size());
        assertEquals(3,body.getClients().size());
        assertEquals(1,body.getValidStartDates().size());
        Interval start = body.getValidStartDates().get(0);




        assertEquals(LocalDate.now(),start.getStart());
        assertEquals(LocalDate.now().plusDays(59),start.getEnd());

        assertNull(body.getValidEndDates());
    }


    @Test
    public void calculateAvailabilityWithClientAndGameSelectedShouldReturnAllClientsAndGamesButFilterStartDatesByGameAndClient(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setClientId(1L);
        request.setGameId(1L);

        ResponseEntity<AvailableResponseDto> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        AvailableResponseDto body = response.getBody();
        assertEquals(6,body.getGames().size());
        assertEquals(3,body.getClients().size());
        assertEquals(2,body.getValidStartDates().size());
        Interval start1 = body.getValidStartDates().get(0);
        Interval start2 = body.getValidStartDates().get(1);

        assertEquals(LocalDate.now(),start1.getStart());
        assertEquals(LocalDate.parse("2026-06-09"),start1.getEnd());

        assertEquals(LocalDate.parse("2026-06-13"),start2.getStart());
        assertEquals(LocalDate.now().plusDays(59),start2.getEnd());

        assertNull(body.getValidEndDates());
    }
    @Test
    public void calculateAvailabilityWithGameSelectedShouldReturnAllGamesAndClientsButFilterStartDatesByGame(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setGameId(1L);

        ResponseEntity<AvailableResponseDto> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        AvailableResponseDto body = response.getBody();
        assertEquals(6,body.getGames().size());
        assertEquals(3,body.getClients().size());
        assertEquals(2,body.getValidStartDates().size());
        Interval start1 = body.getValidStartDates().get(0);
        Interval start2 = body.getValidStartDates().get(1);

        assertEquals(LocalDate.now(),start1.getStart());
        assertEquals(LocalDate.parse("2026-06-09"),start1.getEnd());

        assertEquals(LocalDate.parse("2026-06-13"),start2.getStart());
        assertEquals(LocalDate.now().plusDays(59),start2.getEnd());

        assertNull(body.getValidEndDates());
    }


    @Test
    public void calculateAvailabilityWithStartDateSelectedShouldReturnFilteredGamesAndClientsAndEndDates(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setStartDate(LocalDate.parse("2026-05-29"));

        ResponseEntity<AvailableResponseDto> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        AvailableResponseDto body = response.getBody();
        assertEquals(5,body.getGames().size());
        assertEquals(3,body.getClients().size());
        assertEquals(1,body.getValidStartDates().size());
        assertEquals(1,body.getValidEndDates().size());
        Interval start1 = body.getValidStartDates().get(0);
        Interval end = body.getValidEndDates().get(0);

        assertEquals(LocalDate.now(),start1.getStart());
        assertEquals(LocalDate.now().plusDays(59),start1.getEnd());

        assertEquals(LocalDate.parse("2026-05-29"),end.getStart());
        assertEquals(LocalDate.parse("2026-05-29").plusDays(13),end.getEnd());


    }

    @Test
    public void calculateAvailabilityWithStartDateAndClientAndGameSelectedShouldReturnFiltratedEndDates(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setClientId(1L);
        request.setGameId(1L);
        request.setStartDate(LocalDate.parse("2026-05-31"));

        ResponseEntity<AvailableResponseDto> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        AvailableResponseDto body = response.getBody();
        assertEquals(5,body.getGames().size());
        assertEquals(3,body.getClients().size());
        assertEquals(1,body.getValidEndDates().size());
        Interval end = body.getValidEndDates().get(0);
        assertEquals(LocalDate.parse("2026-05-31"),end.getStart());
        assertEquals(LocalDate.parse("2026-06-09"),end.getEnd());

    }

    @Test
    public void calculateAvailabilityWithNotValidIdShouldThrowException(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setLoanId(999L);

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void calculateAvailabilityWithNullStartDateAndNotNullEndDateShouldThrowException(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setEndDate(LocalDate.now());

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/available",
                HttpMethod.POST,
                new HttpEntity<>(request,getHeaders()),
                responseAvailableType
        );

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
    }

    //TODO
    //Quedan de hacer los tests de save,count,delete e isDeleteable

    @Test
    public void saveWithNoIdAndCorrectValuesShouldCreateANewLoan(){
        AvailableRequestDto request = getEmptyAvailableRequest();
        request.setClientId(1L);
        request.setGameId(1L);
        request.setStartDate(LocalDate.parse("2026-12-01"));
        request.setEndDate(LocalDate.parse("2026-12-05"));

        FilterDataModel initialFilters = getEmptyFilter();
        initialFilters.setDate("2026-12-02");
        PageFilterDto initialRequest = new PageFilterDto(getPageableRequest(),initialFilters);
        ResponseEntity<ResponsePage<LoanDto>> loanPage = restTemplate.exchange(
                LOCALHOST+port+SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(initialRequest),
                responsePageType
        );


        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(request,getHeaders()),
                Void.class
        );

        ResponseEntity<ResponsePage<LoanDto>> loanPageAfterSave = restTemplate.exchange(
                LOCALHOST+port+SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(initialRequest),
                responsePageType
        );
        assertNotNull(loanPage.getBody());
        //assertNotNull(response.getBody());
        assertNotNull(loanPageAfterSave.getBody());

        assertEquals(0,loanPage.getBody().getContent().size());
        assertEquals(1,loanPageAfterSave.getBody().getContent().size());
        LoanDto saved = loanPageAfterSave.getBody().getContent().get(0);

        assertEquals(1L,saved.getClient().getId());
        assertEquals(1L,saved.getGame().getId());
        assertEquals(LocalDate.parse("2026-12-01"),saved.getStartDate());
        assertEquals(LocalDate.parse("2026-12-05"),saved.getEndDate());
    }

    @Test
    public void saveWithIdAndCorrectValuesShouldModifyALoan(){
        //INSERT INTO loan(game_id,client_id,start_date,end_date) VALUES (1,2,'2026-06-10','2026-06-12');

        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

        FilterDataModel filters = getEmptyFilter();
        filters.setDate("2026-06-11");
        PageFilterDto request = new PageFilterDto(getPageableRequest(),filters);

        ResponseEntity<ResponsePage<LoanDto>> initialResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                responsePageType
        );

        assertNotNull(initialResponse.getBody());

        LoanDto initialLoan = initialResponse.getBody().getContent().get(0);
        assertNotEquals(1L,initialLoan.getClient().getId());
        assertNotEquals(2L,initialLoan.getGame().getId());
        assertNotEquals(LocalDate.parse("2026-12-01"),initialLoan.getStartDate());
        assertNotEquals(LocalDate.parse("2026-12-05"),initialLoan.getEndDate());

        Long id = initialLoan.getId();
        restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save/"+id,
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        filters.setDate("2026-12-03");
        ResponseEntity<ResponsePage<LoanDto>> responseAfterSave = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                responsePageType
        );
        assertNotNull(responseAfterSave.getBody());
        assertEquals(1,responseAfterSave.getBody().getContent().size());

        LoanDto loanAfterSave = responseAfterSave.getBody().getContent().get(0);

        assertEquals(id,loanAfterSave.getId());
        assertEquals(1L,loanAfterSave.getClient().getId());
        assertEquals(2L,loanAfterSave.getGame().getId());
        assertEquals(LocalDate.parse("2026-12-01"),loanAfterSave.getStartDate());
        assertEquals(LocalDate.parse("2026-12-05"),loanAfterSave.getEndDate());
    }

    @Test
    public void saveWithNotExistingLoanIdShouldThrowException(){

        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save/999",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithClientNullsInDtoShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(null);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithGameNullsInDtoShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(null);
        saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }
    @Test
    public void saveWithStartDateNullsInDtoShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(null);
        saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }
    @Test
    public void saveWithEndDateNullsInDtoShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
        saveRequest.setEndDate(null);

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithNotExistingClientShouldThrowException(){
            AvailableRequestDto saveRequest = getEmptyAvailableRequest();
            saveRequest.setClientId(999L);
            saveRequest.setGameId(2L);
            saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
            saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

            ResponseEntity<?>response = restTemplate.exchange(
                    LOCALHOST + port + SERVICE_PATH +"/save",
                    HttpMethod.PUT,
                    new HttpEntity<>(saveRequest,getHeaders()),
                    Void.class
            );

            assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithNotExistingGameShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(999L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-01"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-05"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithEndDateBeforeStartDateShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(1L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-05"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-01"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithEndDateAfter14DaysOfStartDateShouldThrowExeption(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(1L);
        saveRequest.setStartDate(LocalDate.parse("2026-12-05"));
        saveRequest.setEndDate(LocalDate.parse("2026-12-30"));

        ResponseEntity<?>response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithNotValidDatesForClientShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2030-01-01"));
        saveRequest.setEndDate(LocalDate.parse("2030-01-05"));

        ResponseEntity<?> save1 = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        saveRequest.setGameId(1L);
        ResponseEntity<?>save2 =  restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        saveRequest.setGameId(3L);
        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void saveWithNotValidDatesForGameShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2030-01-01"));
        saveRequest.setEndDate(LocalDate.parse("2030-01-05"));

        ResponseEntity<?> save1 = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        saveRequest.setClientId(2L);
        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void getLastIdShouldReturnLastIdOfLoans(){
        PageableRequest pageableRequest = getPageableRequest();
        PageFilterDto dto = new PageFilterDto(pageableRequest,getEmptyFilter());

        ResponseEntity<ResponsePage<LoanDto>> pageOfLoans = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                responsePageType
        );

        assertNotNull(pageOfLoans.getBody());
        List<LoanDto> loanList = pageOfLoans.getBody().getContent();
        assertTrue(loanList.size()<pageableRequest.getPageSize());
        Long id = loanList.get(loanList.size()-1).getId();

        ResponseEntity<Long> lastId = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/lastId",
                HttpMethod.GET,
                null,
                Long.class
        );
        assertNotNull(lastId.getBody());

        assertEquals(id,lastId.getBody());


    }

    @Test
    public void isDeleteableWhenALoanIsNotActiveShouldReturnADeleteCheckResponseTrue(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2030-01-01"));
        saveRequest.setEndDate(LocalDate.parse("2030-01-05"));

        ResponseEntity<?> save1 = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        FilterDataModel filters = getEmptyFilter();
        filters.setDate("2030-01-03");
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filters);
        ResponseEntity<ResponsePage<LoanDto>> pageOfLoans = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                responsePageType
        );

        assertNotNull(pageOfLoans.getBody());
        assertEquals(1,pageOfLoans.getBody().getContent().size());
        LoanDto loan = pageOfLoans.getBody().getContent().get(0);

        ResponseEntity<DeleteCheckResponseDto> isDeleteable = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/"+loan.getId() + "/can-delete",
                HttpMethod.GET,
                null,
                DeleteCheckResponseDto.class
        );
        assertNotNull(isDeleteable.getBody());
        assertTrue(isDeleteable.getBody().isCanDelete());
    }

    @Test
    public void isDeleteableWhenALoanIsActiveShouldReturnFalse(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(3L);
        saveRequest.setGameId(4L);
        saveRequest.setStartDate(LocalDate.now());
        saveRequest.setEndDate(LocalDate.now().plusDays(5));

        ResponseEntity<?> save1 = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        FilterDataModel filters = getEmptyFilter();
        filters.setDate(LocalDate.now().toString());
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filters);
        ResponseEntity<ResponsePage<LoanDto>> pageOfLoans = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                responsePageType
        );

        assertNotNull(pageOfLoans.getBody());

        LoanDto loan = pageOfLoans.getBody().getContent().get(pageOfLoans.getBody().getContent().size()-1);

        ResponseEntity<DeleteCheckResponseDto> isDeleteable = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/"+loan.getId() + "/can-delete",
                HttpMethod.GET,
                null,
                DeleteCheckResponseDto.class
        );
        assertNotNull(isDeleteable.getBody());
        assertFalse(isDeleteable.getBody().isCanDelete());
    }

    @Test
    public void isDeleteableWithNotExistingIsShouldThrowException(){
        ResponseEntity<DeleteCheckResponseDto> isDeleteable = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/999/can-delete",
                HttpMethod.GET,
                null,
                DeleteCheckResponseDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND,isDeleteable.getStatusCode());
    }

    @Test
    public void deleteANotActiveLoanShouldDelete(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(1L);
        saveRequest.setGameId(2L);
        saveRequest.setStartDate(LocalDate.parse("2030-01-01"));
        saveRequest.setEndDate(LocalDate.parse("2030-01-05"));

        ResponseEntity<?> save1 = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        FilterDataModel filters = getEmptyFilter();
        filters.setDate("2030-01-03");
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filters);
        ResponseEntity<ResponsePage<LoanDto>> pageOfLoans = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                responsePageType
        );

        assertNotNull(pageOfLoans.getBody());
        assertEquals(1,pageOfLoans.getBody().getContent().size());
        LoanDto loan = pageOfLoans.getBody().getContent().get(0);

        ResponseEntity<?> deleteResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/"+loan.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );


        ResponseEntity<ResponsePage<LoanDto>> pageOfLoansAfterDelete = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                responsePageType
        );

        assertNotNull(pageOfLoansAfterDelete.getBody());
        assertEquals(0,pageOfLoansAfterDelete.getBody().getContent().size());
    }

    @Test
    public void deleteAnActiveLoanShouldThrowException(){
        AvailableRequestDto saveRequest = getEmptyAvailableRequest();
        saveRequest.setClientId(3L);
        saveRequest.setGameId(4L);
        saveRequest.setStartDate(LocalDate.now());
        saveRequest.setEndDate(LocalDate.now().plusDays(5));

        ResponseEntity<?> save1 = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH +"/save",
                HttpMethod.PUT,
                new HttpEntity<>(saveRequest,getHeaders()),
                Void.class
        );

        FilterDataModel filters = getEmptyFilter();
        filters.setDate(LocalDate.now().toString());
        PageFilterDto dto = new PageFilterDto(getPageableRequest(),filters);
        ResponseEntity<ResponsePage<LoanDto>> pageOfLoans = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(dto),
                responsePageType
        );

        assertNotNull(pageOfLoans.getBody());

        LoanDto loan = pageOfLoans.getBody().getContent().get(pageOfLoans.getBody().getContent().size()-1);

        ResponseEntity<?> deleteResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/"+loan.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.CONFLICT,deleteResponse.getStatusCode());
    }

    @Test
    public void deleteANotExistingLoanShouldThrowException(){
        ResponseEntity<?> deleteResponse = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/999",
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND,deleteResponse.getStatusCode());
    }
}
