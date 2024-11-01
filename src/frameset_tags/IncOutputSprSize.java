package frameset_tags;

import frameset.Sprite;

public class IncOutputSprSize extends FrameTag {

	public int incrementWidth;
	public int incrementHeight;

	public IncOutputSprSize(int incrementWidth, int incrementHeight) {
		this.incrementWidth = incrementWidth;
		this.incrementHeight = incrementHeight;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + incrementWidth + ";" + incrementHeight + "}";
	}

	public IncOutputSprSize(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementWidth = Integer.parseInt(params[n++]);
			incrementHeight = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncOriginSprPos getNewInstanceOfThis() {
		return new IncOriginSprPos(incrementWidth, incrementHeight);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOutputWidth(incrementWidth);
		sprite.incOutputHeight(incrementHeight);
	}

}
