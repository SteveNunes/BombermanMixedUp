package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class SetSprIndex extends FrameTag {
	
	private Integer value;
	
	public SetSprIndex(Integer value)
		{ this.value = value; }

	public Integer getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprIndex(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = params[0].equals("-") ? null : Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprIndex getNewInstanceOfThis()
		{ return new SetSprIndex(value); }
	
	@Override
	public void process(Sprite sprite) {
		if (getValue() == null)
			sprite.setVisibleSprite(false);
		else {
			sprite.setVisibleSprite(true);
			sprite.setSpriteIndex(getValue());
		}
	}

}