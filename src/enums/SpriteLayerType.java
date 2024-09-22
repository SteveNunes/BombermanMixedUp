package enums;

public enum SpriteLayerType {
  BACKGROUND,
  GROUND,
  CEIL,
  CLOUD,
  POS_EFFECTS;

	private static SpriteLayerType[] list = {BACKGROUND, GROUND, CEIL, CLOUD, POS_EFFECTS};
	
	public static SpriteLayerType[] getList()
		{ return list; }
	
}
