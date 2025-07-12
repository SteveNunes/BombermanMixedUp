package frameset_tags;

import frameset.Sprite;
import maps.MapSet;
import util.Misc;

public class SetSwitchValue extends FrameTag {

	public String switchName;
	public int value;

	public SetSwitchValue(String switchName, Integer value) {
		this.switchName = switchName;
		this.value = value;
		super.deleteMeAfterFirstRead = true;
	}

	public SetSwitchValue(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			switchName = params[n++];
			value = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSwitchValue getNewInstanceOfThis() {
		return new SetSwitchValue(switchName, value);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.setSwitchValue(switchName, value);
	}

}
