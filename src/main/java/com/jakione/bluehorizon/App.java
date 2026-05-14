package com.jakione.bluehorizon;

import com.jakione.bluehorizon.controller.GameEngine;
import com.jakione.bluehorizon.model.player.Direction;
import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.view.GameRenderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Inizializza il Core Logico
        GameModel model = new GameModel();

        // Inizializza il Core Grafico
        GameRenderer renderer = new GameRenderer(model);

        // Inizializza il Motore (Controller)
        GameEngine engine = new GameEngine(model);

        // Collega l'Engine alla View (Pattern Observer)
        engine.addObserver(renderer);

        // Configura e mostra la finestra JavaFX
        Group root = new Group(renderer);
        Scene scene = new Scene(root);

        // --- GESTIONE INPUT TASTIERA ---

        // In App.java (La tua View)
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
        primaryStage.setResizable(false); // Blocca il ridimensionamento della finestra
        primaryStage.show();

        // Facciamo in modo che la finestra catturi subito gli input della tastiera
        renderer.requestFocus();

        // Accendi il motore!
        engine.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


