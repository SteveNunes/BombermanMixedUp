package frameset_tags;

import entities.Effect;
import frameset.Sprite;
import objmoveutils.Position;

public class RunEffectAtSprite extends RunEffectAt {

	public RunEffectAtSprite(Integer offsetX, Integer offsetY, String frameSetName) {
		super(offsetX, offsetY, frameSetName);
	}

	public RunEffectAtSprite(String tags) {
		super(tags);
	}

	@Override
	public RunEffectAtSprite getNewInstanceOfThis() {
		return new RunEffectAtSprite(offsetX, offsetY, frameSetName);
	}

	@Override
	public void process(Sprite sprite) {
		int x = (int) sprite.getAbsoluteX() + (offsetX == null ? 0 : offsetX), y = (int) sprite.getAbsoluteY() + (offsetY == null ? 0 : offsetY);
		Effect.runEffect(new Position(x, y), frameSetName);
	}

}
