package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class fpr_stasisEmitter extends BaseShipSystemScript {

    public static final float DAM_MULT = 1.5f;
    public static final float RANGE = 1500f;

    public final Color jitterColor = new Color(50, 255, 142, 96);
    //speed multy against ships per ship size
    private final Map<HullSize, Float> slow = new HashMap<>();
    public ShipAPI target;
    public boolean doOnce = true;

    {
        slow.put(HullSize.FIGHTER, 0.1f);
        slow.put(HullSize.FRIGATE, 0.5f);
        slow.put(HullSize.DESTROYER, 0.65f);
        slow.put(HullSize.CRUISER, 0.75f);
        slow.put(HullSize.CAPITAL_SHIP, 0.85f);
    }


    public void apply(MutableShipStatsAPI stats, final String id, State state, float effectLevel) {
        ShipAPI ship;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        if (doOnce) {
            target = findTarget(ship);
            doOnce = false;
        }

        MutableShipStatsAPI targetStats = target.getMutableStats();
        float targetSlow = slow.get(target.getHullSize());
        targetStats.getMaxSpeed().modifyMult("fpr_stasisEmitter" + ship.getId(), targetSlow * effectLevel);
        targetStats.getAcceleration().modifyMult("fpr_stasisEmitter" + ship.getId(), targetSlow * effectLevel);
        targetStats.getDeceleration().modifyMult("fpr_stasisEmitter" + ship.getId(), targetSlow * effectLevel);
        targetStats.getMaxTurnRate().modifyMult("fpr_stasisEmitter" + ship.getId(), targetSlow * effectLevel);
        targetStats.getTurnAcceleration().modifyMult("fpr_stasisEmitter" + ship.getId(), targetSlow * effectLevel);
        target.setJitterShields(false);
        target.setJitter(ship, jitterColor, 2, 6, 0f, 2 * effectLevel);
        

    }


    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        if (target == null) return;
        MutableShipStatsAPI targetStats = target.getMutableStats();
        targetStats.getMaxSpeed().unmodify("fpr_stasisEmitter" + ship.getId());
        targetStats.getAcceleration().unmodify("fpr_stasisEmitter" + ship.getId());
        targetStats.getDeceleration().unmodify("fpr_stasisEmitter" + ship.getId());
        targetStats.getMaxTurnRate().unmodify("fpr_stasisEmitter" + ship.getId());
        targetStats.getTurnAcceleration().unmodify("fpr_stasisEmitter" + ship.getId());
        target.setJitterShields(true);
        target = null;
        doOnce = true;
    }

    protected ShipAPI findTarget(ShipAPI ship) {
        float range = getMaxRange(ship);
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        ShipAPI target = ship.getShipTarget();
        if (target != null) {
            float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
            float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
            if (dist > range + radSum) target = null;
        } else {
            if (target == null || target.getOwner() == ship.getOwner()) {
                if (player) {
                    target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FIGHTER, range, true);
                } else {
                    Object test = ship.getAIFlags().getCustom(AIFlags.MANEUVER_TARGET);
                    if (test instanceof ShipAPI) {
                        target = (ShipAPI) test;
                        float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                        float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
                        if (dist > range + radSum) target = null;
                    }
                }
            }
            if (target == null) {
                target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), HullSize.FIGHTER, range, true);
            }
        }

        return target;
    }


    protected float getMaxRange(ShipAPI ship) {
        return RANGE;
    }


    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (effectLevel > 0) {
            if (index == 0) {
                float damMult = 1f + (DAM_MULT - 1f) * effectLevel;
                return new StatusData("" + (int) ((damMult - 1f) * 100f) + "% more damage to target", false);
            }
        }
        return null;
    }


    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) return null;
        if (system.getState() != SystemState.IDLE) return null;

        ShipAPI target = findTarget(ship);
        if (target != null && target != ship) {
            return "READY";
        }
        if ((target == null) && ship.getShipTarget() != null) {
            return "OUT OF RANGE";
        }
        return "NO TARGET";
    }


    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        //if (true) return true;
        ShipAPI target = findTarget(ship);
        return target != null && target != ship;
    }

}








