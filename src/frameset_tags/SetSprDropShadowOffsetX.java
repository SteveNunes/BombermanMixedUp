package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class SetSprDropShadowOffsetX extends FrameTag {
	
	private double value;
	
	public SetSprDropShadowOffsetX(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprDropShadowOffsetX(String tags) {
		
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprDropShadowOffsetX getNewInstanceOfThis()
		{ return new SetSprDropShadowOffsetX(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getDropShadow().setOffsetX(getValue()); }

}