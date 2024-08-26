package frameset_tags;

import entities.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprColorTintValues extends FrameTag {
	
	private double red;
	private double green;
	private double blue;
	private double alpha;
	private BlendMode blendMode;
	
	public SetSprColorTintValues(double red, double green, double blue, double alpha, BlendMode blendMode) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.blendMode = blendMode;
	}

	public SetSprColorTintValues(double red, double green, double blue, BlendMode blendMode)
		{ this(red, green, blue, 1, blendMode); }

	public SetSprColorTintValues(double red, double green, double blue, double alpha)
		{ this(red, green, blue, 1, BlendMode.SRC_ATOP); }
	
	public SetSprColorTintValues(double red, double green, double blue)
		{ this(red, green, blue, 1, BlendMode.SRC_ATOP); }

	public double getRed()
		{ return red; }

	public double getGreen()
		{ return green; }
		
	public double getBlue()
		{ return blue; }
	
	public double getAlpha()
		{ return alpha; }
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + red + ";" + green + ";" + blue + ";" + alpha + ";" + blendMode.name() + "}"; }

	public SetSprColorTintValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 5);
		int n = 0;
		try {
			red = Double.parseDouble(params[n++]);
			green = Double.parseDouble(params[n++]);
			blue = Double.parseDouble(params[n++]);
			alpha = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorTintValues getNewInstanceOfThis()
		{ return new SetSprColorTintValues(red, green, blue, alpha, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setColorTint(getRed(), getGreen(), getBlue(), getAlpha()); }

	@Override
	public void reset() {
	}

}