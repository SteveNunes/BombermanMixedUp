package frameset_tags;

import frameset.Sprite;
import light_spot_effects.LightSpot;

public class AddLightSpotToSprite extends AddTempLightSpot {

	public AddLightSpotToSprite(int offsetX, int offsetY, int xVariance, int yVariance, double minRadius, double maxRadius, double radiusInc) {
		super(offsetX, offsetY, xVariance, yVariance, minRadius, maxRadius, radiusInc);
		deleteMeAfterFirstRead = true;
	}

	public AddLightSpotToSprite(String tags)
		{ super(tags); }

	@Override
	public AddLightSpotToSprite getNewInstanceOfThis()
		{ return new AddLightSpotToSprite(x, y, xVariance, yVariance, minRadius, maxRadius, radiusInc); }
	
	@Override
	public void process(Sprite sprite) {
		LightSpot.addLightSpot(new LightSpot(sprite.getOutputDrawCoords())
				.setOffset(x, y)
				.setSpotVariance(xVariance, yVariance)
				.setRadiusVariance(minRadius, maxRadius, radiusInc));
	}
	
}
