package com.jakione.bluehorizon.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class GameView {
    private final Canvas canvas;
    private final GraphicsContext gc;

    // Costruttore: prepara la tela quando la classe viene istanziata
    public GameView(double width, double height) {
        this.canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
    }

    // Metodo pubblico per ottenere il Canvas e metterlo nella finestra
    public Canvas getCanvas() {
        return canvas;
    }

    // Metodo dedicato al disegno
    public void render() {
        // Disegna l'oceano
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Disegna la barca fittizia
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(368, 268, 64, 64);
    }
}