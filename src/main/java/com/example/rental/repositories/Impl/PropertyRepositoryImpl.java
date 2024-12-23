package com.example.rental.repositories.Impl;

import com.example.rental.models.Property;
import com.example.rental.repositories.PropertyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PropertyRepositoryImpl extends BaseRepository<Property> implements PropertyRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Property> searchProperty(String search, int page, int size, String propertyStatus, boolean isDeleted) {
        long total = entityManager.createQuery("SELECT COUNT(p) FROM Property p WHERE CONCAT(p.city, p.street, p.houseNumber) LIKE :search AND p.status = :propertyStatus AND p.isDeleted = :isDeleted ", Long.class)
                .setParameter("search", '%' + search + '%').setParameter("propertyStatus", propertyStatus).setParameter("isDeleted", isDeleted).getSingleResult();

        List<Property> property = entityManager.createQuery("SELECT p FROM Property p WHERE CONCAT(p.city, p.street, p.houseNumber) LIKE :search AND p.status = :propertyStatus AND p.isDeleted = :isDeleted", Property.class)
                .setParameter("search", '%' + search + '%').setParameter("propertyStatus", propertyStatus).setFirstResult((page - 1) * size).setMaxResults(size).setParameter("isDeleted", isDeleted).getResultList();

        return new PageImpl<>(property, PageRequest.of(page - 1, size), total);
    }

    @Override
    public List<Property> findAvailableProperties(String propertyStatus, boolean isDeleted) {
        String jpql = "SELECT p FROM Property p WHERE p.status = :propertyStatus AND p.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Property.class).setParameter("propertyStatus", propertyStatus).setParameter("isDeleted", isDeleted).getResultList();
    }

    @Override
    public Integer CountAvailablePropertyByAgentId(int AgentId, String propertyStatus, boolean isDeleted) {
        String jpql = "SELECT COUNT(p) FROM Property p WHERE p.status = :propertyStatus AND p.agent.id = :AgentId AND p.isDeleted = :isDeleted";
        Long result = entityManager.createQuery(jpql, Long.class).setParameter("AgentId", AgentId).setParameter("propertyStatus", propertyStatus).setParameter("isDeleted", isDeleted).getSingleResult();
        return result.intValue();
    }

    @Override
    public List<Property> findAllPropertyByAgentId(int AgentId, boolean isDeleted) {
        String jpql = "SELECT p FROM Property p WHERE p.agent.id = :AgentId AND p.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Property.class).setParameter("AgentId", AgentId).setParameter("isDeleted", isDeleted).getResultList();
    }

    @Override
    public Page<Property> findAvailablePropertyByCity(int page, int size, String city, String propertyStatus, boolean isDeleted) {
        long total = entityManager.createQuery("SELECT COUNT(p) FROM Property p WHERE p.status = :propertyStatus AND p.city = :city AND" +
                        " p.isDeleted = :isDeleted", Long.class)
                .setParameter("propertyStatus", propertyStatus).setParameter("city", city).setParameter("isDeleted", isDeleted).getSingleResult();

        List<Property> property = entityManager.createQuery("SELECT p FROM Property p WHERE p.status = :propertyStatus AND p.city = :city AND" +
                        " p.isDeleted = :isDeleted", Property.class)
                .setParameter("propertyStatus", propertyStatus).setParameter("city", city).setParameter("isDeleted", isDeleted)
                .setFirstResult((page - 1) * size).setMaxResults(size).getResultList();

        return new PageImpl<>(property, PageRequest.of(page - 1, size), total);
    }

    @Override
    public Page<Property> findPropertyByRating(int page, int size, String propertyStatus, boolean isDeleted) {
        long total = entityManager.createQuery("SELECT COUNT(p) FROM Property p WHERE p.status = :propertyStatus AND p.isDeleted = :isDeleted", Long.class)
                .setParameter("propertyStatus", propertyStatus).setParameter("isDeleted", isDeleted).getSingleResult();

        List<Property> property = entityManager.createQuery("SELECT p FROM Property p WHERE p.status = :propertyStatus AND p.isDeleted = :isDeleted " +
                        "ORDER BY p.rating DESC", Property.class)
                .setParameter("propertyStatus", propertyStatus).setParameter("isDeleted", isDeleted).setFirstResult((page - 1) * size)
                .setMaxResults(size).getResultList();

        return new PageImpl<>(property, PageRequest.of(page - 1, size), total);
    }
}
