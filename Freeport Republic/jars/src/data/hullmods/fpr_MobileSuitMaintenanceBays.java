package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class fpr_MobileSuitMaintenanceBays extends BaseHullMod {

    public final float empMult = 0.5f;
    public final float engineDmgMult = 0.5f;
    public final float weaponsDmgMult = 0.5f;

    @Override
    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        if (fighter.getHullSpec().hasTag("Mobile_Suit")) {
            fighter.getMutableStats().getEmpDamageTakenMult().modifyMult(id, empMult);
            fighter.getMutableStats().getEngineDamageTakenMult().modifyMult(id, engineDmgMult);
            fighter.getMutableStats().getWeaponDamageTakenMult().modifyMult(id, weaponsDmgMult);
        }
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return Math.round((1 - empMult) * 100) + "%";
        if (index == 1) return Math.round((1 - engineDmgMult) * 100) + "%";
        if (index == 2) return Math.round((1 - weaponsDmgMult) * 100) + "%";
        return null;
    }
}
