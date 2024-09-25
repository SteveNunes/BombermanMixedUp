package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.StringFrameSet;
import objmoveutils.Position;

public class Effect extends Entity {

	private static Map<Integer, Effect> effects = new HashMap<>();
	private static Map<StringFrameSet, Effect> preLoadedEffects = new HashMap<>();
	
	private StringFrameSet frameSet;
	
	private Effect(Effect effect, Position position)
		{ this(position, effect.frameSet); }

	private Effect(Position position, StringFrameSet frameSet) {
		super();
		this.frameSet = frameSet;
		setTileSize(Main.TILE_SIZE);
		setPosition(position);
		addNewFrameSetFromString(frameSet.name(), frameSet.getString());
		setFrameSet(frameSet.name());
	}
	
	public static void clearPreloadedEffects()
		{ preLoadedEffects.clear(); }
	
	public static Effect runEffect(Position screenPosition, StringFrameSet effectFrameSet) {
		if (!preLoadedEffects.containsKey(effectFrameSet))
			preLoadedEffects.put(effectFrameSet, new Effect(new Position(), effectFrameSet));
		Effect effect = new Effect(preLoadedEffects.get(effectFrameSet), screenPosition);
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
