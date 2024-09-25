package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.Tools;

public class SetSprBloomValues extends FrameTag {
	
	private double threshold;
	private BlendMode blendMode;
	
	public SetSprBloomValues(double threshold, BlendMode blendMode) {
		this.threshold = threshold;
		this.blendMode = blendMode;
	}

	public SetSprBloomValues(double threshold)
		{ this(threshold, BlendMode.SRC_ATOP); }

	public double getThreshold()
		{ return threshold; }
	
	public BlendMode getBlendMode()
		{ return blendMode; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + threshold + ";" + blendMode.name() + "}"; }

	public SetSprBloomValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			threshold = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprBloomValues getNewInstanceOfThis()
		{ return new SetSprBloomValues(threshold, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setBloom(getThreshold(), getBlendMode()); }

}