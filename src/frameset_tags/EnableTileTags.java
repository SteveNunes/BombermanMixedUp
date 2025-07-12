package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;
import util.Misc;

public class EnableTileTags extends FrameTag {

	public List<TileCoord2> targetCoords;
	int targetLayer;

	public EnableTileTags(int layer, List<TileCoord2> targetCoords) {
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	public EnableTileTags(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			targetLayer = Integer.parseInt(params[n = 0]);
			targetCoords = stringToTileCoord2List((n = 1) >= params.length ? null : params[n]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public EnableTileTags getNewInstanceOfThis() {
		return new EnableTileTags(targetLayer, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		processTile(sprite.getTileCoord(), targetCoords, coord -> MapSet.getCurrentLayer().enableTileTags(coord));
	}

}
