package com.example.rental.repositories.Impl;

import com.example.rental.models.Agent;
import com.example.rental.repositories.AgentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgentRepositoryImpl extends BaseRepository<Agent> implements AgentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Agent> findTopAgentByRating(int countAgent, boolean isDeleted) {
        return entityManager.createQuery("SELECT a FROM Agent a WHERE a.isDeleted = :isDeleted ORDER BY a.rating DESC", Agent.class)
                .setParameter("isDeleted", isDeleted).setMaxResults(countAgent).getResultList();
    }

    @Override
    public Agent findAgentByUserId(int userId, boolean isDeleted) {
        String jpql = "SELECT a FROM Agent a WHERE a.user.id = :userId AND a.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Agent.class).setParameter("userId", userId).setParameter("isDeleted", isDeleted).getSingleResult();
    }

    @Override
    public String findByPhone(String phone, boolean isDeleted) {
        String jpql = "SELECT a.phone FROM Agent a WHERE a.phone = :phone AND a.isDeleted = :isDeleted";
        try {
            return entityManager.createQuery(jpql, String.class).setParameter("phone", phone)
                    .setParameter("isDeleted", isDeleted)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

