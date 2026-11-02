package com.marathon.controller;

import com.marathon.model.Participant;
import com.marathon.model.RaceCategory;
import com.marathon.model.RegistrationStatus;
import com.marathon.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RegistrationService registrationService;

    public AdminController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String showAdminDashboard(
            @RequestParam(value = "category", required = false) RaceCategory category,
            @RequestParam(value = "status", required = false) RegistrationStatus status,
            Model model) {

        List<Participant> participants = registrationService.filterParticipants(category, status);

        model.addAttribute("participants", participants);
        model.addAttribute("categories", RaceCategory.values());
        model.addAttribute("statuses", RegistrationStatus.values());
        model.addAttribute("categoryCounts", registrationService.getCategoryCounts());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("totalCount", registrationService.getTotalCount());

        return "admin";
    }

    @PostMapping("/approve/{id}")
    public String approveParticipant(@PathVariable("id") String id) {
        registrationService.approveParticipant(id);
        return "redirect:/admin";
    }

    @PostMapping("/reject/{id}")
    public String rejectParticipant(@PathVariable("id") String id) {
        registrationService.rejectParticipant(id);
        return "redirect:/admin";
    }
}

