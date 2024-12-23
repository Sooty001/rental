package com.example.rental.services.Impl;

import com.example.rental.exceptions.ReviewAlreadyExistsException;
import com.example.rental.models.Agreement;
import com.example.rental.repositories.AgreementRepository;
import com.example.rental.dto.ReviewDto;
import com.example.rental.models.Review;
import com.example.rental.repositories.ReviewRepository;
import com.example.rental.services.AgentService;
import com.example.rental.services.PropertyService;
import com.example.rental.services.ReviewService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AgreementRepository agreementRepository;
    private final AgentService agentService;
    private final PropertyService propertyService;
    private final ModelMapper modelMapper;


    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, AgreementRepository agreementRepository, AgentService agentService, PropertyService propertyService, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.agreementRepository = agreementRepository;
        this.agentService = agentService;
        this.propertyService = propertyService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void existsReviewByAgreementId(int agreementId) {
        Agreement agreement = agreementRepository.findById(Agreement.class, agreementId);
        if (agreement.getReview() != null) { throw new ReviewAlreadyExistsException(); }
    }

    @Override
    public List<ReviewDto> findReviewByPropertyId(int propertyId) {
        return agreementRepository.findAgreementsByReviewNotNullAndPropertyId(propertyId, false).stream().map(a -> modelMapper.map(a.getReview(), ReviewDto.class)).toList();
    }

    @Override
    public Page<ReviewDto> findAll(int page, int size) {
        Page<Review> reviews = reviewRepository.findAll(Review.class, page, size);

        List<ReviewDto> reviewDto = reviews.getContent().stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(reviewDto, reviews.getPageable(), reviews.getTotalElements());
    }

    @Override
    public ReviewDto findById(int id) {
        return modelMapper.map(reviewRepository.findById(Review.class, id), ReviewDto.class);
    }

    @Override
    @Transactional
    public void create(ReviewDto reviewDto) {
        Review review = modelMapper.map(reviewDto, Review.class);
        Agreement agreement = agreementRepository.findById(Agreement.class, reviewDto.getAgreementId());
        review.setIsDeleted(false);
        reviewRepository.create(review);
        agentService.updateAgentRating(agreement);
        propertyService.updatePropertyRating(agreement);
    }

    @Override
    public void update(ReviewDto review, int id) {
        Review reviewById = reviewRepository.findById(Review.class, id);
        Review re = modelMapper.map(review, Review.class);
        re.setAgreement(reviewById.getAgreement());
        re.setId(id);
        reviewRepository.update(re);
    }

    @Override
    public void markDeleteById(int id) {
        Review re = reviewRepository.findById(Review.class, id);
        re.setIsDeleted(true);
        reviewRepository.update(re);
    }
}
