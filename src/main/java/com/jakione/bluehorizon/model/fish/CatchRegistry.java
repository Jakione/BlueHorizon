package com.jakione.bluehorizon.model.fish;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Il registro completo del giocatore. Mappa ogni specie al suo record.
 */
public class CatchRegistry {
    private final Map<FishSpecies, CatchRecord> records;

    public CatchRegistry() {
        this.records = new EnumMap<>(FishSpecies.class);
        // Inizializza i record vuoti per tutte le specie
        for (FishSpecies species : FishSpecies.values()) {
            records.put(species, new CatchRecord());
        }
    }

    public void addCatch(FishSpecies species, double weight, double length) {
        records.get(species).registerCatch(weight, length);
    }

    // Sola lettura per la View
    public Map<FishSpecies, CatchRecord> getRecords() {
        return Collections.unmodifiableMap(records);
    }
}