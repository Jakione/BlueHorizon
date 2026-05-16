package com.jakione.bluehorizon.model.fish;

/**
 * Classe base che racchiude lo stato e il comportamento comune a tutti i pesci.
 * Implementa l'interfaccia Fish per ridurre il boilerplate nelle sottoclassi.
 */
public abstract class AbstractFish implements Fish {

    protected final FishSpecies species; // <-- NUOVO: Riferimento alla specie (Enum)
    protected final String name;
    protected final double weight;
    protected final double length;

    /**
     * Costruttore protetto, invocabile solo dalle classi figlie.
     */
    protected AbstractFish(FishSpecies species, String name, double weight, double length) {
        this.species = species; // <-- Salviamo la specie
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

    @Override
    public FishSpecies getFishSpecies() {
        return this.species; // <-- RISOLTO: Ora restituisce l'enum corretta e non più null!
    }
}