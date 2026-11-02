package com.marathon.controller;

import com.marathon.model.Participant;
import com.marathon.model.RaceCategory;
import com.marathon.model.RegistrationStatus;
import com.marathon.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    @org.junit.jupiter.api.Test
    void testDeliberateDefectForQualityGate() { org.junit.jupiter.api.Assertions.fail("DELIBERATE TEST FAILURE: CI quality gate must abort build before packaging or deployment!"); }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationService registrationService;

    @Test
    @DisplayName("GET / should render the registration landing page")
    void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("participant"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(content().string(containsString("Annual City Marathon 2026")));
    }

    @Test
    @DisplayName("POST /register with valid details should redirect to confirmation")
    void testSuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Morgan Freeman")
                        .param("email", "morgan." + System.currentTimeMillis() + "@example.com")
                        .param("age", "30")
                        .param("gender", "Male")
                        .param("category", "TEN_K")
                        .param("emergencyName", "Jane Freeman")
                        .param("emergencyPhone", "+1-555-8899")
                        .param("waiverAccepted", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/confirmation/*"));
    }

    @Test
    @DisplayName("POST /register with underage runner for Full Marathon should fail validation")
    void testUnderageRegistrationFails() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Young Runner")
                        .param("email", "young." + System.currentTimeMillis() + "@example.com")
                        .param("age", "16") // Full Marathon requires 18+
                        .param("gender", "Female")
                        .param("category", "FULL_MARATHON")
                        .param("emergencyName", "Parent")
                        .param("emergencyPhone", "+1-555-0011")
                        .param("waiverAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(content().string(containsString("Participants must be at least 18 years old for Full Marathon.")));
    }

    @Test
    @DisplayName("GET /status should return tracker page and display runner details when queried")
    void testStatusLookup() throws Exception {
        mockMvc.perform(get("/status").param("query", "REG-FM-101A"))
                .andExpect(status().isOk())
                .andExpect(view().name("status"))
                .andExpect(model().attributeExists("participant"))
                .andExpect(content().string(containsString("Alex Rivera")));
    }

    @Test
    @DisplayName("GET /admin should render administrative dashboard with participant table")
    void testAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("participants"))
                .andExpect(content().string(containsString("Participant Intake & Approval Dashboard")));
    }

    @Test
    @DisplayName("GET /health should return 200 OK with UP status")
    void testHealthProbe() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.version").value("1.0.0-mvp"))
                .andExpect(jsonPath("$.service").value("marathon-registration-portal"));
    }
}
