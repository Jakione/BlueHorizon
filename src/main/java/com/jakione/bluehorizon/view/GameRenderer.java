package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.model.GameObserver;
import com.jakione.bluehorizon.model.Player;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderer extends Canvas implements GameObserver {

    private final int originalTileSize = 16;
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale;

    private final GameModel model;

    public GameRenderer(GameModel model) {
        this.model = model;

        int screenWidth = tileSize * model.getMaxColumns();
        int screenHeight = tileSize * model.getMaxRows();

        this.setWidth(screenWidth);
        this.setHeight(screenHeight);

        // Eseguiamo un primo render manuale per colorare lo sfondo all'avvio
        render();
    }

    /**
     * Questo metodo viene chiamato dal GameEngine (Thread del motore).
     * Usiamo Platform.runLater per delegare il disegno al Thread di JavaFX.
     */
    @Override
    public void onGameStateUpdated() {
        Platform.runLater(() -> render());
    }

    private void render() {
        GraphicsContext gc = this.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Recupera i dati logici aggiornati
        Player player = model.getPlayer();

        // Disegna il giocatore (quadratino bianco) alle coordinate correnti
        gc.setFill(Color.WHITE);
        gc.fillRect(player.getX(), player.getY(), tileSize, tileSize);

        // Qui in futuro disegneremo la mappa, il mare, la barca...
    }
}