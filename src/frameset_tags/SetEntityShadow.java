package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetEntityShadow extends FrameTag {

	public int offsetX;
	public int offsetY;
	public int width;
	public int height;
	public float opacity;

	public SetEntityShadow(int offsetX, int offsetY, int width, int height, float opacity) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.opacity = opacity;
	}

	public SetEntityShadow(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too much parameters");
		int n = 0;
		try {
			offsetX = n >= params.length || params[n].equals("-") ? 0 : Integer.parseInt(params[n]);
			offsetY = ++n >= params.length || params[n].equals("-") ? -3 : Integer.parseInt(params[n]);
			width = ++n >= params.length || params[n].equals("-") ? 14 : Integer.parseInt(params[n]);
			height = ++n >= params.length || params[n].equals("-") ? 6 : Integer.parseInt(params[n]);
			opacity = ++n >= params.length || params[n].equals("-") ? 0.5f : Float.parseFloat(params[n]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityShadow getNewInstanceOfThis() {
		return new SetEntityShadow(offsetX, offsetY, width, height, opacity);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setShadow(offsetX, offsetY, width, height, opacity);
	}

}
