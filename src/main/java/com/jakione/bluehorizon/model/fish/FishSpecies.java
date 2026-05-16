package com.jakione.bluehorizon.model.fish;

import java.util.Random;

public enum FishSpecies {

    // NOME, MIN_W, MAX_W, MIN_L, MAX_L, CATCH_RATE (es. probabilità base o peso nella loot table)
    ECLISSI_DI_CORALLO("Eclissi di Corallo", 30.0, 300.0, 50.0, 300.0, 0.40),       // 40% di base
    GUARDIANO_DEL_LEVIATANO("Guardiano del Leviatano", 20.0, 100.0, 30.0, 150.0, 0.35),   // 35% di base
    RE_DI_GHIACCIO("Re di Ghiaccio", 2.0, 15.0, 10.0, 120.0, 0.20);       // 20% di base

    private final String displayName;
    private final double minWeight;
    private final double maxWeight;
    private final double minLength;
    private final double maxLength;
    private final double catchRate; // <- Nuovo attributo aggiunto

    private static final Random random = new Random();

    FishSpecies(String displayName, double minWeight, double maxWeight, double minLength, double maxLength, double catchRate) {
        this.displayName = displayName;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.catchRate = catchRate;
    }

    public Fish generateCatch() {
        // Applichiamo il fattore di scala per correlare peso e lunghezza come discusso
        double scaleFactor = random.nextDouble();

        double actualWeight = minWeight + ((maxWeight - minWeight) * scaleFactor);
        double actualLength = minLength + ((maxLength - minLength) * scaleFactor);

        actualWeight = Math.round(actualWeight * 100.0) / 100.0;
        actualLength = Math.round(actualLength * 100.0) / 100.0;

        // <-- MODIFICA QUI: Passiamo "this" come primo parametro per iniettare l'identità della specie
        return new StandardFish(this, this.displayName, actualWeight, actualLength);
    }

    public String getDisplayName() { return displayName; }

    // Getter fondamentale per la Loot Table
    public double getCatchRate() { return catchRate; }
}