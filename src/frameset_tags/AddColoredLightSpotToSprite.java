package frameset_tags;

import frameset.Sprite;
import javafx.scene.paint.Color;
import light_spot_effects.ColoredLightSpot;

public class AddColoredLightSpotToSprite extends AddTempColoredLightSpot {

	public AddColoredLightSpotToSprite(int offsetX, int offsetY, int xVariance, int yVariance, Color color, double minRadius, double maxRadius, double radiusInc, double opacity) {
		super(offsetX, offsetY, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity);
		deleteMeAfterFirstRead = true;
	}

	public AddColoredLightSpotToSprite(String tags) {
		super(tags);
	}

	@Override
	public AddColoredLightSpotToSprite getNewInstanceOfThis() {
		return new AddColoredLightSpotToSprite(x, y, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity);
	}

	@Override
	public void process(Sprite sprite) {
		ColoredLightSpot.addColoredLightSpot(new ColoredLightSpot(sprite.getSpritePosition()).setOffset(x, y).setColor(color).setSpotVariance(xVariance, yVariance).setRadiusVariance(minRadius, maxRadius, radiusInc).setOpacity(opacity));
	}

}
