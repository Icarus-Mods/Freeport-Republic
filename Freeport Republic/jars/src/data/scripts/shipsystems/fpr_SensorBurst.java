package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.plugins.fpr_combatPlugin;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class fpr_SensorBurst extends BaseShipSystemScript {

    public static final float debuffRange = 2000f;

    public final float debuffDuration = 10f;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (state == State.COOLDOWN || state == State.IDLE) {
            return;
        }



        SpriteAPI ring = Global.getSettings().getSprite("graphics/fx/fpr_SensorBurst.png");
        ring.setAlphaMult(effectLevel);
        if (state == State.OUT){
            effectLevel = 1;
        }
        MagicRender.singleframe(
                ring,
                ship.getLocation(),
                new Vector2f(debuffRange * 2 * effectLevel, debuffRange * 2 * effectLevel),
                120 * effectLevel,
                new Color(155, 85, 0, 255),
                true
        );

            for (ShipAPI shipToDebuff : AIUtils.getNearbyEnemies(ship, debuffRange * effectLevel)) {
                fpr_combatPlugin.addToList(shipToDebuff, debuffDuration);
                //Global.getCombatEngine().addFloatingText(ship.getLocation(),"hit",20,Color.WHITE,ship,0,0.3f);
            }

    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("enhancing allies' performance", false);
        }
        return null;
    }
}
