package frameset_tags;

import frameset.Sprite;
import javafx.scene.paint.Color;
import light_spot_effects.ColoredLightSpot;
import util.Misc;

public class AddTempColoredLightSpot extends FrameTag {

	int x;
	int y;
	int xVariance;
	int yVariance;
	Color color;
	double minRadius;
	double maxRadius;
	double radiusInc;
	double opacity;

	public AddTempColoredLightSpot(int x, int y, int xVariance, int yVariance, Color color, double minRadius, double maxRadius, double radiusInc, double opacity) {
		this.x = x;
		this.y = y;
		this.xVariance = xVariance;
		this.yVariance = yVariance;
		this.color = color;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.radiusInc = radiusInc;
		this.opacity = opacity;
	}

	public AddTempColoredLightSpot(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 9);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
			xVariance = Integer.parseInt(params[n++]);
			yVariance = Integer.parseInt(params[n++]);
			color = Color.valueOf(params[n++]);
			minRadius = Double.parseDouble(params[n++]);
			maxRadius = Double.parseDouble(params[n++]);
			radiusInc = Double.parseDouble(params[n++]);
			opacity = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public AddTempColoredLightSpot getNewInstanceOfThis() {
		return new AddTempColoredLightSpot(x, y, xVariance, yVariance, color, minRadius, maxRadius, radiusInc, opacity);
	}

	@Override
	public void process(Sprite sprite) {
		ColoredLightSpot.addTempColoredLightSpot(new ColoredLightSpot(x, y).setColor(color).setSpotVariance(xVariance, yVariance).setRadiusVariance(minRadius, maxRadius, radiusInc).setOpacity(opacity));
	}

}
