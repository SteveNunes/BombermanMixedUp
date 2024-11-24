package frameset_tags;

import enums.Direction;
import frameset.Sprite;

public class SetEntityDirection extends FrameTag {

	public Direction direction;

	public SetEntityDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + direction + "}";
	}

	public SetEntityDirection(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			direction = Direction.valueOf(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityDirection getNewInstanceOfThis() {
		return new SetEntityDirection(direction);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().forceDirection(direction);
	}

}
