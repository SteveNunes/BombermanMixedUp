package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import entities.Bomb;
import enums.BombType;
import frameset.Sprite;
import util.Misc;

public class SetFallingBomb extends FrameTag {

	public List<TileCoord2> targetCoords;
	public BombType bombType;
	public int fireDistance; 
	
	public SetFallingBomb(BombType bombType, int fireDistance, List<TileCoord2> targetCoords) {
		this.bombType = bombType;
		this.fireDistance = fireDistance;
		this.targetCoords = targetCoords == null ? null : new ArrayList<>(targetCoords);
	}

	public SetFallingBomb(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			bombType = BombType.valueOf(params[n]);
			fireDistance = Integer.parseInt(params[n = 1]);
			targetCoords = params.length == 2 ? null : stringToTileCoord2List(params[n = 2]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetFallingBomb getNewInstanceOfThis() {
		return new SetFallingBomb(bombType, fireDistance, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		if (targetCoords == null)
			Bomb.dropBombFromSky(sprite.getTileCoordFromCenter(), bombType, fireDistance);
		else
			processTile(sprite.getTileCoord(), targetCoords, coord ->
				Bomb.dropBombFromSky(coord, bombType, fireDistance));
	}

}
