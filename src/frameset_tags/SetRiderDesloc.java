package frameset_tags;

import entities.Ride;
import frameset.Sprite;
import util.Misc;

public class SetRiderDesloc extends FrameTag {

	public int x;
	public int y;

	public SetRiderDesloc(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SetRiderDesloc(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetRiderDesloc getNewInstanceOfThis() {
		return new SetRiderDesloc(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride &&
				((Ride)sprite.getSourceEntity()).getOwner() != null)
					((Ride)sprite.getSourceEntity()).setRiderDesloc(x, y);
	}

}
