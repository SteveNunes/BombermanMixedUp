package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class DecSwitchValue extends FrameTag {
	
	public String switchName;
	public int incValue;
	
	public DecSwitchValue(String switchName, Integer incValue) {
		this.switchName = switchName;
		this.incValue = incValue;
		super.deleteMeAfterFirstRead = true;
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + switchName + ";" + incValue + "}"; }

	public DecSwitchValue(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 2)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			switchName = params[n++];
			incValue = params.length == 1 ? 1 : Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public DecSwitchValue getNewInstanceOfThis()
		{ return new DecSwitchValue(switchName, incValue); }
	
	@Override
	public void process(Sprite sprite)
		{ MapSet.incSwitchValue(switchName, -incValue); }

}



