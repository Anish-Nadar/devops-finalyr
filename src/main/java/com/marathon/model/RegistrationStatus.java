package com.marathon.model;

public enum RegistrationStatus {
    PENDING_APPROVAL("Pending Approval", "warning"),
    APPROVED("Approved", "success"),
    REJECTED("Rejected", "danger");

    private final String label;
    private final String badgeColor;

    RegistrationStatus(String label, String badgeColor) {
        this.label = label;
        this.badgeColor = badgeColor;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeColor() {
        return badgeColor;
    }
}

