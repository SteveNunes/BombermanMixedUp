package frameset_tags;

import entities.Entity;
import entities.Ride;
import frameset.Sprite;

public class SetSafeJumpToCoord extends FrameTag {

	public TileCoord2 target;
	public double jumpStrenght;
	public double strenghtMultipiler;
	public int durationFrames; 
	
	public SetSafeJumpToCoord(double jumpStrenght, double strenghtMultipiler, int durationFrames, TileCoord2 target) {
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		this.durationFrames = durationFrames;
		this.target = target == null ? null : new TileCoord2(target);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + jumpStrenght + ";" + strenghtMultipiler + ";" + durationFrames + ";" + target.getOriginalTag() + "}";
	}

	public SetSafeJumpToCoord(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 4)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			jumpStrenght = Double.parseDouble(params[n]);
			strenghtMultipiler = Double.parseDouble(params[n = 1]);
			durationFrames = Integer.parseInt(params[n = 2]);
			target = params.length == 3 ? null : stringToTileCoord2(params[n = 3]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSafeJumpToCoord getNewInstanceOfThis() {
		return new SetSafeJumpToCoord(jumpStrenght, strenghtMultipiler, durationFrames, target);
	}

	@Override
	public void process(Sprite sprite) {
		if (target == null)
			sprite.getSourceEntity().jumpTo(sprite.getSourceEntity(), sprite.getTileCoordFromCenter(), jumpStrenght, strenghtMultipiler, durationFrames);
		else
			processTile(sprite.getTileCoord(), target, coord -> {
				Entity entity = sprite.getSourceEntity();
				if (entity instanceof Ride)
					entity = (Entity)((Ride)entity).getOwner();
				entity.safeJumpTo(entity, coord, jumpStrenght, strenghtMultipiler, durationFrames);
			});
	}

}
