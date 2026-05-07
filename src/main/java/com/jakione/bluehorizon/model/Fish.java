package com.jakione.bluehorizon.model;

/**
 * Contratto base per tutte le entità pescabili all'interno del gioco.
 * Espone i metodi essenziali ignorando i dettagli implementativi.
 */
public interface Fish {

    String getName();

    double getWeight();

    double getLength();
}