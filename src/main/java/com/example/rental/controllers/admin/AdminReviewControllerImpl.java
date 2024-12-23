package com.example.rental.controllers.admin;

import com.example.rental.dto.BaseUserDto;
import com.example.rental.dto.ReviewDto;
import com.example.rental.services.AuthService;
import com.example.rental.services.ReviewService;
import com.example.rentalcontracts.controllers.admin.AdminReviewController;
import com.example.rentalcontracts.form.SearchForm;
import com.example.rentalcontracts.viewmodel.AdminEditViewModel;
import com.example.rentalcontracts.viewmodel.AdminViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.form.admin.review.ReviewEditForm;
import com.example.rentalcontracts.viewmodel.admin.ReviewView;
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
@RequestMapping("/admin/review")
public class AdminReviewControllerImpl implements AdminReviewController {
    private final ReviewService reviewService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public AdminReviewControllerImpl(ReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @Override
    @GetMapping()
    public String adminPanelReview(@ModelAttribute("filter") SearchForm filter, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminPanelReview method with parameters filter={}", filter);
        int page = filter.page() != null ? filter.page() : 1;
        int size = filter.size() != null ? filter.size() : 10;
        filter = new SearchForm(page, size);

        Page<ReviewDto> reviewPage = reviewService.findAll(page, size);

        List<ReviewView> rows = reviewPage.stream()
                .map(r -> new ReviewView(r.getId(), r.getComment(), r.getAgentRating(), r.getPropertyRating(),
                        r.getAgreementId())).toList();

        AdminViewModel<ReviewView> viewModel = new AdminViewModel<>(createBaseViewModel("Admin", principal),
                reviewPage.getTotalPages(), rows);

        model.addAttribute("model", viewModel);
        model.addAttribute("filter", filter);
        return "admin/review/adminReview";
    }

    @Override
    @GetMapping("/edit/{id}")
    public String adminEditReview(@PathVariable int id, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditReview method with reviewId: {}", id);
        ReviewDto review = reviewService.findById(id);
        AdminEditViewModel viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new ReviewEditForm(review.getId(), review.getComment(),
                review.getAgentRating(), review.getPropertyRating()));
        return "admin/review/adminReviewEdit";
    }

    @Override
    @PostMapping("/edit/{id}")
    public String adminEditReviewPost(@PathVariable int id, @Valid @ModelAttribute("form") ReviewEditForm form, BindingResult bindingResult, Principal principal, Model model) {
        LOG.log(Level.INFO, "Admin accessed the adminEditReviewPost method with reviewId: {}", id);
        if (bindingResult.hasErrors()) {
            var viewModel = new AdminEditViewModel(createBaseViewModel("Admin", principal));
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "admin/review/adminReviewEdit";
        }
        ReviewDto review = new ReviewDto();
        review.setComment(form.comment());
        review.setAgentRating(form.agentRating());
        review.setPropertyRating(form.propertyRating());


        reviewService.update(review, id);
        return "redirect:/admin/review";
    }

    @Override
    @GetMapping("/delete/{id}")
    public String adminDeleteReview(@PathVariable int id) {
        LOG.log(Level.INFO, "Admin accessed the adminDeleteReview method with reviewId: {}", id);
        reviewService.markDeleteById(id);
        return "redirect:/admin/review";
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
