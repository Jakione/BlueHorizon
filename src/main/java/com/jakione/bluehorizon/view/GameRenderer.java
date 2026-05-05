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

import java.util.EnumMap;
import java.util.Map;

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
    private Image sandTexture;
    private Image sandWaterTexture1;
    private Image sandWaterTexture2;
    private Image playerUp;
    private Image playerDown;
    private Image playerLeft;
    private Image playerRight;

    private enum CoastShape {
        NORTH, SOUTH, EAST, WEST,
        NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST,
        DEFAULT // Nel caso di una tile isolata
    }

    // Mappa che collega ogni forma a un array di frame animati (es. indice 0 = frame 1, indice 1 = frame 2)
    private final Map<CoastShape, Image[]> sandWaterAnimations = new EnumMap<>(CoastShape.class);

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

    private void loadSandWaterAsset(CoastShape shape, String basePath) {
        String path1 = basePath + "1.png";
        String path2 = basePath + "2.png";

        java.net.URL url1 = getClass().getResource(path1);
        java.net.URL url2 = getClass().getResource(path2);

        // Controllo 1: I file esistono?
        if (url1 == null) throw new RuntimeException("ERRORE: File non trovato -> " + path1);
        if (url2 == null) throw new RuntimeException("ERRORE: File non trovato -> " + path2);

        Image img1 = new Image(url1.toExternalForm());
        Image img2 = new Image(url2.toExternalForm());

        // Controllo 2: I file sono immagini PNG valide e non corrotte?
        if (img1.isError() || img1.getWidth() == 0) {
            throw new RuntimeException("ERRORE: Il file esiste ma è corrotto o vuoto -> " + path1);
        }
        if (img2.isError() || img2.getWidth() == 0) {
            throw new RuntimeException("ERRORE: Il file esiste ma è corrotto o vuoto -> " + path2);
        }

        sandWaterAnimations.put(shape, new Image[]{ scalePixelArt(img1, scale), scalePixelArt(img2, scale) });
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
            Image originalSand = new Image(getClass().getResourceAsStream("/Tile/Sand/sand.png"));

            loadSandWaterAsset(CoastShape.NORTH,  "/Tile/Sand_Water/Sand_Water_North/");
            loadSandWaterAsset(CoastShape.SOUTH, "/Tile/Sand_Water/Sand_Water_South/");
            loadSandWaterAsset(CoastShape.EAST, "/Tile/Sand_Water/Sand_Water_Est/");
            loadSandWaterAsset(CoastShape.WEST, "/Tile/Sand_Water/Sand_Water_West/");

            loadSandWaterAsset(CoastShape.NORTHEAST, "/Tile/Sand_Water/Sand_Water_Corners/northest/");
            loadSandWaterAsset(CoastShape.NORTHWEST, "/Tile/Sand_Water/Sand_Water_Corners/northwest/");
            loadSandWaterAsset(CoastShape.SOUTHEAST, "/Tile/Sand_Water/Sand_Water_Corners/southest/");
            loadSandWaterAsset(CoastShape.SOUTHWEST, "/Tile/Sand_Water/Sand_Water_Corners/southwest/");
            sandWaterAnimations.put(CoastShape.DEFAULT, sandWaterAnimations.get(CoastShape.NORTH));

            // 2. Ingrandiamo gli asset e assegniamo ai campi della classe
            waterTexture1 = scalePixelArt(originalWater1, scale);
            waterTexture2 = scalePixelArt(originalWater2, scale);
            rockTexture1 = scalePixelArt(originalRock1, scale);
            rockTexture2 = scalePixelArt(originalRock2, scale);
            sandTexture = scalePixelArt(originalSand, scale);


            // Usiamo DIRETTAMENTE le variabili locali raw appena caricate
            this.playerDown = scalePixelArt(rawPlayerDown, scale);
            this.playerUp = scalePixelArt(rawPlayerUp, scale);
            this.playerRight = scalePixelArt(rawPlayerRight, scale);
            this.playerLeft = scalePixelArt(rawPlayerLeft, scale);

        } catch (Exception e) {
            System.err.println("Attenzione: Impossibile caricare qualche risorsa.");
            e.printStackTrace();
        }
    }

    /**
     * Metodo di supporto per l'Auto-Tiling.
     * Considera "Terra" sia la sabbia pura che le altre caselle di bordo.
     */
    private boolean isLand(int col, int row, TileType[][] map) {
        // Se usciamo dai bordi della mappa, consideriamo il vuoto come mare (false)
        if (col < 0 || col >= model.getMaxColumns() || row < 0 || row >= model.getMaxRows()) {
            return false;
        }
        TileType type = map[col][row];
        return type == TileType.SAND || type == TileType.SAND_WATER;
    }

    /**
     * Analizza le adiacenze per determinare quale sprite di costa renderizzare.
     */
    private CoastShape determineCoastShape(int col, int row, TileType[][] map) {
        // 1. Sondiamo i 4 punti cardinali usando la nuova logica "isLand"
        boolean landN = isLand(col, row - 1, map);
        boolean landS = isLand(col, row + 1, map);
        boolean landE = isLand(col + 1, row, map);
        boolean landW = isLand(col - 1, row, map);

        // --- 2. RISOLUZIONE DEGLI ANGOLI ESTERNI ---
        // Un angolo si verifica quando abbiamo terra su due lati adiacenti, e mare sugli altri due.
        // (Es: Se ho terra a Sud e a Est, significa che l'angolo sporge verso Nord-Ovest)
        if (landS && landE && !landN && !landW) return CoastShape.NORTHWEST;
        if (landS && landW && !landN && !landE) return CoastShape.NORTHEAST;
        if (landN && landE && !landS && !landW) return CoastShape.SOUTHWEST;
        if (landN && landW && !landS && !landE) return CoastShape.SOUTHEAST;

        // --- 3. RISOLUZIONE DELLE SPONDE DRITTE ---
        // Se arriviamo qui, non è un angolo. La direzione in cui NON c'è terra indica la costa.
        if (!landN && landS) return CoastShape.NORTH; // Mare a nord, Terra a sud -> Costa NORD
        if (!landS && landN) return CoastShape.SOUTH; // Mare a sud, Terra a nord -> Costa SUD
        if (!landE && landW) return CoastShape.EAST;  // Mare a est, Terra a ovest -> Costa EST
        if (!landW && landE) return CoastShape.WEST;  // Mare a ovest, Terra a est -> Costa OVEST

        // Fallback per blocchi isolati 1x1 o forme impreviste
        return CoastShape.DEFAULT;
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

        // --- GESTIONE TEXTURE SAND_WATER ---
        Image currentSandWaterImage = (sandWaterTexture1 != null && sandWaterTexture2 != null)
                ? ((frameIndex == 0) ? sandWaterTexture1 : sandWaterTexture2)
                : sandWaterTexture1;

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

                        case SAND:
                            if (sandTexture != null){
                                gc.drawImage(sandTexture, drawX, drawY);
                            }
                            break;

                        case SAND_WATER:
                            CoastShape shape = determineCoastShape(col, row, map);
                            Image[] frames = sandWaterAnimations.get(shape);

                            if (frames != null && frames.length == 2) {
                                // Usa l'indice calcolato precedentemente per tutte le animazioni
                                gc.drawImage(frames[frameIndex], drawX, drawY);
                            }
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
        if (original == null) return null;

        // Guardia contro immagini decodificate male
        if (original.getWidth() == 0 || original.getHeight() == 0) {
            System.err.println("Attenzione: bypassato lo scaling per un'immagine con dimensioni 0.");
            return original;
        }

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