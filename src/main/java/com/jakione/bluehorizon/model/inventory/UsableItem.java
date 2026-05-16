package com.jakione.bluehorizon.model.inventory;

/**
 * Enum per gli oggetti consumabili (esche, buff, ecc.).
 */
public enum UsableItem {
    BASIC_BAIT("Esca Semplice"),
    PREMIUM_BAIT("Esca Luccicante"),
    LUCK_POTION("Pozione della Fortuna");

    private final String displayName;

    UsableItem(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}