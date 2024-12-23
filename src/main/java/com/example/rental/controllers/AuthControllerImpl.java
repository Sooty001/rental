package com.example.rental.controllers;

import com.example.rental.dto.UserRegistrationDto;
import com.example.rental.services.AuthService;
import com.example.rentalcontracts.controllers.AuthController;
import com.example.rentalcontracts.form.UserRegisterCreateForm;
import com.example.rentalcontracts.viewmodel.AdminEditViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @GetMapping("/login")
    public String login(Model model) {
        LOG.log(Level.INFO, "login method called");
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Login", null));
        model.addAttribute("model", viewModel);
        return "login";
    }

    @Override
    @GetMapping("/register")
    public String register(Model model) {
        LOG.log(Level.INFO, "register method called");
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Registration", null));
        model.addAttribute("model", viewModel);
        model.addAttribute("userRegisterCreateForm", new UserRegisterCreateForm("", "" ,"", "", "", "", "", "", ""));
        return "register";
    }

    @Override
    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("userRegisterCreateForm") UserRegisterCreateForm userRegisterCreateForm,
                             BindingResult bindingResult,
                             Model model) {
        LOG.log(Level.INFO, "register method called with POST");
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRegisterCreateForm", userRegisterCreateForm);
            model.addAttribute("model", new AdminEditViewModel(createBaseViewModel("Registration", null)));
            return "register";
        }

        authService.register(convertToUserRegistrationDto(userRegisterCreateForm));

        return "redirect:/users/login";
    }

    @Override
    @PostMapping("/login-error")
    public String onFailedLogin(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
            RedirectAttributes redirectAttributes) {
        LOG.log(Level.INFO, "login-error method called with POST");

        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        redirectAttributes.addFlashAttribute("badCredentials", true);

        return "redirect:/users/login";
    }

    private UserRegistrationDto convertToUserRegistrationDto(UserRegisterCreateForm form) {
        return new UserRegistrationDto(form.email(), form.password(), form.confirmPassword(), form.firstName(),
                form.lastName(), form.middleName(), form.phone(), form.photoUrl(), form.role());
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
