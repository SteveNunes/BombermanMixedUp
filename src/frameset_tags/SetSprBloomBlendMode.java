package frameset_tags;

import entities.Sprite;
import javafx.scene.effect.BlendMode;
import tools.GameMisc;

public class SetSprBloomBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprBloomBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprBloomBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprBloomBlendMode getNewInstanceOfThis()
		{ return new SetSprBloomBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getBloom().setBlendMode(getBlendMode()); }

}