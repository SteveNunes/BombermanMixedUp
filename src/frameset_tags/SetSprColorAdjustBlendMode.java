package frameset_tags;

import entities.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprColorAdjustBlendMode extends FrameTag {
	
	private BlendMode blendMode;
	
	public SetSprColorAdjustBlendMode(BlendMode blendMode)
		{ this.blendMode = blendMode; }

	public BlendMode getBlendMode()
		{ return blendMode; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + blendMode.name() + "}"; }

	public SetSprColorAdjustBlendMode(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ blendMode = BlendMode.valueOf(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorAdjustBlendMode getNewInstanceOfThis()
		{ return new SetSprColorAdjustBlendMode(blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorAdjust().setBlendMode(getBlendMode()); }

	@Override
	public void reset() {
	}

}