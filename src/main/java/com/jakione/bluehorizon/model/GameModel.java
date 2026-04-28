package com.jakione.bluehorizon.model;

/**
 * Rappresenta lo stato logico di "Blue Horizon".
 * Contiene unicamente i dati di dominio, ignorando qualsiasi dettaglio visivo.
 */

public class GameModel {

    private final int maxColumns = 16;
    private final int maxRows = 12;
    private boolean isRunning;
    private Player player;

    // Struttura dati che rappresenta il mondo di gioco
    private TileType[][] mapGrid;

    /**
     * Costruisce lo stato iniziale del mondo di gioco.
     *
     */
    public GameModel() {
        this.isRunning = false;
        this.player = new Player(maxColumns/2, maxRows/2); // Facciamo spawnare il giocatore
        // Inizializziamo la mappa
        this.mapGrid = new TileType[maxColumns][maxRows];
        generateWorld();
    }

    private void generateWorld() {
        for (int col = 0; col < maxColumns; col++) {
            for (int row = 0; row < maxRows; row++) {
                mapGrid[col][row] = TileType.WATER;
            }
        }
    }

    /**
     * Gestisce la logica di movimento
     */
    public void movePlayer(Direction direction) {
        int nextCol = player.getCol();
        int nextRow = player.getRow();

        switch (direction) {
            case UP -> nextRow--;
            case DOWN -> nextRow++;
            case LEFT -> nextCol--;
            case RIGHT -> nextCol++;
        }
        player.updatePosition(direction);

    }

    public int getMaxColumns() { return maxColumns; }
    public int getMaxRows() { return maxRows; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { this.isRunning = running; }

    public Player getPlayer() { return player; }

    // Getter per permettere alla View di leggere la mappa
    public TileType[][] getMapGrid() { return mapGrid; }
}