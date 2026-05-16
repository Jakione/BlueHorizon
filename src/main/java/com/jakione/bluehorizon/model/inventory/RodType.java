package com.jakione.bluehorizon.model.inventory;

/**
 * Enum che fa da "etichetta" per l'inventario per le canne base.
 */
public enum RodType {
    BASIC("Canna di Legno"),
    ADVANCED("Canna in Fibra di Carbonio");

    private final String displayName;

    RodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}