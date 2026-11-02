package com.marathon.config;

import com.marathon.model.Participant;
import com.marathon.model.RaceCategory;
import com.marathon.service.RegistrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initSampleData(RegistrationService registrationService) {
        return args -> {
            // Seed Participant 1: Full Marathon (Pending)
            Participant p1 = new Participant(
                    "p1-alex-rivera",
                    "REG-FM-101A",
                    "Alex Rivera",
                    "alex.rivera@example.com",
                    29,
                    "Male",
                    RaceCategory.FULL_MARATHON,
                    "Sarah Rivera",
                    "+1-555-0101",
                    "None",
                    true
            );
            registrationService.register(p1);

            // Seed Participant 2: Half Marathon (Approved with Bib)
            Participant p2 = new Participant(
                    "p2-marcus-chen",
                    "REG-HM-202B",
                    "Marcus Chen",
                    "marcus.chen@example.com",
                    34,
                    "Male",
                    RaceCategory.HALF_MARATHON,
                    "Emily Chen",
                    "+1-555-0202",
                    "Penicillin allergy",
                    true
            );
            registrationService.register(p2);
            registrationService.approveParticipant(p2.getId());

            // Seed Participant 3: 10K Run (Pending)
            Participant p3 = new Participant(
                    "p3-elena-silva",
                    "REG-10K-303C",
                    "Elena Silva",
                    "elena.silva@example.com",
                    26,
                    "Female",
                    RaceCategory.TEN_K,
                    "Carlos Silva",
                    "+1-555-0303",
                    "Asthma - carries inhaler",
                    true
            );
            registrationService.register(p3);
        };
    }
}
