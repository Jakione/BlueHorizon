package com.jakione.bluehorizon;

import com.jakione.bluehorizon.controller.GameEngine;
import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.view.GameRenderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Inizializza il Core Logico
        GameModel model = new GameModel();

        // 2. Inizializza il Core Grafico
        GameRenderer renderer = new GameRenderer(model);

        // 3. Inizializza il Motore (Controller)
        GameEngine engine = new GameEngine(model);

        // 4. Collega l'Engine alla View (Pattern Observer)
        engine.addObserver(renderer);

        // 5. Configura e mostra la finestra JavaFX
        Group root = new Group(renderer);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Blue Horizon");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Blocca il ridimensionamento della finestra
        primaryStage.show();

        // 6. Accendi il motore!
        engine.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


