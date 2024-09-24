package enums;

public enum SpriteLayerType {
	TEMP,
  BACKGROUND,
  GROUND,
  CEIL,
  CLOUD,
  POS_EFFECTS;

	private static SpriteLayerType[] list = {BACKGROUND, GROUND, CEIL, CLOUD, POS_EFFECTS};
	
	public static SpriteLayerType[] getList()
		{ return list; }
	
}
