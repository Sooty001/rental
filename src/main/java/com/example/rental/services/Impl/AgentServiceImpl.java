package com.example.rental.services.Impl;

import com.example.rental.dto.AgentCreateDto;
import com.example.rental.enums.UserRoles;
import com.example.rental.models.*;
import com.example.rental.repositories.*;
import com.example.rental.dto.AgentDto;
import com.example.rental.services.AgentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@EnableCaching
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final AgreementRepository agreementRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, AgreementRepository agreementRepository, PropertyRepository propertyRepository, UserRepository userRepository, ReviewRepository reviewRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.agreementRepository = agreementRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    @Cacheable("topAgent")
    public List<AgentDto> choosingSort(String sort) {
        List<AgentDto> agentDto;
        if (sort.equals("loyalty")) { agentDto = findAgentByLoyaltyClient(10); }
        else { agentDto = findAgentByRating(10); }
        return agentDto;
    }

    @Override
    public List<AgentDto> findAgentByRating(int countAgent) {
        return agentRepository.findTopAgentByRating(countAgent, false).stream().map(a ->
                modelMapper.map(a, AgentDto.class)).toList();
    }


    @Override
    public List<AgentDto> findAgentByLoyaltyClient(int countAgent) {
        List<Agreement> agreements = agreementRepository.getAll(Agreement.class);

        Map<Agent, Map<Integer, Integer>> agentClientDeals = new HashMap<>();

        for (Agreement agre : agreements) {

            Property property = agre.getProperty();
            Agent agent = property.getAgent();
            int clientId = agre.getClient().getId();

            agentClientDeals.computeIfAbsent(agent, k -> new HashMap<>());

            Map<Integer, Integer> clientDeals = agentClientDeals.get(agent);
            clientDeals.put(clientId, clientDeals.getOrDefault(clientId, 0) + 1);
        }

        Map<Agent, Integer> agentLoyalty = new HashMap<>();
        for (Agent agent : agentClientDeals.keySet()) {
            Map<Integer, Integer> clientDeals = agentClientDeals.get(agent);
            int repeatVisits = 0;


            for (int visits : clientDeals.values()) {
                if (visits > 1) {
                    repeatVisits += visits - 1;
                }
            }
            if (repeatVisits > 0) {
                agentLoyalty.put(agent, repeatVisits);
            }
        }

        List<Agent> sortedAgents = new ArrayList<>(agentLoyalty.keySet());
        sortedAgents.sort((a1, a2) -> agentLoyalty.get(a2) - agentLoyalty.get(a1));

        List<AgentDto> agentDto = new ArrayList<>();
        for (Agent agent : sortedAgents) {
            agentDto.add(modelMapper.map(agent, AgentDto.class));
            if (countAgent == 0) break;
            countAgent--;
        }

        return agentDto;
    }

    @Override
    public void updateAgentRating(Agreement agreement) {
        Agent agent = agreement.getProperty().getAgent();
        List<Property> propertyList = propertyRepository.findAllPropertyByAgentId(agent.getId(), false);
        double countReview = 0;
        double sumTotalRating = 0;

        for (Property property : propertyList) {
            List<Agreement> agreementList = agreementRepository.findAgreementsByReviewNotNullAndPropertyId(property.getId(), false);

            for (Agreement agreement1 : agreementList) {
                sumTotalRating += reviewRepository.findReviewByAgreementId(agreement1.getId()).getAgentRating();
                countReview++;
            }
        }

        double rating = Math.round((sumTotalRating / countReview) * 10.0) / 10.0;
        agent.setRating(rating);
        agentRepository.update(agent);
    }


    @Override
    public Page<AgentDto> findAll(int page, int size) {
        Page<Agent> agents = agentRepository.findAll(Agent.class, page, size);

        List<AgentDto> agentDto = agents.getContent().stream()
                .map(agent -> modelMapper.map(agent, AgentDto.class)).toList();

        return new PageImpl<>(agentDto, PageRequest.of(page - 1, size), agents.getTotalElements());
    }

    @Override
    public AgentDto findById(int id) {
        return modelMapper.map(agentRepository.findById(Agent.class, id), AgentDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "topAgent", allEntries = true)
    public void create(AgentCreateDto agentCreateDto) {
        User user = new User(agentCreateDto.getEmail(), userRepository.findByEmail(agentCreateDto.getEmail()),
                agentCreateDto.getPassword(), passwordEncoder.encode(agentCreateDto.getPassword()), UserRoles.AGENT);
        Agent agent = modelMapper.map(agentCreateDto, Agent.class);
        userRepository.create(user);
        agent.setPhone(agentCreateDto.getPhone(), agentRepository.findByPhone(agentCreateDto.getPhone(), false));
        agent.setUser(user);
        if (agentCreateDto.getPhotoUrl().isBlank()) { agent.setPhotoUrl("unknown.jpg"); }
        agentRepository.create(agent);
    }

    @Override
    @CacheEvict(cacheNames = "topAgent", key = "#id")
    public void update(AgentDto agentDto, int id) {
        Agent oldAgent = agentRepository.findById(Agent.class, id);
        Agent ag = modelMapper.map(agentDto, Agent.class);
        String uniquePhone = agentRepository.findByPhone(agentDto.getPhone(), false);
        ag.setId(id);
        if (!oldAgent.getPhone().equals(agentDto.getPhone())) {
            ag.setPhone(agentDto.getPhone(), uniquePhone);
        }
        ag.setRating(oldAgent.getRating());
        ag.setUser(oldAgent.getUser());
        agentRepository.update(ag);
    }

    @Override
    @CacheEvict(cacheNames = "topAgent", key = "#id")
    public void markDeleteById(int id) {
        Agent agent = agentRepository.findById(Agent.class, id);
        agent.setIsDeleted(true);
        agentRepository.update(agent);
    }
}


