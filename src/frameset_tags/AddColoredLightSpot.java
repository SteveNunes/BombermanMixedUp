package frameset_tags;

import frameset.Sprite;
import javafx.scene.paint.Color;
import light_spot_effects.ColoredLightSpot;

public class AddColoredLightSpot extends AddTempColoredLightSpot {

	public AddColoredLightSpot(int x, int y, int xVariance, int yVariance, Color color, double minRadius, double maxRadius, double radiusInc, double opacity) {
		super(x, y, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity);
		deleteMeAfterFirstRead = true;
	}
	
	public AddColoredLightSpot(String tags)
		{ super(tags); }

	@Override
	public AddColoredLightSpot getNewInstanceOfThis()
		{ return new AddColoredLightSpot(x, y, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity); }
	
	@Override
	public void process(Sprite sprite) {
		ColoredLightSpot.addColoredLightSpot(new ColoredLightSpot(x, y)
				.setColor(color)
				.setSpotVariance(xVariance, yVariance)
				.setRadiusVariance(minRadius, maxRadius, radiusInc)
				.setOpacity(opacity));
	}

}
