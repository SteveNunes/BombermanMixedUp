package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOutputSprSize extends FrameTag {

	public int incrementWidth;
	public int incrementHeight;

	public IncOutputSprSize(int incrementWidth, int incrementHeight) {
		this.incrementWidth = incrementWidth;
		this.incrementHeight = incrementHeight;
	}

	public IncOutputSprSize(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementWidth = Integer.parseInt(params[n++]);
			incrementHeight = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
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
