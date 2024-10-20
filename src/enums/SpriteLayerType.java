package enums;

public enum SpriteLayerType {
  BACKGROUND,
  GROUND,
	SPRITE,
  CEIL,
  CLOUD;

	private static SpriteLayerType[] list = {BACKGROUND, GROUND, SPRITE, CEIL, CLOUD};
	
	public static SpriteLayerType[] getList()
		{ return list; }
	
}
