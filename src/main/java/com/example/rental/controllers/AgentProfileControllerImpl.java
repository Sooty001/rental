package com.example.rental.controllers;

import com.example.rental.dto.AgentDto;
import com.example.rental.dto.PropertyDto;
import com.example.rental.services.AuthService;
import com.example.rental.services.PropertyService;
import com.example.rentalcontracts.controllers.AgentProfileController;
import com.example.rentalcontracts.form.AgentPropertyCreateForm;
import com.example.rentalcontracts.form.AgentPropertyEditForm;
import com.example.rentalcontracts.viewmodel.*;
import com.example.rentalcontracts.viewmodel.cards.PropertyNowCardViewModel;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/agent")
public class AgentProfileControllerImpl implements AgentProfileController {
    private final PropertyService propertyService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(AgentProfileControllerImpl.class);

    @Autowired
    public AgentProfileControllerImpl(PropertyService propertyService, AuthService authService) {
        this.propertyService = propertyService;
        this.authService = authService;
    }

    @Override
    @GetMapping("/profile")
    public String agentProfile(Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the agentProfile method");
        AgentDto ag = authService.getAgent(username);

        List<PropertyDto> propertyList = propertyService.findAllPropertyByAgentId(ag.getId());

        List<PropertyNowCardViewModel> propertyListCards = propertyList.stream()
                .map(n -> new PropertyNowCardViewModel(n.getId(), n.getCity(), n.getStreet(), n.getHouseNumber())).toList();

        AgentProfileViewModel viewModel = new AgentProfileViewModel(createBaseViewModel("Профиль", principal),
                ag.getFirstName(),
                ag.getLastName(),
                ag.getMiddleName(),
                ag.getPhone(),
                username,
                ag.getRating(),
                propertyListCards
        );

        model.addAttribute("model", viewModel);
        return "agentProfile";
    }

    @Override
    @GetMapping("/createProperty")
    public String createFormPropertyView(Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the createProperty method");
        AgentDto ag = authService.getAgent(username);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Создание", principal));

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new AgentPropertyCreateForm("", "", 0, 0,
                0, 0, 0, 0, 0, "", ""));
        return "agentPropertyCreate";
    }

    @Override
    @PostMapping("/createProperty")
    public String createFormProperty(@Valid @ModelAttribute("form") AgentPropertyCreateForm form, BindingResult bindingResult, Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the POST method createProperty");
        AgentDto ag = authService.getAgent(username);
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Создание", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "agentPropertyCreate";
        }

        propertyService.create(new PropertyDto(form.city(), form.street(), form.houseNumber(), form.floor(), form.apartmentNumber(),
                form.distanceToCityCenter(), form.square(), form.rooms(), form.price(), form.status(), 0,
                form.photoUrl(), ag.getId()));
        return "redirect:/agent/profile";
    }

    @Override
    @GetMapping("/editProperty/{propertyId}")
    public String editFormPropertyView(@PathVariable int propertyId, Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the editProperty method");
        AgentDto ag = authService.getAgent(username);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Редактирование", principal));
        propertyService.validatePropertyOwnershipByAgent(propertyId, ag.getId());
        PropertyDto pr = propertyService.findById(propertyId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new AgentPropertyEditForm(pr.getId(), pr.getCity(), pr.getStreet(), pr.getHouseNumber(),
                pr.getFloor(), pr.getApartmentNumber(), pr.getDistanceToCenter(), pr.getSquare(), pr.getRooms(), pr.getPrice(),
                pr.getStatus(), pr.getPhotoUrl()));
        return "agentPropertyEdit";
    }

    @Override
    @PostMapping("/editProperty/{propertyId}")
    public String editFormProperty(@PathVariable int propertyId, @Valid @ModelAttribute("form") AgentPropertyCreateForm form, BindingResult bindingResult, Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the POST method editProperty");
        AgentDto ag = authService.getAgent(username);
        propertyService.validatePropertyOwnershipByAgent(propertyId, ag.getId());
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Редактирование", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "agentPropertyCreate";
        }

        propertyService.update(new PropertyDto(form.city(), form.street(), form.houseNumber(), form.floor(), form.apartmentNumber(),
                form.distanceToCityCenter(), form.square(), form.rooms(), form.price(), form.status(), 0,
                form.photoUrl(), ag.getId()), propertyId);
        return "redirect:/agent/profile";
    }

    @Override
    @GetMapping("/deleteProperty/{propertyId}")
    public String deleteProperty(@PathVariable int propertyId, Principal principal) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the deleteProperty method");
        AgentDto ag = authService.getAgent(username);
        propertyService.validatePropertyOwnershipByAgent(propertyId, ag.getId());
        propertyService.markDeleteById(propertyId);
        return "redirect:/agent/profile";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, Principal principal) {
        AgentDto agentDto = authService.getAgent(principal.getName());
        return new BaseViewModel(
                title,
                agentDto.getFirstName(),
                agentDto.getPhotoUrl()
        );
    }
}
