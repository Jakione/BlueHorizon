package com.jakione.bluehorizon.model;

import com.jakione.bluehorizon.Props;
import com.jakione.bluehorizon.model.player.Direction;
import com.jakione.bluehorizon.model.player.Player;
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

    private Weather currentWeather = Weather.getRandomWeather();
    private TimeOfDay currentTimeOfDay = TimeOfDay.getRealTimePhase();

    // Struttura dati che rappresenta il mondo di gioco
    private TileType[][] mapGrid;
    private Props[][] propGrid;

    /**
     * Costruisce lo stato iniziale del mondo di gioco.
     *
     */
    public GameModel() {
        this.isRunning = false;

        generateWorld("/map.txt", "/props.txt"); // Il file dovrà trovarsi in src/main/resources/map.txt
        this.player = new Player(maxColumns/2, maxRows/2); // Facciamo spawnare il giocatore

    }


    private void generateWorld(String mapFilePath, String propsFilePath) {
        // 1. CARICAMENTO TERRENO (Layer 0)
        int[][] rawMap = MapReader.loadMapMatrix(mapFilePath);
        this.maxColumns = rawMap.length;
        this.maxRows = rawMap[0].length;
        this.mapGrid = new TileType[maxColumns][maxRows];

        for (int col = 0; col < maxColumns; col++) {
            for (int row = 0; row < maxRows; row++) {
                mapGrid[col][row] = TileType.fromId(rawMap[col][row]);
            }
        }

        // 2. CARICAMENTO PROPS (Layer 1)
        // Usiamo lo stesso MapReader! Zero boilerplate aggiuntivo.
        int[][] rawProps = MapReader.loadMapMatrix(propsFilePath);
        this.propGrid = new Props[maxColumns][maxRows];

        for (int col = 0; col < maxColumns; col++) {
            for (int row = 0; row < maxRows; row++) {
                // Se il file props.txt è più piccolo per errore, evitiamo crash
                if (col < rawProps.length && row < rawProps[0].length) {
                    propGrid[col][row] = Props.fromId(rawProps[col][row]);
                } else {
                    propGrid[col][row] = Props.NONE;
                }
            }
        }
    }

    /**
     * Metodo esposto per il Renderer.
     */
    public Props getPropAt(int col, int row) {
        if (col >= 0 && col < maxColumns && row >= 0 && row < maxRows) {
            return propGrid[col][row];
        }
        return Props.NONE;
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

    public Weather getCurrentWeather() { return this.currentWeather; }

    public void setCurrentWeather(Weather newWeather) {
        if (this.currentWeather != newWeather) {
            this.currentWeather = newWeather;
        }
    }

    public TimeOfDay getTimeOfDay() { return currentTimeOfDay; }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        if (this.currentTimeOfDay != timeOfDay) {
            this.currentTimeOfDay = timeOfDay;
        }
    }

}