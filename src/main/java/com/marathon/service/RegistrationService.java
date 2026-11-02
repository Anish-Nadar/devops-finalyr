package com.marathon.service;

import com.marathon.model.Participant;
import com.marathon.model.RaceCategory;
import com.marathon.model.RegistrationStatus;
import com.marathon.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final ParticipantRepository repository;
    private final BibGeneratorService bibGeneratorService;

    public RegistrationService(ParticipantRepository repository, BibGeneratorService bibGeneratorService) {
        this.repository = repository;
        this.bibGeneratorService = bibGeneratorService;
    }

    public Participant register(Participant participant) {
        // Enforce category minimum age constraint
        if (participant.getAge() < participant.getCategory().getMinimumAge()) {
            throw new IllegalArgumentException(String.format(
                    "Participants must be at least %d years old for %s.",
                    participant.getCategory().getMinimumAge(),
                    participant.getCategory().getDisplayName()
            ));
        }

        // Check waiver
        if (!participant.isWaiverAccepted()) {
            throw new IllegalArgumentException("Medical and liability waiver must be accepted to register.");
        }

        // Check for duplicate active registration with the same email
        Optional<Participant> existing = repository.findByEmail(participant.getEmail());
        if (existing.isPresent() && existing.get().getStatus() != RegistrationStatus.REJECTED) {
            throw new IllegalArgumentException("A registration with email " + participant.getEmail() + " already exists.");
        }

        // Generate unique internal ID and public Registration ID
        if (participant.getId() == null || participant.getId().isBlank()) {
            participant.setId(UUID.randomUUID().toString());
        }

        if (participant.getRegistrationId() == null || participant.getRegistrationId().isBlank()) {
            String shortHex = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            String prefix = switch (participant.getCategory()) {
                case FULL_MARATHON -> "REG-FM-";
                case HALF_MARATHON -> "REG-HM-";
                case TEN_K -> "REG-10K-";
                case FIVE_K -> "REG-5K-";
            };
            participant.setRegistrationId(prefix + shortHex);
        }

        participant.setStatus(RegistrationStatus.PENDING_APPROVAL);
        return repository.save(participant);
    }

    public Participant approveParticipant(String id) {
        Participant participant = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with ID: " + id));

        if (participant.getStatus() == RegistrationStatus.APPROVED && participant.getBibNumber() != null) {
            return participant;
        }

        String bibNumber = bibGeneratorService.generateBibNumber(participant.getCategory());
        participant.setBibNumber(bibNumber);
        participant.setStatus(RegistrationStatus.APPROVED);
        participant.setUpdatedAt(LocalDateTime.now());
        return repository.save(participant);
    }

    public Participant rejectParticipant(String id) {
        Participant participant = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with ID: " + id));

        participant.setStatus(RegistrationStatus.REJECTED);
        participant.setUpdatedAt(LocalDateTime.now());
        return repository.save(participant);
    }

    public Optional<Participant> findById(String id) {
        return repository.findById(id);
    }

    public Optional<Participant> findByRegistrationId(String regId) {
        return repository.findByRegistrationId(regId);
    }

    public Optional<Participant> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Participant> search(String query) {
        if (query == null || query.isBlank()) return Optional.empty();
        String trimmed = query.trim();
        Optional<Participant> byRegId = repository.findByRegistrationId(trimmed);
        if (byRegId.isPresent()) {
            return byRegId;
        }
        return repository.findByEmail(trimmed);
    }

    public List<Participant> getAllParticipants() {
        return repository.findAll();
    }

    public List<Participant> filterParticipants(RaceCategory category, RegistrationStatus status) {
        return repository.findAll().stream()
                .filter(p -> category == null || p.getCategory() == category)
                .filter(p -> status == null || p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Map<RaceCategory, Long> getCategoryCounts() {
        Map<RaceCategory, Long> counts = new HashMap<>();
        for (RaceCategory cat : RaceCategory.values()) {
            counts.put(cat, repository.countByCategory(cat));
        }
        return counts;
    }

    public long getTotalCount() {
        return repository.count();
    }
}
