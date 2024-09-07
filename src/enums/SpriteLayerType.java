package enums;

public enum SpriteLayerType {
	
  BACKGROUND,
  GROUND,
  IN_FRONT_BASED_ON_Y,
  CEIL,
  CLOUD;

	private static SpriteLayerType[] list = {BACKGROUND, GROUND, IN_FRONT_BASED_ON_Y, CEIL, CLOUD};
	
	public static SpriteLayerType[] getList()
		{ return list; }
	
}
