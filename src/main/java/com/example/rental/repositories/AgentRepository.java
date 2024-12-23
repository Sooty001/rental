package com.example.rental.repositories;

import com.example.rental.models.Agent;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AgentRepository {
    List<Agent> findTopAgentByRating(int countAgent, boolean isDeleted);
    Agent findAgentByUserId(int userId, boolean isDeleted);
    String findByPhone(String phone, boolean isDeleted);

    Page<Agent> findAll(Class<Agent> entityClass, int page, int size);
    Agent findById(Class<Agent> entityClass, int id);
    void create(Agent entity);
    void update(Agent entity);
}
