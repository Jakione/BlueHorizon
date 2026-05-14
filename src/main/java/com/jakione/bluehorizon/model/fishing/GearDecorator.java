package com.jakione.bluehorizon.model.fishing;

import com.jakione.bluehorizon.model.fish.FishSpecies;

import java.util.List;

public abstract class GearDecorator implements FishingGear{
    protected FishingGear wrappedGear;

    public GearDecorator(FishingGear gearToWrap) {
        this.wrappedGear = gearToWrap;
    }

    @Override
    public double getCatchMultiplier() {
        return wrappedGear.getCatchMultiplier();
    }

    @Override
    public List<FishSpecies> getTargetableSpecies() {
        return wrappedGear.getTargetableSpecies();
    }
}
