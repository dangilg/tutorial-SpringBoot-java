package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import com.ccsw.tutorial.exceptions.NotValidDtoException;
import com.ccsw.tutorial.exceptions.NotValidLoanException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.filter.FilterDataModel;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private LoanServiceImp loanService;



    @Test
    void getLastIdShouldReturnRepositoryValue_WhenGreaterThanCurrent() {
        when(loanRepository.getLastId()).thenReturn(5L);

        long result = loanService.getLastId();

        assertEquals(5L, result);
        verify(loanRepository).getLastId();
    }

    @Test
    void getLastIdShouldKeepPreviousValueWhenRepositoryReturnsLowerValue() {
        // Primera llamada → setea lastId = 10
        when(loanRepository.getLastId()).thenReturn(10L);
        loanService.getLastId();

        // Segunda llamada → repo devuelve menor
        when(loanRepository.getLastId()).thenReturn(5L);

        long result = loanService.getLastId();

        assertEquals(10L, result); // NO debe bajar
        verify(loanRepository, times(2)).getLastId();
    }

    @Test
    void getLastIdShouldKeepPreviousValueWhenRepositoryReturnsSameValue() {
        // Primera llamada → lastId = 7
        when(loanRepository.getLastId()).thenReturn(7L);
        loanService.getLastId();

        // Segunda llamada → mismo valor
        when(loanRepository.getLastId()).thenReturn(7L);

        long result = loanService.getLastId();

        assertEquals(7L, result);
        verify(loanRepository, times(2)).getLastId();
    }




    @Test
    void isDeleteableShouldReturnTrueWhenLoanIsInPast() {
        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now().minusDays(10));
        loan.setEndDate(LocalDate.now().minusDays(5));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        DeleteCheckResponseDto result = loanService.isDeleteable(1L);

        assertTrue(result.isCanDelete());
        verify(loanRepository).findById(1L);
    }

    @Test
    void isDeleteableShouldReturnFalseWhenLoanStartsToday() {
        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusDays(5));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        DeleteCheckResponseDto result = loanService.isDeleteable(1L);

        assertFalse(result.isCanDelete());
        assertEquals("EN PROCESO", result.getReason());
    }

    @Test
    void isDeleteableShouldReturnFalseWhenLoanEndsToday() {
        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now().minusDays(5));
        loan.setEndDate(LocalDate.now());

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        DeleteCheckResponseDto result = loanService.isDeleteable(1L);

        assertFalse(result.isCanDelete());
        assertEquals("EN PROCESO", result.getReason());
    }

    @Test
    void isDeleteableShouldReturnFalsewhenLoanIsActive() {
        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now().minusDays(2));
        loan.setEndDate(LocalDate.now().plusDays(3));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        DeleteCheckResponseDto result = loanService.isDeleteable(1L);

        assertFalse(result.isCanDelete());
    }

    @Test
    void isDeleteableShouldReturnTrueWhenLoanIsInFuture() {
        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now().plusDays(5));
        loan.setEndDate(LocalDate.now().plusDays(10));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        DeleteCheckResponseDto result = loanService.isDeleteable(1L);

        assertTrue(result.isCanDelete());
    }

    @Test
    void isDeleteableWhenIdIsNullShouldThrowException() {
        assertThrows(NoIdFoundException.class, () -> {
            loanService.isDeleteable(null);
        });
    }

    @Test
    void isDeleteableWhenLoanDoesNotExistShouldThrowException() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            loanService.isDeleteable(1L);
        });

        verify(loanRepository).findById(1L);
    }



    @Test
    void deleteShouldDeleteLoanWhenValid() {
        Long id = 1L;

        when(loanRepository.existsById(id)).thenReturn(true);

        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now().minusDays(10));
        loan.setEndDate(LocalDate.now().minusDays(5));

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        loanService.delete(id);

        verify(loanRepository).deleteById(id);
    }

    @Test
    void deleteWhenIdIsNullShouldThrowException() {
        assertThrows(NoIdFoundException.class, () -> {
            loanService.delete(null);
        });

        verify(loanRepository, never()).deleteById(any());
    }

    @Test
    void deleteWhenLoanDoesNotExistShouldThrowException() {
        Long id = 1L;

        when(loanRepository.existsById(id)).thenReturn(false);

        assertThrows(NoIdFoundException.class, () -> {
            loanService.delete(id);
        });

        verify(loanRepository).existsById(id);
        verify(loanRepository, never()).deleteById(any());
    }

    @Test
    void deleteWhenLoanIsNotDeleteableShouldThrowException() {
        Long id = 1L;

        when(loanRepository.existsById(id)).thenReturn(true);

        Loan loan = new Loan();
        loan.setStartDate(LocalDate.now().minusDays(2));
        loan.setEndDate(LocalDate.now().plusDays(2)); // en curso → NO borrable

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        assertThrows(NotDeleteableException.class, () -> {
            loanService.delete(id);
        });

        verify(loanRepository, never()).deleteById(any());
    }




    @Test
    void findPageFilteredShouldReturnPageWhenValidDto() {
        PageFilterDto dto = new PageFilterDto();

        PageableRequest pageableRequest = mock(PageableRequest.class);
        when(pageableRequest.getPageable()).thenReturn(Pageable.unpaged());

        FilterDataModel filters = new FilterDataModel();
        filters.setClientId(1L);
        filters.setGameId(2L);
        filters.setDate("2024-01-01");

        dto = new PageFilterDto(pageableRequest, filters);

        Page<Loan> page = mock(Page.class);

        when(loanRepository.findAll(any(), (Pageable) any())).thenReturn(page);

        Page<Loan> result = loanService.findPageFiltered(dto);

        assertNotNull(result);
        assertEquals(page, result);

        verify(loanRepository).findAll(any(), (Pageable) any());
    }

    @Test
    void findPageFilteredShouldWorkWithNullFiltersValues() {
        PageableRequest pageableRequest = mock(PageableRequest.class);
        when(pageableRequest.getPageable()).thenReturn(Pageable.unpaged());

        FilterDataModel filters = new FilterDataModel();
        filters.setClientId(null);
        filters.setGameId(null);
        filters.setDate(null);

        PageFilterDto dto = new PageFilterDto(pageableRequest, filters);

        when(loanRepository.findAll(any(), (Pageable) any())).thenReturn(mock(Page.class));

        Page<Loan> result = loanService.findPageFiltered(dto);

        assertNotNull(result);
        verify(loanRepository).findAll(any(), (Pageable) any());
    }

    @Test
    void findPageFilteredWhenFiltersIsNullShouldThrowException() {
        PageableRequest pageableRequest = mock(PageableRequest.class);

        PageFilterDto dto = new PageFilterDto(pageableRequest, null);

        assertThrows(NotValidDtoException.class, () -> {
            loanService.findPageFiltered(dto);
        });
    }

    @Test
    void findPageFilteredWhenPageableIsNullShouldThrowException() {
        FilterDataModel filters = new FilterDataModel();

        PageFilterDto dto = new PageFilterDto(null, filters);

        assertThrows(NotValidDtoException.class, () -> {
            loanService.findPageFiltered(dto);
        });
    }


    @Test
    void calculateAvailabilityWithoutDatesShouldReturnAllClientsAndGames() {
        AvailableRequestDto dto = new AvailableRequestDto();

        List<Client> clients = List.of(new Client(), new Client());
        List<Game> games = List.of(new Game(), new Game());

        when(clientRepository.findAll()).thenReturn(clients);
        when(gameRepository.findAll()).thenReturn(games);

        AvailableResponseDto result = loanService.calculateAvailability(dto);

        assertEquals(2, result.getClients().size());
        assertEquals(2, result.getGames().size());

        verify(clientRepository).findAll();
        verify(gameRepository).findAll();
    }

    @Test
    void calculateAvailabilityWithStartDateShouldUseAvailabilityQueries() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());

        when(clientRepository.findAvailableClients(any(), any(), any()))
                .thenReturn(List.of(new Client()));

        when(gameRepository.findAvailableGames(any(), any(), any()))
                .thenReturn(List.of(new Game()));

        AvailableResponseDto result = loanService.calculateAvailability(dto);

        assertEquals(1, result.getClients().size());
        assertEquals(1, result.getGames().size());

        verify(clientRepository).findAvailableClients(any(), any(), any());
        verify(gameRepository).findAvailableGames(any(), any(), any());
    }

    @Test
    void calculateAvailabilityClientWithTwoLoansShouldBlockDates() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setClientId(1L);
        dto.setStartDate(LocalDate.now());

        when(loanRepository.countOverlappingLoansByClient(any(), anyLong(), any(), any()))
                .thenReturn(2L);

        when(clientRepository.findAvailableClients(any(), any(), any()))
                .thenReturn(List.of(new Client()));

        when(gameRepository.findAvailableGames(any(), any(), any()))
                .thenReturn(List.of(new Game()));

        AvailableResponseDto result = loanService.calculateAvailability(dto);

        assertTrue(result.getValidStartDates().isEmpty());
    }

    @Test
    void calculateAvailabilityGameWithOverlapShouldBlockDates() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setGameId(1L);
        dto.setStartDate(LocalDate.now());

        when(loanRepository.findOverlappingLoansByGame(any(), anyLong(), any(), any()))
                .thenReturn(List.of(new Loan()));

        when(clientRepository.findAvailableClients(any(), any(), any()))
                .thenReturn(List.of(new Client()));

        when(gameRepository.findAvailableGames(any(), any(), any()))
                .thenReturn(List.of(new Game()));

        AvailableResponseDto result = loanService.calculateAvailability(dto);

        assertTrue(result.getValidStartDates().isEmpty());
    }

    @Test
    void calculateAvailabilityConsecutiveDatesWithFiltersShouldReturnSingleInterval() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(loanRepository.countOverlappingLoansByClient(any(), anyLong(), any(), any()))
                .thenReturn(0L);

        when(loanRepository.findOverlappingLoansByGame(any(), anyLong(), any(), any()))
                .thenReturn(List.of());

        when(clientRepository.findAvailableClients(any(), any(), any()))
                .thenReturn(List.of(new Client()));

        when(gameRepository.findAvailableGames(any(), any(), any()))
                .thenReturn(List.of(new Game()));

        AvailableResponseDto result = loanService.calculateAvailability(dto);

        assertEquals(1, result.getValidStartDates().size());
    }

    @Test
    void calculateAvailabilityWithNonExistingLoanIdShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setLoanId(1L);

        when(loanRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoIdFoundException.class, () -> {
            loanService.calculateAvailability(dto);
        });
    }

    @Test
    void calculateAvailabilityWithEndDateOnlyShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setEndDate(LocalDate.now()); // start = null

        assertThrows(NotValidDtoException.class, () -> {
            loanService.calculateAvailability(dto);
        });
    }


    @Test
    public void calculateAvailabilityWithNotExistingLoanIdShouldThrowException(){
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setLoanId(999L);
        assertThrows(NoIdFoundException.class, () ->{
            loanService.calculateAvailability(dto);
        });
    }
    @Test
    void saveWithNullIdAndValidDtoShouldSaveLoan() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(5));
        dto.setClientId(1L);
        dto.setGameId(1L);

        Client client = new Client();
        Game game = new Game();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        when(loanRepository.countOverlappingLoansByClient(any(), anyLong(), any(), any()))
                .thenReturn(0L);

        when(loanRepository.findOverlappingLoansByGame(any(), anyLong(), any(), any()))
                .thenReturn(List.of());

        ArgumentCaptor<Loan> captor = ArgumentCaptor.forClass(Loan.class);

        loanService.save(null, dto);

        verify(loanRepository).save(captor.capture());

        Loan saved = captor.getValue();

        assertEquals(client, saved.getClient());
        assertEquals(game, saved.getGame());
        assertEquals(dto.getStartDate(), saved.getStartDate());
        assertEquals(dto.getEndDate(), saved.getEndDate());
    }


    @Test
    void saveWithExistingIdAndValidDtoShouldUpdateLoan() {
        Loan loan = new Loan();
        loan.setId(1L);

        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(3));
        dto.setClientId(1L);
        dto.setGameId(1L);

        Client client = new Client();
        Game game = new Game();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        when(loanRepository.countOverlappingLoansByClient(any(), anyLong(), any(), any()))
                .thenReturn(0L);

        when(loanRepository.findOverlappingLoansByGame(any(), anyLong(), any(), any()))
                .thenReturn(List.of());

        loanService.save(1L, dto);

        verify(loanRepository).save(loan);
    }


    @Test
    void saveWithNullDtoShouldThrowException() {
        assertThrows(NotValidDtoException.class, () -> {
            loanService.save(null, null);
        });
    }

    @Test
    void saveWithNonExistingIdShouldThrowException() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        AvailableRequestDto dto = new AvailableRequestDto();

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(1L, dto);
        });
    }

    @Test
    void saveWithNullStartDateShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setEndDate(LocalDate.now());
        dto.setClientId(1L);
        dto.setGameId(1L);

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }
    @Test
    void saveWithNullEndDateShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setClientId(1L);
        dto.setGameId(1L);

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithNullClientIdShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());
        dto.setGameId(1L);

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithNullGameIdShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());
        dto.setClientId(1L);

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithNonExistingClientShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithNonExistingGameShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithEndDateBeforeStartDateShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().minusDays(1));
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(new Game()));

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithMoreThanFourteenDaysShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(20));
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(new Game()));

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithClientHavingTwoLoansShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(2));
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(new Game()));

        when(loanRepository.countOverlappingLoansByClient(any(), anyLong(), any(), any()))
                .thenReturn(2L);

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }

    @Test
    void saveWithGameAlreadyLoanedShouldThrowException() {
        AvailableRequestDto dto = new AvailableRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(2));
        dto.setClientId(1L);
        dto.setGameId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(new Game()));

        when(loanRepository.countOverlappingLoansByClient(any(), anyLong(), any(), any()))
                .thenReturn(0L);

        when(loanRepository.findOverlappingLoansByGame(any(), anyLong(), any(), any()))
                .thenReturn(List.of(new Loan()));

        assertThrows(NotValidLoanException.class, () -> {
            loanService.save(null, dto);
        });
    }


}
