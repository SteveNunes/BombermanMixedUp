package frameset_tags;

import entities.Effect;
import enums.StringFrameSet;
import frameset.Sprite;
import objmoveutils.Position;
import tools.Tools;

public class RunEffectFromSprite extends FrameTag {
	
	private StringFrameSet effectFrameSet;
	private Integer offsetX;
	private Integer offsetY;
	
	public RunEffectFromSprite(Integer offsetX, Integer offsetY, StringFrameSet effectFrameSet) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.effectFrameSet = effectFrameSet;
	}
	
	public Integer getOffsetX()
		{ return offsetX; }	

	public Integer getOffsetY()
		{ return offsetY; }	

	public StringFrameSet getEffectFrameSet()
		{ return effectFrameSet; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + offsetX + ";" + offsetY + ";" + effectFrameSet.name() + "}"; }

	public RunEffectFromSprite(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length == 2 || params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			effectFrameSet = StringFrameSet.valueOf(params[n++]);
			offsetX = params.length == 1 ? null : Integer.parseInt(params[n++]);
			offsetY = params.length == 1 ? null : Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public RunEffectFromSprite getNewInstanceOfThis()
		{ return new RunEffectFromSprite(offsetX, offsetY, effectFrameSet); }

	@Override
	public void process(Sprite sprite) {
		int x = (int)sprite.getAbsoluteX() + (offsetX == null ? 0 : offsetX),
				y = (int)sprite.getAbsoluteY() + (offsetY == null ? 0 : offsetY);
		Effect.runEffect(new Position(x, y), effectFrameSet);
	}

}
