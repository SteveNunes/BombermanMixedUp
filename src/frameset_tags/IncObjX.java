package frameset_tags;

import frameset.Sprite;

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
