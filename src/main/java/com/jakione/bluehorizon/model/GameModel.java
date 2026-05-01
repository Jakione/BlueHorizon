package com.jakione.bluehorizon.model;

import com.jakione.bluehorizon.persistence.MapReader;

/**
 * Rappresenta lo stato logico di "Blue Horizon".
 * Contiene unicamente i dati di dominio, ignorando qualsiasi dettaglio visivo.
 */

public class GameModel {

    private int maxColumns;
    private int maxRows;
    private boolean isRunning;
    private final Player player;

    // Struttura dati che rappresenta il mondo di gioco
    private TileType[][] mapGrid;

    /**
     * Costruisce lo stato iniziale del mondo di gioco.
     *
     */
    public GameModel() {
        this.isRunning = false;

        generateWorld("/map.txt"); // Il file dovrà trovarsi in src/main/resources/map.txt
        this.player = new Player(maxColumns/2, maxRows/2); // Facciamo spawnare il giocatore

    }

    private void generateWorld(String mapFilePath) {
        // Deleghiamo la lettura grezza al modulo di persistenza
        int[][] rawMap = MapReader.loadMapMatrix(mapFilePath);

        // Estrapoliamo le dimensioni reali generate dal reader
        this.maxColumns = rawMap.length;
        this.maxRows = rawMap[0].length;

        this.mapGrid = new TileType[maxColumns][maxRows];

        // Traduzione dei dati grezzi negli Enum di dominio
        for (int col = 0; col < maxColumns; col++) {
            for (int row = 0; row < maxRows; row++) {
                mapGrid[col][row] = TileType.fromId(rawMap[col][row]);
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
        if (nextCol >= 0 && nextCol < maxColumns && nextRow >= 0 && nextRow < maxRows) {
            TileType destinationTile = mapGrid[nextCol][nextRow];
            if (destinationTile == TileType.WATER) {
                player.updatePosition(direction);
            }
        }
    }

    public int getMaxColumns() { return maxColumns; }
    public int getMaxRows() { return maxRows; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { this.isRunning = running; }

    public Player getPlayer() { return player; }

    // Getter per permettere alla View di leggere la mappa
    public TileType[][] getMapGrid() { return mapGrid; }
}