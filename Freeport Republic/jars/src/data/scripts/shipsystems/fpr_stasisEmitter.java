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
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class fpr_stasisEmitter extends BaseShipSystemScript {

    public static final float RANGE = 1500f;

    public final Color jitterColor = new Color(25, 255, 202, 255);
    public ShipAPI target;
    public boolean doOnce = true;

    //speed multy against ships per ship size
    private final Map<HullSize, Float> slow = new HashMap<>();
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
        target.setJitterUnder(ship, jitterColor, 6 * effectLevel, 12, 0f, 3 * effectLevel);

        float size = ship.getCollisionRadius() * 2;
        //if (size < 100) size = 100;
        float angle = VectorUtils.getAngle(ship.getLocation(), target.getLocation());
        float size2 = target.getCollisionRadius() * 2;
        if (size2 < 100) size2 = 100;

        boolean visible = MagicRender.screenCheck(1f, ship.getLocation());

        if (visible) {
            MagicRender.objectspace(
                    Global.getSettings().getSprite("fx", "fpr_stasisMark"),
                    ship,
                    new Vector2f(),
                    new Vector2f(),
                    new Vector2f(size, size),
                    new Vector2f(),
                    angle + 180,
                    0,
                    false,
                    new Color(255, 255, 255, (int) (200 * effectLevel)),
                    true,
                    0.05f,
                    0.0f,
                    0.05f,
                    true
            );
        }

        if (MagicRender.screenCheck(0.1f, target.getLocation())) {
            MagicRender.objectspace(
                    Global.getSettings().getSprite("fx", "fpr_stasisMark"),
                    target,
                    new Vector2f(),
                    new Vector2f(),
                    new Vector2f(size2, size2),
                    new Vector2f(),
                    angle,
                    0,
                    false,
                    new Color(255, 255, 255, (int) (200 * effectLevel)),
                    true,
                    0.05f,
                    0.0f,
                    0.1f,
                    true
            );
        }
    }


    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship;
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
        if (target == null) return null;
        float targetSlow = slow.get(target.getHullSize());
        if (effectLevel > 0) {
            if (index == 0) {
                return new StatusData("Target slowed down by " + Math.round((1 - targetSlow) * effectLevel * 100) + "%", false);
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
        return target != null && target != ship && MathUtils.isWithinRange(target, ship, RANGE);
    }

}








