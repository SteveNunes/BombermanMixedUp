package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprGlowValues extends FrameTag {
	
	public int level;
	public BlendMode blendMode;
	
	public SetSprGlowValues(int level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode;
	}

	public SetSprGlowValues(int threshold)
		{ this(threshold, BlendMode.SRC_ATOP); }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + level + ";" + blendMode.name() + "}"; }
	
	public SetSprGlowValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			level = Integer.parseInt(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGlowValues getNewInstanceOfThis()
		{ return new SetSprGlowValues(level, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setGlow(level, blendMode); }

}






