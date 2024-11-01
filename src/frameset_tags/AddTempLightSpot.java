package frameset_tags;

import frameset.Sprite;
import light_spot_effects.LightSpot;

public class AddTempLightSpot extends FrameTag {

	int x;
	int y;
	int xVariance;
	int yVariance;
	double minRadius;
	double maxRadius;
	double radiusInc;

	public AddTempLightSpot(int x, int y, int xVariance, int yVariance, double minRadius, double maxRadius, double radiusInc) {
		this.x = x;
		this.y = y;
		this.xVariance = xVariance;
		this.yVariance = yVariance;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.radiusInc = radiusInc;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + x + ";" + y + ";" + xVariance + ";" + yVariance + ";" + minRadius + ";" + maxRadius + ";" + radiusInc + "}";
	}

	public AddTempLightSpot(String tags) {
		String[] params = validateStringTags(this, tags, 7);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
			xVariance = Integer.parseInt(params[n++]);
			yVariance = Integer.parseInt(params[n++]);
			minRadius = Double.parseDouble(params[n++]);
			maxRadius = Double.parseDouble(params[n++]);
			radiusInc = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public AddTempLightSpot getNewInstanceOfThis() {
		return new AddTempLightSpot(x, y, xVariance, yVariance, minRadius, maxRadius, radiusInc);
	}

	@Override
	public void process(Sprite sprite) {
		LightSpot.addTempLightSpot(new LightSpot(x, y).setSpotVariance(xVariance, yVariance).setRadiusVariance(minRadius, maxRadius, radiusInc));
	}

}
