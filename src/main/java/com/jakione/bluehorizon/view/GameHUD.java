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
    private boolean fishingResultActive = false;

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

        timeLabel = new Label("⏱ --:--");
        timeLabel.setStyle("-fx-text-fill: " + TEXT_HIGHLIGHT + "; -fx-font-size: 15px; -fx-font-weight: bold;");

        rightStats.getChildren().addAll(timeLabel);

        // Assembliamo la barra superiore includendo il menu
        topBar.getChildren().addAll(titleLabel, menuButton, spacer1, weatherLabel, spacer2, rightStats);
        this.setTop(topBar);
    }

    private void openInventoryView() {
        // Costruiamo il nodo passando l'inventario e la lambda per la chiusura
        javafx.scene.Node inventoryNode = InventoryView.build(
                model.getPlayer().getInventory(),
                () -> this.setCenter(null) // Callback: svuota il centro alla chiusura
        );

        // Posizioniamo l'inventario al centro dell'HUD
        this.setCenter(inventoryNode);
    }

    private void openRegistryView() {
        // Costruiamo il nodo del compendio
        javafx.scene.Node registryNode = RegistryView.build(
                model.getPlayer().getCatchRegistry(),
                () -> this.setCenter(null)
        );

        // Posizioniamo il compendio al centro dell'HUD
        this.setCenter(registryNode);
    }

    private void triggerSaveGame() {
        System.out.println("Richiesta di salvataggio inoltrata...");
        // Istanziamo il DAO ed eseguiamo l'operazione sul modello
        com.jakione.bluehorizon.persistence.GameDAO dao = new com.jakione.bluehorizon.persistence.SQLiteGameDAO();
        dao.saveGame(model);
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

    /**
     * Verifica se vi è un overlay di menu attualmente visualizzato al centro dell'HUD.
     * @return true se un menu è aperto, false altrimenti.
     */
    public boolean isOverlayActive() {
        return this.getCenter() != null;
    }

    public void showFishingSuccess(String fishName, double weight, double length) {
        javafx.application.Platform.runLater(() -> {
            this.fishingResultActive = true; // Segnaliamo che è aperta la notifica
            javafx.scene.layout.VBox resultNode = FishingResultView.build(
                    true, fishName, weight, length, this::closeCurrentOverlay
            );
            this.setCenter(resultNode);
        });
    }

    public void showFishingFailure() {
        javafx.application.Platform.runLater(() -> {
            this.fishingResultActive = true; // Segnaliamo che è aperta la notifica
            javafx.scene.layout.VBox resultNode = FishingResultView.build(
                    false, null, 0, 0, this::closeCurrentOverlay
            );
            this.setCenter(resultNode);
        });
    }

    /**
     * Verifica se l'overlay attualmente aperto è la notifica di pesca.
     */
    public boolean isFishingResultActive() {
        return fishingResultActive;
    }

    /**
     * Metodo centralizzato per chiudere qualsiasi overlay e resettare lo stato.
     */
    public void closeCurrentOverlay() {
        this.setCenter(null);
        this.fishingResultActive = false;
    }
}