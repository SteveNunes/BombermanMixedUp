package frameset_tags;

import entities.BomberMan;
import entities.Ride;
import frameset.Sprite;

public class SetLinedBombsInFront extends FrameTag {

	public SetLinedBombsInFront(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
		
	@Override
	public SetLinedBombsInFront getNewInstanceOfThis() {
		return new SetLinedBombsInFront(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).setLinedBombsInFront();
		else if (sprite.getSourceEntity() instanceof Ride)
			((Ride)sprite.getSourceEntity()).getOwner().setLinedBombsInFront();
	}

}