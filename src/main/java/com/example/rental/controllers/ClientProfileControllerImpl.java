package com.example.rental.controllers;

import com.example.rental.dto.ClientDto;
import com.example.rental.dto.PropertyDto;
import com.example.rental.dto.AgreementDto;
import com.example.rental.services.AgreementService;
import com.example.rental.services.AuthService;
import com.example.rental.services.PropertyService;
import com.example.rentalcontracts.controllers.ClientProfileController;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.viewmodel.ClientProfileViewModel;
import com.example.rentalcontracts.viewmodel.cards.PropertyHistoryCardViewModel;
import com.example.rentalcontracts.viewmodel.cards.PropertyNowCardViewModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientProfileControllerImpl implements ClientProfileController {
    private final PropertyService propertyService;
    private final AgreementService agreementService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public ClientProfileControllerImpl(PropertyService propertyService, AgreementService agreementService, AuthService authService) {
        this.propertyService = propertyService;
        this.agreementService = agreementService;
        this.authService = authService;
    }

    @Override
    @GetMapping("/profile")
    public String clientProfile(Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the clientProfile method");
        ClientDto cl = authService.getClient(username);

        List<PropertyDto> nowProperty = propertyService.findPropertyByCurrentAgreementAndClientId(cl.getId());
        List<PropertyDto> historyProperty = propertyService.findPropertyByCompletedAgreementAndClientId(cl.getId());
        List<AgreementDto> historyAgreement = agreementService.findCompletedAgreementByClientId(cl.getId());

        List<PropertyNowCardViewModel> nowPropertyList = nowProperty.stream()
                .map(n -> new PropertyNowCardViewModel(n.getId(), n.getCity(), n.getStreet(), n.getHouseNumber())).toList();

        List<PropertyHistoryCardViewModel> historyPropertyList = new ArrayList<>();
        int i = 0;
        for (PropertyDto h : historyProperty) {
            AgreementDto agreementDto = historyAgreement.get(i);
            historyPropertyList.add(new PropertyHistoryCardViewModel(h.getId(), h.getCity(), h.getStreet(), h.getHouseNumber(),
                    agreementDto.getId(), agreementDto.getReviewId()));
            i++;
        }

        ClientProfileViewModel viewModel = new ClientProfileViewModel(createBaseViewModel("Профиль", principal),
                cl.getFirstName(),
                cl.getLastName(),
                cl.getMiddleName(),
                cl.getPhone(),
                username,
                nowPropertyList,
                historyPropertyList
        );

        model.addAttribute("model", viewModel);
        return "clientProfile";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, Principal principal) {
        ClientDto clientDto = authService.getClient(principal.getName());
        return new BaseViewModel(
                title,
                clientDto.getFirstName(),
                clientDto.getPhotoUrl()
        );
    }
}
