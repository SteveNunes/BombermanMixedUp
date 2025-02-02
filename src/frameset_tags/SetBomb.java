package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import entities.Bomb;
import enums.BombType;
import frameset.Sprite;

public class SetBomb extends FrameTag {

	public List<TileCoord2> targetCoords;
	public BombType bombType;
	public int fireDistance; 
	
	public SetBomb(BombType bombType, int fireDistance, List<TileCoord2> targetCoords) {
		this.bombType = bombType;
		this.fireDistance = fireDistance;
		this.targetCoords = targetCoords == null ? null : new ArrayList<>(targetCoords);
	}

	public SetBomb(String tags) {
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
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetBomb getNewInstanceOfThis() {
		return new SetBomb(bombType, fireDistance, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		if (targetCoords == null)
			Bomb.addBomb(sprite.getSourceEntity(), sprite.getTileCoordFromCenter(), bombType, fireDistance);
		else
			processTile(sprite.getTileCoord(), targetCoords, coord -> {
				Bomb.addBomb(sprite.getSourceEntity(), coord, bombType, fireDistance);
			});
	}

}
