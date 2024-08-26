package frameset_tags;

import entities.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprDropShadowValues extends FrameTag {
	
	private double offsetX;
	private double offsetY;
	private BlendMode blendMode;
	
	public SetSprDropShadowValues(double offsetX, double offsetY, BlendMode blendMode) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.blendMode = blendMode;
	}

	public SetSprDropShadowValues(double offsetX, double offsetY)
		{ this(offsetX, offsetY, BlendMode.SRC_ATOP); }

	public double getOffsetX()
		{ return offsetX; }

	public double getOffsetY()
		{ return offsetY; }	
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + offsetX + ";" + offsetY + ";" + blendMode.name() + "}"; }

	public SetSprDropShadowValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 3);
		int n = 0;
		try {
			offsetX = Double.parseDouble(params[n++]);
			offsetY = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprDropShadowValues getNewInstanceOfThis()
		{ return new SetSprDropShadowValues(offsetX, offsetY, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setDropShadow(getOffsetX(), getOffsetY(), getBlendMode()); }

	@Override
	public void reset() {
	}

}