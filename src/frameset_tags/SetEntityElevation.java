package frameset_tags;

import enums.Elevation;
import frameset.Sprite;

public class SetEntityElevation extends FrameTag {

	public Elevation value;

	public SetEntityElevation(Elevation value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + value.name() + "}";
	}

	public SetEntityElevation(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Elevation.valueOf(params[0]);
		}
		catch (Exception e) {
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
