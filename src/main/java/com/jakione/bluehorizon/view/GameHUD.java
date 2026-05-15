package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.model.GameObserver;
import com.jakione.bluehorizon.model.TimeOfDay;
import com.jakione.bluehorizon.model.Weather;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Gestisce l'interfaccia utente (HUD) in sovrimpressione al gioco.
 * Mantiene la logica della GUI separata dal rendering del mondo (GameRenderer).
 */
public class GameHUD extends BorderPane implements GameObserver {

    private final GameModel model;
    private Label timeLabel;
    private Label weatherLabel;
    private Weather lastWeather;
    private TimeOfDay lastTimeOfDay;

    public GameHUD(GameModel model) {
        this.model = model;

        // Rendiamo il BorderPane stesso trasparente per far vedere il Canvas sotto
        this.setStyle("-fx-background-color: transparent;");

        // Impediamo che l'HUD blocchi i click del mouse diretti al Canvas
        this.setPickOnBounds(false);

        buildTopBar();
        buildBottomBar();

        // Aggiornamento iniziale
        updateUI();
    }

    private void buildTopBar() {
        HBox topBar = new HBox(20); // Spazio di 20px tra gli elementi
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10, 20, 10, 20));

        // Stile CSS integrato per la barra: nero con il 60% di opacità (pseudo-trasparente) e testo bianco
        topBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        timeLabel = new Label("Ora: --");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        weatherLabel = new Label("Meteo: --");
        weatherLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        topBar.getChildren().addAll(timeLabel, weatherLabel);

        // Posizioniamo la barra in alto nel BorderPane
        this.setTop(topBar);
    }

    private void buildBottomBar() {
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(15, 20, 15, 20));

        // Stessa pseudo-trasparenza per l'inventario
        bottomBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        Label inventoryPlaceholder = new Label("Inventory (Empty)");
        inventoryPlaceholder.setStyle("-fx-text-fill: lightgray; -fx-font-size: 14px; -fx-font-style: italic;");

        bottomBar.getChildren().add(inventoryPlaceholder);

        // Posizioniamo la barra in basso nel BorderPane
        this.setBottom(bottomBar);
    }

    /**
     * Aggiorna i testi leggendo lo stato attuale dal Model.
     */
    private void updateUI() {
        if (model.getTimeOfDay() != null) {
            timeLabel.setText("TimeOfDay: " + model.getTimeOfDay().getDescription());
        }
        if (model.getCurrentWeather() != null) {
            weatherLabel.setText("Weather: " + model.getCurrentWeather().getDescription());
        }
    }

    @Override
    public void onGameStateUpdated() {
        // Leggiamo lo stato attuale
        Weather currentWeather = model.getCurrentWeather();
        TimeOfDay currentTimeOfDay = model.getTimeOfDay();

        // Verifichiamo se ci sono stati dei cambiamenti rispetto all'ultimo frame
        boolean hasChanged = false;

        if (currentWeather != lastWeather) {
            lastWeather = currentWeather;
            hasChanged = true;
        }

        if (currentTimeOfDay != lastTimeOfDay) {
            lastTimeOfDay = currentTimeOfDay;
            hasChanged = true;
        }

        // Deleghiamo l'aggiornamento alla UI di JavaFX SOLO se necessario
        if (hasChanged) {
            javafx.application.Platform.runLater(this::updateUI);
        }
    }
}
