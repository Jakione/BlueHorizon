package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.fish.CatchRecord;
import com.jakione.bluehorizon.model.fish.CatchRegistry;
import com.jakione.bluehorizon.model.fish.FishSpecies;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Gestisce la visualizzazione interna del registro delle catture.
 * Condivide lo stesso stile dell'InventoryView per coerenza grafica all'interno dell'HUD.
 */
public class RegistryView {

    /**
     * Costruisce il nodo del registro catture da inserire al centro dell'HUD.
     * @param registry Il registro di dominio del giocatore.
     * @param onClose Callback per notificare la chiusura dell'overlay.
     * @return Il nodo VBox stilizzato.
     */
    public static VBox build(CatchRegistry registry, Runnable onClose) {
        // Semplificazione: usiamo direttamente VBox come radice (Root) dello schermo del registro
        VBox root = new VBox(25); // Spazio verticale costante tra i componenti interni
        root.setPadding(new Insets(30, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);

        // APPLICAZIONE DELLO STILE UNIFORME (Sfondo scuro 95% opacità, contorno azzurro, angoli smussati a 10px)
        root.setStyle("-fx-background-color: rgba(26, 26, 29, 0.95); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #92a8d1; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Manteniamo le proporzioni verticali adatte al compendio
        root.setMaxSize(400, 550);

        // Titolo della sezione (allineato cromaticamente all'azzurro dell'HUD)
        Label titleLabel = new Label("COMPENDIO DEI MARI");
        titleLabel.setStyle("-fx-text-fill: #92a8d1; -fx-font-size: 18px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
        root.getChildren().add(titleLabel);

        // Estrazione e popolamento dei record di gioco
        Map<FishSpecies, CatchRecord> records = registry.getRecords();
        root.getChildren().add(buildFishRow(records.get(FishSpecies.ECLISSI_DI_CORALLO), "Eclissi di Corallo", "/Fishes/Eclissi_di_corallo.png"));
        root.getChildren().add(buildFishRow(records.get(FishSpecies.GUARDIANO_DEL_LEVIATANO), "Guardiano del Leviatano", "/Fishes/Guardiano_del_leviatano.png"));
        root.getChildren().add(buildFishRow(records.get(FishSpecies.RE_DI_GHIACCIO), "Re di Ghiaccio", "/Fishes/Re_di_ghiaccio.png"));

        // Pulsante di chiusura (identico nel comportamento e nello stile a quello dell'inventario)
        Button closeBtn = new Button("Chiudi");
        closeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> onClose.run());

        // Un piccolo margine superiore per staccare il bottone dall'ultima riga dei pesci
        VBox.setMargin(closeBtn, new Insets(15, 0, 0, 0));
        root.getChildren().add(closeBtn);

        return root;
    }

    /**
     * Costruisce la singola riga contenente Sprite, Nome e Grandezza Massima.
     */
    private static HBox buildFishRow(CatchRecord record, String displayName, String imagePath) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        // Uno sfondo nero molto sfumato per far risaltare le singole righe all'interno del pannello principale
        row.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-padding: 10; -fx-background-radius: 5;");

        ImageView fishSprite = new ImageView();
        try {
            Image image = new Image(RegistryView.class.getResourceAsStream(imagePath));
            fishSprite.setImage(image);
            fishSprite.setFitWidth(64);
            fishSprite.setFitHeight(32);
            fishSprite.setSmooth(false); // Mantiene l'effetto Pixel Art nitido
        } catch (Exception e) {
            System.err.println("Impossibile caricare sprite: " + imagePath);
        }

        VBox textContainer = new VBox(5);

        Label nameLabel = new Label();
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label statsLabel;

        if (record == null || !record.isCaught()) {
            ColorAdjust silhouette = new ColorAdjust();
            silhouette.setBrightness(-1.0); // Trasforma lo sprite in una sagoma nera
            fishSprite.setEffect(silhouette);

            nameLabel.setText("???");
            statsLabel = new Label("Ancora da scoprire...");
            statsLabel.setStyle("-fx-text-fill: #a0aabf; -fx-font-style: italic;");
        } else {
            fishSprite.setEffect(null);
            nameLabel.setText(displayName);

            String formattedWeight = String.format("%.2f kg", record.getMaxWeight());
            String formattedLength = String.format("%.2f cm", record.getRecordLength());

            statsLabel = new Label("Record: " + formattedWeight + " - " + formattedLength + " | Tot: " + record.getTotalCaught());
            statsLabel.setStyle("-fx-text-fill: white;");
        }

        textContainer.getChildren().addAll(nameLabel, statsLabel);
        row.getChildren().addAll(fishSprite, textContainer);

        return row;
    }
}