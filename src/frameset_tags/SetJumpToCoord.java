package frameset_tags;

import frameset.Sprite;

public class SetJumpToCoord extends FrameTag {

	public TileCoord2 target;
	public double jumpStrenght;
	public double strenghtMultipiler;
	public int durationFrames; 
	
	public SetJumpToCoord(double jumpStrenght, double strenghtMultipiler, int durationFrames, TileCoord2 target) {
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		this.durationFrames = durationFrames;
		this.target = target == null ? null : new TileCoord2(target);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + jumpStrenght + ";" + strenghtMultipiler + ";" + durationFrames + ";" + target.getOriginalTag() + "}";
	}

	public SetJumpToCoord(String tags) {
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
	public SetJumpToCoord getNewInstanceOfThis() {
		return new SetJumpToCoord(jumpStrenght, strenghtMultipiler, durationFrames, target);
	}

	@Override
	public void process(Sprite sprite) {
		if (target == null)
			sprite.getSourceEntity().jumpTo(sprite.getSourceEntity(), sprite.getTileCoordFromCenter(), jumpStrenght, strenghtMultipiler, durationFrames);
		else
			processTile(sprite.getTileCoord(), target, coord ->
				sprite.getSourceEntity().jumpTo(sprite.getSourceEntity(), coord, jumpStrenght, strenghtMultipiler, durationFrames));
	}

}