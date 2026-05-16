package com.jakione.bluehorizon.model.fish;

/**
 * Implementazione concreta standard di un pesce.
 * Può essere utilizzata dal GameEngine per generare dinamicamente qualsiasi
 * tipo di pesce "normale" basandosi unicamente sui parametri passati al costruttore,
 * senza necessità di creare una classe per ogni specie.
 * (Per Normale si intendono pesci con comportamenti di gioco standard)
 */
public class StandardFish extends AbstractFish {

    // <-- AGGIUNTO: Riceviamo FishSpecies nel costruttore
    public StandardFish(FishSpecies species, String name, double weight, double length) {
        super(species, name, weight, length); // Passiamo la specie alla classe base
    }

}