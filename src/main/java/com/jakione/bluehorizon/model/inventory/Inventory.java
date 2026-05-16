package com.jakione.bluehorizon.model.inventory;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Gestisce il possesso degli oggetti del giocatore.
 */
public class Inventory {
    private final Map<RodType, Integer> rods;
    private final Map<UsableItem, Integer> usables;

    public Inventory() {
        this.rods = new EnumMap<>(RodType.class);
        this.usables = new EnumMap<>(UsableItem.class);

        // --- SETUP INIZIALE DEL GIOCATORE ---
        addRod(RodType.BASIC, 1);
        addUsable(UsableItem.BASIC_BAIT, 5);
    }

    public void addRod(RodType type, int amount) {
        rods.put(type, rods.getOrDefault(type, 0) + 1);
    }

    public void addUsable(UsableItem item, int amount) {
        usables.put(item, usables.getOrDefault(item, 0) + amount);
    }

    /**
     * Tenta di consumare un oggetto.
     * @return true se l'oggetto era presente ed è stato consumato, false altrimenti.
     */
    public boolean consumeUsable(UsableItem item) {
        int count = usables.getOrDefault(item, 0);
        if (count > 0) {
            usables.put(item, count - 1);
            return true;
        }
        return false;
    }

    public boolean hasRod(RodType type) {
        return rods.getOrDefault(type, 0) > 0;
    }

    // Esponiamo mappe in SOLA LETTURA per la GUI (nessun rischio di manomissione dalla View)
    public Map<RodType, Integer> getRods() { return Collections.unmodifiableMap(rods); }
    public Map<UsableItem, Integer> getUsables() { return Collections.unmodifiableMap(usables); }
}