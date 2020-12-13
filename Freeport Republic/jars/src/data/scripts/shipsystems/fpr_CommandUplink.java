package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class fpr_CommandUplink extends BaseShipSystemScript {

    public static final float buffRange = 2000f;
    public final float rangeBonus = 15f;

    private List<ShipAPI> buffed = new ArrayList<>();

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            id = id + "_" + ship.getId();
        } else {
            return;
        }

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (state == State.COOLDOWN || state == State.IDLE) {
            return;
        }

        SpriteAPI ring = Global.getSettings().getSprite("graphics/fx/fpr_CommandUplink.png");
        ring.setAlphaMult(effectLevel);
        MagicRender.singleframe(
                ring,
                ship.getLocation(),
                new Vector2f(buffRange * 2, buffRange * 2),
                0,
                new Color(155, 0, 0, 255),
                true
        );

        if (state == State.ACTIVE) {
            for (ShipAPI shipToUnBuff : buffed) {
                unapplyBuff(shipToUnBuff);
            }
            buffed = AIUtils.getNearbyAllies(ship, buffRange);
            for (ShipAPI shipToBuff : buffed) {
                applyBuff(shipToBuff);
            }
        }
        if (state == State.OUT) {
            for (ShipAPI shipToUnBuff : buffed) {
                unapplyBuff(shipToUnBuff);
            }
            buffed.clear();
        }
    }

    private void applyBuff(ShipAPI ship) {
        ship.setJitterUnder(ship, new Color(243, 144, 32, 230), 2f, 6, 4);
        ship.setJitterShields(false);
        String id = "fpr_CommandUplink";
        ship.getMutableStats().getEnergyWeaponRangeBonus().modifyPercent(id, rangeBonus);
        ship.getMutableStats().getBallisticWeaponRangeBonus().modifyPercent(id, rangeBonus);
    }

    private void unapplyBuff(ShipAPI ship) {
        String id = "fpr_CommandUplink";
        ship.setJitterShields(true);
        ship.getMutableStats().getEnergyWeaponRangeBonus().unmodify(id);
        ship.getMutableStats().getBallisticWeaponRangeBonus().unmodify(id);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        for (ShipAPI shipToUnBuff : buffed) {
            unapplyBuff(shipToUnBuff);
        }
        buffed.clear();
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("enhancing allies' performance", false);
        }
        return null;
    }
}
