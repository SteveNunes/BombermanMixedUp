package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;
import util.Misc;

public class SetFallingWall extends FrameTag {

	public List<TileCoord2> targetCoords;
	
	public SetFallingWall(List<TileCoord2> targetCoords) {
		this.targetCoords = targetCoords == null ? null : new ArrayList<>(targetCoords);
	}

	public SetFallingWall(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		try {
			targetCoords = params.length == 0 ? null : stringToTileCoord2List(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			e.printStackTrace();
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetFallingWall getNewInstanceOfThis() {
		return new SetFallingWall(targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		if (targetCoords == null)
			MapSet.dropWallFromSky(sprite.getTileCoordFromCenter());
		else
			processTile(sprite.getTileCoord(), targetCoords, coord ->
				MapSet.dropWallFromSky(coord));
	}

}
