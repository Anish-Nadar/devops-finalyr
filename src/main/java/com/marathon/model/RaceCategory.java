package com.marathon.model;

public enum RaceCategory {
    FULL_MARATHON("Full Marathon", "42.195 km", 18, "BIB-FM-"),
    HALF_MARATHON("Half Marathon", "21.0975 km", 16, "BIB-HM-"),
    TEN_K("10K Challenge", "10 km", 14, "BIB-10K-"),
    FIVE_K("5K Fun Run", "5 km", 10, "BIB-5K-");

    private final String displayName;
    private final String distance;
    private final int minimumAge;
    private final String bibPrefix;

    RaceCategory(String displayName, String distance, int minimumAge, String bibPrefix) {
        this.displayName = displayName;
        this.distance = distance;
        this.minimumAge = minimumAge;
        this.bibPrefix = bibPrefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDistance() {
        return distance;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public String getBibPrefix() {
        return bibPrefix;
    }
}

