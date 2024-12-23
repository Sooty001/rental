package com.example.rental.services;

import com.example.rental.dto.ClientCreateDto;
import com.example.rental.dto.ClientDto;
import org.springframework.data.domain.Page;


public interface ClientService {
    Page<ClientDto> findAll(int page, int size);
    ClientDto findById(int id);
    void create(ClientCreateDto clientCreateDto);
    void update(ClientDto clientDto, int id);
    void markDeleteById(int id);
}
