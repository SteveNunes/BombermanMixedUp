package frameset_tags;

import entities.Effect;
import frameset.Sprite;
import objmoveutils.Position;

public class RunEffectFromEntity extends FrameTag {
	
	public String frameSetName;
	public Integer offsetX;
	public Integer offsetY;
	
	public RunEffectFromEntity(Integer offsetX, Integer offsetY, String franeSetName) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameSetName = franeSetName;
	}
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + offsetX + ";" + offsetY + ";" + frameSetName + "}"; }

	public RunEffectFromEntity(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length == 2 || params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			frameSetName = params[n++];
			offsetX = params.length == 1 ? null : Integer.parseInt(params[n++]);
			offsetY = params.length == 1 ? null : Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public RunEffectFromEntity getNewInstanceOfThis()
		{ return new RunEffectFromEntity(offsetX, offsetY, frameSetName); }

	@Override
	public void process(Sprite sprite) {
		int x = (int)sprite.getMainFrameSet().getEntity().getX() + (offsetX == null ? 0 : offsetX),
				y = (int)sprite.getMainFrameSet().getEntity().getY() + (offsetY == null ? 0 : offsetY);
		Effect.runEffect(new Position(x, y), frameSetName);
	}

}









