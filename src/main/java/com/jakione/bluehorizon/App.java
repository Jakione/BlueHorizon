package com.jakione.bluehorizon;

import com.jakione.bluehorizon.controller.GameEngine;
import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.view.GameRenderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
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

        // Quando un tasto viene PREMUTO, accendiamo il flag nel Model
        scene.setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case W -> model.getPlayer().setMovingUp(true);
                case S -> model.getPlayer().setMovingDown(true);
                case A -> model.getPlayer().setMovingLeft(true);
                case D -> model.getPlayer().setMovingRight(true);
                default -> {} // Ignora altri tasti
            }
        });

        // Quando un tasto viene RILASCIATO, spegniamo il flag nel Model
        scene.setOnKeyReleased((KeyEvent event) -> {
            switch (event.getCode()) {
                case W -> model.getPlayer().setMovingUp(false);
                case S -> model.getPlayer().setMovingDown(false);
                case A -> model.getPlayer().setMovingLeft(false);
                case D -> model.getPlayer().setMovingRight(false);
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


