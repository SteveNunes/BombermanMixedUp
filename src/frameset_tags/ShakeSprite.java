package frameset_tags;

import frameset.Sprite;

public class ShakeSprite extends FrameTag {
	
	public Double startStrengthX;
	public Double startStrengthY;
	public Double strengthX;
	public Double strengthY;
	public Double incStrengthX;
	public Double incStrengthY;
	public Double finalStrengthX;
	public Double finalStrengthY;
	
	public ShakeSprite(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		this.startStrengthX = startStrengthX;
		this.startStrengthY = startStrengthY;
		this.strengthX = startStrengthX;
		this.strengthY = startStrengthX;
		this.incStrengthX = incStrengthX;
		this.incStrengthY = incStrengthY;
		this.finalStrengthX = finalStrengthX;
		this.finalStrengthY = finalStrengthY;
	}
	
	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + startStrengthX + ";" + startStrengthY + ";" + incStrengthX + ";" + incStrengthY + ";" + finalStrengthX + ";" + finalStrengthY + "}"; }

	public ShakeSprite(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3 || params.length == 5)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			if (params.length == 3) {
				startStrengthX = startStrengthY = Double.parseDouble(params[n = 0]);
				incStrengthX = incStrengthY = Double.parseDouble(params[n = 1]);
				finalStrengthX = finalStrengthY = Double.parseDouble(params[n = 2]);
			}
			else if (params.length == 4) {
				startStrengthX = startStrengthY = null;
				incStrengthX = Double.parseDouble(params[n = 0]);
				incStrengthY = Double.parseDouble(params[n = 1]);
				finalStrengthX = Double.parseDouble(params[n = 2]);
				finalStrengthY = Double.parseDouble(params[n = 3]);
			}
			else {
				startStrengthX = Double.parseDouble(params[n = 0]);
				startStrengthY = Double.parseDouble(params[n = 1]);
				incStrengthX = Double.parseDouble(params[n = 2]);
				incStrengthY = Double.parseDouble(params[n = 3]);
				finalStrengthX = Double.parseDouble(params[n = 4]);
				finalStrengthY = Double.parseDouble(params[n = 5]);
			}
		}
		catch (Exception e)
			{ throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public ShakeSprite getNewInstanceOfThis()
		{ return new ShakeSprite(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY); }

	@Override
	public void process(Sprite sprite)
		{ sprite.setShake(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY); }

}
