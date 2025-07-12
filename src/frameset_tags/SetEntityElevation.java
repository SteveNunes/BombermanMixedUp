package frameset_tags;

import enums.Elevation;
import frameset.Sprite;
import util.Misc;

public class SetEntityElevation extends FrameTag {

	public Elevation value;

	public SetEntityElevation(Elevation value) {
		this.value = value;
	}

	public SetEntityElevation(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Elevation.valueOf(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityElevation getNewInstanceOfThis() {
		return new SetEntityElevation(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setElevation(value);
	}

}
