package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;

public class IncTicksPerFrame extends FrameTag {
	
	public int increment;
	
	public IncTicksPerFrame(int increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncTicksPerFrame(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncTicksPerFrame getNewInstanceOfThis()
		{ return new IncTicksPerFrame(increment); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getSourceFrameSet();
		frameSet.incFramesPerTick(increment);
	}

}



