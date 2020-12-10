package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

public class fpr_combatPlugin extends BaseEveryFrameCombatPlugin {

    //Sensor Burst
    private static final Map<ShipAPI, Float> debuffedShips = new HashMap<>();
    private final IntervalUtil timer = new IntervalUtil(0.25f, 0.25f);

    public static void addToList(ShipAPI fighter, float duration) {
        debuffedShips.put(fighter, duration);
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
                    entry.getKey().setWeaponGlow(0,Color.BLACK, EnumSet.allOf(com.fs.starfarer.api.combat.WeaponAPI.WeaponType.class));
                    entry.getKey().getMutableStats().getBallisticWeaponRangeBonus().unmodify("fpr_SensorBurst");
                    entry.getKey().getMutableStats().getEnergyWeaponRangeBonus().unmodify("fpr_SensorBurst");
                    entry.getKey().getMutableStats().getAutofireAimAccuracy().unmodify("fpr_SensorBurst");
                } else {
                    entry.setValue(entry.getValue() - amount);
                    //Global.getCombatEngine().addFloatingText(entry.getKey().getLocation(),entry.getValue() + " left",20,Color.WHITE,entry.getKey(),0,0.3f);
                    entry.getKey().setWeaponGlow(2,new Color(132, 255, 44, 193), EnumSet.allOf(com.fs.starfarer.api.combat.WeaponAPI.WeaponType.class));
                    entry.getKey().getMutableStats().getBallisticWeaponRangeBonus().modifyMult("fpr_SensorBurst", 0.9f);
                    entry.getKey().getMutableStats().getEnergyWeaponRangeBonus().modifyMult("fpr_SensorBurst", 0.9f);
                    entry.getKey().getMutableStats().getAutofireAimAccuracy().modifyMult("fpr_SensorBurst", 0.8f);
                    if (entry.getKey() == Global.getCombatEngine().getPlayerShip()){
                        Global.getCombatEngine().maintainStatusForPlayerShip("fpr_SensorBurst", null, "Sensor Burst", "Weapon range redused", true);
                    }
                }
            }

        //

    }
}