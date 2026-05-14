package com.jakione.bluehorizon.model.fishing;

import com.jakione.bluehorizon.model.fish.FishSpecies;

import java.util.List;

public interface FishingGear {

    /**
     * @return Il modificatore di probabilità base
     */
    double getCatchMultiplier();

    /**
     * @return La lista di specie che questo specifico set di equipaggiamento può catturare.
     */
    List<FishSpecies> getTargetableSpecies();
}
