package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.*;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GameRenderer extends Canvas implements GameObserver {

    private final int originalTileSize = 16;
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale;

    private final GameModel model;

    // CACHE DEGLI ASSET GRAFICI
    private Image waterTexture1;
    private Image waterTexture2;
    private Image rockTexture1;
    private Image rockTexture2;
    private Image playerUp;
    private Image playerDown;
    private Image playerLeft;
    private Image playerRight;

    // STATO DELL'ANIMAZIONE VISIVA
    private int waterFrameCounter = 0;
    private final int animationSpeed = 60;

    // --- NUOVE VARIABILI PER LA TELECAMERA ---
    // Inizializzate a -1 per l'allineamento istantaneo al primo frame
    private double cameraX = -1;
    private double cameraY = -1;

    private final double SMOOTHING_FACTOR = 0.075; // Un po' più basso per un effetto "mare" più morbido

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
            // 1. Carichiamo gli asset originali (piccoli) in variabili locali
            Image originalWater1 = new Image(getClass().getResourceAsStream("/Tile/Water/watertile1.png"));
            Image originalWater2 = new Image(getClass().getResourceAsStream("/Tile/Water/watertile2.png"));
            Image originalRock1 = new Image(getClass().getResourceAsStream("/Tile/Rock/rock1.png"));
            Image originalRock2 = new Image(getClass().getResourceAsStream("/Tile/Rock/rock2.png"));
            Image rawPlayerDown = new Image(getClass().getResourceAsStream("/Player/P2down.png"));
            Image rawPlayerUp = new Image(getClass().getResourceAsStream("/Player/P2up.png"));
            Image rawPlayerRight = new Image(getClass().getResourceAsStream("/Player/P2right.png"));
            Image rawPlayerLeft = new Image(getClass().getResourceAsStream("/Player/P2left.png"));

            // 2. Ingrandiamo gli asset e assegniamo ai campi della classe
            waterTexture1 = scalePixelArt(originalWater1, scale);
            waterTexture2 = scalePixelArt(originalWater2, scale);
            rockTexture1 = scalePixelArt(originalRock1, scale);
            rockTexture2 = scalePixelArt(originalRock2, scale);

            // Usiamo DIRETTAMENTE le variabili locali raw appena caricate
            this.playerDown = scalePixelArt(rawPlayerDown, scale);
            this.playerUp = scalePixelArt(rawPlayerUp, scale);
            this.playerRight = scalePixelArt(rawPlayerRight, scale);
            this.playerLeft = scalePixelArt(rawPlayerLeft, scale);

        } catch (Exception e) {
            System.err.println("Attenzione: Impossibile caricare gli asset: " + e.getMessage());
            e.printStackTrace(); // Questo ti aiuterà a vedere l'errore esatto nella console se capita ancora
        }
    }

    @Override
    public void onGameStateUpdated() {
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

        // --- 1. CALCOLO DELLE COORDINATE DEL MONDO E LIMITI ---
        double playerWorldX = player.getCol() * tileSize;
        double playerWorldY = player.getRow() * tileSize;

        double screenWidth = this.getWidth();
        double screenHeight = this.getHeight();

        double mapPixelWidth = model.getMaxColumns() * tileSize;
        double mapPixelHeight = model.getMaxRows() * tileSize;

        // --- 2. CALCOLO DELLA TELECAMERA IDEALE (Angolo in alto a sx) ---
        // Vogliamo centrare il giocatore, quindi la telecamera deve stare mezza schermata in alto a sinistra rispetto a lui
        double idealCameraX = playerWorldX - (screenWidth / 2.0) + (tileSize / 2.0);
        double idealCameraY = playerWorldY - (screenHeight / 2.0) + (tileSize / 2.0);

        // --- 3. CLAMPING (Vincolo ai bordi) ---
        // Non permettiamo alla telecamera di andare sotto lo 0 o oltre la dimensione massima della mappa
        double maxCameraX = Math.max(0, mapPixelWidth - screenWidth);
        double maxCameraY = Math.max(0, mapPixelHeight - screenHeight);

        double targetCameraX = Math.max(0, Math.min(idealCameraX, maxCameraX));
        double targetCameraY = Math.max(0, Math.min(idealCameraY, maxCameraY));

        // Allineamento istantaneo al primissimo frame
        if (cameraX == -1 && cameraY == -1) {
            cameraX = targetCameraX;
            cameraY = targetCameraY;
        }

        // Movimento fluido della telecamera verso il target bloccato
        cameraX += (targetCameraX - cameraX) * SMOOTHING_FACTOR;
        cameraY += (targetCameraY - cameraY) * SMOOTHING_FACTOR;

        // --- GESTIONE TEXTURE WATER ---
        int frameIndex = (waterFrameCounter / animationSpeed) % 2;
        Image currentWaterImage = (waterTexture1 != null && waterTexture2 != null)
                ? ((frameIndex == 0) ? waterTexture1 : waterTexture2)
                : waterTexture1;
        // --- GESTIONE TEXTURE ROCK ---
        Image currentRockImage = (rockTexture1 != null && rockTexture2 != null)
                ? ((frameIndex == 0) ? rockTexture1 : rockTexture2)
                : rockTexture1;

        // --- 4. RENDER DELLA MAPPA ---
        for (int col = 0; col < model.getMaxColumns(); col++) {
            for (int row = 0; row < model.getMaxRows(); row++) {

                double drawX = (col * tileSize) - cameraX;
                double drawY = (row * tileSize) - cameraY;

                // Culling: Disegna solo se visibile all'interno della telecamera
                if (drawX + tileSize > 0 && drawX < screenWidth &&
                        drawY + tileSize > 0 && drawY < screenHeight) {

                    // Estrapoliamo il tipo di casella letto dal file di testo tramite il Model
                    TileType currentTile = map[col][row];

                    // Smistiamo il rendering in base all'Enum
                    switch (currentTile) {
                        case WATER:
                            if (currentWaterImage != null) {
                                gc.drawImage(currentWaterImage, drawX, drawY);
                            }
                            break;

                        case ROCK:
                            if (currentWaterImage != null) {
                                gc.drawImage(currentWaterImage, drawX, drawY);
                            }
                            if (currentRockImage != null) {
                                gc.drawImage(currentRockImage, drawX, drawY);
                            }
                            break;

                        case LAND:
                            // Predisposto per il futuro, pronto all'uso!
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        // --- 5. RENDER DEL GIOCATORE ---

        // Il giocatore viene disegnato esattamente con la stessa formula della mappa!
        // Posizione nel mondo del giocatore - Posizione Telecamera
        double playerDrawX = playerWorldX - cameraX;
        double playerDrawY = playerWorldY - cameraY;
        // Recuperiamo la direzione attuale dal Modello
        Direction currentDir = player.getCurrentDirection();
        Image spriteToDraw = playerDown; // Sprite di default per sicurezza

        // Selezioniamo l'immagine corretta in base alla direzione
        switch (currentDir) {
            case UP -> spriteToDraw = this.playerUp;
            case DOWN -> spriteToDraw = this.playerDown;
            case LEFT -> spriteToDraw = this.playerLeft;
            case RIGHT -> spriteToDraw = this.playerRight;
        }

        // Disegniamo l'immagine selezionata
        gc.drawImage(spriteToDraw, playerDrawX, playerDrawY);

    }

    /**
     * Ingrandisce un'immagine nitida (pixel-art) mantenendo la nitidezza
     * utilizzando l'algoritmo Nearest Neighbor.
     *
     * @param original L'immagine originale nitida (piccola).
     * @param scale Il fattore di ingrandimento intero.
     * @return Una nuova immagine ingrandita e nitida.
     */
    private Image scalePixelArt(Image original, int scale) {
        if (scale <= 1) return original; // Nessun ingrandimento necessario

        int originalWidth = (int) original.getWidth();
        int originalHeight = (int) original.getHeight();
        int targetWidth = originalWidth * scale;
        int targetHeight = originalHeight * scale;

        PixelReader reader = original.getPixelReader();
        WritableImage scaled = new WritableImage(targetWidth, targetHeight);
        PixelWriter writer = scaled.getPixelWriter();

        // Copiamo i pixel con l'algoritmo Nearest Neighbor
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                // Troviamo il pixel originale corrispondente
                // (arrotondamento per Nearest Neighbor: per scale=3, x=0,1,2 map to srcX=0)
                int srcX = x / scale;
                int srcY = y / scale;

                // Copiamo il colore dal pixel originale
                writer.setColor(x, y, reader.getColor(srcX, srcY));
            }
        }
        return scaled;
    }
}