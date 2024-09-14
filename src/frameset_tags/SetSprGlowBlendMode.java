package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.GameMisc;

public class SetSprGlowBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprGlowBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprGlowBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGlowBlendMode getNewInstanceOfThis()
		{ return new SetSprGlowBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setGlow(sprite.getEffects().getGlow().getLevel(), getBlendMode()); }

}