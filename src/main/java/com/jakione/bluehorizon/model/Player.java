package com.jakione.bluehorizon.model;

/**
 * Rappresenta l'entità del giocatore (la barca) all'interno del mondo logico.
 * Gestisce la posizione, la velocità e lo stato del movimento.
 */
public class Player {

    // Coordinate logiche (non sono pixel fissi a schermo, ma unità di spazio del mondo)
    private double x;
    private double y;

    // Velocità di spostamento (unità per tick logico)
    private final double speed;

    /**
     * Inizializza il giocatore in una posizione di partenza.
     */
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.speed = 48; // Velocità logica di base
    }

    /**
     * Aggiorna le coordinate del giocatore in base ai flag di movimento attivi.
     * Questo metodo verrà chiamato unicamente dal GameEngine.
     */
    public void updatePosition(Direction direction) {
        if (direction == Direction.UP) {
            y -= speed;
        }
        if (direction == Direction.DOWN) {
            y += speed;
        }
        if (direction == Direction.LEFT) {
            x -= speed;
        }
        if (direction == Direction.RIGHT) {
            x += speed;
        }
    }
    // --- GETTER (Per la View che deve sapere dove disegnare la barca) ---
    public double getX() { return x; }
    public double getY() { return y; }
}