package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import entities.Bomb;
import enums.BombType;
import frameset.Sprite;
import objmoveutils.TileCoord;

public class SetJumpingBomb extends FrameTag {

	public List<TileCoord2> initialCoords;
	public List<TileCoord2> targetCoords;
	public double jumpStrenght;
	public double strenghtMultipiler;
	public int durationFrames; 
	public BombType bombType;
	public int fireDistance; 
	
	public SetJumpingBomb(BombType bombType, int fireDistance, double jumpStrenght, double strenghtMultipiler, int durationFrames, List<TileCoord2> initialCoords, List<TileCoord2> targetCoords) {
		this.bombType = bombType;
		this.fireDistance = fireDistance;
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		this.durationFrames = durationFrames;
		this.initialCoords = initialCoords == null ? null : new ArrayList<>(initialCoords);
		this.targetCoords = new ArrayList<>(targetCoords);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + bombType.name() + ";" + fireDistance + ";" + jumpStrenght + ";" + strenghtMultipiler + ";" + durationFrames + ";" + tileCoord2ListToString(initialCoords) + ";" + tileCoord2ListToString(targetCoords) + "}";
	}

	public SetJumpingBomb(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 7)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 6)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			bombType = BombType.valueOf(params[n]);
			fireDistance = Integer.parseInt(params[n = 1]);
			jumpStrenght = Double.parseDouble(params[n = 2]);
			strenghtMultipiler = Double.parseDouble(params[n = 3]);
			durationFrames = Integer.parseInt(params[n = 4]);
			initialCoords = params.length == 6 ? null : stringToTileCoord2List(params[n = 5]);
			targetCoords = stringToTileCoord2List(params[n = (params.length == 6 ? 5 : 6)]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetJumpingBomb getNewInstanceOfThis() {
		return new SetJumpingBomb(bombType, fireDistance, jumpStrenght, strenghtMultipiler, durationFrames, initialCoords, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		List<TileCoord> coords1 = new ArrayList<>();
		List<TileCoord> coords2 = new ArrayList<>();
		if (initialCoords != null)
			processTile(sprite.getTileCoord(), initialCoords, coord -> coords1.add(coord));
		processTile(sprite.getTileCoord(), targetCoords, coord -> coords2.add(coord));
		for (int n = 0; n < coords2.size(); n++) {
			TileCoord c1 = initialCoords == null ? sprite.getTileCoordFromCenter().getNewInstance() : coords1.get(n).getNewInstance();
			TileCoord c2 = coords2.get(n).getNewInstance();
			Bomb bomb = new Bomb(sprite.getSourceEntity(), c1, bombType, fireDistance);
			bomb.jumpTo(c2, jumpStrenght, strenghtMultipiler, durationFrames);
			Bomb.addBomb(bomb);
		}
	}

}
