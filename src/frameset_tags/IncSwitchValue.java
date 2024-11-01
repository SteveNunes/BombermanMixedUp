package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class IncSwitchValue extends FrameTag {

	public String switchName;
	public int incValue;

	public IncSwitchValue(String switchName, Integer incValue) {
		this.switchName = switchName;
		this.incValue = incValue;
		super.deleteMeAfterFirstRead = true;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + switchName + ";" + incValue + "}";
	}

	public IncSwitchValue(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 2)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			switchName = params[n++];
			incValue = params.length == 1 ? 1 : Integer.parseInt(params[n]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncSwitchValue getNewInstanceOfThis() {
		return new IncSwitchValue(switchName, incValue);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.incSwitchValue(switchName, incValue);
	}

}
