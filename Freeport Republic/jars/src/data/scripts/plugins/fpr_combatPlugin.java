package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import java.awt.*;
import java.util.List;
import java.util.*;

public class fpr_combatPlugin extends BaseEveryFrameCombatPlugin {

    //Sensor Burst
    private static final Map<ShipAPI, Float> debuffedShips = new HashMap<>();
    //LaunchCatapult
    private static final Map<ShipAPI, Float> LaunchCatapult = new HashMap<>();
    private static final float LaunchCatapult_speedBonus = 35f;

    public static void addToList(ShipAPI fighter, float duration) {
        debuffedShips.put(fighter, duration);
    }

    public static void LaunchCatapult_addToList(ShipAPI fighter, float duration) {
        LaunchCatapult.put(fighter, duration);
    }


    //

    @Override
    public void init(CombatEngineAPI engine) {
        super.init(engine);
        debuffedShips.clear();
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        super.advance(amount, events);

        //Sensor Burst
        for (Iterator<Map.Entry<ShipAPI, Float>> iter = debuffedShips.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<ShipAPI, Float> entry = iter.next();
            if (!entry.getKey().isAlive() || (entry.getValue() - amount) < 0) {
                iter.remove();
                entry.getKey().setWeaponGlow(0, Color.BLACK, EnumSet.allOf(com.fs.starfarer.api.combat.WeaponAPI.WeaponType.class));
                entry.getKey().getMutableStats().getBallisticWeaponRangeBonus().unmodify("fpr_SensorBurst");
                entry.getKey().getMutableStats().getEnergyWeaponRangeBonus().unmodify("fpr_SensorBurst");
                entry.getKey().getMutableStats().getAutofireAimAccuracy().unmodify("fpr_SensorBurst");
            } else {
                entry.setValue(entry.getValue() - amount);
                //Global.getCombatEngine().addFloatingText(entry.getKey().getLocation(),entry.getValue() + " left",20,Color.WHITE,entry.getKey(),0,0.3f);
                entry.getKey().setWeaponGlow(2, new Color(132, 255, 44, 193), EnumSet.allOf(com.fs.starfarer.api.combat.WeaponAPI.WeaponType.class));
                entry.getKey().getMutableStats().getBallisticWeaponRangeBonus().modifyMult("fpr_SensorBurst", 0.9f);
                entry.getKey().getMutableStats().getEnergyWeaponRangeBonus().modifyMult("fpr_SensorBurst", 0.9f);
                entry.getKey().getMutableStats().getAutofireAimAccuracy().modifyMult("fpr_SensorBurst", 0.8f);
                if (entry.getKey() == Global.getCombatEngine().getPlayerShip()) {
                    Global.getCombatEngine().maintainStatusForPlayerShip("fpr_SensorBurst", null, "Sensor Burst", "Weapon range redused", true);
                }
            }
        }

        //LaunchCatapult

        for (Iterator<Map.Entry<ShipAPI, Float>> iter = LaunchCatapult.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<ShipAPI, Float> entry = iter.next();
            if (!entry.getKey().isAlive() || (entry.getValue() - amount) < 0) {
                iter.remove();
                entry.getKey().getMutableStats().getMaxSpeed().unmodify("fpr_LaunchCatapult");
            } else {
                entry.setValue(entry.getValue() - amount);
                entry.getKey().getMutableStats().getMaxSpeed().modifyPercent("fpr_LaunchCatapult", LaunchCatapult_speedBonus);
                entry.getKey().setJitterUnder(entry.getKey(), new Color(187, 243, 32, 230), 2f, 6, 4);
            }
        }

        //
    }
}