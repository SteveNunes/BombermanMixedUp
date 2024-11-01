package frameset_tags;

import entities.Effect;
import frameset.Sprite;
import objmoveutils.Position;

public class RunEffectAtEntity extends RunEffectAt {

	public RunEffectAtEntity(Integer offsetX, Integer offsetY, String frameSetName) {
		super(offsetX, offsetY, frameSetName);
	}

	public RunEffectAtEntity(String tags) {
		super(tags);
	}

	@Override
	public RunEffectAtEntity getNewInstanceOfThis() {
		return new RunEffectAtEntity(offsetX, offsetY, frameSetName);
	}

	@Override
	public void process(Sprite sprite) {
		int x = (int) sprite.getSourceEntity().getX() + (offsetX == null ? 0 : offsetX), y = (int) sprite.getSourceEntity().getY() + (offsetY == null ? 0 : offsetY);
		Effect.runEffect(new Position(x, y), frameSetName);
	}

}
