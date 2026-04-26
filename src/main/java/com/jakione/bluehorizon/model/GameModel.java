package com.jakione.bluehorizon.model;

/**
 * Rappresenta lo stato logico di "Blue Horizon".
 * Contiene unicamente i dati di dominio, ignorando qualsiasi dettaglio visivo.
 */

public class GameModel {

    private final int maxColumns = 16;
    private final int maxRows = 12;

    private boolean isRunning;

    // Riferimento al giocatore
    private Player player;

    /**
     * Costruisce lo stato iniziale del mondo di gioco.
     *
     */
    public GameModel() {
        this.isRunning = false;

        // Facciamo spawnare il giocatore, ad esempio, alle coordinate logiche 100, 100
        this.player = new Player(100.0, 100.0);
    }

    public int getMaxColumns() { return maxColumns; }
    public int getMaxRows() { return maxRows; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { this.isRunning = running; }

    // Permette agli altri componenti di accedere allo stato del giocatore
    public Player getPlayer() { return player; }
}