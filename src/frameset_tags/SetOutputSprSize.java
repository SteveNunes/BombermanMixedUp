package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOutputSprSize extends FrameTag {

	public int width;
	public int height;

	public SetOutputSprSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public SetOutputSprSize(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			width = Integer.parseInt(params[n++]);
			height = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetOutputSprSize getNewInstanceOfThis() {
		return new SetOutputSprSize(width, height);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOutputWidth(width);
		sprite.setOutputHeight(height);
	}

}
