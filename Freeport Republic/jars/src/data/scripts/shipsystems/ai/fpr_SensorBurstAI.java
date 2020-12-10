package data.scripts.shipsystems.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import data.scripts.shipsystems.fpr_SensorBurst;

public class fpr_SensorBurstAI implements ShipSystemAIScript{

    private CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipSystemAPI system;
    private final IntervalUtil timer = new IntervalUtil(1f,2f);

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine){
        this.ship = ship;
        this.system = system;
        this.engine = engine;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target){
        if(engine.isPaused()){
            return;
        }

        timer.advance(amount);
        if(timer.intervalElapsed()){
            float range = fpr_SensorBurst.debuffRange;
            if(!system.isActive() && AIUtils.canUseSystemThisFrame(ship) && !AIUtils.getNearbyEnemies(ship, range * 0.8f).isEmpty()){
                ship.useSystem();
            }
        }
    }
}