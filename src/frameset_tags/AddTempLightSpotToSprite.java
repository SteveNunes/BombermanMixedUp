package frameset_tags;

import frameset.Sprite;
import light_spot_effects.LightSpot;

public class AddTempLightSpotToSprite extends AddTempLightSpot {

	public AddTempLightSpotToSprite(int offsetX, int offsetY, int xVariance, int yVariance, double minRadius, double maxRadius, double radiusInc) {
		super(offsetX, offsetY, xVariance, yVariance, minRadius, maxRadius, radiusInc);
	}

	public AddTempLightSpotToSprite(String tags) {
		super(tags);
	}

	@Override
	public AddTempLightSpotToSprite getNewInstanceOfThis() {
		return new AddTempLightSpotToSprite(x, y, xVariance, yVariance, minRadius, maxRadius, radiusInc);
	}

	@Override
	public void process(Sprite sprite) {
		LightSpot.addTempLightSpot(new LightSpot(sprite.getSpritePosition()).setOffset(x, y).setSpotVariance(xVariance, yVariance).setRadiusVariance(minRadius, maxRadius, radiusInc));
	}

}
