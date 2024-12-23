package com.example.rental.controllers;

import com.example.rental.dto.AgentDto;
import com.example.rental.dto.BaseUserDto;
import com.example.rental.services.AgentService;
import com.example.rental.services.AgreementService;
import com.example.rental.services.AuthService;
import com.example.rental.services.PropertyService;
import com.example.rentalcontracts.controllers.AgentTopController;
import com.example.rentalcontracts.viewmodel.AgentViewModel;
import com.example.rentalcontracts.viewmodel.BaseViewModel;
import com.example.rentalcontracts.viewmodel.cards.AgentCardViewModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/agents")
public class AgentTopControllerImpl implements AgentTopController {
    private final AgentService agentService;
    private final AgreementService agreementService;
    private final PropertyService propertyService;
    private final AuthService authService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    public AgentTopControllerImpl(AgentService agentService, AgreementService agreementService, PropertyService propertyService, AuthService authService) {
        this.agentService = agentService;
        this.agreementService = agreementService;
        this.propertyService = propertyService;
        this.authService = authService;
    }

    @Override
    @GetMapping("/top")
    public String listAgent(@RequestParam(required = false) String sort, Principal principal, Model model) {
        String username = principal.getName();
        LOG.log(Level.INFO, "User with email: " + username + " accessed the agentsTop method with parameters: sort={}", sort);

        sort = sort != null ?  sort : "";

        List<AgentDto> agentDto = agentService.choosingSort(sort);

        List<AgentCardViewModel> agentCardViewModel = agentDto.stream().map(ag -> {
            Map<Integer, Integer> agreementsMap = agreementService.countAgreementsWithReviewsAndAllByAgentId(ag.getId());
            int agreementsWithReviews = agreementsMap.keySet().iterator().next();
            int allAgreements = agreementsMap.get(agreementsWithReviews);

            return new AgentCardViewModel(
                    ag.getPhotoUrl(),
                    ag.getFirstName(),
                    ag.getMiddleName(),
                    ag.getRating(),
                    agreementsWithReviews,
                    allAgreements,
                    propertyService.countAvailablePropertyByAgentId(ag.getId())
            );
        }).toList();

        AgentViewModel viewModel = new AgentViewModel(createBaseViewModel("Агенты", principal),
                agentCardViewModel);


        model.addAttribute("model", viewModel);
        return "topAgent";
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
