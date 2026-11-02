package com.marathon.controller;

import com.marathon.model.Participant;
import com.marathon.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class ParticipantApiController {

    private final RegistrationService registrationService;
    private final LocalDateTime serverStartTime = LocalDateTime.now();

    public ParticipantApiController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, Object>> getStatusApi(@RequestParam(value = "query") String query) {
        Optional<Participant> found = registrationService.search(query);
        Map<String, Object> response = new HashMap<>();

        if (found.isPresent()) {
            Participant p = found.get();
            response.put("found", true);
            response.put("registrationId", p.getRegistrationId());
            response.put("fullName", p.getFullName());
            response.put("category", p.getCategory().getDisplayName());
            response.put("status", p.getStatus().name());
            response.put("statusLabel", p.getStatus().getLabel());
            response.put("bibNumber", p.getBibNumber());
            response.put("registeredAt", p.getCreatedAt());
            response.put("updatedAt", p.getUpdatedAt());
            return ResponseEntity.ok(response);
        } else {
            response.put("found", false);
            response.put("message", "No registration found matching query: " + query);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthProbe() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "marathon-registration-portal");
        health.put("version", "1.0.0-mvp");
        health.put("startTime", serverStartTime.toString());
        health.put("totalRegistrations", registrationService.getTotalCount());
        health.put("systemTimestamp", LocalDateTime.now().toString());
        health.put("memoryFreeBytes", Runtime.getRuntime().freeMemory());
        health.put("memoryTotalBytes", Runtime.getRuntime().totalMemory());
        return ResponseEntity.ok(health);
    }
}

