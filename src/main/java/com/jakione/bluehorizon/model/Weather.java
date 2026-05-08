package com.jakione.bluehorizon.model;

import java.util.Random;

/**
 * Rappresenta le condizioni meteorologiche del gioco.
 * Ogni condizione atmosferica porta con sé un modificatore base che influisce
 * sull'attività generale dei pesci e sulla difficoltà di pesca.
 */
public enum Weather {
    SUNNY("Soleggiato", 1.0, 50),    // Molto comune
    CLOUDY("Nuvoloso", 1.1, 25),    // Comune
    RAINY("Pioggia", 1.25, 20),     // Raro
    STORMY("Tempesta", 0.7, 5);     // Molto raro

    private final String description;
    private final double baseCatchModifier;
    private final int weight; // Il "peso" per la probabilità

    Weather(String description, double baseCatchModifier, int weight) {
        this.description = description;
        this.baseCatchModifier = baseCatchModifier;
        this.weight = weight;
    }

    public int getWeight() { return weight; }

    /**
     * Seleziona un meteo casuale basato sui pesi (rarità).
     */
    public static Weather getRandomWeather() {
        int totalWeight = 0;
        for (Weather w : values()) {
            totalWeight += w.getWeight();
        }

        int randomIndex = new Random().nextInt(totalWeight);
        int currentSum = 0;

        for (Weather w : values()) {
            currentSum += w.getWeight();
            if (randomIndex < currentSum) {
                return w;
            }
        }
        return SUNNY;
    }

    /**
     * @return La descrizione del meteo (utile per la UI senza usare name()).
     */
    public String getDescription() {
        return description;
    }

    /**
     * Restituisce il modificatore base di cattura. Questo valore verrà combinato
     * con il modificatore del FishingTool e le statistiche del pesce.
     *
     * @return Il moltiplicatore (es. 1.0 neutro, > 1.0 bonus, < 1.0 malus).
     */
    public double getBaseCatchModifier() {
        return baseCatchModifier;
    }
}