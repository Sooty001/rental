package com.example.rental.repositories.Impl;

import com.example.rental.models.Agreement;
import com.example.rental.repositories.AgreementRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class AgreementRepositoryImpl extends BaseRepository<Agreement> implements AgreementRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Agreement> findAgreementsByReviewNotNullAndPropertyId(int propertyId, boolean isDeleted) {
        String jpql = "SELECT a FROM Agreement a WHERE a.property.id = :propertyId AND a.review IS NOT NULL AND a.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Agreement.class).setParameter("propertyId", propertyId).setParameter("isDeleted", isDeleted).getResultList();
    }

    @Override
    public int countAgreementsByPropertyId(int propertyId, boolean isDeleted) {
        String jpql = "SELECT COUNT(a) FROM Agreement a WHERE a.property.id = :propertyId AND a.isDeleted = :isDeleted";
        Long result = entityManager.createQuery(jpql, Long.class).setParameter("propertyId", propertyId).setParameter("isDeleted", isDeleted).getSingleResult();
        return result.intValue();
    }

    @Override
    public int countAgreementsWithReviewByPropertyId(int propertyId, boolean isDeleted) {
        String jpql = "SELECT COUNT(a) FROM Agreement a WHERE a.property.id = :propertyId AND a.review IS NOT NULL AND a.isDeleted = :isDeleted";
        Long result = entityManager.createQuery(jpql, Long.class).setParameter("propertyId", propertyId).setParameter("isDeleted", isDeleted).getSingleResult();
        return result.intValue();
    }

    @Override
    public List<Agreement> findCurrentAgreementByClientId(int clientId, LocalDate nowDate, boolean isDeleted) {
        String jpql = "SELECT a FROM Agreement a WHERE a.client.id = :clientId AND a.endDate > :nowDate AND a.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Agreement.class).setParameter("clientId", clientId)
                .setParameter("nowDate", nowDate).setParameter("isDeleted", isDeleted).getResultList();
    }

    @Override
    public List<Agreement> findCompletedAgreementByClientId(int clientId, LocalDate nowDate, boolean isDeleted) {
        String jpql = "SELECT a FROM Agreement a WHERE a.client.id = :clientId AND a.endDate < :nowDate AND a.isDeleted = :isDeleted";
        return entityManager.createQuery(jpql, Agreement.class).setParameter("clientId", clientId)
                .setParameter("nowDate", nowDate).setParameter("isDeleted", isDeleted).getResultList();
    }
}
