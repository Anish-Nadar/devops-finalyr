package com.marathon.controller;

import com.marathon.model.Participant;
import com.marathon.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class StatusController {

    private final RegistrationService registrationService;

    public StatusController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/status")
    public String showStatusPage(
            @RequestParam(value = "query", required = false) String query,
            Model model) {

        if (query != null && !query.trim().isEmpty()) {
            Optional<Participant> found = registrationService.search(query.trim());
            model.addAttribute("query", query.trim());
            if (found.isPresent()) {
                model.addAttribute("participant", found.get());
            } else {
                model.addAttribute("notFound", true);
            }
        }

        return "status";
    }
}

