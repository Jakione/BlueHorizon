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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

/**
 * Gestisce l'interfaccia utente (HUD) in sovrimpressione al gioco.
 * Mantiene la logica della GUI separata dal rendering del mondo.
 */
public class GameHUD extends BorderPane implements GameObserver {

    private final GameModel model;

    // Elementi dinamici della UI
    private Label timeLabel;
    private Label weatherLabel;
    private Label bottomModifierLabel;
    private Label caughtLabel; // Predisposizione per l'inventario

    // Caching dello stato per ottimizzare i rendering
    private Weather lastWeather;
    private TimeOfDay lastTimeOfDay;

    // Palette Colori (Nero all'85% di opacità, senza bordi)
    private final String BG_COLOR = "rgba(0, 0, 0, 1)";
    private final String TEXT_MUTED = "#a0aabf";
    private final String TEXT_HIGHLIGHT = "#ffffff";
    private final String PILL_BG = "rgba(255, 255, 255, 0.15)";

    public GameHUD(GameModel model) {
        this.model = model;

        // L'HUD generale è trasparente (lascia vedere il gioco in mezzo)
        this.setStyle("-fx-background-color: transparent;");
        this.setPickOnBounds(false);

        buildTopBar();
        buildBottomBar();

        updateUI();
    }

    private void buildTopBar() {
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));

        // Sfondo nero semitrasparente, nessun bordo
        topBar.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- SINISTRA: Titolo ---
        Label titleLabel = new Label("🎣 BLUE HORIZON");
        titleLabel.setStyle("-fx-text-fill: #92a8d1; -fx-font-size: 18px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");

        // --- MENU A TENDINA ---
        MenuButton menuButton = new MenuButton();
        menuButton.setFocusTraversable(false);
        menuButton.setStyle("-fx-background-color: " + PILL_BG + ";" +
                "-fx-background-radius: 5;");

        // Creiamo una Label dedicata per aggirare il blocco del colore di JavaFX
        Label menuLabel = new Label("☰ Menu");
        menuLabel.setStyle("-fx-text-fill: " + TEXT_HIGHLIGHT + "; -fx-font-weight: bold;");
        menuButton.setGraphic(menuLabel); // Impostiamo la label come contenuto del bottone

        MenuItem inventoryItem = new MenuItem("🎒 Inventario");
        MenuItem registryItem = new MenuItem("📖 Registro Catture");
        MenuItem saveItem = new MenuItem("💾 Salva Partita");

        // Gestione degli eventi del menu (da collegare poi al Controller/Model)
        inventoryItem.setOnAction(e -> openInventoryView());
        registryItem.setOnAction(e -> openRegistryView());
        saveItem.setOnAction(e -> triggerSaveGame());

        menuButton.getItems().addAll(inventoryItem, registryItem, saveItem);

        // Spaziatore elastico 1
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // --- CENTRO: Pillola Meteo ---
        weatherLabel = new Label("☁ --");
        weatherLabel.setStyle("-fx-background-color: " + PILL_BG + ";" +
                "-fx-text-fill: " + TEXT_HIGHLIGHT + ";" +
                "-fx-padding: 5 20 5 20;" +
                "-fx-background-radius: 15;" +
                "-fx-font-size: 14px;");

        // Spaziatore elastico 2
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // --- DESTRA: Statistiche e Tempo ---
        HBox rightStats = new HBox(15);
        rightStats.setAlignment(Pos.CENTER_RIGHT);

        caughtLabel = new Label("Pescati: 0");
        caughtLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px;");

        timeLabel = new Label("⏱ --:--");
        timeLabel.setStyle("-fx-text-fill: " + TEXT_HIGHLIGHT + "; -fx-font-size: 15px; -fx-font-weight: bold;");

        rightStats.getChildren().addAll(caughtLabel, timeLabel);

        // Assembliamo la barra superiore includendo il menu
        topBar.getChildren().addAll(titleLabel, menuButton, spacer1, weatherLabel, spacer2, rightStats);
        this.setTop(topBar);
    }

    private void openInventoryView() {
        // Estraiamo l'inventario in sola lettura dal Player tramite il Model
        // e lo passiamo alla nuova finestra grafica
        InventoryView.show(model.getPlayer().getInventory());
    }

    private void openRegistryView() {
        // Mostriamo la finestra del registro passandogli i dati di dominio
        RegistryView.show(model.getPlayer().getCatchRegistry());
    }

    private void triggerSaveGame() {
        System.out.println("Richiesta di salvataggio inoltrata...");
    }

    private void buildBottomBar() {
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(8, 20, 8, 20));

        // Sfondo nero semitrasparente, nessun bordo
        bottomBar.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- SINISTRA: Info Canna (Placeholder) ---
        Label toolLabel = new Label("🎣 Canna selezionata: Canna Leggera");
        toolLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        // Spaziatore elastico
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- DESTRA: Modificatore Meteo ---
        bottomModifierLabel = new Label();
        bottomModifierLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        bottomBar.getChildren().addAll(toolLabel, spacer, bottomModifierLabel);
        this.setBottom(bottomBar);
    }

    /**
     * Aggiorna i testi leggendo lo stato attuale dal Model.
     */
    private void updateUI() {
        // Uso di Simboli Unicode Base per evitare i quadratini di JavaFX
        if (model.getTimeOfDay() != null) {
            String icon = (model.getTimeOfDay() == TimeOfDay.DAY) ? "☀ " : "☾ ";
            timeLabel.setText(icon + model.getTimeOfDay().getDescription());
        }

        // Aggiornamento Meteo
        if (model.getCurrentWeather() != null) {
            Weather w = model.getCurrentWeather();

            // Simboli Unicode Base (senza Variation Selectors)
            String weatherIcon = switch (w) {
                case SUNNY -> "☀ ";
                case CLOUDY -> "☁ ";
                case RAINY -> "☂ ";
                case STORMY -> "☈ ";
            };
            weatherLabel.setText(weatherIcon + w.getDescription());

            // Calcolo dinamico del modificatore percentuale
            double mod = w.getBaseCatchModifier();
            int percentage = (int) Math.round((mod - 1.0) * 100);

            String sign = (percentage >= 0) ? "+" : "";
            String colorCSS = (percentage >= 0) ? "#4caf50" : "#f44336";

            bottomModifierLabel.setGraphic(buildColoredModifierText("Meteo: ", sign + percentage + "%", " probabilità", colorCSS));
            bottomModifierLabel.setText("");
        }
    }

    /**
     * Helper per creare un testo con parti colorate in modo diverso all'interno della stessa Label.
     */
    private TextFlow buildColoredModifierText(String prefix, String coloredValue, String suffix, String colorCSS) {
        Text t1 = new Text(prefix);
        t1.setStyle("-fx-fill: " + TEXT_MUTED + ";");

        Text t2 = new Text(coloredValue);
        t2.setStyle("-fx-fill: " + colorCSS + "; -fx-font-weight: bold;");

        Text t3 = new Text(suffix);
        t3.setStyle("-fx-fill: " + TEXT_MUTED + ";");

        return new TextFlow(t1, t2, t3);
    }

    @Override
    public void onGameStateUpdated() {
        Weather currentWeather = model.getCurrentWeather();
        TimeOfDay currentTimeOfDay = model.getTimeOfDay();
        boolean hasChanged = false;

        if (currentWeather != lastWeather) {
            lastWeather = currentWeather;
            hasChanged = true;
        }

        if (currentTimeOfDay != lastTimeOfDay) {
            lastTimeOfDay = currentTimeOfDay;
            hasChanged = true;
        }

        if (hasChanged) {
            javafx.application.Platform.runLater(this::updateUI);
        }
    }
}