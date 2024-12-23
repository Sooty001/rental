package com.example.rental.services.Impl;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
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
        if (agreement == null) { throw new AgreementNotFoundException(); }
        if (agreement.getClient().getId() != clientId) { throw new AccessDeniedException(); }
    }

    @Override
    public List<AgreementDto> findCompletedAgreementByClientId(int clientId) {
        List<Agreement> agreements = agreementRepository.findCompletedAgreementByClientId(clientId, LocalDate.now(), false);
        return agreements.stream().map(agreement -> modelMapper.map(agreement, AgreementDto.class)).toList();
    }

    @Override
    public Map<Integer, Integer> countAgreementsWithReviewsAndAllByAgentId(int agentId) {
        int countReviews = 0;
        int countAllAgreement = 0;
        List<Property> listProperty = propertyRepository.findAllPropertyByAgentId(agentId, false);
        Map<Integer, Integer> map = new HashMap<>();

        for (Property pr : listProperty) {
            countReviews += agreementRepository.countAgreementsWithReviewByPropertyId(pr.getId(), false);
            countAllAgreement += agreementRepository.countAgreementsByPropertyId(pr.getId(), false);
        }

        map.put(countReviews, countAllAgreement);
        return map;
    }

    @Override
    public Page<AgreementDto> findAll(int page, int size) {
        Page<Agreement> agreements = agreementRepository.findAll(Agreement.class, page, size);

        List<AgreementDto> agreementDto = agreements.getContent().stream()
                .map(agreement -> modelMapper.map(agreement, AgreementDto.class))
                .collect(Collectors.toList());

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
    public void create(AgreementDto agreementDto) {
        Agreement agreement = modelMapper.map(agreementDto, Agreement.class);
        agreement.setReview(null);
        agreement.setEndDate(agreementDto.getEndDate(), agreementDto.getStartDate());
        agreementRepository.create(agreement);
    }

    @Override
    public void update(AgreementDto agreementDto, int id) {
        Agreement oldAgreement = agreementRepository.findById(Agreement.class, id);
        Agreement ag = modelMapper.map(agreementDto, Agreement.class);
        ag.setReview(oldAgreement.getReview());
        ag.setId(id);
        agreementRepository.update(ag);
    }

    @Override
    public void markDeleteById(int id) {
        Agreement agreement = agreementRepository.findById(Agreement.class, id);
        agreement.setIsDeleted(true);
        agreementRepository.update(agreement);
    }
}
