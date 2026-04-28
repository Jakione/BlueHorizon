package com.jakione.bluehorizon.model;

/**
 * Definisce la natura logica di una casella della mappa.
 * Il GameModel lo usa per la logica (es. collisioni, zone di pesca),
 * il GameRenderer lo usa per decidere quale texture disegnare.
 */
public enum TileType {
    WATER,
    LAND,       // Per isole future
    OBSTACLE    // Per scogli o moli
}