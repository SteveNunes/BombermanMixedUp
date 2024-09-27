package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprGlowBlendMode extends FrameTag {
	
	public BlendMode blendMode;
	
	public SetSprGlowBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprGlowBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGlowBlendMode getNewInstanceOfThis()
		{ return new SetSprGlowBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setGlow(sprite.getEffects().getGlow().getLevel(), blendMode); }

}



