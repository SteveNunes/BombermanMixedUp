package frameset_tags;

import entities.Effect;
import frameset.Sprite;
import objmoveutils.Position;
import util.Misc;

public class RunEffectAtTile extends FrameTag {

	public String frameSetName;
	public Integer tileX;
	public Integer tileY;
	public Integer offsetX;
	public Integer offsetY;

	public RunEffectAtTile(Integer tileX, Integer tileY, Integer offsetX, Integer offsetY, String frameSetName) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameSetName = frameSetName;
	}

	public RunEffectAtTile(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			frameSetName = params[n++];
			tileX = Integer.parseInt(params[n++]);
			tileY = Integer.parseInt(params[n++]);
			offsetX = params.length == 3 ? 0 : Integer.parseInt(params[n++]);
			offsetY = params.length == 3 ? 0 : Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public RunEffectAtTile getNewInstanceOfThis() {
		return new RunEffectAtTile(tileX, tileY, offsetX, offsetY, frameSetName);
	}

	@Override
	public void process(Sprite sprite) {
		Effect.runEffect(new Position(offsetX, offsetY), frameSetName);
	}

}
