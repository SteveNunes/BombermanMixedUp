package frameset_tags;

import entities.Effect;
import enums.StringFrameSet;
import frameset.Sprite;
import objmoveutils.Position;
import tools.GameMisc;

public class RunEffectFromEntity extends FrameTag {
	
	private StringFrameSet effectFrameSet;
	private Integer offsetX;
	private Integer offsetY;
	
	public RunEffectFromEntity(Integer offsetX, Integer offsetY, StringFrameSet effectFrameSet) {
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

	public RunEffectFromEntity(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 3)
			GameMisc.throwRuntimeException(tags + " - Too much parameters");
		if (params.length == 2 || params.length < 1)
			GameMisc.throwRuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			effectFrameSet = StringFrameSet.valueOf(params[n++]);
			offsetX = params.length == 1 ? null : Integer.parseInt(params[n++]);
			offsetY = params.length == 1 ? null : Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public RunEffectFromEntity getNewInstanceOfThis()
		{ return new RunEffectFromEntity(offsetX, offsetY, effectFrameSet); }

	@Override
	public void process(Sprite sprite) {
		int x = (int)sprite.getMainFrameSet().getEntity().getX() + (offsetX == null ? 0 : offsetX),
				y = (int)sprite.getMainFrameSet().getEntity().getY() + (offsetY == null ? 0 : offsetY);
		Effect.runEffect(new Position(x, y), effectFrameSet);
	}

}
