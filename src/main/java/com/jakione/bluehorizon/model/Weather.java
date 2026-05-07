package com.jakione.bluehorizon.model;

/**
 * Rappresenta le condizioni meteorologiche del gioco.
 * Ogni condizione atmosferica porta con sé un modificatore base che influisce
 * sull'attività generale dei pesci e sulla difficoltà di pesca.
 */
public enum Weather {

    /**
     * Condizioni standard. Nessun bonus o malus.
     */
    SUNNY("Soleggiato", 1.0),

    /**
     * I pesci tendono ad avvicinarsi alla superficie. Leggero bonus.
     */
    CLOUDY("Nuvoloso", 1.1),

    /**
     * Ottime condizioni per pescare, l'acqua è mossa e i pesci sono attivi.
     */
    RAINY("Pioggia", 1.25),

    /**
     * Condizioni estreme. Molto difficile pescare, ma potrebbe far apparire pesci rari.
     * (Il malus generale è compensato dal fatto che alcuni pesci o strumenti
     * specifici potrebbero avere bonus immensi durante le tempeste).
     */
    STORMY("Tempesta", 0.7);

    private final String description;
    private final double baseCatchModifier;

    /**
     * Costruttore dell'enum (sempre privato di default in Java).
     *
     * @param description       Descrizione testuale per la UI.
     * @param baseCatchModifier Moltiplicatore base di probabilità.
     */
    Weather(String description, double baseCatchModifier) {
        this.description = description;
        this.baseCatchModifier = baseCatchModifier;
    }

    /**
     * @return La descrizione del meteo (utile per la UI senza usare name()).
     */
    public String getDescription() {
        return description;
    }

    /**
     * Restituisce il modificatore base di cattura. Questo valore verrà combinato
     * con il modificatore del FishingTool e le statistiche del pesce.
     *
     * @return Il moltiplicatore (es. 1.0 neutro, > 1.0 bonus, < 1.0 malus).
     */
    public double getBaseCatchModifier() {
        return baseCatchModifier;
    }
}