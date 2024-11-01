package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import frameset.FrameSet;
import objmoveutils.Position;
import tools.IniFiles;

public class Effect extends Entity {

	private static Map<Integer, Effect> effects = new HashMap<>();
	private static Map<String, Effect> preLoadedEffects = new HashMap<>();
	private static Map<String, Effect> tempEffects = new HashMap<>();
	
	private Entity owner;
	private Predicate<Entity> closingPredicate;
	
	public Effect(Effect effect) {
		super();
		closingPredicate = null;
		owner = null;
		addFrameSet("FrameSet", new FrameSet(effect.getFrameSet("FrameSet"), this));
		setFrameSet("FrameSet");
	}

	public Effect(String effectName, String effectFrameSet) {
		super();
		closingPredicate = null;
		owner = null;
		addNewFrameSetFromString("FrameSet", effectFrameSet);
		setFrameSet("FrameSet");
	}

	public boolean isDone() {
		return closingPredicate != null && closingPredicate.test(owner); 
	}

	public Effect linkTo(Entity entity)
		{ return linkTo(entity, 0, 0); }
	
	public Effect linkTo(Entity entity, int offsetX, int offsetY) {
		getCurrentFrameSet().getSourceEntity().linkToEntity(entity);
		getCurrentFrameSet().setPosition(offsetX, offsetY);
		return this;
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

	public static void clearTempEffects() {
		tempEffects.clear();
	}
	
	public static Effect runEffect(Position screenPosition, String effectName) {
		return runEffect(screenPosition, effectName, null);
	}

	public static Effect runEffect(Position screenPosition, String effectName, Predicate<Entity> closingPredicate) {
		Effect effect = new Effect(preLoadedEffects.get(effectName));
		effect.setPosition(screenPosition);
		effects.put(effect.hashCode(), effect);
		return effect;
	}

	public Effect setClosingPredicate(Entity owner, Predicate<Entity> closingPredicate) {
		this.owner = owner;
		this.closingPredicate = closingPredicate;
		return this;
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
		List<Effect> effectList = new ArrayList<>(effects.values());
		for (Effect effect : effectList) {
			effect.run();
			if (effect.isDone()) {
				effect.getCurrentFrameSet().stop();
				effects.remove(effect.hashCode());
			}
		}
	}

}
