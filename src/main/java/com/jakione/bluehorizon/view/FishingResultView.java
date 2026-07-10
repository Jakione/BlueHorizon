package com.jakione.bluehorizon.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Gestisce il pannello grafico di notifica per l'esito della pesca.
 * Mostra i dettagli del pesce catturato o un messaggio di fallimento.
 */
public class FishingResultView {

    /**
     * Costruisce il pannello del risultato della pesca.
     * * @param success Indica se la cattura è andata a buon fine.
     * @param fishName   Il nome del pesce catturato (può essere null o vuoto in caso di fallimento).
     * @param weight     Il peso del pesce catturato.
     * @param length     La lunghezza del pesce catturato.
     * @param onClose    Callback eseguita alla pressione del tasto di chiusura.
     * @return Un nodo VBox formattato e stilizzato.
     */
    public static VBox build(boolean success, String fishName, double weight, double length, Runnable onClose) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        // STILE UNIFORME: Stesso sfondo, bordi arrotondati e contorno azzurro dell'inventario
        root.setStyle("-fx-background-color: rgba(26, 26, 29, 0.95); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #92a8d1; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        root.setMaxSize(320, 240); // Dimensioni compatte adatte a una finestra di notifica

        Label titleLabel = new Label();
        Label detailLabel = new Label();

        if (success) {
            // Configurazione in caso di cattura riuscita (Testo verde di successo)
            titleLabel.setText("🎣 PESCA RIUSCITA!");
            titleLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-size: 16px; -fx-font-weight: bold;");

            String formattedStats = String.format("Hai pescato un:\n\n✨ %s ✨\n\nPeso: %.2f kg\nLunghezza: %.2f cm",
                    fishName, weight, length);

            detailLabel.setText(formattedStats);
            detailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-text-alignment: center;");
        } else {
            // Configurazione in caso di fallimento (Testo grigio smorzato)
            titleLabel.setText("🌊 ACQUA CHETA...");
            titleLabel.setStyle("-fx-text-fill: #a0aabf; -fx-font-size: 16px; -fx-font-weight: bold;");

            detailLabel.setText("Non hai pescato nulla questa volta!");
            detailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        }

        // Pulsante di chiusura per confermare la lettura e sbloccare il gioco
        Button closeBtn = new Button("Continua");
        closeBtn.setStyle("-fx-background-color: #92a8d1; -fx-text-fill: #1a1a1d; -fx-font-weight: bold; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> onClose.run());

        VBox.setMargin(closeBtn, new Insets(10, 0, 0, 0));

        root.getChildren().addAll(titleLabel, detailLabel, closeBtn);
        return root;
    }
}