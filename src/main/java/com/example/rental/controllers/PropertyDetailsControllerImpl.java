package com.example.rental.controllers;

import com.example.rental.dto.*;
import com.example.rental.services.*;
import com.example.rentalcontracts.controllers.PropertyDetailsController;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.viewmodel.PropertyDetailsViewModel;
import com.example.rentalcontracts.viewmodel.cards.ReviewCardViewModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/property")
public class PropertyDetailsControllerImpl implements PropertyDetailsController {
    private final PropertyService propertyService;
    private final AgentService agentService;
    private final ReviewService reviewService;
    private final ClientService clientService;
    private final AgreementService agreementService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public PropertyDetailsControllerImpl(PropertyService propertyService, AgentService agentService, ReviewService reviewService, ClientService clientService, AgreementService agreementService, AuthService authService) {
        this.propertyService = propertyService;
        this.agentService = agentService;
        this.reviewService = reviewService;
        this.clientService = clientService;
        this.agreementService = agreementService;
        this.authService = authService;
    }

    @Override
    @GetMapping("/{propertyId}")
    public String propertyDetails(@PathVariable int propertyId, Principal principal, Model model) {
        if (principal != null) {
            LOG.log(Level.INFO, "User with email: " + principal.getName() + " accessed the propertyDetails method with propertyId: " + propertyId);
        } else {
            LOG.log(Level.INFO, "User unauthorized accessed the propertyDetails method with propertyId: " + propertyId);
        }
        PropertyDto pr = propertyService.findById(propertyId);
        AgentDto ag = agentService.findById(pr.getAgentId());
        List<ReviewDto> reviews = reviewService.findReviewByPropertyId(propertyId);
        List<ReviewCardViewModel> reviewList = new ArrayList<>();

        for (ReviewDto re : reviews) {
             AgreementDto agreement = agreementService.findById(re.getAgreementId());
             ClientDto c = clientService.findById(agreement.getClientId());

            reviewList.add(new ReviewCardViewModel(c.getFirstName(), re.getComment(), re.getPropertyRating()));
        }

        PropertyDetailsViewModel viewModel = new PropertyDetailsViewModel(createBaseViewModel("Детали", principal),
                pr.getPhotoUrl(), ag.getPhotoUrl(), ag.getFirstName(), ag.getPhone(), pr.getRooms(), pr.getSquare(), pr.getFloor(), pr.getPrice(),
                pr.getRating(), pr.getDistanceToCenter(), pr.getCity(), pr.getStreet(), pr.getHouseNumber(), reviewList);

        model.addAttribute("model", viewModel);
        return "propertyDetails";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            BaseUserDto user = authService.getUser(username);
            return new BaseViewModel(title, user.getFirstName(), user.getPhotoUrl());
        }
        return new BaseViewModel(title, null, null);
    }
}
