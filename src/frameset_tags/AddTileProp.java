package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import enums.TileProp;
import frameset.Sprite;
import maps.MapSet;
import maps.Tile;

public class AddTileProp extends FrameTag {
	
	public List<TileProp> tileProps;
	public List<TileCoord2> targetCoords;
	int targetLayer;
	
	public AddTileProp(int layer, List<TileCoord2> targetCoords, List<TileProp> tileProps) {
		this.tileProps = new ArrayList<>(tileProps);
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TileProp prop : tileProps) {
			if (sb.isEmpty())
				sb.append("!");
			sb.append(prop.name());
		}
		return "{" + FrameTag.getClassName(this) + ";" + targetLayer + ";" + FrameTag.tileCoord2ListToString(targetCoords) + ";" + sb.toString() + "}";
	}

	public AddTileProp(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			tileProps = new ArrayList<>();
			int layer = Integer.parseInt(params[n = 0]);
			String[] split = params[n = 1].split("!");
			for (String s : split)
				tileProps.add(TileProp.valueOf(s));
			targetCoords = FrameTag.stringToTileCoord2List(++n >= params.length ? null : params[n]);
			targetLayer = layer;
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public AddTileProp getNewInstanceOfThis()
		{ return new AddTileProp(targetLayer, targetCoords, tileProps); }
	
	@Override
	public void process(Sprite sprite) {
		FrameTag.processTile(sprite, targetCoords, coord -> {
			Tile tile = MapSet.getLayer(targetLayer).getFirstBottomTileFromCoord(coord);
			tileProps.forEach(p -> tile.tileProp.add(p));
		});
	}

}