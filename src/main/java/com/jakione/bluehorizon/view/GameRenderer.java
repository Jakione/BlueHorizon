package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.model.GameObserver;
import com.jakione.bluehorizon.model.Player;
import com.jakione.bluehorizon.model.TileType;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameRenderer extends Canvas implements GameObserver {

    private final int originalTileSize = 16;
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale;

    private final GameModel model;

    // CACHE DEGLI ASSET GRAFICI (Ora caricati una volta sola)
    private Image waterTexture1;
    private Image waterTexture2;

    // STATO DELL'ANIMAZIONE (Solo visivo)
    private int waterFrameCounter = 0;
    // Velocità: cambia immagine ogni 15 frame (4 volte/sec a 60 FPS)
    private final int animationSpeed = 60;

    public GameRenderer(GameModel model) {
        this.model = model;

        int screenWidth = tileSize * model.getMaxColumns();
        int screenHeight = tileSize * model.getMaxRows();
        this.setWidth(screenWidth);
        this.setHeight(screenHeight);

        loadAssets(); // Carichiamo le risorse in memoria una volta sola
        render();
    }

    /**
     * Carica in RAM tutti i file grafici dal classpath di Maven (cartella resources).
     */
    private void loadAssets() {
        try {
            // Il path inizia sempre con "/" che punta alla root di src/main/resources/
            waterTexture1 = new Image(getClass().getResourceAsStream("/Tile/water_tile.png"));
            waterTexture2 = new Image(getClass().getResourceAsStream("/Tile/water_tile_2.png"));
        } catch (Exception e) {
            System.err.println("Attenzione: Impossibile caricare water_1.png o water_2.png.");
            // Le texture rimarranno null
        }
    }

    @Override
    public void onGameStateUpdated() {
        // Incrementiamo il contatore dei frame visivi ogni volta che il modello si aggiorna
        waterFrameCounter++;
        Platform.runLater(() -> render());
    }

    private void render() {
        GraphicsContext gc = this.getGraphicsContext2D();

        // Pulizia dello schermo
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        TileType[][] map = model.getMapGrid();
        Player player = model.getPlayer();

        // LOGICA DI SELEZIONE DEL FRAME
        // Calcoliamo quale dei due frame visualizzare: 0 o 1
        int frameIndex = (waterFrameCounter / animationSpeed) % 2;
        Image currentWaterImage;

        // Scegliamo l'immagine basandoci sul frameIndex e sul fatto che siano state caricate
        if (waterTexture1 != null && waterTexture2 != null) {
            currentWaterImage = (frameIndex == 0) ? waterTexture1 : waterTexture2;
        } else if (waterTexture1 != null) {
            currentWaterImage = waterTexture1; // Fallback se manca la seconda
        } else {
            currentWaterImage = null; // Manca tutto
        }

        // 1. Render del Layer Sfondo (La Mappa Animata)
        for (int col = 0; col < model.getMaxColumns(); col++) {
            for (int row = 0; row < model.getMaxRows(); row++) {

                double drawX = col * tileSize;
                double drawY = row * tileSize;

                if (map[col][row] == TileType.WATER) {
                    if (currentWaterImage != null) {
                        // Disegna la texture scalandola alla dimensione della casella
                        gc.drawImage(currentWaterImage, drawX, drawY, tileSize, tileSize);
                    } else {
                        // Fallback in caso di immagine mancante
                        gc.setFill(Color.web("#1E90FF"));
                        gc.fillRect(drawX, drawY, tileSize, tileSize);
                    }
                }
            }
        }

        // 2. Render del Layer Entità (Il Giocatore)
        double playerDrawX = player.getCol() * tileSize;
        double playerDrawY = player.getRow() * tileSize;
        gc.setFill(Color.WHITE);
        gc.fillRect(playerDrawX, playerDrawY, tileSize, tileSize);
    }
}