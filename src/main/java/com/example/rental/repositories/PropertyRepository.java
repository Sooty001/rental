package com.example.rental.repositories;

import com.example.rental.models.Property;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PropertyRepository {
    Page<Property> searchProperty(String search, int page, int size, String propertyStatus, boolean isDeleted);
    List<Property> findAvailableProperties(String propertyStatus, boolean isDeleted);
    Integer CountAvailablePropertyByAgentId(int AgentId, String propertyStatus, boolean isDeleted);
    List<Property> findAllPropertyByAgentId(int AgentId, boolean isDeleted);
    Page<Property> findAvailablePropertyByCity(int page, int size, String city, String propertyStatus, boolean isDeleted);
    Page<Property> findPropertyByRating(int page, int size, String propertyStatus, boolean isDeleted);

    Page<Property> findAll(Class<Property> entityClass, int page, int size);
    Property findById(Class<Property> entityClass, int id);
    void create(Property entity);
    void update(Property entity);
}
