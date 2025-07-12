package frameset_tags;

import enums.Direction;
import frameset.Sprite;
import util.Misc;

public class SetEntityDirection extends FrameTag {

	public Direction direction;

	public SetEntityDirection(Direction direction) {
		this.direction = direction;
	}

	public SetEntityDirection(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			direction = Direction.valueOf(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityDirection getNewInstanceOfThis() {
		return new SetEntityDirection(direction);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setDirection(direction);
	}

}
