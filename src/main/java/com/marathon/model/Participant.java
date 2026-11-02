package com.marathon.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class Participant {

    private String id;
    private String registrationId;
    private String bibNumber;

    @NotBlank(message = "Full Name is required")
    @Size(min = 2, max = 100, message = "Full Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 10, message = "Minimum age to register is 10")
    @Max(value = 99, message = "Please enter a valid age")
    private Integer age;

    @NotBlank(message = "Gender selection is required")
    private String gender;

    @NotNull(message = "Race Category selection is required")
    private RaceCategory category;

    @NotBlank(message = "Emergency Contact Name is required")
    private String emergencyName;

    @NotBlank(message = "Emergency Contact Phone is required")
    @Pattern(regexp = "^[+0-9\\s-]{8,20}$", message = "Emergency Phone must be between 8 and 20 digits/characters")
    private String emergencyPhone;

    private String medicalDeclaration;

    private boolean waiverAccepted;

    private RegistrationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Participant() {
        this.status = RegistrationStatus.PENDING_APPROVAL;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Participant(String id, String registrationId, String fullName, String email, Integer age,
                       String gender, RaceCategory category, String emergencyName, String emergencyPhone,
                       String medicalDeclaration, boolean waiverAccepted) {
        this();
        this.id = id;
        this.registrationId = registrationId;
        this.fullName = fullName;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.category = category;
        this.emergencyName = emergencyName;
        this.emergencyPhone = emergencyPhone;
        this.medicalDeclaration = medicalDeclaration;
        this.waiverAccepted = waiverAccepted;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getBibNumber() {
        return bibNumber;
    }

    public void setBibNumber(String bibNumber) {
        this.bibNumber = bibNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public RaceCategory getCategory() {
        return category;
    }

    public void setCategory(RaceCategory category) {
        this.category = category;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getMedicalDeclaration() {
        return medicalDeclaration;
    }

    public void setMedicalDeclaration(String medicalDeclaration) {
        this.medicalDeclaration = medicalDeclaration;
    }

    public boolean isWaiverAccepted() {
        return waiverAccepted;
    }

    public void setWaiverAccepted(boolean waiverAccepted) {
        this.waiverAccepted = waiverAccepted;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
