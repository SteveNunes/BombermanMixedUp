package maps;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Entity;
import entities.TileCoord;
import enums.ImageFlip;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Sprite;
import frameset.Tags;
import gui.util.ImageUtils;
import javafx.scene.paint.Color;
import objmoveutils.Position;
import tools.Tools;
import util.MyConverters;

public class Tile {

	public int spriteX;
	public int spriteY;
	public int outX;
	public int outY;
	public ImageFlip flip;
	public int rotate;
	public List<TileProp> tileProp;
	public Color tint;
	public double opacity;
	public DrawImageEffects effects;
	public Tags tileTags;
	public String oldTags;
	public static Map<Integer, Map<TileCoord, String>> tags = new HashMap<>();
	
	public Tile(Tile tile) {
		spriteX = tile.spriteX;
		spriteY = tile.spriteY;
		outX = tile.outX;
		outY = tile.outY;
		flip = tile.flip;
		rotate = tile.rotate;
		tileProp = new ArrayList<>(tile.tileProp);
		while (tileProp.contains(TileProp.EXPLOSION))
			tileProp.remove(TileProp.EXPLOSION);
		tileTags = tile.tileTags == null ? null : new Tags(tile.tileTags);
		tint = tile.tint;
		opacity = tile.opacity;
		effects = new DrawImageEffects(tile.effects);
	}
	
	public Tile(int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp)
		{ this(spriteX, spriteY, outX, outY, tileProp, ImageFlip.NONE, 0, 1, Color.TRANSPARENT, null); }

	public Tile(int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp, ImageFlip flip, int rotate, double opacity)
		{ this(spriteX, spriteY, outX, outY, tileProp, flip, rotate, opacity, Color.TRANSPARENT, null); }

	public Tile(int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp, ImageFlip flip, int rotate, double opacity, Color tint)
		{ this(spriteX, spriteY, outX, outY, tileProp, flip, rotate, opacity, tint, null); }
	
	public Tile(int spriteX, int spriteY, int outX, int outY, List<TileProp> tileProp, ImageFlip flip, int rotate, double opacity, Color tint, DrawImageEffects effects) {
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
	
	public Tile(String strFromIni) {
		tileProp = new ArrayList<>();
		String[] split = strFromIni.split(" ");
		if (split.length < 11)
			throw new RuntimeException(strFromIni + " - Too few parameters");
		int n = 0, r, g, b, a, layer;
		try {
			oldTags = "";
			layer = split.length <= n ? 0 : Integer.parseInt(split[n]);
			// O segundo parametro eh ignorado pq so eh util no construtor da classe Layer
			n += 2; outX = split.length <= n ? 0 : Integer.parseInt(split[n]) * Main.TILE_SIZE;
			n++; outY = split.length <= n ? 0 : Integer.parseInt(split[n]) * Main.TILE_SIZE;
			n++; spriteX = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; spriteY = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; int f = split.length <= n ? 0 : Integer.parseInt(split[n]);
			flip = Arrays.asList(ImageFlip.NONE, ImageFlip.HORIZONTAL, ImageFlip.VERTICAL, ImageFlip.BOTH).get(f);
			n++; rotate = split.length <= n ? 0 : Integer.parseInt(split[n]) * 90;
			n++; String[] split2 = split[n].split("!");
			if (!MapSet.isValidLayer(layer) || MapSet.getLayer(layer).haveTilesOnCoord(getTileCoord()))
				for (String s : split2) {
					int v = Integer.parseInt(s);
					if (TileProp.getPropFromValue(v) != null) {
						TileProp prop = TileProp.getPropFromValue(v);
						tileProp.add(prop);
					}
				}
			else
				tileProp.add(TileProp.NOTHING);
			n++; opacity = split.length <= n ? 1 : Double.parseDouble(split[n]);
			n++; String[] rgba = split[n].split("!");
			r = Integer.parseInt(rgba[0]);
			g = Integer.parseInt(rgba[1]);
			b = Integer.parseInt(rgba[2]);
			a = Integer.parseInt(rgba[3]);
			tint = r + g + b + a == 0 ? null : ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
			n++; effects = split.length <= n ? null : Tools.loadEffectsFromString(MyConverters.arrayToString(split, n));
			if (Main.mapEditor != null && split.length > 12) {
				oldTags = MyConverters.arrayToString(split, 12);
				tileTags = Tags.loadTagsFromString(oldTags);
				tileTags.setRootSprite(new Sprite(new FrameSet(new Entity()), "", new Rectangle(), new Rectangle(outX, outY, 0, 0)));
				if (tileTags.getFrameSetTags().isEmpty() && !oldTags.isEmpty())
					addStringTag(layer, outX / Main.TILE_SIZE, outY / Main.TILE_SIZE, oldTags);
			}
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public static void addStringTag(int layer, int tileDX, int tileDY, String tag) {
		if (!tags.containsKey(layer))
			tags.put(layer, new HashMap<>());
		tags.get(layer).put(new TileCoord(tileDX, tileDY), tag);
	}
	
	public static String getStringTag(int layer, TileCoord coord) {
		if (MapSet.isValidLayer(layer) && MapSet.getCurrentLayer().haveTilesOnCoord(coord)) {
			Tile tile = MapSet.getCurrentLayer().getFirstBottomTileFromCoord(coord);
			if (tile.tileTags != null && tile.tileTags.getFrameSetTags().isEmpty())
				return tile.tileTags.toString();
			return tags.containsKey(layer) && tags.get(layer).containsKey(coord) ? tags.get(layer).get(coord) : null;
		}
		return null;
	}
	
	public TileCoord getTileCoord()
		{ return new TileCoord(outX / 16, outY / 16); }
	
	public int getTileX()
		{ return outX / 16; }
	
	public int getTileY()
		{ return outY / 16; }

	public static void addTileShadow(Position shadowType, TileCoord coord)
		{ addTileShadow(coord, shadowType, true); }
	
	public static void addTileShadow(TileCoord coord, Position shadowType, boolean updateLayer) {
		Tile tile = !MapSet.getLayer(26).haveTilesOnCoord(coord) ? null : MapSet.getLayer(26).getTopTileFromCoord(coord);
		Position groundTile = MapSet.getGroundTile();
		if (tile == null || (tile.spriteX == groundTile.getX() && tile.spriteY == groundTile.getY())) {
			if (tile != null)
				MapSet.getLayer(26).removeFirstTileFromCoord(coord);
			tile = new Tile((int)shadowType.getX(), (int)shadowType.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY(), tile == null ? new ArrayList<>() : new ArrayList<>(tile.tileProp));
			MapSet.getLayer(26).addTile(tile);
			if (updateLayer)
				MapSet.getLayer(26).buildLayer();
		}
	}
	
	public static void removeTileShadow(TileCoord coord)
		{ removeTileShadow(coord, true); }
	
	public static void removeTileShadow(TileCoord coord, boolean updateLayer) {
		Tile tile = !MapSet.getLayer(26).haveTilesOnCoord(coord) ? null : MapSet.getLayer(26).getTopTileFromCoord(coord);
		Position groundTile = MapSet.getGroundTile(),
						 brickShadow = MapSet.getGroundWithBrickShadow(),
						 wallShadow = MapSet.getGroundWithWallShadow();
		if (tile == null || (tile.spriteX == brickShadow.getX() && tile.spriteY == brickShadow.getY()) ||
				(tile.spriteX == wallShadow.getX() && tile.spriteY == wallShadow.getY())) {
					if (tile != null)
						MapSet.getLayer(26).removeFirstTileFromCoord(coord);
					tile = new Tile((int)groundTile.getX(), (int)groundTile.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY(), tile == null ? new ArrayList<>() : new ArrayList<>(tile.tileProp));
					MapSet.getLayer(26).addTile(tile);
					if (updateLayer)
						MapSet.getLayer(26).buildLayer();
		}
	}

	public void setCoord(TileCoord coord) {
		outX = coord.getX() * Main.TILE_SIZE;
		outY = coord.getY() * Main.TILE_SIZE;
		if (tileTags != null)
			tileTags.setRootSprite(new Sprite(new FrameSet(new Entity()), "", new Rectangle(), new Rectangle(outX, outY, 0, 0)));
	}

}
