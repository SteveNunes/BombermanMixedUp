package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import objmoveutils.Position;
import tools.IniFiles;

public class Effect extends Entity {

	private static Map<Integer, Effect> effects = new HashMap<>();
	private static Map<String, Effect> preLoadedEffects = new HashMap<>();
	
	private Effect(Effect effect, Position position) {
		this(position, effect.getFrameSetsNames().iterator().next());
		setPosition(position);
	}

	private Effect(Position position, String frameSetName) {
		super();
		setTileSize(Main.TILE_SIZE);
		setPosition(position);
		addNewFrameSetFromString(frameSetName, IniFiles.effects.read(frameSetName, "FrameSet"));
		setFrameSet(frameSetName);
	}
	
	public static void clearPreloadedEffects()
		{ preLoadedEffects.clear(); }
	
	public static Effect runEffect(Position screenPosition, String frameSetName) {
		if (!preLoadedEffects.containsKey(frameSetName))
			preLoadedEffects.put(frameSetName, new Effect(new Position(), frameSetName));
		Effect effect = new Effect(preLoadedEffects.get(frameSetName), screenPosition);
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
