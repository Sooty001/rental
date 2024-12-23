package com.example.rental.controllers.admin;

import com.example.rental.dto.BaseUserDto;
import com.example.rental.dto.PropertyDto;
import com.example.rental.services.AuthService;
import com.example.rental.services.PropertyService;
import com.example.rentalcontracts.controllers.admin.AdminPropertyController;
import com.example.rentalcontracts.form.SearchForm;
import com.example.rentalcontracts.viewmodel.AdminEditViewModel;
import com.example.rentalcontracts.viewmodel.AdminViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.form.admin.property.PropertyCreateForm;
import com.example.rentalcontracts.form.admin.property.PropertyEditForm;
import com.example.rentalcontracts.viewmodel.admin.PropertyView;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/property")
public class AdminPropertyControllerImpl implements AdminPropertyController {
    private final PropertyService propertyService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public AdminPropertyControllerImpl(PropertyService propertyService, AuthService authService) {
        this.propertyService = propertyService;
        this.authService = authService;
    }

    @Override
    @GetMapping()
    public String adminPanelProperty(@ModelAttribute("filter") SearchForm filter, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminPanelProperty method with parameters filter={}", filter);
        int page = filter.page() != null ? filter.page() : 1;
        int size = filter.size() != null ? filter.size() : 10;
        filter = new SearchForm(page, size);

        Page<PropertyDto> propertyPage = propertyService.findAll(page, size);

        List<PropertyView> rows = propertyPage.stream()
                .map(pr -> new PropertyView(pr.getId(), pr.getCity(), pr.getStreet(), pr.getHouseNumber(),
                        pr.getFloor(), pr.getApartmentNumber(), pr.getDistanceToCenter(), pr.getSquare(), pr.getRooms(), pr.getPrice(),
                        pr.getStatus(), pr.getRating(), pr.getPhotoUrl(), pr.getAgentId())).toList();

        AdminViewModel<PropertyView> viewModel = new AdminViewModel<>(createBaseViewModel("Admin", principal), propertyPage.getTotalPages(), rows);

        model.addAttribute("model", viewModel);
        model.addAttribute("filter", filter);
        return "admin/property/adminProperty";
    }

    @Override
    @GetMapping("/edit/{id}")
    public String adminEditProperty(@PathVariable int id, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditProperty method with propertyId: {}", id);
        PropertyDto pr = propertyService.findById(id);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new PropertyEditForm(pr.getId(), pr.getCity(), pr.getStreet(), pr.getHouseNumber(),
                pr.getFloor(), pr.getApartmentNumber(), pr.getDistanceToCenter(), pr.getSquare(), pr.getRooms(), pr.getPrice(),
                pr.getStatus(), pr.getPhotoUrl(), pr.getAgentId()));
        return "admin/property/adminPropertyEdit";
    }

    @Override
    @PostMapping("/edit/{id}")
    public String adminEditPropertyPost(@PathVariable int id, @Valid @ModelAttribute("form") PropertyEditForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditPropertyPost method with propertyId: {}", id);
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/property/adminPropertyEdit";
        }

        propertyService.update(convertToPropertyDto(form), id);
        return "redirect:/admin/property";
    }

    @Override
    @GetMapping("/create")
    public String adminCreateProperty(Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateProperty method");
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
        model.addAttribute("model", viewModel);
        model.addAttribute("form", new PropertyCreateForm("", "", 0, 0,
                0, 0, 0, 0, 0, "", "", 0));
        return "admin/property/adminPropertyCreate";
    }

    @Override
    @PostMapping("/create")
    public String adminCreatePropertyPost(@Valid @ModelAttribute("form") PropertyCreateForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreatePropertyPost method");
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin",principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/property/adminPropertyCreate";
        }

        propertyService.create(convertToPropertyDto(form));
        return "redirect:/admin/property";
    }

    @Override
    @GetMapping("/delete/{id}")
    public String adminDeleteProperty(@PathVariable int id) {
        LOG.log(Level.INFO, "Admin accessed the adminDeleteProperty method with propertyId: {}", id);
        propertyService.markDeleteById(id);
        return "redirect:/admin/property";
    }

    private PropertyDto convertToPropertyDto(PropertyCreateForm form) {
        return new PropertyDto(form.city(), form.street(), form.houseNumber(), form.floor(), form.apartmentNumber(),
                form.distanceToCityCenter(), form.square(), form.rooms(), form.price(), form.status(), 0,
                form.photoUrl(), form.agentId());
    }

    private PropertyDto convertToPropertyDto(PropertyEditForm form) {
        return new PropertyDto(form.city(), form.street(), form.houseNumber(), form.floor(), form.apartmentNumber(),
                form.distanceToCityCenter(), form.square(), form.rooms(), form.price(), form.status(), 0,
                form.photoUrl(), form.agentId());
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
