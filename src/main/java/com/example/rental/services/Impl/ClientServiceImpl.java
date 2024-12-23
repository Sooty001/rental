package com.example.rental.services.Impl;

import com.example.rental.dto.ClientCreateDto;
import com.example.rental.dto.ClientDto;
import com.example.rental.enums.UserRoles;
import com.example.rental.models.Client;
import com.example.rental.models.User;
import com.example.rental.repositories.ClientRepository;
import com.example.rental.repositories.UserRepository;
import com.example.rental.services.ClientService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ClientDto> findAll(int page, int size) {
        List<ClientDto> clientDto = new ArrayList<>();
        Page<Client> clients = clientRepository.findAll(Client.class, page, size);

        for (Client client : clients) {
            ClientDto modClient = modelMapper.map(client, ClientDto.class);
            if (client.getUser() != null) {
                modClient.setEmail(client.getUser().getEmail());
            }
            clientDto.add(modClient);
        }

        return new PageImpl<>(clientDto, clients.getPageable(), clients.getTotalElements());
    }

    @Override
    public ClientDto findById(int id) {
        Client client = clientRepository.findById(Client.class, id);
        ClientDto clientDto = modelMapper.map(client, ClientDto.class);

        if (client.getUser() != null) {
            clientDto.setEmail(client.getUser().getEmail());
        }
        return clientDto;
    }

    @Override
    @Transactional
    public void create(ClientCreateDto clientCreateDto) {
        User user = new User(clientCreateDto.getEmail(), userRepository.findByEmail(clientCreateDto.getEmail()), clientCreateDto.getPassword(), passwordEncoder.encode(clientCreateDto.getPassword()), UserRoles.CLIENT);
        Client client = modelMapper.map(clientCreateDto, Client.class);
        userRepository.create(user);
        client.setPhone(clientCreateDto.getPhone(), clientRepository.findByPhone(clientCreateDto.getPhone(), false));
        client.setUser(user);
        if (clientCreateDto.getPhotoUrl().isBlank()) { client.setPhotoUrl("unknown.jpg"); }
        clientRepository.create(client);
    }

    @Override
    public void update(ClientDto clientDto, int id) {
        Client oldClient = clientRepository.findById(Client.class, id);
        Client client = modelMapper.map(clientDto, Client.class);
        String uniquePhone = clientRepository.findByPhone(clientDto.getPhone(), false);
        String uniqueEmail = userRepository.findByEmail(clientDto.getEmail());
        client.setId(id);
        if (!oldClient.getPhone().equals(clientDto.getPhone())) {
            client.setPhone(clientDto.getPhone(), uniquePhone);
        }
        client.setUser(oldClient.getUser());
        if (!oldClient.getUser().getEmail().equals(clientDto.getEmail())) {
            client.getUser().setEmail(clientDto.getEmail(), uniqueEmail);
        }
        clientRepository.update(client);
    }

    @Override
    public void markDeleteById(int id) {
        Client client = clientRepository.findById(Client.class, id);
        client.setIsDeleted(true);
        clientRepository.update(client);
    }
}
