package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.Props;
import com.jakione.bluehorizon.model.*;
import com.jakione.bluehorizon.model.player.Direction;
import com.jakione.bluehorizon.model.player.Player;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.*;

public class GameRenderer extends Canvas implements GameObserver {

    private final int originalTileSize = 16;
    private final int scale = 4;
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
    private Image playerFishing;
    private Image palmTexture1;
    private Image palmTexture2;

    private enum CoastShape {
        NORTH, SOUTH, EAST, WEST,
        NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST, // Angoli esterni (CornersW)
        INNER_NORTHEAST, INNER_NORTHWEST, INNER_SOUTHEAST, INNER_SOUTHWEST, // Nuovi angoli interni (CornersS)
        DEFAULT
    }

    /**
     * Record interno per gestire l'Y-Sorting delle entità.
     */
    private record RenderableEntity(Image sprite, double drawX, double drawY, double worldY) implements Comparable<RenderableEntity> {
        @Override
        public int compareTo(RenderableEntity other) {
            // Ordina in modo crescente in base alla coordinata Y nel mondo (worldY).
            // Chi ha una Y minore (sta più in alto nello schermo) viene disegnato prima.
            return Double.compare(this.worldY, other.worldY);
        }
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

    private final int MAX_RAINDROPS = 150;
    private final RainDrop[] rainDrops = new RainDrop[MAX_RAINDROPS];

    /**
     * Classe interna privata per gestire le particelle visive della pioggia.
     * Esiste solo nella View, il Model non sa nulla di tutto ciò.
     */
    private class RainDrop {
        double x, y, speed, length;

        void reset(double screenWidth, double screenHeight) {
            // Aggiungiamo un buffer di 300 pixel fuori dallo schermo a destra.
            // Il vento soffia verso sinistra: in questo modo le gocce nate
            // nell'area invisibile voleranno verso l'angolo in basso a destra.
            this.x = Math.random() * (screenWidth + 300);

            // Le facciamo nascere leggermente fuori dallo schermo in alto
            this.y = Math.random() * screenHeight - screenHeight;
            this.speed = 15 + Math.random() * 15; // Velocità variabile
            this.length = 10 + Math.random() * 15;
        }
    }

    public GameRenderer(GameModel model) {
        this.model = model;

        // Nota: Qui puoi impostare la dimensione della finestra che preferisci!
        // Non deve più dipendere dalla grandezza della mappa.
        int screenWidth = 800;  // Esempio di risoluzione fissa
        int screenHeight = 600;
        this.setWidth(screenWidth);
        this.setHeight(screenHeight);

        // Inizializza l'Object Pool per le particelle visive
        for (int i = 0; i < MAX_RAINDROPS; i++) {
            rainDrops[i] = new RainDrop();
            rainDrops[i].reset(screenWidth, screenHeight);
        }

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
            Image originalWater1 = new Image(getClass().getResourceAsStream("/Tile/Water/watertile1.png"));
            Image originalWater2 = new Image(getClass().getResourceAsStream("/Tile/Water/watertile2.png"));
            Image originalRock1 = new Image(getClass().getResourceAsStream("/Tile/Rock/rock1.png"));
            Image originalRock2 = new Image(getClass().getResourceAsStream("/Tile/Rock/rock2.png"));
            Image rawPlayerDown = new Image(getClass().getResourceAsStream("/Player/P2down.png"));
            Image rawPlayerUp = new Image(getClass().getResourceAsStream("/Player/P2up.png"));
            Image rawPlayerRight = new Image(getClass().getResourceAsStream("/Player/P2right.png"));
            Image rawPlayerLeft = new Image(getClass().getResourceAsStream("/Player/P2left.png"));
            Image rawPlayerFishing = new Image(getClass().getResourceAsStream("/Player/P2_fishing_rod.png"));
            Image originalSand = new Image(getClass().getResourceAsStream("/Tile/Sand/sand.png"));
            Image originalPalm1 = new Image(getClass().getResourceAsStream("/Props/palm1.png"));
            Image originalPalm2 = new Image(getClass().getResourceAsStream("/Props/palm2.png"));

            loadSandWaterAsset(CoastShape.NORTH,  "/Tile/Sand_Water/Sand_Water_North/");
            loadSandWaterAsset(CoastShape.SOUTH, "/Tile/Sand_Water/Sand_Water_South/");
            loadSandWaterAsset(CoastShape.EAST, "/Tile/Sand_Water/Sand_Water_Est/");
            loadSandWaterAsset(CoastShape.WEST, "/Tile/Sand_Water/Sand_Water_West/");

            // Caricamento Angoli ESTERNI (CornersW - Convessi)
            loadSandWaterAsset(CoastShape.NORTHEAST, "/Tile/Sand_Water/Sand_Water_CornersW/northest/");
            loadSandWaterAsset(CoastShape.NORTHWEST, "/Tile/Sand_Water/Sand_Water_CornersW/northwest/");
            loadSandWaterAsset(CoastShape.SOUTHEAST, "/Tile/Sand_Water/Sand_Water_CornersW/southest/");
            loadSandWaterAsset(CoastShape.SOUTHWEST, "/Tile/Sand_Water/Sand_Water_CornersW/southwest/");

            // Caricamento Angoli INTERNI (CornersS - Concavi)
            loadSandWaterAsset(CoastShape.INNER_NORTHEAST, "/Tile/Sand_Water/Sand_Water_CornersS/northest/");
            loadSandWaterAsset(CoastShape.INNER_NORTHWEST, "/Tile/Sand_Water/Sand_Water_CornersS/northwest/");
            loadSandWaterAsset(CoastShape.INNER_SOUTHEAST, "/Tile/Sand_Water/Sand_Water_CornersS/southest/");
            loadSandWaterAsset(CoastShape.INNER_SOUTHWEST, "/Tile/Sand_Water/Sand_Water_CornersS/southwest/");

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
            this.playerFishing = scalePixelArt(rawPlayerFishing, scale);

            this.palmTexture1 = scalePixelArt(originalPalm1, scale);
            this.palmTexture2 = scalePixelArt(originalPalm2, scale);

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
     * Logica ibrida: Speculare per i bordi esterni, Diretta per i nuovi angoli interni.
     */
    private CoastShape determineCoastShape(int col, int row, TileType[][] map) {
        // 1. Sondiamo le 4 direzioni cardinali
        boolean landN = isLand(col, row - 1, map);
        boolean landS = isLand(col, row + 1, map);
        boolean landE = isLand(col + 1, row, map);
        boolean landW = isLand(col - 1, row, map);

        // --- 2. GESTIONE DEGLI ANGOLI INTERNI (CornersS - Concavi) ---
        if (landN && landS && landE && landW) {
            boolean landNE = isLand(col + 1, row - 1, map);
            boolean landNW = isLand(col - 1, row - 1, map);
            boolean landSE = isLand(col + 1, row + 1, map);
            boolean landSW = isLand(col - 1, row + 1, map);

            // NESSUNA INVERSIONE: Se l'acqua è a Nord-Est, chiamiamo la cartella NORTHEAST
            if (!landNE) return CoastShape.INNER_NORTHEAST;
            if (!landNW) return CoastShape.INNER_NORTHWEST;
            if (!landSE) return CoastShape.INNER_SOUTHEAST;
            if (!landSW) return CoastShape.INNER_SOUTHWEST;

            return CoastShape.DEFAULT;
        }

        // --- 3. RISOLUZIONE DEGLI ANGOLI ESTERNI (CornersW - Convessi) ---
        // Manteniamo l'inversione speculare che ha risolto il problema precedente
        if (landN && landE && !landS && !landW) return CoastShape.SOUTHWEST;
        if (landN && landW && !landS && !landE) return CoastShape.SOUTHEAST;
        if (landS && landE && !landN && !landW) return CoastShape.NORTHWEST;
        if (landS && landW && !landN && !landE) return CoastShape.NORTHEAST;

        // --- 4. RISOLUZIONE DELLE SPONDE DRITTE ---
        // Manteniamo l'inversione speculare
        if (!landS && landN) return CoastShape.SOUTH;
        if (!landN && landS) return CoastShape.NORTH;
        if (!landW && landE) return CoastShape.WEST;
        if (!landE && landW) return CoastShape.EAST;

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

        // --- 5. RENDER DELLE ENTITÀ (Y-SORTED) ---
        renderYAlignedEntities(gc, cameraX, cameraY);

        // --- 6. RENDER DEL METEO (NUOVO) ---
        // Assumendo che il Model esponga il meteo attuale. Adatta il getter se ha un nome diverso.
        Weather currentWeather = model.getCurrentWeather();
        if (currentWeather != null) {
            renderWeather(gc, currentWeather, screenWidth, screenHeight);
        }

        // --- 7. RENDER GIORNO/NOTTE (NUOVO) ---
        renderTimeOfDay(gc, screenWidth, screenHeight);
    }

    private void renderWeather(GraphicsContext gc, Weather currentWeather, double screenWidth, double screenHeight) {
        if (currentWeather == Weather.SUNNY) {
            return; // Nessun effetto
        }

        // 1. GESTIONE DEL VELO (OVERLAY)
        Color overlayColor = Color.TRANSPARENT;

        switch (currentWeather) {
            case CLOUDY -> overlayColor = Color.rgb(20, 20, 30, 0.2); // Grigino leggero
            case RAINY -> overlayColor = Color.rgb(10, 10, 40, 0.4);  // Bluastro scuro
            case STORMY -> overlayColor = Color.rgb(0, 0, 20, 0.6);   // Molto scuro
        }

        gc.setFill(overlayColor);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // 2. GESTIONE DELLE PARTICELLE (Solo Pioggia/Tempesta)
        if (currentWeather == Weather.RAINY || currentWeather == Weather.STORMY) {

            gc.setStroke(Color.rgb(150, 150, 255, 0.6)); // Colore della pioggia

            // In tempesta la pioggia è più spessa e veloce
            double speedMultiplier = (currentWeather == Weather.STORMY) ? 1.8 : 1.0;
            gc.setLineWidth((currentWeather == Weather.STORMY) ? 2.0 : 1.0);

            // Inclinazione del vento (opzionale, ma dà un bell'effetto)
            double windDrift = (currentWeather == Weather.STORMY) ? -4.0 : -1.0;

            for (RainDrop drop : rainDrops) {
                // Aggiorna posizione
                drop.y += drop.speed * speedMultiplier;
                drop.x += windDrift;

                // Se la goccia esce dallo schermo, la resettiamo in alto
                if (drop.y > screenHeight || drop.x < 0) {
                    drop.reset(screenWidth, screenHeight);
                    // drop.x = Math.random() * screenWidth - windDrift * 50; // --- CORREZIONE: RIMOSSA RIGA PROBLEMÁTICA ---
                }

                // Disegna la linea della goccia
                gc.strokeLine(drop.x, drop.y, drop.x - windDrift, drop.y + drop.length);
            }
        }
    }

    /**
     * Applica un filtro visivo in base all'orario per simulare l'illuminazione.
     */
    private void renderTimeOfDay(GraphicsContext gc, double screenWidth, double screenHeight) {
        TimeOfDay currentCycle = model.getTimeOfDay();

        if (currentCycle == TimeOfDay.NIGHT) {
            // Un blu scuro con alpha al 55% per oscurare la scena senza nasconderla del tutto
            gc.setFill(Color.rgb(5, 10, 30, 0.55));
            gc.fillRect(0, 0, screenWidth, screenHeight);
        }
    }

    private void renderYAlignedEntities(GraphicsContext gc, double cameraX, double cameraY) {
        List<RenderableEntity> renderQueue = new ArrayList<>();

        // 1. Aggiungiamo i Props alla coda di rendering
        for (int col = 0; col < model.getMaxColumns(); col++) {
            for (int row = 0; row < model.getMaxRows(); row++) {

                Props currentProp = model.getPropAt(col, row);

                if (currentProp == Props.PALM) {
                    double palmWorldX = col * tileSize;
                    double palmWorldY = row * tileSize;
                    double drawX = palmWorldX - cameraX;
                    double drawY = palmWorldY - cameraY - tileSize;

                    // --- INIZIO NUOVA LOGICA ---
                    // Generiamo un numero pseudo-casuale stabile basato sulle coordinate usando numeri primi.
                    // Moltiplicare per 73 e 31 (numeri primi) evita che caselle adiacenti abbiano pattern troppo ripetitivi.
                    int hash = (col * 73 + row * 31);

                    // Usiamo il modulo 2 per decidere: pari = texture 1, dispari = texture 2
                    Image selectedPalm = (hash % 2 == 0) ? palmTexture1 : palmTexture2;
                    // --- FINE NUOVA LOGICA ---

                    // Aggiungiamo alla coda l'immagine selezionata (se è stata caricata correttamente)
                    if (selectedPalm != null) {
                        renderQueue.add(new RenderableEntity(selectedPalm, drawX, drawY, palmWorldY));
                    }
                }
            }
        }

        // 2. Aggiungiamo il Giocatore alla coda di rendering
        Player player = model.getPlayer();
        double playerWorldX = player.getCol() * tileSize;
        double playerWorldY = player.getRow() * tileSize;
        double playerDrawX = playerWorldX - cameraX;
        double playerDrawY = playerWorldY - cameraY;

        Image playerSprite = model.getPlayer().isFishing() ? this.playerFishing :
                switch (player.getCurrentDirection()) {
                    case UP -> this.playerUp;
                    case DOWN -> this.playerDown;
                    case LEFT -> this.playerLeft;
                    case RIGHT -> this.playerRight;
                };

        // La profondità del giocatore è la sua coordinata Y nel mondo
        renderQueue.add(new RenderableEntity(playerSprite, playerDrawX, playerDrawY, playerWorldY));

        // 3. Ordiniamo la coda (Y-Sorting)
        Collections.sort(renderQueue);

        // 4. Disegniamo tutto nell'ordine corretto
        for (RenderableEntity entity : renderQueue) {
            gc.drawImage(entity.sprite(), entity.drawX(), entity.drawY());
        }
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