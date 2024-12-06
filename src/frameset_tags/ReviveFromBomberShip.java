package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ReviveFromBomberShip extends FrameTag {

	public String tags;
	
	public ReviveFromBomberShip() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public ReviveFromBomberShip(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public ReviveFromBomberShip getNewInstanceOfThis() {
		return new ReviveFromBomberShip(tags);
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