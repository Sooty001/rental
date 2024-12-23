package com.example.rental.repositories;

import com.example.rental.models.Agreement;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface AgreementRepository {
    List<Agreement> findAgreementsByReviewNotNullAndPropertyId(int propertyId, boolean isDeleted);
    int countAgreementsByPropertyId(int id, boolean isDeleted);
    int countAgreementsWithReviewByPropertyId(int id, boolean isDeleted);
    List<Agreement> findCurrentAgreementByClientId(int ClientId, LocalDate date, boolean isDeleted);
    List<Agreement> findCompletedAgreementByClientId(int ClientId, LocalDate date, boolean isDeleted);

    Page<Agreement> findAll(Class<Agreement> entityClass, int page, int size);
    Agreement findById(Class<Agreement> entityClass, int id);
    void create(Agreement entity);
    void update(Agreement entity);
    List<Agreement> getAll(Class<Agreement> entityClass);
}