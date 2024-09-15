package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.TileCoord;
import enums.ImageFlip;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.paint.Color;
import objmoveutils.Position;
import tools.GameMisc;
import util.MyConverters;

public class Tile {

	MapSet originMapSet;
	int spriteX;
	int spriteY;
	public int outX;
	public int outY;
	ImageFlip flip;
	int rotate;
	public List<TileProp> tileProp;
	Color tint;
	double opacity;
	DrawImageEffects effects;
	public static Map<Integer, Map<TileCoord, String>> tags = new HashMap<>();
	
	public Tile(MapSet originMapSet, int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp)
		{ this(originMapSet, spriteX, spriteY, outX, outY, tileProp, ImageFlip.NONE, 0, 1, Color.WHITE, null); }

	public Tile(MapSet originMapSet, int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp, ImageFlip flip, int rotate, double opacity)
		{ this(originMapSet, spriteX, spriteY, outX, outY, tileProp, flip, rotate, opacity, Color.WHITE, null); }

	public Tile(MapSet originMapSet, int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp, ImageFlip flip, int rotate, double opacity, Color tint)
		{ this(originMapSet, spriteX, spriteY, outX, outY, tileProp, flip, rotate, opacity, tint, null); }
	
	public Tile(MapSet originMapSet, int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp, ImageFlip flip, int rotate, double opacity, Color tint, DrawImageEffects effects) {
		this.originMapSet = originMapSet;
		this.spriteX = spriteX;
		this.spriteY = spriteY;
		this.outX = outX;
		this.outY = outY;
		this.flip = flip;
		this.rotate = rotate;
		this.tileProp = tileProp;
		this.tint = tint;
		this.opacity = opacity;
		this.effects = effects;
	}
	
	public Tile(MapSet originMapSet, String strFromIni) {
		tileProp = new ArrayList<>();
		String[] split = strFromIni.split(" ");
		if (split.length < 14)
			GameMisc.throwRuntimeException(strFromIni + " - Too few parameters");
		int n = 0, r, g, b, a, layer;
		this.originMapSet = originMapSet;
		try {
			layer = split.length <= n ? 0 : Integer.parseInt(split[n]);
			// O segundo parametro eh ignorado pq so eh util no construtor da classe Layer
			n += 2; outX = split.length <= n ? 0 : Integer.parseInt(split[n]) * Main.tileSize;
			n++; outY = split.length <= n ? 0 : Integer.parseInt(split[n]) * Main.tileSize;
			n++; spriteX = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; spriteY = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; int f = split.length <= n ? 0 : Integer.parseInt(split[n]);
			flip = Arrays.asList(ImageFlip.NONE, ImageFlip.HORIZONTAL, ImageFlip.VERTICAL, ImageFlip.BOTH).get(f);
			n++; rotate = split.length <= n ? 0 : Integer.parseInt(split[n]) * 90;
			n++; String[] split2 = split[n].split("!");
			for (String s : split2) {
				int v = Integer.parseInt(s);
				if (TileProp.getPropFromValue(v) != null) {
					TileProp prop = TileProp.getPropFromValue(v);
					tileProp.add(prop);
				}
			}			
			n++; opacity = split.length <= n ? 1 : Double.parseDouble(split[n]);
			n++; r = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; g = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; b = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; a = split.length <= n ? 255 : Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
			n++; effects = split.length <= n ? null : GameMisc.loadEffectsFromString(MyConverters.arrayToString(split, n));
			if (Main.mapEditor != null && split.length >= 15) {
				String str = MyConverters.arrayToString(split, 15);
				if (!str.isEmpty())
					addStringTag(layer, outX / Main.tileSize, outY / Main.tileSize, str);
			}
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public static void addStringTag(int layer, int tileDX, int tileDY, String tag) {
		if (!tags.containsKey(layer))
			tags.put(layer, new HashMap<>());
		tags.get(layer).put(new TileCoord(tileDX, tileDY), tag);
	}
	
	public static String getStringTag(int layer, int tileDX, int tileDY) {
		TileCoord coord = new TileCoord(tileDX, tileDY);
		return tags.containsKey(layer) && tags.get(layer).containsKey(coord) ? tags.get(layer).get(coord) : null;
	}
	
	public TileCoord getTileCoord()
		{ return new TileCoord(outX / 16, outY / 16); }
	
	public int getTileX()
		{ return outX / 16; }
	
	public int getTileY()
		{ return outY / 16; }

	public static void addTileShadow(MapSet mapSet, Position shadowType, TileCoord coord)
		{ addTileShadow(mapSet, coord, shadowType, true); }
	
	public static void addTileShadow(MapSet mapSet, TileCoord coord, Position shadowType, boolean updateLayer) {
		Tile tile = !mapSet.getLayer(26).haveTilesOnCoord(coord) ? null : mapSet.getLayer(26).getFirstTileFromCoord(coord);
		Position groundTile = mapSet.getGroundTile();
		if (tile == null || (tile.spriteX == groundTile.getX() && tile.spriteY == groundTile.getY())) {
			if (tile != null)
				mapSet.getLayer(26).removeFirstTileFromCoord(coord);
			tile = new Tile(mapSet, (int)shadowType.getX(), (int)shadowType.getY(), (int)coord.getPosition(Main.tileSize).getX(), (int)coord.getPosition(Main.tileSize).getY(), tile == null ? new ArrayList<>() : new ArrayList<>(tile.tileProp));
			mapSet.getLayer(26).addTile(tile);
			if (updateLayer)
				mapSet.getLayer(26).buildLayer();
		}
	}
	
	public static void removeTileShadow(MapSet mapSet, TileCoord coord)
		{ removeTileShadow(mapSet, coord, true); }
	
	public static void removeTileShadow(MapSet mapSet, TileCoord coord, boolean updateLayer) {
		Tile tile = !mapSet.getLayer(26).haveTilesOnCoord(coord) ? null : mapSet.getLayer(26).getFirstTileFromCoord(coord);
		Position groundTile = mapSet.getGroundTile(),
						 brickShadow = mapSet.getGroundWithBrickShadow(),
						 wallShadow = mapSet.getGroundWithWallShadow();
		if (tile == null || (tile.spriteX == brickShadow.getX() && tile.spriteY == brickShadow.getY()) ||
				(tile.spriteX == wallShadow.getX() && tile.spriteY == wallShadow.getY())) {
					if (tile != null)
						mapSet.getLayer(26).removeFirstTileFromCoord(coord);
					tile = new Tile(mapSet, (int)groundTile.getX(), (int)groundTile.getY(), (int)coord.getPosition(Main.tileSize).getX(), (int)coord.getPosition(Main.tileSize).getY(), tile == null ? new ArrayList<>() : new ArrayList<>(tile.tileProp));
					mapSet.getLayer(26).addTile(tile);
					if (updateLayer)
						mapSet.getLayer(26).buildLayer();
		}
	}

}
