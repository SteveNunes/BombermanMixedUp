package frameset_tags;

import entities.Ride;
import frameset.Sprite;

public class SetEntityPos extends FrameTag {

	public int x;
	public int y;

	public SetEntityPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SetEntityPos(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityPos getNewInstanceOfThis() {
		return new SetEntityPos(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride)
			((Ride)sprite.getSourceEntity()).getOwner().setPosition(x, y);
		else
			sprite.getSourceEntity().setPosition(x, y);
	}

}
