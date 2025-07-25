package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetHoldingDesloc extends FrameTag {

	public int x;
	public int y;

	public SetHoldingDesloc(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SetHoldingDesloc(String tags) {
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
	public SetHoldingDesloc getNewInstanceOfThis() {
		return new SetHoldingDesloc(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity().isHoldingEntity())
			sprite.getSourceEntity().getHoldingEntity().setHolderDesloc(x, y);
	}

}
