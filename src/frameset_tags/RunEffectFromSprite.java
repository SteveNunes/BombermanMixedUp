package frameset_tags;

import entities.Effect;
import frameset.Sprite;
import objmoveutils.Position;

public class RunEffectFromSprite extends FrameTag {
	
	public String franeSetName;
	public Integer offsetX;
	public Integer offsetY;
	
	public RunEffectFromSprite(Integer offsetX, Integer offsetY, String franeSetName) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.franeSetName = franeSetName;
	}
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + offsetX + ";" + offsetY + ";" + franeSetName + "}"; }

	public RunEffectFromSprite(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length == 2 || params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			franeSetName = params[n++];
			offsetX = params.length == 1 ? null : Integer.parseInt(params[n++]);
			offsetY = params.length == 1 ? null : Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public RunEffectFromSprite getNewInstanceOfThis()
		{ return new RunEffectFromSprite(offsetX, offsetY, franeSetName); }

	@Override
	public void process(Sprite sprite) {
		int x = (int)sprite.getAbsoluteX() + (offsetX == null ? 0 : offsetX),
				y = (int)sprite.getAbsoluteY() + (offsetY == null ? 0 : offsetY);
		Effect.runEffect(new Position(x, y), franeSetName);
	}

}









