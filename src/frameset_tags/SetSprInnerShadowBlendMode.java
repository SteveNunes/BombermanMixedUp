package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprInnerShadowBlendMode extends FrameTag {
	
	public BlendMode blendMode;
	
	public SetSprInnerShadowBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprInnerShadowBlendMode(String tags) {
		String[] params = validateStringTags(this, tags, 1);
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
		{ sprite.getEffects().getInnerShadow().setBlendMode(blendMode); }

}



