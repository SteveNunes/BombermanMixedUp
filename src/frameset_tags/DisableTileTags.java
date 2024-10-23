package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;

public class DisableTileTags extends FrameTag {
	
	public List<TileCoord2> targetCoords;
	int targetLayer;
	
	public DisableTileTags(int layer, List<TileCoord2> targetCoords) {
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + targetLayer + ";" + tileCoord2ListToString(targetCoords) + "}"; }

	public DisableTileTags(String tags) {
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
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public DisableTileTags getNewInstanceOfThis()
		{ return new DisableTileTags(targetLayer, targetCoords); }

	@Override
	public void process(Sprite sprite)
		{ processTile(sprite, targetCoords, coord -> MapSet.getCurrentLayer().disableTileTags(coord)); }

}
