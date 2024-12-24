package com.example.rental.services;

import com.example.rental.dto.AgreementDto;
import com.example.rental.dto.AgreementsStatDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AgreementService {
    void validateAgreementOwnership(int agreementId, int clientId);
    List<AgreementDto> findCompletedAgreementByClientId(int clientId);
    AgreementsStatDto countAgreementsWithReviewsAndAllByAgentId(int agentId);

    Page<AgreementDto> findAll(int page, int size);
    AgreementDto findById(int id);
    void create(AgreementDto agreementDto);
    void update(AgreementDto agreementDto, int id);
    void markDeleteById(int id);
}

