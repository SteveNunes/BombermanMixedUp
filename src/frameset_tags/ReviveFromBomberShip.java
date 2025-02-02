package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ReviveFromBomberShip extends FrameTag {

	public ReviveFromBomberShip(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public ReviveFromBomberShip getNewInstanceOfThis() {
		return new ReviveFromBomberShip(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan) {
			BomberMan bomber = (BomberMan)sprite.getSourceEntity();
			if (bomber.bomberShipIsActive() && bomber.getBomberShip().getVictim() != null)
				bomber.getBomberShip().reviveFromBomberShip();
		}
	}

}