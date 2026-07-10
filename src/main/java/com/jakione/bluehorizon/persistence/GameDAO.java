package com.jakione.bluehorizon.persistence;

import com.jakione.bluehorizon.model.GameModel;

/**
 * Contratto astratto per la persistenza dei dati di gioco.
 * Isola la logica di business dalle tecnologie di memorizzazione sottostanti.
 */
public interface GameDAO {

    /**
     * Salva lo stato corrente del modello di gioco (Giocatore, Inventario, Registro).
     * @param model Il modello globale da cui estrarre i dati.
     */
    void saveGame(GameModel model);

    /**
     * Carica i dati salvati ripristinando lo stato all'interno del modello.
     * @param model Il modello globale da aggiornare.
     * @return true se il caricamento ha avuto successo, false se non esistono salvataggi.
     */
    boolean loadGame(GameModel model);
}