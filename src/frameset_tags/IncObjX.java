package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncObjX extends FrameTag {

	public int increment;

	public IncObjX(int increment) {
		this.increment = increment;
	}

	public IncObjX(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncObjX getNewInstanceOfThis() {
		return new IncObjX(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().incX(increment);
	}

}
