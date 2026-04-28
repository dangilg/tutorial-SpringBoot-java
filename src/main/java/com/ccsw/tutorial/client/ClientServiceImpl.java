package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import com.ccsw.tutorial.exceptions.NotValidClientNameException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public Client getById(Long id){
        return this.clientRepository.findById(id).orElse(null);
    }



    @Override
    public List<Client> findAll(){
        return (List<Client>) this.clientRepository.findAll();
    }

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
}
