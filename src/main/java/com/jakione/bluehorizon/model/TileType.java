package com.jakione.bluehorizon.model;

/**
 * Definisce la natura logica di una casella della mappa.
 * Il GameModel lo usa per la logica (es. collisioni, zone di pesca),
 * il GameRenderer lo usa per decidere quale texture disegnare.
 */

public enum TileType {
    WATER(0),
    LAND(1),
    ROCK(2);

    private final int id;

    TileType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Factory method per convertire l'intero della mappa nell'Enum corrispondente.
     */
    public static TileType fromId(int id) {
        for (TileType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        // Fallback di sicurezza per evitare NullPointerException
        return WATER;
    }
}