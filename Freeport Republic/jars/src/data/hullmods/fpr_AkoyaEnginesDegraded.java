package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class fpr_AkoyaEnginesDegraded extends BaseLogisticsHullMod {

	public static final float PROFILE_MULT = 0.8f;
	public static final float HEALTH_BONUS = 70f;
	public static final float HULL_BONUS = 5f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getEngineHealthBonus().modifyPercent(id, HEALTH_BONUS);
		stats.getSensorProfile().modifyMult(id, PROFILE_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) HEALTH_BONUS + "%";
		if (index == 1) return "" + (int) ((1f - PROFILE_MULT) * 100f) + "%";
		return null;
	}


}
