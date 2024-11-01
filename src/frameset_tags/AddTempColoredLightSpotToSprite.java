package frameset_tags;

import frameset.Sprite;
import javafx.scene.paint.Color;
import light_spot_effects.ColoredLightSpot;

public class AddTempColoredLightSpotToSprite extends AddTempColoredLightSpot {

	public AddTempColoredLightSpotToSprite(int offsetX, int offsetY, int xVariance, int yVariance, Color color, double minRadius, double maxRadius, double radiusInc, double opacity) {
		super(offsetX, offsetY, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity);
	}

	public AddTempColoredLightSpotToSprite(String tags) {
		super(tags);
	}

	@Override
	public AddTempColoredLightSpotToSprite getNewInstanceOfThis() {
		return new AddTempColoredLightSpotToSprite(x, y, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity);
	}

	@Override
	public void process(Sprite sprite) {
		ColoredLightSpot.addTempColoredLightSpot(new ColoredLightSpot(sprite.getSpritePosition()).setOffset(x, y).setColor(color).setSpotVariance(xVariance, yVariance).setRadiusVariance(minRadius, maxRadius, radiusInc).setOpacity(opacity));
	}

}
