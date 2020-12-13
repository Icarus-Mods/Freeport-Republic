package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class fpr_AkoyaSurplus extends BaseHullMod {

    private final float
            maintenanceMult = 0.5f,
            recoveryMult = 0.5f;


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCRPerDeploymentPercent().modifyMult(id, recoveryMult);
        stats.getSuppliesPerMonth().modifyMult(id, maintenanceMult);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return Math.round((1 - maintenanceMult) * 100) + "%";
        if (index == 1) return Math.round((1 - recoveryMult) * 100) + "%";
        return null;
    }
}

