package com.example.rental.services;


import com.example.rental.dto.PropertyDto;
import com.example.rental.models.Agreement;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PropertyService {
    void validatePropertyOwnershipByAgent(int propertyId, int agentId);
    Page<PropertyDto> choosingSort(String city, String sort, int page, int size, String search);
    Page<PropertyDto> searchProperty(String search, int page, int size);
    Page<PropertyDto> findPropertyByRating(int page, int size);
    Page<PropertyDto> findPopularProperties(int page, int size);
    Page<PropertyDto> findProfitableProperties(int page, int size);
    Page<PropertyDto> findPropertyByCity(String city, int page, int size);
    List<PropertyDto> findAllPropertyByAgentId(int agentId);
    int countAvailablePropertyByAgentId(int id);
    List<PropertyDto> findPropertyByCurrentAgreementAndClientId(int ClientId);
    List<PropertyDto> findPropertyByCompletedAgreementAndClientId(int ClientId);
    void updatePropertyRating(Agreement agreement);

    Page<PropertyDto> findAll(int page, int size);
    PropertyDto findById(int id);
    void create(PropertyDto propertyDto);
    void update(PropertyDto propertyDto, int id);
    void markDeleteById(int id);
}
