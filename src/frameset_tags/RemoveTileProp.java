package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import entities.TileCoord;
import enums.TileProp;
import frameset.Sprite;
import maps.MapSet;
import maps.Tile;

public class RemoveTileProp extends FrameTag {
	
	public List<TileProp> tileProps;
	public TileCoord targetCoord;
	int targetLayer;
	int offsetX;
	int offsetY;
	
	public RemoveTileProp(int layer, int tx, int ty, int offsetX, int offsetY, List<TileProp> tileProps) {
		this.tileProps = tileProps == null ? null : new ArrayList<>(tileProps);
		targetLayer = layer;
		targetCoord = new TileCoord(tx, ty);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public String toString() {
		StringBuilder sb = null;
		if (tileProps != null) {
			sb = new StringBuilder();
			for (TileProp prop : tileProps) {
				if (sb.isEmpty())
					sb.append("!");
				sb.append(prop.name());
			}
		}
		return "{" + FrameTag.getClassName(this) + ";" + targetLayer + ";" + targetCoord.getX() + ";" + targetCoord.getY() + ";" + (sb == null ? "ALL" : sb.toString()) + "}";
	}

	public RemoveTileProp(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			tileProps = new ArrayList<>();
			int layer = Integer.parseInt(params[n = 0]);
			try {
				if (params[n = 3].equals("ALL"))
					tileProps = null;
				else {
					String[] split = params[n].split("!");
					for (String s : split)
						tileProps.add(TileProp.valueOf(s));
				}
				n = 1;
			}
			catch (Exception e) {
				if (params[n = 1].equals("ALL"))
					tileProps = null;
				else {
					String[] split = params[n].split("!");
					for (String s : split)
						tileProps.add(TileProp.valueOf(s));
				}
				n = -1;
			}
			int[] pos = n == -1 || params.length < 2 ? null : FrameTag.getPosWithDeslocFromString(params[n = 1]);
			int tx = pos == null ? -1 : pos[0];
			offsetX = pos == null ? 0 : pos[1];
			pos = n == -1 || params.length < 3 ? null : FrameTag.getPosWithDeslocFromString(params[n = 2]);
			int ty = pos == null ? -1 : pos[0];
			offsetY = pos == null ? 0 : pos[1];
			targetLayer = layer;
			targetCoord = new TileCoord(tx, ty);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public RemoveTileProp getNewInstanceOfThis()
		{ return new RemoveTileProp(targetLayer, targetCoord.getX(), targetCoord.getY(), offsetX, offsetY, tileProps); }
	
	@Override
	public void process(Sprite sprite) {
		int tx = targetCoord.getX(), ty = targetCoord.getY();
		if (tx == -1 && ty == -1) {
			TileCoord coord = sprite.getTileCoord();
			tx = coord.getX();
			ty = coord.getY();
		}
		Tile tile = MapSet.getLayer(targetLayer).getFirstBottomTileFromCoord(new TileCoord(tx + offsetX, ty + offsetY));
		if (tileProps == null)
			tile.tileProp.clear();
		else
			tileProps.forEach(p -> tile.tileProp.remove(p));
		if (tile.tileProp.isEmpty())
			tile.tileProp.add(TileProp.NOTHING);
	}

}