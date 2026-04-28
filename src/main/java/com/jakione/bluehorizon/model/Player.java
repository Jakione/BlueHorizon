package com.jakione.bluehorizon.model;

/**
 * Rappresenta l'entità del giocatore.
 * Utilizza coordinate intere per identificare la posizione sulla griglia logica.
 */
public class Player {

    private int col;
    private int row;

    public Player(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
    }

    /**
     * Modifica la posizione logica sulla griglia.
     * La validazione dei confini viene gestita dal GameModel.
     */
    public void updatePosition(Direction direction) {
        switch (direction) {
            case UP -> row--;
            case DOWN -> row++;
            case LEFT -> col--;
            case RIGHT -> col++;
        }
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
}