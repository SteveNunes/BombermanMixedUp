package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetSwitchValue extends FrameTag {

	public String switchName;
	public int value;

	public SetSwitchValue(String switchName, Integer value) {
		this.switchName = switchName;
		this.value = value;
		super.deleteMeAfterFirstRead = true;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + switchName + ";" + value + "}";
	}

	public SetSwitchValue(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			switchName = params[n++];
			value = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
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
