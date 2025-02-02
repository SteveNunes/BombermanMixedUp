package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ActiveBomberShip extends FrameTag {

	public ActiveBomberShip(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags);
	}

	@Override
	public ActiveBomberShip getNewInstanceOfThis() {
		return new ActiveBomberShip(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).activeBomberShip();
	}

}