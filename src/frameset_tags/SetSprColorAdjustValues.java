package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.GameMisc;

public class SetSprColorAdjustValues extends FrameTag {
	
	private double hue;
	private double saturation;
	private double brightness;
	private BlendMode blendMode;
	
	public SetSprColorAdjustValues(double hue, double saturation, double brightness, BlendMode blendMode) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
		this.blendMode = blendMode;
	}

	public SetSprColorAdjustValues(double hue, double saturation, double brightness)
		{ this(hue, saturation, brightness, BlendMode.SRC_ATOP); }

	public double getHue()
		{ return hue; }

	public double getSaturation()
		{ return saturation; }
		
	public double getBrightness()
		{ return brightness; }
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + hue + ";" + saturation + ";" + brightness + ";" + blendMode.name() + "}"; }

	public SetSprColorAdjustValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 4);
		int n = 0;
		try {
			hue = Double.parseDouble(params[n++]);
			saturation = Double.parseDouble(params[n++]);
			brightness = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorAdjustValues getNewInstanceOfThis()
		{ return new SetSprColorAdjustValues(hue, saturation, brightness, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setColorAdjust(getHue(), getSaturation(), getBrightness(), getBlendMode()); }

}