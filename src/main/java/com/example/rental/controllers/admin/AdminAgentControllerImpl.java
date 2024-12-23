package com.example.rental.controllers.admin;

import com.example.rental.dto.AgentCreateDto;
import com.example.rental.dto.BaseUserDto;
import com.example.rental.services.AgentService;
import com.example.rental.services.AuthService;
import com.example.rentalcontracts.controllers.admin.AdminAgentController;
import com.example.rentalcontracts.form.SearchForm;
import com.example.rentalcontracts.viewmodel.AdminEditViewModel;
import com.example.rentalcontracts.viewmodel.AdminViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.form.admin.agent.AgentCreateForm;
import com.example.rentalcontracts.form.admin.agent.AgentEditForm;
import com.example.rentalcontracts.viewmodel.admin.AgentView;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.rental.dto.AgentDto;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/agent")
public class AdminAgentControllerImpl implements AdminAgentController {
    private final AgentService agentService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    public AdminAgentControllerImpl(AgentService agentService, AuthService authService) {
        this.agentService = agentService;
        this.authService = authService;
    }

    @Override
    @GetMapping()
    public String adminPanelAgent(@ModelAttribute("filter") SearchForm filter, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminPanelAgent method with parameters filter={}", filter);
        int page = filter.page() != null ? filter.page() : 1;
        int size = filter.size() != null ? filter.size() : 10;
        filter = new SearchForm(page, size);

        Page<AgentDto> agentPage = agentService.findAll(page, size);

        List<AgentView> rows = agentPage.stream()
                .map(a -> new AgentView(a.getId(), a.getFirstName(), a.getLastName(), a.getMiddleName(),
                        a.getPhone(), authService.findById(a.getUserId()).getEmail(), a.getRating(), a.getPhotoUrl())).toList();

        AdminViewModel<AgentView> viewModel = new AdminViewModel<>(createBaseViewModel("Admin", principal), agentPage.getTotalPages(), rows);


        model.addAttribute("model", viewModel);
        model.addAttribute("filter", filter);
        return "admin/agent/adminAgent";
    }

    @Override
    @GetMapping("/edit/{id}")
    public String adminEditAgent(@PathVariable int id, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditAgent method with agentId: " + id);
        AgentDto agent = agentService.findById(id);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
        model.addAttribute("model", viewModel);
        model.addAttribute("form", new AgentEditForm(agent.getId(), agent.getFirstName(), agent.getLastName(), agent.getMiddleName(),
                agent.getPhone(), agent.getPhotoUrl()));
        return "admin/agent/adminAgentEdit";
    }

    @Override
    @PostMapping("/edit/{id}")
    public String adminEditAgentPost(@PathVariable int id, @Valid @ModelAttribute("form") AgentEditForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditAgentPost method with agentId: " + id);
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/agent/adminAgentEdit";
        }

        agentService.update(convertToAgentDto(form), id);
        return "redirect:/admin/agent";
    }

    @Override
    @GetMapping("/create")
    public String adminCreateAgent(Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateAgent method");
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
        model.addAttribute("model", viewModel);
        model.addAttribute("form", new AgentCreateForm("", "", "", "+7",
                "", "", ""));
        return "admin/agent/adminAgentCreate";
    }

    @Override
    @PostMapping("/create")
    public String adminCreateAgentPost(@Valid @ModelAttribute("form") AgentCreateForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateAgentPost method");
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/agent/adminAgentCreate";
        }

        agentService.create(convertToAgentDto(form));
        return "redirect:/admin/agent";
    }

    @Override
    @GetMapping("/delete/{id}")
    public String adminDeleteAgent(@PathVariable int id) {
        LOG.log(Level.INFO, "Admin accessed the adminDeleteAgent method with agentId: " + id);
        agentService.markDeleteById(id);
        return "redirect:/admin/agent";
    }

    private AgentDto convertToAgentDto(AgentEditForm form) {
        return new AgentDto(form.firstName(), form.lastName(), form.middleName(),
                form.phone(), 0, form.photoUrl());
    }

    private AgentCreateDto convertToAgentDto(AgentCreateForm form) {
        return new AgentCreateDto(form.firstName(), form.lastName(), form.middleName(),
                form.phone(), form.email(), form.password(), form.photoUrl());
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
