package frameset_tags;

import entities.BomberMan;
import entities.Ride;
import frameset.Sprite;

public class SetLinedBombsInFront extends FrameTag {

	public String tags;
	
	public SetLinedBombsInFront() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public SetLinedBombsInFront(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public SetLinedBombsInFront getNewInstanceOfThis() {
		return new SetLinedBombsInFront(tags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).setLinedBombsInFront();
		else if (sprite.getSourceEntity() instanceof Ride)
			((Ride)sprite.getSourceEntity()).getOwner().setLinedBombsInFront();
	}

}