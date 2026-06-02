package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckObject;
import com.ccsw.tutorial.common.deleteCheck.DeleteCheckResponseDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidClientNameException;
import com.ccsw.tutorial.loan.LoanRepository;
import com.ccsw.tutorial.loan.model.Loan;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    LoanRepository loanRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Client getById(Long id){
        return this.clientRepository.findById(id).orElse(null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll(){
        return (List<Client>) this.clientRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Client save(Long id, ClientDto dto) throws NoIdFoundException,NotValidClientNameException {
        Client client;

        if(id==null){
            client = new Client();
        }
        else{
            client = this.getById(id);
        }
        if(client==null){
            throw  new NoIdFoundException();
        }
        if(this.clientRepository.existsByName(dto.getName())){
            throw new NotValidClientNameException();
        }
        client.setName(dto.getName());
        this.clientRepository.save(client);
        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws NoIdFoundException{
        if (this.getById(id) == null) {
            throw new NoIdFoundException();
        }

        this.clientRepository.deleteById(id);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteCheckResponseDto isDeleteable(Long id)throws NoIdFoundException{
        if(clientRepository.findById(id).isEmpty()){
            throw new NoIdFoundException();
        }

        LocalDate today = LocalDate.now();
        List<Loan> conflictLoans = loanRepository.findByClientId(id)
                .stream()
                .filter(loan->
                        (
                                !today.isBefore(loan.getStartDate())
                                &&
                                !today.isAfter(loan.getEndDate())
                        )
                        ||
                        loan.getStartDate().isAfter(today)
                )
                .toList();
        if(!conflictLoans.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            //obtenemos el id y el nombre del juego que tiene reservado + las fechas de la reserva
            List<DeleteCheckObject> list = conflictLoans.stream().map(g->new DeleteCheckObject(g.getId(),
                    g.getGame().getTitle()+" [ "+g.getStartDate().format(formatter)+" - "+g.getEndDate().format(formatter)+" ]"))
                    .toList();
            return new DeleteCheckResponseDto(false,"EN USO", list);
        }
        else{
            return new DeleteCheckResponseDto(true, "",List.of());
        }
    }
}
