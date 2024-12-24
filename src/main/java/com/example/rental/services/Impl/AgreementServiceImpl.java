package com.example.rental.services.Impl;

import com.example.rental.dto.AgreementsStatDto;
import com.example.rental.exceptions.AccessDeniedException;
import com.example.rental.exceptions.AgreementNotFoundException;
import com.example.rental.models.Property;
import com.example.rental.repositories.PropertyRepository;
import com.example.rental.dto.AgreementDto;
import com.example.rental.models.Agreement;
import com.example.rental.repositories.AgreementRepository;
import com.example.rental.services.AgreementService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableCaching
public class AgreementServiceImpl implements AgreementService {
    private final AgreementRepository agreementRepository;
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AgreementServiceImpl(AgreementRepository agreementRepository, PropertyRepository propertyRepository, ModelMapper modelMapper) {
        this.agreementRepository = agreementRepository;
        this.propertyRepository = propertyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void validateAgreementOwnership(int agreementId, int clientId) {
        Agreement agreement = agreementRepository.findById(Agreement.class, agreementId);
        if (agreement.getClient().getId() != clientId) { throw new AccessDeniedException(); }
    }

    @Override
    public List<AgreementDto> findCompletedAgreementByClientId(int clientId) {
        List<Agreement> agreements = agreementRepository.findCompletedAgreementByClientId(clientId, LocalDate.now(), false);
        return agreements.stream().map(agreement -> modelMapper.map(agreement, AgreementDto.class)).toList();
    }

    @Override
    @Cacheable(value = "agreementsWithReviews")
    public AgreementsStatDto countAgreementsWithReviewsAndAllByAgentId(int agentId) {
        int countReviews = 0;
        int countAllAgreement = 0;
        List<Property> listProperty = propertyRepository.findAllPropertyByAgentId(agentId, false);

        for (Property pr : listProperty) {
            countReviews += agreementRepository.countAgreementsWithReviewByPropertyId(pr.getId(), false);
            countAllAgreement += agreementRepository.countAgreementsByPropertyId(pr.getId(), false);
        }

        return new AgreementsStatDto(countReviews, countAllAgreement);
    }

    @Override
    public Page<AgreementDto> findAll(int page, int size) {
        Page<Agreement> agreements = agreementRepository.findAll(Agreement.class, page, size);

        List<AgreementDto> agreementDto = agreements.getContent().stream()
                .map(agreement -> modelMapper.map(agreement, AgreementDto.class))
                .toList();

        return new PageImpl<>(agreementDto, agreements.getPageable(), agreements.getTotalElements());
    }

    @Override
    public AgreementDto findById(int id) {
        Agreement agreement = agreementRepository.findById(Agreement.class, id);
        if (agreement == null) {
            throw new AgreementNotFoundException("Agreement with id:" + id + " not found");
        }
        return modelMapper.map(agreement, AgreementDto.class);
    }

    @Override
    @CacheEvict(cacheNames = "agreementsWithReviews", allEntries = true)
    public void create(AgreementDto agreementDto) {
        Agreement agreement = modelMapper.map(agreementDto, Agreement.class);
        agreement.setReview(null);
        agreement.setEndDate(agreementDto.getEndDate(), agreementDto.getStartDate());
        agreementRepository.create(agreement);
    }

    @Override
    @CacheEvict(cacheNames = "agreementsWithReviews", allEntries = true)
    public void update(AgreementDto agreementDto, int id) {
        Agreement oldAgreement = agreementRepository.findById(Agreement.class, id);
        Agreement ag = modelMapper.map(agreementDto, Agreement.class);
        ag.setReview(oldAgreement.getReview());
        ag.setId(id);
        agreementRepository.update(ag);
    }

    @Override
    @CacheEvict(cacheNames = "agreementsWithReviews", allEntries = true)
    public void markDeleteById(int id) {
        Agreement agreement = agreementRepository.findById(Agreement.class, id);
        agreement.setIsDeleted(true);
        agreementRepository.update(agreement);
    }
}
