package frameset_tags;

import entities.Ride;
import frameset.Sprite;
import util.Misc;

public class SetEntityY extends FrameTag {

	public int value;

	public SetEntityY(int value) {
		this.value = value;
	}

	public SetEntityY(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityY getNewInstanceOfThis() {
		return new SetEntityY(value);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride)
			((Ride)sprite.getSourceEntity()).getOwner().setY(value);
		else
			sprite.getSourceEntity().setY(value);
	}

}
