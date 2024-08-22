package frameset_tags;

import javafx.scene.effect.BlendMode;

public class SetSprDropShadowBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprDropShadowBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprDropShadowBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprDropShadowBlendMode getNewInstanceOfThis()
		{ return new SetSprDropShadowBlendMode(blendMode); }
	
}