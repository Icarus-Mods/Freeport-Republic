package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class fpr_av_system extends BaseHullMod {
    public static final float MANEUVER_BONUS = 50.0F;

    public fpr_av_system() {
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getAcceleration().modifyPercent(id, 100.0F);
        stats.getDeceleration().modifyPercent(id, 50.0F);
        stats.getTurnAcceleration().modifyPercent(id, 100.0F);
        stats.getMaxTurnRate().modifyPercent(id, 50.0F);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        return index == 0 ? "50" : null;
    }
}
