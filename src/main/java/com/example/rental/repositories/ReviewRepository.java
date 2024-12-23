package com.example.rental.repositories;

import com.example.rental.models.Review;
import org.springframework.data.domain.Page;

public interface ReviewRepository {
    Review findReviewByAgreementId(int agreementId);

    Page<Review> findAll(Class<Review> entityClass, int page, int size);
    Review findById(Class<Review> entityClass, int id);
    void create(Review entity);
    void update(Review entity);
}
