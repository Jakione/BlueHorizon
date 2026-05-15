package com.jakione.bluehorizon;

import com.jakione.bluehorizon.controller.GameEngine;
import com.jakione.bluehorizon.model.player.Direction;
import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.view.GameHUD;
import com.jakione.bluehorizon.view.GameRenderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Inizializza il Core Logico
        GameModel model = new GameModel();

        // Inizializza i componenti grafici
        GameRenderer renderer = new GameRenderer(model);
        GameHUD hud = new GameHUD(model); // <-- 1. Istanzia l'HUD

        // Inizializza il Motore (Controller)
        GameEngine engine = new GameEngine(model);

        // Collega l'Engine alle View (Pattern Observer)
        engine.addObserver(renderer);
        engine.addObserver(hud); // <-- 2. Iscrivi l'HUD agli aggiornamenti del motore

        // Configura e mostra la finestra JavaFX
        // <-- 3. Usa lo StackPane per sovrapporre l'HUD al Canvas
        StackPane root = new StackPane();
        root.getChildren().addAll(renderer, hud); // Ordine cruciale: renderer sotto, hud sopra

        Scene scene = new Scene(root);

        // --- GESTIONE INPUT TASTIERA ---
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> engine.handleMovementRequest(Direction.UP);
                case S -> engine.handleMovementRequest(Direction.DOWN);
                case A -> engine.handleMovementRequest(Direction.LEFT);
                case D -> engine.handleMovementRequest(Direction.RIGHT);
                case ENTER -> engine.handleFishingRequest();
                default -> {}
            }
        });
        // -------------------------------

        primaryStage.setTitle("Blue Horizon");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        renderer.requestFocus();
        engine.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


