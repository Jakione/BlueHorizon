package com.jakione.bluehorizon.controller;


import com.jakione.bluehorizon.model.*;
import com.jakione.bluehorizon.model.player.Direction;


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
    private static final long WEATHER_CYCLE_MS = 3000; // Il meteo cambia ogni 30 secondi (per test)
    private long lastWeatherChange = System.currentTimeMillis();
    private long lastTimeCheck = 0;

    public GameEngine(GameModel model) {
        this.model = model;
        this.observers = new ArrayList<>();
    }

    private void updateEnvironment() {
        long now = System.currentTimeMillis();

        // 1. GESTIONE GIORNO/NOTTE REALE (Controllo ogni 5 secondi per non pesare sulla CPU)
        if (now - lastTimeCheck >= 5000) {
            updateRealTimeCycle();
            lastTimeCheck = now;
        }

        // 2. GESTIONE CICLO METEO PROBABILISTICO
        if (now - lastWeatherChange >= WEATHER_CYCLE_MS) {
            Weather nextWeather = Weather.getRandomWeather();
            model.setCurrentWeather(nextWeather);
            lastWeatherChange = now;
            System.out.println("Meteo cambiato in: " + nextWeather.getDescription());
        }
    }

    private void updateRealTimeCycle() {
        // Il controller non deve più sapere quali sono le ore di giorno o notte
        TimeOfDay targetPhase = TimeOfDay.getRealTimePhase();

        if (model.getTimeOfDay() != targetPhase) {
            model.setTimeOfDay(targetPhase);
            System.out.println("Fase oraria aggiornata: " + targetPhase.getDescription());
        }
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
                updateEnvironment();

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