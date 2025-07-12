package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import enums.TileProp;
import frameset.Sprite;
import maps.MapSet;
import util.Misc;

public class RemoveTileProp extends FrameTag {

	public List<TileProp> tileProps;
	public List<TileCoord2> targetCoords;
	int targetLayer;

	public RemoveTileProp(int layer, List<TileCoord2> targetCoords, List<TileProp> tileProps) {
		this.tileProps = new ArrayList<>(tileProps);
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	public RemoveTileProp(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		if (params.length > 4)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			tileProps = new ArrayList<>();
			targetLayer = params[n = 0].equals("-") ? 26 : Integer.parseInt(params[n]);
			String[] split = params[n = 1].split("!");
			for (String s : split)
				tileProps.add(TileProp.valueOf(s));
			targetCoords = stringToTileCoord2List((n = 2) >= params.length ? null : params[n]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public RemoveTileProp getNewInstanceOfThis() {
		return new RemoveTileProp(targetLayer, targetCoords, tileProps);
	}

	@Override
	public void process(Sprite sprite) {
		processTile(sprite.getTileCoord(), targetCoords, coord -> tileProps.forEach(p -> MapSet.getLayer(targetLayer).removeTileProp(coord, p)));
	}

}