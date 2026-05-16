package com.jakione.bluehorizon.model.fish;

/**
 * Tiene traccia delle statistiche di cattura per una singola specie.
 */
public class CatchRecord {
    private int totalCaught = 0;
    private double maxWeight = 0.0;
    private double recordLength = 0.0; // La lunghezza associata al pesce più pesante

    /**
     * Aggiorna il record se il nuovo pesce è più pesante di quello precedente.
     */
    public void registerCatch(double weight, double length) {
        this.totalCaught++;
        if (weight > this.maxWeight) {
            this.maxWeight = weight;
            this.recordLength = length;
        }
    }

    public int getTotalCaught() { return totalCaught; }
    public double getMaxWeight() { return maxWeight; }
    public double getRecordLength() { return recordLength; }
    public boolean isCaught() { return totalCaught > 0; }
}