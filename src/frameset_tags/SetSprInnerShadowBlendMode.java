package frameset_tags;

import entities.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprInnerShadowBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprInnerShadowBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprInnerShadowBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprInnerShadowBlendMode getNewInstanceOfThis()
		{ return new SetSprInnerShadowBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getInnerShadow().setBlendMode(getBlendMode()); }

}