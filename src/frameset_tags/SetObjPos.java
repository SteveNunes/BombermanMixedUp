package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetObjPos extends FrameTag {

	public int x;
	public int y;

	public SetObjPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SetObjPos(String tags) {
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
	public SetObjPos getNewInstanceOfThis() {
		return new SetObjPos(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().setPosition(x, y);
	}

}
