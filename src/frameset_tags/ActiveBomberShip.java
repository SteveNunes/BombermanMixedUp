package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ActiveBomberShip extends FrameTag {

	public String tags;
	
	public ActiveBomberShip() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public ActiveBomberShip(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public ActiveBomberShip getNewInstanceOfThis() {
		return new ActiveBomberShip(tags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).activeBomberShip();
	}

}