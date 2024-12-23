package com.example.rental.controllers;

import com.example.rental.dto.PropertyDto;
import com.example.rentalcontracts.controllers.MainController;
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
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    public MainControllerImpl(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    @GetMapping
    public String mainPage(@RequestParam(required = false) String sort,
                           @RequestParam(required = false) String city,
                           @RequestParam(required = false) String search,
                           @ModelAttribute("form") SearchForm form, Model model) {
        LOG.log(Level.INFO, "mainPage method called with parameters: sort={}, city={}, search={}, page={}, " +
                "size={}", sort, city, search, form.page(), form.size());
        int page = form.page() != null ? form.page() : 1;
        int size = form.size() != null ? form.size() : 3;
        form = new SearchForm(page, size);

        Page<PropertyDto> propertyPage = propertyService.choosingSort(city, sort, page, size, search);

        List<PropertyCardViewModel> propertyCardViewModel = propertyPage.stream()
                .map(p -> new PropertyCardViewModel(p.getPhotoUrl(), p.getId(),p.getRooms(), p.getSquare(), p.getFloor(),
                        p.getPrice(), p.getRating(), p.getDistanceToCenter(), p.getCity(), p.getStreet(), p.getHouseNumber())).toList();

        MainViewModel viewModel = new MainViewModel(createBaseViewModel("Главная", null),
                propertyCardViewModel, propertyPage.getTotalPages());

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        model.addAttribute("city", city);
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);
        return "main";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, Principal principal) {
        return new BaseViewModel(
                title,
                null,
                null
        );
    }
}
