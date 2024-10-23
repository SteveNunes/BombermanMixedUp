package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;

public class SetTileTags extends FrameTag {
	
	public String tags;
	public String replacedTags;
	public List<TileCoord2> targetCoords;
	public int targetLayer;
	public String originalTag;
	
	public SetTileTags(int layer, List<TileCoord2> targetCoords, String tags) {
		this.tags = tags;
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
		replacedTags = "{" + tags.replace("@", ";") + "}";
	}

	public String getOriginalTag()
		{ return originalTag; }

	public void setOriginalTag(String originalTag)
		{ this.originalTag = originalTag; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + targetLayer + ";" + tags + ";" + tileCoord2ListToString(targetCoords) + "}"; }

	public SetTileTags(String stringTags) {
		String[] params = validateStringTags(this, stringTags);
		if (params.length < 2)
			throw new RuntimeException(stringTags + " - Too few parameters");
		if (params.length > 3)
			throw new RuntimeException(stringTags + " - Too few parameters");
		int n = 0;
		try {
			targetLayer = params[n = 0].equals("-") ? 26 : Integer.parseInt(params[n]);
			tags = params[1];
			replacedTags = "{" + tags.replace("@", ";") + "}";
			targetCoords = stringToTileCoord2List((n = 2) >= params.length ? null : params[n]);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public SetTileTags getNewInstanceOfThis()
		{ return new SetTileTags(targetLayer, targetCoords, tags); }
	
	@Override
	public void process(Sprite sprite)
		{ processTile(sprite, targetCoords, coord -> MapSet.getLayer(targetLayer).setTileTagsFromString(coord, replacedTags)); }

}