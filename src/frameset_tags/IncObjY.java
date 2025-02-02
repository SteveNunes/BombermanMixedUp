package frameset_tags;

import frameset.Sprite;

public class IncObjY extends FrameTag {

	public int increment;

	public IncObjY(int increment) {
		this.increment = increment;
	}

	public IncObjY(String tags) {
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
	public IncObjY getNewInstanceOfThis() {
		return new IncObjY(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().incY(increment);
	}

}
