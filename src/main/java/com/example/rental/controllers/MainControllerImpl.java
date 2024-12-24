package com.example.rental.controllers;

import com.example.rental.dto.BaseUserDto;
import com.example.rental.dto.PropertyDto;
import com.example.rental.services.AuthService;
import com.example.rentalcontracts.controllers.MainController;
import com.example.rentalcontracts.form.SortForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import com.example.rental.services.PropertyService;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.viewmodel.MainViewModel;
import com.example.rentalcontracts.viewmodel.cards.PropertyCardViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.rentalcontracts.form.SearchForm;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

@Controller
@RequestMapping("/")
public class MainControllerImpl implements MainController {
    private final PropertyService propertyService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public MainControllerImpl(PropertyService propertyService, AuthService authService) {
        this.propertyService = propertyService;
        this.authService = authService;
    }

    @Override
    @GetMapping
    public String mainPage(@RequestParam(required = false) String search,
                           @ModelAttribute("sortForm") SortForm sortForm,
                           @ModelAttribute("form") SearchForm form, Principal principal, Model model) {
        LOG.log(Level.INFO, "User " + (principal != null ? principal.getName() : "unauthorized") + " called mainPage method with parameters: sort={}, city={}, search={}, page={}, " +
                "size={}", sortForm.sort(), sortForm.city(), search, form.page(), form.size());

        int page = form.page() != null ? form.page() : 1;
        int size = form.size() != null ? form.size() : 3;
        form = new SearchForm(page, size);

        Page<PropertyDto> propertyPage = propertyService.choosingSort(sortForm.city(), sortForm.sort(), page, size, search);

        List<PropertyCardViewModel> propertyCardViewModel = propertyPage.stream()
                .map(p -> new PropertyCardViewModel(p.getPhotoUrl(), p.getId(),p.getRooms(), p.getSquare(), p.getFloor(),
                        p.getPrice(), p.getRating(), p.getDistanceToCenter(), p.getCity(), p.getStreet(), p.getHouseNumber())).toList();

        MainViewModel viewModel = new MainViewModel(createBaseViewModel("Главная", principal),
                propertyCardViewModel, propertyPage.getTotalPages());

        model.addAttribute("model", viewModel);
        model.addAttribute("sortForm", sortForm);
        model.addAttribute("form", form);
        model.addAttribute("search", search);
        return "main";
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
