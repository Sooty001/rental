package com.example.rental.services;

import com.example.rental.dto.ReviewDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    void existsReviewByAgreementId(int agreementId);
    List<ReviewDto> findReviewByPropertyId(int propertyId);

    Page<ReviewDto> findAll(int page, int size);
    ReviewDto findById(int id);
    void create(ReviewDto review);
    void update(ReviewDto review, int id);
    void markDeleteById(int id);
}
