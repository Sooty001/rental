package com.example.rental.repositories;

import com.example.rental.models.Client;
import org.springframework.data.domain.Page;

public interface ClientRepository {
    Client findClientByUserId(int userId, boolean isDeleted);
    String findByPhone(String phone, boolean isDeleted);

    Page<Client> findAll(Class<Client> entityClass, int page, int size);
    Client findById(Class<Client> entityClass, int id);
    void create(Client entity);
    void update(Client entity);
}
