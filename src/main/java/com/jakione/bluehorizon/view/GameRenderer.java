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

    // CACHE DEGLI ASSET GRAFICI
    private Image waterTexture1;
    private Image waterTexture2;
    private Image playerImage;

    // STATO DELL'ANIMAZIONE VISIVA
    private int waterFrameCounter = 0;
    private final int animationSpeed = 60;

    // --- NUOVE VARIABILI PER LA TELECAMERA ---
    // Inizializzate a -1 per l'allineamento istantaneo al primo frame
    private double cameraX = -1;
    private double cameraY = -1;

    private final double SMOOTHING_FACTOR = 0.15; // Un po' più basso per un effetto "mare" più morbido

    public GameRenderer(GameModel model) {
        this.model = model;

        // Nota: Qui puoi impostare la dimensione della finestra che preferisci!
        // Non deve più dipendere dalla grandezza della mappa.
        int screenWidth = 800;  // Esempio di risoluzione fissa
        int screenHeight = 600;
        this.setWidth(screenWidth);
        this.setHeight(screenHeight);

        loadAssets();
        render();
    }

    private void loadAssets() {
        try {
            waterTexture1 = new Image(getClass().getResourceAsStream("/Tile/watertile1.png"));
            waterTexture2 = new Image(getClass().getResourceAsStream("/Tile/watertile2.png"));
            playerImage = new Image(getClass().getResourceAsStream("/Fisherman/south.png"));
        } catch (Exception e) {
            System.err.println("Attenzione: Impossibile caricare water_1.png o water_2.png.");
        }
    }

    @Override
    public void onGameStateUpdated() {
        waterFrameCounter++;
        Platform.runLater(() -> render());
    }

    private void render() {
        GraphicsContext gc = this.getGraphicsContext2D();

        // Pulizia dello schermo (il colore di sfondo fa da "vuoto" fuori dalla mappa)
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        TileType[][] map = model.getMapGrid();
        Player player = model.getPlayer();

        // 1. CALCOLO DELLA TELECAMERA FLUIDA
        // Il target della telecamera è la posizione assoluta del giocatore nel mondo
        double targetCameraX = player.getCol() * tileSize;
        double targetCameraY = player.getRow() * tileSize;

        // Allineamento istantaneo al primissimo frame
        if (cameraX == -1 && cameraY == -1) {
            cameraX = targetCameraX;
            cameraY = targetCameraY;
        }

        // Movimento fluido della telecamera
        cameraX += (targetCameraX - cameraX) * SMOOTHING_FACTOR;
        cameraY += (targetCameraY - cameraY) * SMOOTHING_FACTOR;

        // Calcolo del centro dello schermo (offset per centrare la vista)
        double screenCenterX = (this.getWidth() / 2.0) - (tileSize / 2.0);
        double screenCenterY = (this.getHeight() / 2.0) - (tileSize / 2.0);

        // --- GESTIONE TEXTURE ACQUA ---
        int frameIndex = (waterFrameCounter / animationSpeed) % 2;
        Image currentWaterImage = (waterTexture1 != null && waterTexture2 != null)
                ? ((frameIndex == 0) ? waterTexture1 : waterTexture2)
                : waterTexture1;

        // 2. RENDER DELLA MAPPA (Traslata in base alla telecamera)
        for (int col = 0; col < model.getMaxColumns(); col++) {
            for (int row = 0; row < model.getMaxRows(); row++) {

                // Formula Magica: Posizione nel mondo - Posizione Telecamera + Metà Schermo
                double drawX = (col * tileSize) - cameraX + screenCenterX;
                double drawY = (row * tileSize) - cameraY + screenCenterY;

                // Ottimizzazione: Disegniamo il tile SOLO se è visibile a schermo! (Culling)
                if (drawX + tileSize > 0 && drawX < this.getWidth() &&
                        drawY + tileSize > 0 && drawY < this.getHeight()) {

                    if (map[col][row] == TileType.WATER) {
                        if (currentWaterImage != null) {
                            gc.drawImage(currentWaterImage, drawX, drawY, tileSize, tileSize);
                        } else {
                            gc.setFill(Color.web("#1E90FF"));
                            gc.fillRect(drawX, drawY, tileSize, tileSize);
                        }
                    }
                }
            }
        }

        // 3. RENDER DEL GIOCATORE
        // Il giocatore ora è FISSO al centro dello schermo!
        // Ma per dargli quell'effetto di spinta/inerzia quando inizia a muoversi,
        // lo disegniamo calcolando la sua differenza con la telecamera.
        double playerVisualX = targetCameraX - cameraX + screenCenterX;
        double playerVisualY = targetCameraY - cameraY + screenCenterY;

        gc.drawImage(playerImage, playerVisualX, playerVisualY, tileSize, tileSize);
    }
}