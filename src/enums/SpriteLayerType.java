package enums;

public enum SpriteLayerType {
	TEMP,
  BACKGROUND,
  GROUND,
  CEIL,
  CLOUD,
  TINT;

	private static SpriteLayerType[] list = {TEMP, BACKGROUND, GROUND, CEIL, CLOUD, TINT};
	
	public static SpriteLayerType[] getList()
		{ return list; }
	
}
