package com.marathon.controller;

import com.marathon.model.Participant;
import com.marathon.model.RaceCategory;
import com.marathon.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("participant")) {
            model.addAttribute("participant", new Participant());
        }
        model.addAttribute("categories", RaceCategory.values());
        model.addAttribute("categoryCounts", registrationService.getCategoryCounts());
        return "index";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("participant") Participant participant,
            BindingResult bindingResult,
            Model model) {

        // Check Bean Validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", RaceCategory.values());
            model.addAttribute("categoryCounts", registrationService.getCategoryCounts());
            return "index";
        }

        try {
            Participant saved = registrationService.register(participant);
            return "redirect:/confirmation/" + saved.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", RaceCategory.values());
            model.addAttribute("categoryCounts", registrationService.getCategoryCounts());
            return "index";
        }
    }

    @GetMapping("/confirmation/{id}")
    public String showConfirmation(@PathVariable("id") String id, Model model) {
        Participant participant = registrationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found for ID: " + id));
        model.addAttribute("participant", participant);
        return "confirmation";
    }
}
