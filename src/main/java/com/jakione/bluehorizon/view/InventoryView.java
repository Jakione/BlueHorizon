package com.jakione.bluehorizon.view;

import com.jakione.bluehorizon.model.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
     * Costruisce il nodo dell'inventario da inserire nell'HUD.
     * @param inventory L'inventario del giocatore.
     * @param onClose Azione da eseguire quando si preme il tasto chiudi.
     * @return Il nodo radice VBox dell'inventario.
     */
    public static VBox build(Inventory inventory, Runnable onClose) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        // Aggiungiamo un bordo arrotondato e un'ombra per distaccarlo dal gioco
        root.setStyle("-fx-background-color: rgba(26, 26, 29, 0.95); -fx-background-radius: 10; -fx-border-color: #92a8d1; -fx-border-radius: 10; -fx-border-width: 2;");
        root.setMaxSize(350, 400); // Evita che si espanda per tutto lo schermo

        Label rodsTitle = new Label("🎣 Canne da Pesca");
        rodsTitle.setStyle("-fx-text-fill: #92a8d1; -fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(rodsTitle);

        inventory.getRods().forEach((rod, qty) -> {
            Label itemLabel = new Label("• " + rod.getDisplayName());
            itemLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            root.getChildren().add(itemLabel);
        });

        Label spacer = new Label(" ");
        root.getChildren().add(spacer);

        Label usablesTitle = new Label("🎒 Oggetti Utilizzabili");
        usablesTitle.setStyle("-fx-text-fill: #92a8d1; -fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(usablesTitle);

        inventory.getUsables().forEach((item, qty) -> {
            Label itemLabel = new Label("• " + item.getDisplayName() + " (Quantità: " + qty + ")");
            itemLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            root.getChildren().add(itemLabel);
        });

        // Pulsante di chiusura
        Button closeBtn = new Button("Chiudi");
        closeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> onClose.run());

        VBox.setMargin(closeBtn, new Insets(20, 0, 0, 0));
        root.getChildren().add(closeBtn);
        root.setAlignment(Pos.TOP_CENTER);

        return root;
    }
}