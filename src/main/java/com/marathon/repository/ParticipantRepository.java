package com.marathon.repository;

import com.marathon.model.Participant;
import com.marathon.model.RaceCategory;
import com.marathon.model.RegistrationStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ParticipantRepository {

    private final ConcurrentHashMap<String, Participant> store = new ConcurrentHashMap<>();

    public Participant save(Participant participant) {
        store.put(participant.getId(), participant);
        return participant;
    }

    public Optional<Participant> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Participant> findByRegistrationId(String registrationId) {
        if (registrationId == null) return Optional.empty();
        return store.values().stream()
                .filter(p -> registrationId.equalsIgnoreCase(p.getRegistrationId()))
                .findFirst();
    }

    public Optional<Participant> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return store.values().stream()
                .filter(p -> email.equalsIgnoreCase(p.getEmail()))
                .findFirst();
    }

    public List<Participant> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Participant> findByCategory(RaceCategory category) {
        return store.values().stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Participant> findByStatus(RegistrationStatus status) {
        return store.values().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public long countByCategory(RaceCategory category) {
        return store.values().stream()
                .filter(p -> p.getCategory() == category)
                .count();
    }

    public long count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
