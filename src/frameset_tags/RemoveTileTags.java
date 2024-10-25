package frameset_tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;

public class RemoveTileTags extends FrameTag {
	
	public List<String> tagsToBeRemoved;
	public List<TileCoord2> targetCoords;
	public int targetLayer;
	public String originalTag;
	
	public RemoveTileTags(int layer, List<TileCoord2> targetCoords, List<String> tagsToBeRemoved) {
		this.tagsToBeRemoved = tagsToBeRemoved;
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	public String getOriginalTag()
		{ return originalTag; }

	public void setOriginalTag(String originalTag)
		{ this.originalTag = originalTag; }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String str : tagsToBeRemoved) {
			if (!sb.isEmpty())
				sb.append(":");
			sb.append(str);
		}
		return "{" + getClassName(this) + ";" + targetLayer + ";" + sb.toString() + ";" + tileCoord2ListToString(targetCoords) + "}";
	}

	public RemoveTileTags(String stringTags) {
		String[] params = validateStringTags(this, stringTags);
		if (params.length < 2)
			throw new RuntimeException(stringTags + " - Too few parameters");
		if (params.length > 3)
			throw new RuntimeException(stringTags + " - Too few parameters");
		int n = 0;
		try {
			targetLayer = params[n = 0].equals("-") ? 26 : Integer.parseInt(params[n]);
			tagsToBeRemoved = new ArrayList<>(Arrays.asList(params[n = 1].split(":")));
			targetCoords = stringToTileCoord2List((n = 2) >= params.length ? null : params[n]);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public RemoveTileTags getNewInstanceOfThis()
		{ return new RemoveTileTags(targetLayer, targetCoords, tagsToBeRemoved); }
	
	@Override
	public void process(Sprite sprite) {
		processTile(sprite.getTileCoord(), targetCoords, coord -> {
			for (String tag : tagsToBeRemoved)
				MapSet.getLayer(targetLayer).removeTileTag(coord, tag);
		});
	}

}