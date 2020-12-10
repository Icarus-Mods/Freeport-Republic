package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;

/**
 * @author Tartiflette  (old position data: TORSO_OFFSET=-45, LEFT_ARM_OFFSET=-75, RIGHT_ARM_OFFSET=-25, MAX_OVERLAP = 10)
 */
public class fpr_MS_RifleEffect implements EveryFrameWeaponEffectPlugin {

    private final float TORSO_OFFSET = -15, LEFT_ARM_OFFSET = -76, RIGHT_ARM_OFFSET = -5, MAX_OVERLAP = 10;
    private boolean runOnce = false;
    private ShipAPI ship;
    private WeaponAPI armL, armR, pauldronL, pauldronR, torso;
    private float overlap = 0;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!runOnce) {
            runOnce = true;
            ship = weapon.getShip();
            for (WeaponAPI w : ship.getAllWeapons()) {
                switch (w.getSlot().getId()) {
                    case "TORSO":
                        torso = w;
                        break;
                    case "L_S_ARM":
                        armL = w;
                        break;
                    case "R_S_ARM":
                        armR = w;
                        break;
                    case "L_SHOULDER":
                        pauldronL = w;
                        break;
                    case "R_SHOULDER":
                        pauldronR = w;
                        break;
                }
            }
        }

        if (engine.isPaused()) {
            return;
        }

        if (ship.getEngineController().isAccelerating()) {
            if (overlap > (MAX_OVERLAP - 0.1f)) {
                overlap = MAX_OVERLAP;
            } else {
                overlap = Math.min(MAX_OVERLAP, overlap + ((MAX_OVERLAP - overlap) * amount * 5));
            }
        } else if (ship.getEngineController().isDecelerating() || ship.getEngineController().isAcceleratingBackwards()) {
            if (overlap < -(MAX_OVERLAP - 0.1f)) {
                overlap = -MAX_OVERLAP;
            } else {
                overlap = Math.max(-MAX_OVERLAP, overlap + ((-MAX_OVERLAP + overlap) * amount * 5));
            }
        } else {
            if (Math.abs(overlap) < 0.1f) {
                overlap = 0;
            } else {
                overlap -= (overlap / 2) * amount * 3;
            }
        }

        float sineA = 0, sinceB = 0;
        sineA = 1;
        sinceB = 1;


        float global = ship.getFacing();
        float aim = MathUtils.getShortestRotation(global, weapon.getCurrAngle());

        torso.setCurrAngle(global + sineA * TORSO_OFFSET + aim * 0.4f);

        armR.setCurrAngle(weapon.getCurrAngle() + RIGHT_ARM_OFFSET);

        pauldronR.setCurrAngle(global + sineA * TORSO_OFFSET * 0.5f + aim * 0.75f + RIGHT_ARM_OFFSET * 0.5f);

        armL.setCurrAngle(
                global
                        +
                        ((aim + LEFT_ARM_OFFSET) * sinceB)
                        +
                        ((overlap + aim * 0.25f) * (1 - sinceB))
        );

        pauldronL.setCurrAngle(torso.getCurrAngle() + MathUtils.getShortestRotation(torso.getCurrAngle(), armL.getCurrAngle()) * 0.4f);

    }
}
