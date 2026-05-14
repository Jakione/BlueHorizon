package com.jakione.bluehorizon.model.player;

import com.jakione.bluehorizon.model.fishing.BasicRod;
import com.jakione.bluehorizon.model.fishing.FishingGear;

/**
 * Rappresenta l'entità del giocatore.
 * Utilizza coordinate intere per identificare la posizione sulla griglia logica.
 */
public class Player {

    private int col;
    private int row;
    private int animationFrame = 0;
    private Direction currentDirection = Direction.DOWN;
    private FishingGear equippedGear;
    private boolean isFishing = false;


    public Player(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
        this.equippedGear = new BasicRod();
    }

    /**
     * Modifica la posizione logica sulla griglia.
     * La validazione dei confini viene gestita dal GameModel.
     */
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
    public int getAnimationFrame() { return animationFrame; }
    public Direction getCurrentDirection() { return currentDirection; }
    public FishingGear getEquippedGear() { return equippedGear; }
    public boolean isFishing() { return isFishing; }
    public void setFishing(boolean fishing) { isFishing = fishing; }
}