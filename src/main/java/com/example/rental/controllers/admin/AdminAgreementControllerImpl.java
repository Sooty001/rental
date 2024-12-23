package com.example.rental.controllers.admin;

import com.example.rental.dto.BaseUserDto;
import com.example.rental.services.AgreementService;
import com.example.rental.services.AuthService;
import com.example.rentalcontracts.controllers.admin.AdminAgreementController;
import com.example.rentalcontracts.form.SearchForm;
import com.example.rentalcontracts.viewmodel.AdminEditViewModel;
import com.example.rentalcontracts.viewmodel.AdminViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.viewmodel.admin.AgreementView;
import com.example.rentalcontracts.form.admin.agreement.AgreementCreateForm;
import com.example.rentalcontracts.form.admin.agreement.AgreementEditForm;
import com.example.rental.dto.AgreementDto;
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
@RequestMapping("/admin/agreement")
public class AdminAgreementControllerImpl implements AdminAgreementController {
    private final AgreementService agreementService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public AdminAgreementControllerImpl(AgreementService agreementService, AuthService authService) {
        this.agreementService = agreementService;
        this.authService = authService;
    }

    @Override
    @GetMapping()
    public String adminPanelAgreement(@ModelAttribute("filter") SearchForm filter, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminPanelAgreement method with parameters filter={}", filter);
        int page = filter.page() != null ? filter.page() : 1;
        int size = filter.size() != null ? filter.size() : 10;
        filter = new SearchForm(page, size);

        Page<AgreementDto> agreementPage = agreementService.findAll(page, size);

        List<AgreementView> rows = agreementPage.stream()
                .map(ag -> new AgreementView(ag.getId(), ag.getStartDate(), ag.getEndDate(),
                        ag.getRentAmount(), ag.getClientId(), ag.getPropertyId(), ag.getReviewId())).toList();

        AdminViewModel<AgreementView> viewModel = new AdminViewModel<>(createBaseViewModel("Admin", principal), agreementPage.getTotalPages(), rows);

        model.addAttribute("model", viewModel);
        model.addAttribute("filter", filter);
        return "admin/agreement/adminAgreement";
    }

    @Override
    @GetMapping("/edit/{id}")
    public String adminEditAgreement(@PathVariable int id, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditAgreement method with agreementId: {}", id);
        AgreementDto agreement = agreementService.findById(id);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new AgreementEditForm(agreement.getId(), agreement.getStartDate(), agreement.getEndDate(),
                agreement.getRentAmount(), agreement.getClientId(), agreement.getPropertyId()));
        return "admin/agreement/adminAgreementEdit";
    }

    @Override
    @PostMapping("/edit/{id}")
    public String adminEditAgreementPost(@PathVariable int id, @Valid @ModelAttribute("form") AgreementEditForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditAgreementPost method with agreementId: {}", id);
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/agreement/adminAgreementEdit";
        }

        agreementService.update(convertToAgreementDto(form), id);
        return "redirect:/admin/agreement";
    }

    @Override
    @GetMapping("/create")
    public String adminCreateAgreement(Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateAgreement method");
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new AgreementCreateForm(null, null, 0, 0,
                0));
        return "admin/agreement/adminAgreementCreate";
    }

    @Override
    @PostMapping("/create")
    public String adminCreateAgreementPost(@Valid @ModelAttribute("form") AgreementCreateForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminCreateAgreementPost method");
        if (bindingResult.hasErrors()) {
            AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/agreement/adminAgreementCreate";
        }

        agreementService.create(convertToAgreementDto(form));
        return "redirect:/admin/agreement";
    }

    @Override
    @GetMapping("/delete/{id}")
    public String adminDeleteAgreement(@PathVariable int id) {
        LOG.log(Level.INFO, "Admin accessed the adminDeleteAgreement method with agreementId: {}", id);
        agreementService.markDeleteById(id);
        return "redirect:/admin/agreement";
    }

    private AgreementDto convertToAgreementDto(AgreementCreateForm form) {
        return new AgreementDto(form.startDate(), form.endDate(), form.rentAmount(),
                form.clientId(), form.propertyId());
    }

    private AgreementDto convertToAgreementDto(AgreementEditForm form) {
        return new AgreementDto(form.startDate(), form.endDate(), form.rentAmount(),
                form.clientId(), form.propertyId());
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
