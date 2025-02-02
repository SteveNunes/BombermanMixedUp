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

	public IncSwitchValue(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			switchName = params[n++];
			incValue = Integer.parseInt(params[n]);
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
