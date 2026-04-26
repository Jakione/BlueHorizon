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

    // Flag di stato del movimento (modificati dall'esterno tramite input utente)
    private boolean movingUp;
    private boolean movingDown;
    private boolean movingLeft;
    private boolean movingRight;

    /**
     * Inizializza il giocatore in una posizione di partenza.
     */
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.speed = 4.0; // Velocità logica di base
    }

    /**
     * Aggiorna le coordinate del giocatore in base ai flag di movimento attivi.
     * Questo metodo verrà chiamato unicamente dal GameEngine.
     */
    public void updatePosition() {
        if (movingUp) {
            y -= speed;
        }
        if (movingDown) {
            y += speed;
        }
        if (movingLeft) {
            x -= speed;
        }
        if (movingRight) {
            x += speed;
        }
    }

    // --- GETTER (Per la View che deve sapere dove disegnare la barca) ---
    public double getX() { return x; }
    public double getY() { return y; }

    // --- SETTER (Per il Controller/View che intercetta i tasti WASD/Frecce) ---
    public void setMovingUp(boolean movingUp) { this.movingUp = movingUp; }
    public void setMovingDown(boolean movingDown) { this.movingDown = movingDown; }
    public void setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }
}