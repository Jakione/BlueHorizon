package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Gestisce la finestra grafica (View) dell'inventario.
 * È una finestra modale: blocca l'interazione con il gioco sottostante finché non viene chiusa.
 */
public class InventoryView {

    /**
     * Metodo statico per renderizzare e mostrare l'inventario a schermo.
     * @param inventory L'inventario del giocatore (sola lettura) estratto dal Model.
     */
    public static void show(Inventory inventory) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Blocca il gioco
        stage.setTitle("Bacheca Inventario");

        // Layout verticale per impilare gli oggetti
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1a1a1d;"); // Sfondo scuro elegante

        // Titolo sezione Canne
        Label rodsTitle = new Label("🎣 Canne da Pesca");
        rodsTitle.setStyle("-fx-text-fill: #92a8d1; -fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(rodsTitle);

        // Cicla la mappa delle canne e genera le label
        inventory.getRods().forEach((rod, qty) -> {
            Label itemLabel = new Label("• " + rod.getDisplayName());
            itemLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            root.getChildren().add(itemLabel);
        });

        // Spaziatore visivo
        Label spacer = new Label(" ");
        root.getChildren().add(spacer);

        // Titolo sezione Consumabili
        Label usablesTitle = new Label("🎒 Oggetti Utilizzabili");
        usablesTitle.setStyle("-fx-text-fill: #92a8d1; -fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(usablesTitle);

        // Cicla la mappa degli oggetti e genera le label
        inventory.getUsables().forEach((item, qty) -> {
            Label itemLabel = new Label("• " + item.getDisplayName() + " (Quantità: " + qty + ")");
            itemLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            root.getChildren().add(itemLabel);
        });

        // Configurazione e mostra finestra
        Scene scene = new Scene(root, 350, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}