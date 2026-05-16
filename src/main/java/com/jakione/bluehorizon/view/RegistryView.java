package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.fish.CatchRecord;
import com.jakione.bluehorizon.model.fish.CatchRegistry;
import com.jakione.bluehorizon.model.fish.FishSpecies;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Finestra modale che mostra il registro delle catture con le relative sagome.
 */
public class RegistryView {

    public static void show(CatchRegistry registry) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Registro Catture");

        StackPane root = new StackPane(); // Permette di sovrapporre UI allo sfondo

        // --- 1. CARICAMENTO DELLO SFONDO ---
        try {
            Image bgImage = new Image(RegistryView.class.getResourceAsStream("/InventoryBackground/vertical.png"));
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(400);
            bgView.setFitHeight(550);
            root.getChildren().add(bgView);
        } catch (Exception e) {
            System.err.println("Impossibile caricare lo sfondo del registro.");
        }

        // --- 2. CONTENUTO DEL REGISTRO ---
        VBox contentBox = new VBox(25); // Spazio verticale tra un pesce e l'altro
        contentBox.setPadding(new Insets(40, 20, 20, 20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("COMPENDIO DEI MARI");
        titleLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 18px; -fx-font-weight: bold;");
        contentBox.getChildren().add(titleLabel);

        // Generiamo dinamicamente le righe per i tre pesci (aggiorna i nomi nell'Enum se serve)
        Map<FishSpecies, CatchRecord> records = registry.getRecords();

        contentBox.getChildren().add(buildFishRow(records.get(FishSpecies.ECLISSI_DI_CORALLO), "Eclissi di Corallo", "/Fishes/Eclissi_di_corallo.png"));
        contentBox.getChildren().add(buildFishRow(records.get(FishSpecies.GUARDIANO_DEL_LEVIATANO), "Guardiano del Leviatano", "/Fishes/Guardiano_del_leviatano.png"));
        contentBox.getChildren().add(buildFishRow(records.get(FishSpecies.RE_DI_GHIACCIO), "Re di Ghiaccio", "/Fishes/Re_di_ghiaccio.png"));

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 400, 550);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Costruisce la singola riga contenente Sprite, Nome e Grandezza Massima.
     */
    /**
     * Costruisce la singola riga contenente Sprite, Nome e Grandezza Massima.
     */
    private static HBox buildFishRow(CatchRecord record, String displayName, String imagePath) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 10; -fx-background-radius: 5;");

        // --- CARICAMENTO SPRITE PIXEL ART ---
        ImageView fishSprite = new ImageView();
        try {
            Image image = new Image(RegistryView.class.getResourceAsStream(imagePath));
            fishSprite.setImage(image);
            fishSprite.setFitWidth(64);
            fishSprite.setFitHeight(32);
            fishSprite.setSmooth(false);
        } catch (Exception e) {
            System.err.println("Impossibile caricare sprite: " + imagePath);
        }

        VBox textContainer = new VBox(5);

        // Creiamo la Label del nome vuota per ora, decideremo sotto cosa scriverci
        Label nameLabel = new Label();
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label statsLabel;

        // --- LOGICA SILHOUETTE (SAGOMA NERA E NOME SEGRETO) ---
        if (record == null || !record.isCaught()) {
            // Oscura l'immagine
            ColorAdjust silhouette = new ColorAdjust();
            silhouette.setBrightness(-1.0);
            fishSprite.setEffect(silhouette);

            // Nasconde il nome del pesce
            nameLabel.setText("???");

            statsLabel = new Label("Ancora da scoprire...");
            statsLabel.setStyle("-fx-text-fill: #a0aabf; -fx-font-style: italic;");
        } else {
            // Immagine a colori
            fishSprite.setEffect(null);

            // Mostra il vero nome del pesce
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