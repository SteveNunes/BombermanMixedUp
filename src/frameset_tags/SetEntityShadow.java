package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;
import tools.Tools;

public class SetEntityShadow extends FrameTag {
	
	private int offsetX;
	private int offsetY;
	private int width;
	private int height;
	private float opacity;
	
	public SetEntityShadow(int offsetX, int offsetY, int width, int height, float opacity) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.opacity = opacity;
	}

	public int getOffsetX()
		{ return offsetX; }

	public int getOffsetY()
		{ return offsetY; }	

	public int getWidth()
		{ return width; }
	
	public int getHeight()
		{ return height; }	

	public float getOpacity()
		{ return opacity; }	

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
		entity.setShadow(getOffsetX(), getOffsetY(), getWidth(), getHeight(), getOpacity());
	}

}
