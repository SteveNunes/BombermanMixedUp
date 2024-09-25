package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.Tools;

public class SetSprSepiaToneValues extends FrameTag {
	
	private double level;
	private BlendMode blendMode;
	
	public SetSprSepiaToneValues(double level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode;
	}

	public SetSprSepiaToneValues(double threshold)
		{ this(threshold, BlendMode.SRC_ATOP); }

	public double getLevel()
		{ return level; }
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + level + ";" + blendMode.name() + "}"; }
	
	public SetSprSepiaToneValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			level = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprSepiaToneValues getNewInstanceOfThis()
		{ return new SetSprSepiaToneValues(level, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setSepiaTone(getLevel(), getBlendMode()); }

}