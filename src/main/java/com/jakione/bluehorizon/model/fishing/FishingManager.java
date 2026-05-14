package com.jakione.bluehorizon.model.fishing;

import com.jakione.bluehorizon.model.fish.Fish;
import com.jakione.bluehorizon.model.fish.FishSpecies;
import com.jakione.bluehorizon.model.Weather;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class FishingManager {
    private final Random random;
    private static final double BASE_BITE_CHANCE = 0.30; // 30% di probabilità base che qualcosa abbocchi

    public FishingManager() {
        this.random = new Random();
    }

    /**
     * Metodo core chiamato dal GameEngine quando il giocatore preme il tasto per pescare.
     *
     * @param gear L'equipaggiamento attualmente in uso (decorato o meno)
     * @param weather Il meteo attuale dal GameModel
     * @return L'istanza del pesce pescato, oppure null se non ha abboccato nulla.
     */
    public Optional<Fish> attemptCatch(FishingGear gear, Weather weather) {

        // FASE 1: Calcolo del Morso
        if (!doesFishBite(gear, weather)) {
            return Optional.empty();
        }

        // FASE 2: Estrazione della Specie (Loot Table)
        List<FishSpecies> availableSpecies = gear.getTargetableSpecies();

        // Un piccolo controllo di sicurezza extra
        if (availableSpecies == null || availableSpecies.isEmpty()) {
            return Optional.empty();
        }

        FishSpecies caughtSpecies = rollLootTable(availableSpecies);

        // Impacchettiamo il pesce generato dentro un Optional
        return Optional.of(caughtSpecies.generateCatch());
    }

    private boolean doesFishBite(FishingGear gear, Weather weather) {
        double finalChance = BASE_BITE_CHANCE * gear.getCatchMultiplier();

        // Esempio di interazione con l'ambiente: se piove, bonus del 20%
        if (weather != null && weather.name().equals("RAINY")) {
            finalChance *= 1.20;
        }

        // Limite massimo di probabilità per evitare il 100% garantito (cap al 95%)
        finalChance = Math.min(finalChance, 0.95);

        // Lancio del dado (da 0.0 a 1.0)
        return random.nextDouble() < finalChance;
    }

    private FishSpecies rollLootTable(List<FishSpecies> speciesList) {
        double totalWeight = 0.0;

        // 1. Calcoliamo la somma totale dei ratei di cattura
        for (FishSpecies species : speciesList) {
            totalWeight += species.getCatchRate();
        }

        // 2. Lanciamo il dado pesato
        double roll = random.nextDouble() * totalWeight;

        // 3. Troviamo il vincitore
        for (FishSpecies species : speciesList) {
            roll -= species.getCatchRate();
            if (roll <= 0.0) {
                return species;
            }
        }

        // Fallback di sicurezza (non dovrebbe mai essere raggiunto per via della matematica sui double)
        return speciesList.get(speciesList.size() - 1);
    }
}
