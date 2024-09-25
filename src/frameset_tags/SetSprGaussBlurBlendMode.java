package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.Tools;

public class SetSprGaussBlurBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprGaussBlurBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprGaussBlurBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGaussBlurBlendMode getNewInstanceOfThis()
		{ return new SetSprGaussBlurBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setGaussianBlur(sprite.getEffects().getGaussianBlur().getRadius(), getBlendMode()); }

}