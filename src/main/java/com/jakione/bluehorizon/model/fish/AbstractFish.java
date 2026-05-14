package com.jakione.bluehorizon.model.fish;

/**
 * Classe base che racchiude lo stato e il comportamento comune a tutti i pesci.
 * Implementa l'interfaccia Fish per ridurre il boilerplate nelle sottoclassi.
 */
public abstract class AbstractFish implements Fish {

    protected final String name;
    protected final double weight;
    protected final double length;

    /**
     * Costruttore protetto, invocabile solo dalle classi figlie.
     */
    protected AbstractFish(String name, double weight, double length) {
        this.name = name;
        this.weight = weight;
        this.length = length;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public double getLength() {
        return this.length;
    }
}