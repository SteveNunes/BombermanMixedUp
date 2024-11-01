package frameset_tags;

import entities.Effect;
import frameset.Sprite;
import objmoveutils.Position;

public class RunEffectAt extends FrameTag {

	public String frameSetName;
	public Integer offsetX;
	public Integer offsetY;

	public RunEffectAt(Integer offsetX, Integer offsetY, String frameSetName) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameSetName = frameSetName;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + offsetX + ";" + offsetY + ";" + frameSetName + "}";
	}

	public RunEffectAt(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length == 2 || params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			frameSetName = params[n++];
			offsetX = params.length == 1 ? 0 : Integer.parseInt(params[n++]);
			offsetY = params.length == 1 ? 0 : Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public RunEffectAt getNewInstanceOfThis() {
		return new RunEffectAt(offsetX, offsetY, frameSetName);
	}

	@Override
	public void process(Sprite sprite) {
		Effect.runEffect(new Position(offsetX, offsetY), frameSetName);
	}

}
