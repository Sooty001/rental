package com.example.rental.repositories.Impl;

import com.example.rental.models.Client;
import com.example.rental.repositories.ClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRepositoryImpl extends BaseRepository<Client> implements ClientRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Client findClientByUserId(int userId, boolean isDeleted) {
        String jpql = "SELECT c FROM Client c WHERE c.user.id = :userId AND c.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Client.class).setParameter("userId", userId).setParameter("isDeleted", isDeleted).getSingleResult();
    }

    @Override
    public String findByPhone(String phone, boolean isDeleted) {
        String jpql = "SELECT c.phone FROM Client c WHERE c.phone = :phone AND c.isDeleted = :isDeleted";
        try {
            return entityManager.createQuery(jpql, String.class).setParameter("phone", phone)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
