package com.jakione.bluehorizon.model.fishing;

import com.jakione.bluehorizon.model.fish.FishSpecies;

import java.util.Arrays;
import java.util.List;

public class BasicRod implements FishingGear {
    @Override
    public double getCatchMultiplier() {
        return 1.0;
    }

    @Override
    public List<FishSpecies> getTargetableSpecies() {
        return Arrays.asList(FishSpecies.CARP, FishSpecies.SALMON);
    }
}
