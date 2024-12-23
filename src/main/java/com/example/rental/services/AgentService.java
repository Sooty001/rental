package com.example.rental.services;

import com.example.rental.dto.AgentCreateDto;
import com.example.rental.models.Agreement;
import com.example.rental.dto.AgentDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AgentService {
    List<AgentDto> choosingSort(String sort);
    List<AgentDto> findAgentByRating(int countAgent);
    List<AgentDto> findAgentByLoyaltyClient(int countAgent);
    void updateAgentRating(Agreement agreement);

    Page<AgentDto> findAll(int page, int size);
    AgentDto findById(int id);
    void create(AgentCreateDto AgentCreateDto);
    void update(AgentDto agentDto, int id);
    void markDeleteById(int id);
}
