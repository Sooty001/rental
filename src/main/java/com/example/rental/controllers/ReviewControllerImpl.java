package com.example.rental.controllers;

import com.example.rental.dto.*;
import com.example.rental.services.*;
import com.example.rentalcontracts.controllers.ReviewController;
import com.example.rentalcontracts.form.admin.review.ReviewCreateForm;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.viewmodel.ReviewViewModel;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/review")
public class ReviewControllerImpl implements ReviewController {
    private final ReviewService reviewService;
    private final PropertyService propertyService;
    private final AgentService agentService;
    private final AgreementService agreementService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    public ReviewControllerImpl(ReviewService reviewService, PropertyService propertyService, AgentService agentService, AgreementService agreementService, AuthService authService) {
        this.reviewService = reviewService;
        this.propertyService = propertyService;
        this.agentService = agentService;
        this.agreementService = agreementService;
        this.authService = authService;
    }

    @Override
    @GetMapping("/{agreementId}")
    public String reviewPage(@PathVariable int agreementId, Principal principal, Model model) {
        String email = principal.getName();
        LOG.log(Level.INFO, "User with email: " + email + " accessed the reviewPage method" +
                "with agreementId: " + agreementId);
        agreementService.validateAgreementOwnership(agreementId, authService.getClient(email).getId());
        reviewService.existsReviewByAgreementId(agreementId);

        model.addAttribute("model", createReviewViewModel(agreementId, principal));
        model.addAttribute("form", new ReviewCreateForm("", 5, 5, agreementId));
        return "review";
    }

    @Override
    @PostMapping("/create/{agreementId}")
    public String createReview(@PathVariable int agreementId,
                         @Valid @ModelAttribute("form") ReviewCreateForm form, BindingResult bindingResul, Principal principal, Model model) {
        String email = principal.getName();
        LOG.log(Level.INFO, "User with email: " + email + " accessed the POST method createReview with agreementId: " + agreementId);
        AgreementDto agreementDto = agreementService.findById(agreementId);
        if (bindingResul.hasErrors()) {
            model.addAttribute("model", createReviewViewModel(agreementId, principal));
            model.addAttribute("form", form);
            return "review";
        }

        ReviewDto review = new ReviewDto();
        review.setComment(form.comment());
        review.setAgentRating(form.agentRating());
        review.setPropertyRating(form.propertyRating());
        review.setAgreementId(agreementId);

        reviewService.create(review);
        return "redirect:/property/" + agreementDto.getPropertyId();
    }

    private ReviewViewModel createReviewViewModel(int agreementId, Principal principal) {
        AgreementDto agr = agreementService.findById(agreementId);
        PropertyDto pr = propertyService.findById(agr.getPropertyId());
        AgentDto ag = agentService.findById(pr.getAgentId());

        return new ReviewViewModel(createBaseViewModel("Отзыв", principal),
                pr.getCity(), pr.getStreet(), pr.getHouseNumber(), pr.getPhotoUrl(), ag.getFirstName(), agreementId);
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, Principal principal) {
        BaseUserDto baseUserDto = authService.getUser(principal.getName());
        return new BaseViewModel(
                title,
                baseUserDto.getFirstName(),
                baseUserDto.getPhotoUrl()
        );
    }
}
