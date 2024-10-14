package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import frameset.FrameSet;
import objmoveutils.Position;
import tools.IniFiles;

public class Effect extends Entity {

	private static Map<Integer, Effect> effects = new HashMap<>();
	private static Map<String, Effect> preLoadedEffects = new HashMap<>();
	private static Map<String, Effect> tempEffects = new HashMap<>();
	
	public Effect(Effect effect) {
		super();
		addFrameSet("FrameSet", new FrameSet(effect.getFrameSet("FrameSet"), this));
		setFrameSet("FrameSet");
	}
	
	public Effect(String effectName, String effectFrameSet) {
		super();
		addNewFrameSetFromString("FrameSet", effectFrameSet);
		setFrameSet("FrameSet");
	}

	public static void loadEffects() {
		for (String effectName : IniFiles.effects.getSectionList()) {
			Effect effect = new Effect(effectName, IniFiles.effects.read(effectName, "FrameSet"));
			preLoadedEffects.put(effectName, effect);
		}
	}
	
	public static void addNewTempEffect(String tempEffectName, String effectFrameSet) {
		Effect effect = new Effect(tempEffectName, effectFrameSet);
		tempEffects.put(tempEffectName, effect);
	}
	
	public static void clearTempEffects()
		{ tempEffects.clear(); }

	public static Effect runEffect(Position screenPosition, String effectName) {
		Effect effect = new Effect(preLoadedEffects.get(effectName));
		effect.setPosition(screenPosition);
		effects.put(effect.hashCode(), effect);
		return effect;
	}
	
	public static void removeEffect(Effect effect) {
		if (effects.containsKey(effect.hashCode())) {
			effect.setDisabled();
			effects.remove(effect.hashCode());
		}
	}
	
	public static void clearEffects() {
		List<Effect> list = new ArrayList<>(effects.values());
		list.forEach(effect -> removeEffect(effect));
	}
	
	public static void drawEffects() {
		for (Effect effect : effects.values())
			effect.run();
	}
	
}
