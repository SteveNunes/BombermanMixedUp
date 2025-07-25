package frameset_tags;

import entities.Ride;
import frameset.Sprite;
import util.Misc;

public class SetEntityX extends FrameTag {

	public int value;

	public SetEntityX(int value) {
		this.value = value;
	}

	public SetEntityX(String tags) {
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
	public SetEntityX getNewInstanceOfThis() {
		return new SetEntityX(value);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride)
			((Ride)sprite.getSourceEntity()).getOwner().setX(value);
		else
			sprite.getSourceEntity().setX(value);
	}

}
