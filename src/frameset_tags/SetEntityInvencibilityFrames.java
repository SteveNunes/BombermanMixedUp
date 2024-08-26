package frameset_tags;

import entities.Entity;
import entities.FrameSet;
import entities.Sprite;

public class SetEntityInvencibilityFrames extends FrameTag {
	
	private int value;
	
	public SetEntityInvencibilityFrames(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetEntityInvencibilityFrames(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetEntityInvencibilityFrames getNewInstanceOfThis()
		{ return new SetEntityInvencibilityFrames(value); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.setInvencibilityFrames(getValue());
	}

	@Override
	public void reset() {
	}

}