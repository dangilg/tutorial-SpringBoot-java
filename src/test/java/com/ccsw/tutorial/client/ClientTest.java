package com.ccsw.tutorial.client;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;

import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotDeleteableException;
import com.ccsw.tutorial.exceptions.NotValidClientNameException;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.LoanRepository;
import com.ccsw.tutorial.loan.model.Loan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    public static final Long EXISTS_CLIENT_ID = 1L;
    public static final Long NOT_EXISTS_CLIENT_ID = 0L;
    public static final String CLIENT_TEST_NAME = "Cliente Test";
    public static final String CLIENT_TEST_UPDATED_NAME = "Nuevo nombre cliente test";

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void saveWithoutIdShouldCreateClient() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_TEST_NAME);

        when(clientRepository.existsByName(CLIENT_TEST_NAME)).thenReturn(false);

        Client result = clientService.save(null, dto);

        assertEquals("Cliente Test", result.getName());
    }

    @Test
    public void saveWithExistingIdShouldUpdateClient() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_TEST_UPDATED_NAME);

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);
        client.setName(CLIENT_TEST_NAME);

        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));
        when(clientRepository.existsByName(CLIENT_TEST_UPDATED_NAME)).thenReturn(false);

        Client result = clientService.save(EXISTS_CLIENT_ID, dto);

        verify(clientRepository).findById(EXISTS_CLIENT_ID);
        verify(clientRepository).save(client);

        assertEquals(CLIENT_TEST_UPDATED_NAME, client.getName());
        assertEquals(CLIENT_TEST_UPDATED_NAME, result.getName());
    }

    @Test
    public void saveWithNotExistingIdShouldThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_TEST_NAME);

        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            clientService.save(NOT_EXISTS_CLIENT_ID, dto);
        });

        verify(clientRepository).findById(NOT_EXISTS_CLIENT_ID);
        verify(clientRepository, never()).save(any());
    }

    @Test
    public void saveWithDuplicatedNameShouldThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName("Daniel");

        when(clientRepository.existsByName("Daniel")).thenReturn(true);

        assertThrows(NotValidClientNameException.class, () -> {
            clientService.save(null, dto);
        });

        verify(clientRepository, never()).save(any());
    }

    @Test
    public void deleteWithExistingIdShouldDeleteClient() throws NoIdFoundException {

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);

        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.delete(EXISTS_CLIENT_ID);

        verify(clientRepository).findById(EXISTS_CLIENT_ID);
        verify(clientRepository).deleteById(EXISTS_CLIENT_ID);
    }

    @Test
    public void deleteWithNotExistingIdShouldThrowException() {

        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            clientService.delete(NOT_EXISTS_CLIENT_ID);
        });

        verify(clientRepository).findById(NOT_EXISTS_CLIENT_ID);
        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    public void deleteANonDeleteableClientShouldThrowException(){
        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStartDate(LocalDate.now().minusDays(1));
        loan.setEndDate(LocalDate.now().plusDays(1));

        Game game = new Game();
        game.setTitle("Juego Test");
        loan.setGame(game);

        when(clientRepository.findById(EXISTS_CLIENT_ID))
                .thenReturn(Optional.of(client));

        when(loanRepository.findByClientId(EXISTS_CLIENT_ID))
                .thenReturn(List.of(loan));

        assertThrows(NotDeleteableException.class,()->{
            clientService.delete(EXISTS_CLIENT_ID);
        });

        verify(clientRepository,times(2)).findById(EXISTS_CLIENT_ID);
        verify(loanRepository).findByClientId(EXISTS_CLIENT_ID);

    }
    @Test
    public void getByIdWithExistingIdShouldReturnClient() {

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);
        client.setName(CLIENT_TEST_NAME);

        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        Client result = clientService.getById(EXISTS_CLIENT_ID);

        assertNotNull(result);
        assertEquals(EXISTS_CLIENT_ID, result.getId());
        assertEquals(CLIENT_TEST_NAME, result.getName());

        verify(clientRepository).findById(EXISTS_CLIENT_ID);
    }

    @Test
    public void getByIdWithNotExistingIdShouldReturnNull() {

        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        Client result = clientService.getById(NOT_EXISTS_CLIENT_ID);

        assertNull(result);

        verify(clientRepository).findById(NOT_EXISTS_CLIENT_ID);
    }

    @Test
    public void findAllShouldReturnListWithClients() {

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);
        client.setName(CLIENT_TEST_NAME);

        List<Client> mockList = List.of(client);

        when(clientRepository.findAll()).thenReturn(mockList);

        List<Client> result = clientService.findAll();

    }

    @Test
    public void findAllShouldReturnEmptyList() {

        when(clientRepository.findAll()).thenReturn(List.of());

        List<Client> result = clientService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clientRepository).findAll();
    }

    @Test
    public void isDeleteableWithNotExistingIdShouldThrowException() {

        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(NoIdFoundException.class, () -> {
            clientService.isDeleteable(NOT_EXISTS_CLIENT_ID);
        });

        verify(clientRepository).findById(NOT_EXISTS_CLIENT_ID);
        verify(loanRepository, never()).findByClientId(any());
    }

    @Test
    public void isDeleteableWithNoLoansShouldReturnTrue() {

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);

        when(clientRepository.findById(EXISTS_CLIENT_ID))
                .thenReturn(Optional.of(client));

        when(loanRepository.findByClientId(EXISTS_CLIENT_ID))
                .thenReturn(List.of());

        DeleteCheckResponseDto result = clientService.isDeleteable(EXISTS_CLIENT_ID);

        assertNotNull(result);
        assertTrue(result.isCanDelete());
        assertEquals("", result.getReason());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    public void isDeleteableWithActiveLoanShouldReturnFalse() {

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStartDate(LocalDate.now().minusDays(1));
        loan.setEndDate(LocalDate.now().plusDays(1));

        Game game = new Game();
        game.setTitle("Juego Test");
        loan.setGame(game);

        when(clientRepository.findById(EXISTS_CLIENT_ID))
                .thenReturn(Optional.of(client));

        when(loanRepository.findByClientId(EXISTS_CLIENT_ID))
                .thenReturn(List.of(loan));

        DeleteCheckResponseDto result = clientService.isDeleteable(EXISTS_CLIENT_ID);

        assertNotNull(result);
        assertFalse(result.isCanDelete());
        assertEquals("EN USO", result.getReason());
        assertEquals(1, result.getList().size());
    }

    @Test
    public void isDeleteableWithFutureLoanShouldReturnFalse() {

        Client client = new Client();
        client.setId(EXISTS_CLIENT_ID);

        Loan loan = new Loan();
        loan.setId(2L);
        loan.setStartDate(LocalDate.now().plusDays(1));
        loan.setEndDate(LocalDate.now().plusDays(5));

        Game game = new Game();
        game.setTitle("Juego Futuro");
        loan.setGame(game);

        when(clientRepository.findById(EXISTS_CLIENT_ID))
                .thenReturn(Optional.of(client));

        when(loanRepository.findByClientId(EXISTS_CLIENT_ID))
                .thenReturn(List.of(loan));

        DeleteCheckResponseDto result = clientService.isDeleteable(EXISTS_CLIENT_ID);

        assertNotNull(result);
        assertFalse(result.isCanDelete());
        assertEquals("EN USO", result.getReason());
        assertEquals(1, result.getList().size());
    }
}