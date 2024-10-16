package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Entity;
import entities.TileCoord;
import enums.ImageFlip;
import enums.TileProp;
import frameset.FrameSet;
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
	public double opacity;
	public DrawImageEffects effects;
	private String tileTags;
	private boolean disabledTileTags;
	private FrameSet tileTagsFrameSet;
	
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
		if ((tileTags = tile.tileTags) != null)
			reloadTags(true);
		else
			tileTagsFrameSet = null;
		disabledTileTags = tile.disabledTileTags;
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
		this.opacity = opacity;
		this.effects = effects;
	}
	
	public Tile(String strFromIni) {
		tileTagsFrameSet = null;
		tileProp = new ArrayList<>();
		String[] split = strFromIni.split(" ");
		if (split.length < 8)
			throw new RuntimeException(strFromIni + " - Too few parameters");
		int n = 0;
		try {
			int layer = Integer.parseInt(split[n]);
			// O segundo parametro eh ignorado pq so eh util no construtor da classe Layer
			String[] split2 = split[n += 2].split("!");
			spriteX = Integer.parseInt(split2[0]) * Main.TILE_SIZE;
			spriteY = Integer.parseInt(split2[1]) * Main.TILE_SIZE;
			split2 = split[++n].split("!");
			outX = Integer.parseInt(split2[0]) * Main.TILE_SIZE;
			outY = Integer.parseInt(split2[1]) * Main.TILE_SIZE;
			flip = ImageFlip.valueOf(split[++n]);
			rotate = Integer.parseInt(split[++n]);
			split2 = split[++n].split("!");
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
			opacity = Double.parseDouble(split[++n]);
			effects = Tools.loadEffectsFromString(MyConverters.arrayToString(split, ++n));
			if (Main.mapEditor != null && split.length > 9)
				setTileTagsFrameSetFromString(MyConverters.arrayToString(split, 9));
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	private void reloadTags(boolean force) {
		if (force || tileTagsFrameSet != null) {
			tileTagsFrameSet = new FrameSet(new Position(outX, outY));
			tileTagsFrameSet.loadFromString("{SetSprSource;MainSprites;0;0;0;0;0;0;" + outX +";" + outY +";0;0},{SetSprIndex;-}," + tileTags);
		}
	}
	
	public void removeTileTagsFrameSet() {
		tileTags = null;
		tileTagsFrameSet = null;
	}

	public void setTileTagsFrameSetFromString(String tagsFrameSet) {
		tileTags = tagsFrameSet;
		reloadTags(true);
	}

	public FrameSet getTileTagsFrameSet()
		{ return disabledTileTags ? null : tileTagsFrameSet; }
	
	public String getStringTags()
		{ return disabledTileTags ? null : tileTags; }
	
	public void disableTags()
		{ disabledTileTags = true; }
	
	public void enableTags()
		{ disabledTileTags = false; }

	public TileCoord getTileCoord()
		{ return new TileCoord(outX / 16, outY / 16); }
	
	public int getTileX()
		{ return outX / 16; }
	
	public int getTileY()
		{ return outY / 16; }

	public static void addTileShadow(Position shadowType, TileCoord coord)
		{ addTileShadow(coord, shadowType, true); }
	
	public static void addTileShadow(TileCoord coord, Position shadowType, boolean updateLayer) {
		Tile tile = !MapSet.haveTilesOnCoord(coord) ? null : MapSet.getTopTileFromCoord(coord);
		Position groundTile = MapSet.getGroundTile();
		if (tile == null || (tile.spriteX == groundTile.getX() && tile.spriteY == groundTile.getY())) {
			if (tile != null)
				MapSet.getCurrentLayer().removeFirstTileFromCoord(coord);
			tile = new Tile((int)shadowType.getX(), (int)shadowType.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY(), tile == null ? new ArrayList<>() : new ArrayList<>(tile.tileProp));
			MapSet.getCurrentLayer().addTile(tile);
			if (updateLayer)
				MapSet.getCurrentLayer().buildLayer();
		}
	}
	
	public static void removeTileShadow(TileCoord coord)
		{ removeTileShadow(coord, true); }
	
	public static void removeTileShadow(TileCoord coord, boolean updateLayer) {
		Tile tile = !MapSet.haveTilesOnCoord(coord) ? null : MapSet.getTopTileFromCoord(coord);
		Position groundTile = MapSet.getGroundTile(),
						 brickShadow = MapSet.getGroundWithBrickShadow(),
						 wallShadow = MapSet.getGroundWithWallShadow();
		if (tile == null || (tile.spriteX == brickShadow.getX() && tile.spriteY == brickShadow.getY()) ||
				(tile.spriteX == wallShadow.getX() && tile.spriteY == wallShadow.getY())) {
					if (tile != null)
						MapSet.getCurrentLayer().removeFirstTileFromCoord(coord);
					tile = new Tile((int)groundTile.getX(), (int)groundTile.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY(), tile == null ? new ArrayList<>() : new ArrayList<>(tile.tileProp));
					MapSet.getCurrentLayer().addTile(tile);
					if (updateLayer)
						MapSet.getCurrentLayer().buildLayer();
		}
	}

	public void setCoord(TileCoord coord) {
		outX = coord.getX() * Main.TILE_SIZE;
		outY = coord.getY() * Main.TILE_SIZE;
		reloadTags(false);
	}

	public void runTags(Entity owner) {
		if (!disabledTileTags && getTileTagsFrameSet() != null) {
			tileTagsFrameSet.setEntity(owner);
			String key = getTileCoord().toString(), key2 = key;
			for (int n = 1; MapSet.runningStageTags.containsKey(key); key = key2 + n++);
			MapSet.runningStageTags.put(key, new FrameSet(tileTagsFrameSet));
		}		
	}

}
