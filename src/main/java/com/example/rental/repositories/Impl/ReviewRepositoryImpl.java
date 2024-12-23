package com.example.rental.repositories.Impl;

import com.example.rental.models.Review;
import com.example.rental.repositories.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepositoryImpl extends BaseRepository<Review> implements ReviewRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Review findReviewByAgreementId(int agreementId) {
        String jpql = "SELECT r FROM Review r WHERE r.agreement.id = :agreementId";
        return entityManager.createQuery(jpql, Review.class).setParameter("agreementId", agreementId).getSingleResult();
    }
}
