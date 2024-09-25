package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.Tools;

public class SetSprGaussBlurValues extends FrameTag {
	
	private int radius;
	private BlendMode blendMode;
	
	public SetSprGaussBlurValues(int radius, BlendMode blendMode) {
		this.radius = radius;
		this.blendMode = blendMode;
	}

	public SetSprGaussBlurValues(int radius)
		{ this(radius, BlendMode.SRC_ATOP); }

	public int getRadius()
		{ return radius; }
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + radius + ";" + blendMode.name() + "}"; }

	public SetSprGaussBlurValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			radius = Integer.parseInt(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGaussBlurValues getNewInstanceOfThis()
		{ return new SetSprGaussBlurValues(radius, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setGaussianBlur(getRadius(), getBlendMode()); }

}