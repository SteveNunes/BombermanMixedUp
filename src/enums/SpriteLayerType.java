package enums;

public enum SpriteLayerType {
  BACKGROUND,
  GROUND,
  CEIL,
  CLOUD;

	private static SpriteLayerType[] list = {BACKGROUND, GROUND, CEIL, CLOUD};
	
	public static SpriteLayerType[] getList()
		{ return list; }
	
}
