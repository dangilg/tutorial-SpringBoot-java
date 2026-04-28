package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;

import java.util.List;

public interface ClientService {

    Client getById(Long id);


    List<Client> findAll();

    Client save(Long id, ClientDto dto) throws NoIdFoundException;
}
