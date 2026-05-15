package com.jakione.bluehorizon.model;

public enum Props {
    NONE(0),   // Nessun oggetto in questa cella
    PALM(1);

    private final int id;

    Props(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Props fromId(int id) {
        for (Props prop : values()) {
            if (prop.id == id) {
                return prop;
            }
        }
        return NONE; // Default di sicurezza: cella vuota, non palma!
    }
}