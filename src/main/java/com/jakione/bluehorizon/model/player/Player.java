package com.jakione.bluehorizon.model.player;

import com.jakione.bluehorizon.model.fish.CatchRegistry;
import com.jakione.bluehorizon.model.fishing.BasicRod;
import com.jakione.bluehorizon.model.fishing.FishingGear;
import com.jakione.bluehorizon.model.inventory.Inventory;
import com.jakione.bluehorizon.model.inventory.RodType;

public class Player {

    private int col;
    private int row;
    private int animationFrame = 0;
    private Direction currentDirection = Direction.DOWN;
    private final CatchRegistry catchRegistry = new CatchRegistry();

    private FishingGear equippedGear;
    private boolean isFishing = false;

    private final Inventory inventory;

    public Player(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
        this.inventory = new Inventory();

        equipRod(RodType.BASIC);
    }

    /**
     * Equipaggia una canna solo se presente nell'inventario.
     * Istanzia il polimorfismo corretto di FishingGear.
     */
    public void equipRod(RodType rodType) {
        if (inventory.hasRod(rodType)) {
            switch (rodType) {
                case BASIC -> this.equippedGear = new BasicRod();
                // Qui in futuro aggiungerai: case ADVANCED -> this.equippedGear = new AdvancedRod();
            }
        }
    }

    public void updatePosition(Direction direction) {
        this.currentDirection = direction;
        switch (direction) {
            case UP -> row--;
            case DOWN -> row++;
            case LEFT -> col--;
            case RIGHT -> col++;
        }
        this.animationFrame = (this.animationFrame + 1) % 2;
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
    public void setCol(int col) { this.col = col; }
    public void setRow(int row) { this.row = row; }
    public int getAnimationFrame() { return animationFrame; }
    public Direction getCurrentDirection() { return currentDirection; }
    public FishingGear getEquippedGear() { return equippedGear; }
    public boolean isFishing() { return isFishing; }
    public void setFishing(boolean fishing) { isFishing = fishing; }

    public CatchRegistry getCatchRegistry() {
        return catchRegistry;
    }
    public Inventory getInventory() { return inventory; }
}