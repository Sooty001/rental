package com.example.rental.services.Impl;

import com.example.rental.dto.AgentDto;
import com.example.rental.dto.ClientDto;
import com.example.rental.dto.BaseUserDto;
import com.example.rental.dto.UserRegistrationDto;
import com.example.rental.enums.UserRoles;
import com.example.rental.exceptions.PasswordConfirmationMismatchException;
import com.example.rental.models.Agent;
import com.example.rental.models.Client;
import com.example.rental.models.User;
import com.example.rental.repositories.AgentRepository;
import com.example.rental.repositories.ClientRepository;
import com.example.rental.repositories.UserRepository;
import com.example.rental.services.AuthService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final AgentRepository agentRepository;

    @Autowired
    public AuthServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, ClientRepository clientRepository, AgentRepository agentRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    @Transactional
    public void register(UserRegistrationDto regDto) {
        if (!regDto.getPassword().equals(regDto.getConfirmPassword())) {
            throw new PasswordConfirmationMismatchException();
        }

        UserRoles role = regDto.getRole().equals("CLIENT") ? UserRoles.CLIENT : UserRoles.AGENT;
        User user = new User(
                regDto.getEmail(),
                userRepository.findByEmail(regDto.getEmail()),
                regDto.getPassword(),
                passwordEncoder.encode(regDto.getPassword()),
                role
        );

        userRepository.create(user);
        if (role == UserRoles.CLIENT) {
            Client client = modelMapper.map(regDto, Client.class);
            client.setPhone(regDto.getPhone(), clientRepository.findByPhone(regDto.getPhone(), false));
            client.setUser(user);
            if (regDto.getPhotoUrl().isBlank()) { client.setPhotoUrl("unknown.jpg"); }
            clientRepository.create(client);
        } else {
            Agent agent = modelMapper.map(regDto, Agent.class);
            agent.setPhone(regDto.getPhone(), agentRepository.findByPhone(regDto.getPhone(), false));
            agent.setUser(user);
            if (regDto.getPhotoUrl().isBlank()) { agent.setPhotoUrl("unknown.jpg"); }
            agentRepository.create(agent);
        }
    }

    @Override
    public BaseUserDto getUser(String email) {
        User user = userRepository.findUserByEmail(email);

        BaseUserDto baseUserDto = null;

        if (user.getRole() == UserRoles.CLIENT || user.getRole() == UserRoles.ADMIN) {
            Client client = clientRepository.findClientByUserId(user.getId(), false);
            baseUserDto = modelMapper.map(client, BaseUserDto.class);
        }
        else if (user.getRole() == UserRoles.AGENT) {
            Agent agent = agentRepository.findAgentByUserId(user.getId(),false);
            baseUserDto = modelMapper.map(agent, BaseUserDto.class);
        }

        return baseUserDto;
    }

    @Override
    public ClientDto getClient(String email) {
        User user = userRepository.findUserByEmail(email);

        Client client = null;
        if (user.getRole() == UserRoles.CLIENT || user.getRole() == UserRoles.ADMIN) {
            client = clientRepository.findClientByUserId(user.getId(), false);
        }

        return modelMapper.map(client, ClientDto.class);
    }

    @Override
    public AgentDto getAgent(String email) {
        User user = userRepository.findUserByEmail(email);

        Agent agent = null;
        if (user.getRole() == UserRoles.AGENT) {
            agent = agentRepository.findAgentByUserId(user.getId(), false);
        }

        return modelMapper.map(agent, AgentDto.class);
    }

    public User findById(int userId) {
        return userRepository.findById(User.class, userId);
    }
}
