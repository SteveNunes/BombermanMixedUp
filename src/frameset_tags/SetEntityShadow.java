package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;

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

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + offsetX + ";" + offsetY + ";" + width + ";" + height + ";" + opacity + "}"; }

	public SetEntityShadow(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 5);
		int n = 0;
		try {
			offsetX = Integer.parseInt(params[n++]);
			offsetY = Integer.parseInt(params[n++]);
			width = Integer.parseInt(params[n++]);
			height = Integer.parseInt(params[n++]);
			opacity = Float.parseFloat(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetEntityShadow getNewInstanceOfThis()
		{ return new SetEntityShadow(offsetX, offsetY, width, height, opacity); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.setShadow(offsetX, offsetY, width, height, opacity);
	}

}















