package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import enums.TileProp;
import frameset.Sprite;
import maps.MapSet;

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
		return "{" + FrameTag.getClassName(this) + ";" + targetLayer + ";" + sb.toString() + ";" + FrameTag.tileCoord2ListToString(targetCoords) + "}";
	}

	public AddTileProp(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		if (params.length > 4)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			tileProps = new ArrayList<>();
			targetLayer = Integer.parseInt(params[n = 0]);
			String[] split = params[n = 1].split("!");
			for (String s : split)
				tileProps.add(TileProp.valueOf(s));
			targetCoords = FrameTag.stringToTileCoord2List((n = 2) >= params.length ? null : params[n]);
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
			tileProps.forEach(p -> MapSet.getCurrentLayer().addTileProp(coord, p));
		});
	}

}