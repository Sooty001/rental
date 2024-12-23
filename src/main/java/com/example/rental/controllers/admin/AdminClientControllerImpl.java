package com.example.rental.controllers.admin;

import com.example.rental.dto.BaseUserDto;
import com.example.rental.dto.ClientCreateDto;
import com.example.rental.dto.ClientDto;
import com.example.rental.services.AuthService;
import com.example.rental.services.ClientService;
import com.example.rentalcontracts.controllers.admin.AdminClientController;
import com.example.rentalcontracts.form.SearchForm;
import com.example.rentalcontracts.viewmodel.AdminEditViewModel;
import com.example.rentalcontracts.viewmodel.AdminViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.form.admin.client.ClientCreateForm;
import com.example.rentalcontracts.form.admin.client.ClientEditForm;
import com.example.rentalcontracts.viewmodel.admin.ClientView;
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
@RequestMapping("/admin/client")
public class AdminClientControllerImpl implements AdminClientController {
    private final ClientService clientService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public AdminClientControllerImpl(ClientService clientService, AuthService autoService) {
        this.clientService = clientService;
        this.authService = autoService;
    }

    @Override
    @GetMapping()
    public String adminPanelClient(@ModelAttribute("filter") SearchForm filter, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminPanelClient method with parameters filter={}", filter);
        int page = filter.page() != null ? filter.page() : 1;
        int size = filter.size() != null ? filter.size() : 10;
        filter = new SearchForm(page, size);
        Page<ClientDto> clientPage = clientService.findAll(page, size);

        List<ClientView> rows = clientPage.stream()
                .map(c -> new ClientView(c.getId(), c.getFirstName(), c.getLastName(), c.getMiddleName(), c.getPhone(),
                        c.getEmail(), c.getPhotoUrl())).toList();

        AdminViewModel<ClientView> viewModel = new AdminViewModel<>(createBaseViewModel("Admin", principal), clientPage.getTotalPages(), rows);

        model.addAttribute("model", viewModel);
        model.addAttribute("filter", filter);
        return "admin/client/adminClient";
    }

    @Override
    @GetMapping("/edit/{id}")
    public String adminEditClient(@PathVariable int id, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditClient method with clientId: {}", id);
        ClientDto client = clientService.findById(id);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
        model.addAttribute("model", viewModel);
        model.addAttribute("form", new ClientEditForm(client.getId(), client.getFirstName(), client.getLastName(),
                client.getMiddleName() , client.getPhone() , client.getEmail(), client.getPhotoUrl()));
        return "admin/client/adminClientEdit";
    }

    @Override
    @PostMapping("/edit/{id}")
    public String adminEditClientPost(@PathVariable int id, @Valid @ModelAttribute("form") ClientEditForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditClientPost method with clientId: {}", id);
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/client/adminClientEdit";
        }

        clientService.update(convertToClientDto(form), id);
        return "redirect:/admin/client";
    }

    @Override
    @GetMapping("/create")
    public String adminCreateClient(Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateClient method");
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
        model.addAttribute("model", viewModel);
        model.addAttribute("form", new ClientCreateForm("", "", "", "",
                "", "", ""));
        return "admin/client/adminClientCreate";
    }

    @Override
    @PostMapping("/create")
    public String adminCreateClientPost(@Valid @ModelAttribute("form") ClientCreateForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateClientPost method");
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/client/adminClientCreate";
        }

        clientService.create(convertToClientDto(form));
        return "redirect:/admin/client";
    }

    @Override
    @GetMapping("/delete/{id}")
    public String adminDeleteClient(@PathVariable int id) {
        LOG.log(Level.INFO, "Admin accessed the adminDeleteClient method with clientId: {}", id);
        clientService.markDeleteById(id);
        return "redirect:/admin/client";
    }

    private ClientCreateDto convertToClientDto(ClientCreateForm form) {
        return new ClientCreateDto(form.firstName(), form.lastName(), form.middleName(),
                form.phone(), form.email(), form.password(), form.photoUrl());
    }

    private ClientDto convertToClientDto(ClientEditForm form) {
        return new ClientDto(form.firstName(), form.lastName(), form.middleName(),
                form.phone(), form.email(), form.photoUrl());
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
