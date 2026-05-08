package com.jakione.bluehorizon.model;

import java.time.LocalTime;

public enum TimeOfDay {
    DAY("Giorno", 1.0),
    NIGHT("Notte", 1.00);

    private final String description;
    private final double modifier;

    TimeOfDay(String description, double modifier) {
        this.description = description;
        this.modifier = modifier;
    }

    public static TimeOfDay getRealTimePhase() {
        int hour = LocalTime.now().getHour();
        // Range 06:00 - 20:00
        if (hour >= 6 && hour < 20) {
            return DAY;
        } else {
            return NIGHT;
        }
    }

    public String getDescription() { return description; }
    public double getModifier() { return modifier; }
}
