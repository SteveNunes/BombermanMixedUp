package frameset_tags;

import entities.Sprite;
import javafx.scene.effect.BlendMode;
import tools.GameMisc;

public class SetSprMotionBlurBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprMotionBlurBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprMotionBlurBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprMotionBlurBlendMode getNewInstanceOfThis()
		{ return new SetSprMotionBlurBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getMotionBlur().setBlendMode(getBlendMode()); }

}