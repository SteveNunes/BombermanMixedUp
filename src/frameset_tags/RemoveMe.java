package frameset_tags;

import entities.Bomb;
import entities.BomberMan;
import entities.Effect;
import entities.Entity;
import entities.Ride;
import frameset.Sprite;
import maps.Brick;
import maps.Item;

public class RemoveMe extends FrameTag {

	public RemoveMe(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public RemoveMe getNewInstanceOfThis() {
		return new RemoveMe(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		if (entity instanceof Bomb)
			Bomb.removeBomb((Bomb)entity);
		else if (entity instanceof Brick)
			Brick.removeBrick((Brick)entity);
		else if (entity instanceof Item)
			Item.removeItem((Item)entity);
		else if (entity instanceof Effect)
			Effect.removeEffect((Effect)entity);
		else if (entity instanceof BomberMan)
			BomberMan.removeBomberMan((BomberMan)entity);
		else if (entity instanceof Ride)
			Ride.removeRide((Ride)entity);
	}

}