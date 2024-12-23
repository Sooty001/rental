package com.example.rental.services;

import com.example.rental.dto.AgentDto;
import com.example.rental.dto.BaseUserDto;
import com.example.rental.dto.ClientDto;
import com.example.rental.dto.UserRegistrationDto;
import com.example.rental.models.User;

public interface AuthService {
    void register(UserRegistrationDto registrationDTO);
    BaseUserDto getUser(String email);
    ClientDto getClient(String email);
    AgentDto getAgent(String email);
    User findById(int userId);
}
