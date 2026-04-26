package com.jakione.bluehorizon.model;

/**
 * Interfaccia Observer per disaccoppiare la logica di business dalla GUI.
 * Qualsiasi componente grafico (es. il Controller di JavaFX) che necessita
 * di aggiornarsi ad ogni ciclo del game loop dovrà implementare questa interfaccia.
 */
public interface GameObserver {
    /**
     * Metodo invocato dal GameEngine alla fine di ogni iterazione logica.
     */
    void onGameStateUpdated();
}