package frameset_tags;

import frameset.Sprite;
import light_spot_effects.LightSpot;

public class AddLightSpot extends AddTempLightSpot {

	public AddLightSpot(int x, int y, int xVariance, int yVariance, double minRadius, double maxRadius, double radiusInc) {
		super(x, y, xVariance, yVariance, minRadius, maxRadius, radiusInc);
		deleteMeAfterFirstRead = true;
	}

	public AddLightSpot(String tags) {
		super(tags);
	}

	@Override
	public AddLightSpot getNewInstanceOfThis() {
		return new AddLightSpot(x, y, xVariance, yVariance, minRadius, maxRadius, radiusInc);
	}

	@Override
	public void process(Sprite sprite) {
		LightSpot.addLightSpot(new LightSpot(x, y).setSpotVariance(xVariance, yVariance).setRadiusVariance(minRadius, maxRadius, radiusInc));
	}

}
