package com.jakione.bluehorizon.controller;


import com.jakione.bluehorizon.model.Direction;
import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.model.GameObserver;


import java.util.ArrayList;
import java.util.List;

/**
 * Core logico del videogioco. Agisce da Controller nel pattern MVC.
 * Implementa un Game Loop continuo su un thread dedicato, aggiornando
 * le meccaniche (pesca, meteo, buff) e notificando la View.
 */
public class GameEngine implements Runnable {

    private final GameModel model;
    private final List<GameObserver> observers;
    private Thread gameThread;

    private static final long MOVE_COOLDOWN_MS = 150; // 250ms di pausa tra una casella e l'altra
    private long lastMoveTime = 0;


    // Nelle fasi avanzate, useremo un delta-time per un loop più preciso
    private final int targetFPS = 60;

    /**
     * Inizializza il motore agganciandolo al modello dei dati.
     *
     */
    public GameEngine(GameModel model) {
        this.model = model;
        this.observers = new ArrayList<>();
    }


    public void handleMovementRequest(Direction direction) {
        long currentTime = System.currentTimeMillis();

        // Controlliamo se è passato abbastanza tempo dall'ultimo movimento
        if (currentTime - lastMoveTime >= MOVE_COOLDOWN_MS) {

            // Chiamata incapsulata: il controller non sa come si muove il player, lo chiede al modello
            model.movePlayer(direction);
            lastMoveTime = currentTime;
        }
    }


    /**
     * Registra un nuovo observer (tipicamente la View JavaFX).
     */
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Avvia il thread principale e segna il modello come in esecuzione.
     */
    public void startGame() {
        if (gameThread == null || !model.isRunning()) {
            model.setRunning(true);
            gameThread = new Thread(this, "BlueHorizon-EngineLoop");
            gameThread.start();
        }
    }

    /**
     * Il Game Loop vero e proprio.
     */
    @Override
    public void run() {
        // Intervallo in nanosecondi tra un aggiornamento e l'altro
        double drawInterval = 1000000000.0 / targetFPS;
        long lastTime = System.nanoTime();
        long currentTime;

        while (model.isRunning()) {
            currentTime = System.nanoTime();

            // Applichiamo la logica e notifichiamo solo se è passato il tempo necessario
            if (currentTime - lastTime >= drawInterval) {
                // 1. Aggiorna la posizione dei pesci che nuotano
                // model.updateFishes();

                // 2. Aggiorna il meteo o la durata dei buff/debuff
                // model.updateEnvironment();

                // 3. Notifica la View che il mondo è cambiato
                notifyObservers();

                lastTime = currentTime;
            }
        }
    }


    /**
     * Segnala a tutte le viste in ascolto che i dati sono mutati.
     */
    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onGameStateUpdated();
        }
    }
}