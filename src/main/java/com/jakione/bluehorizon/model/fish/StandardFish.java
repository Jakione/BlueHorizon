package com.jakione.bluehorizon.model.fish;

/**
 * Implementazione concreta standard di un pesce.
 * Può essere utilizzata dal GameEngine per generare dinamicamente qualsiasi
 * tipo di pesce "normale" basandosi unicamente sui parametri passati al costruttore,
 * senza necessità di creare una classe per ogni specie.
 * (Per Normale si intendono pesci con comportamenti di gioco standard)
 */
public class StandardFish extends AbstractFish {

    public StandardFish(String name, double weight, double length) {
        super(name, weight, length);
    }

    // Essendo un pesce "standard", non ha comportamenti aggiuntivi.
    // In futuro, se introdurrai meccaniche speciali (es. un pesce che si dimena e
    // ha un metodo tryEscape()), potrai creare una nuova classe concreta
    // come 'FighterFish extends AbstractFish'.
}