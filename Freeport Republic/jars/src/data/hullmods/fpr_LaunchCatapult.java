package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.plugins.fpr_combatPlugin;

public class fpr_LaunchCatapult extends BaseHullMod {

    private final float duration = 5f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

    }

    @Override
    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        fpr_combatPlugin.LaunchCatapult_addToList(fighter, duration);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return 35 + "%";
        if (index == 1) return Math.round(duration) + " seconds";
        return null;
    }
}
