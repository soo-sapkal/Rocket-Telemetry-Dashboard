package com.rocketdashboard.model;

public enum Stage {
    // Point events (single moments)
    LAUNCH("Launch"),
    APOGEE("Apogee"),
    PARACHUTE_DEPLOYED("Parachute Deployed"),
    LANDING("Landing"),

    // Range stages (multiple data points)
    ASCENT("Ascent"),
    DESCENT("Descent");

    private final String displayName;

    Stage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Helper method to check if this is a point event
    public boolean isPointEvent() {
        return this == LAUNCH || this == APOGEE || this == PARACHUTE_DEPLOYED || this == LANDING;
    }

    // Helper method to check if this is a range stage
    public boolean isRangeStage() {
        return this == ASCENT || this == DESCENT;
    }
}