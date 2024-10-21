package maps;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Entity;
import entities.TileCoord;
import enums.ImageFlip;
import enums.TileProp;
import frameset.FrameSet;
import javafx.scene.paint.Color;
import objmoveutils.Position;
import tools.Tools;
import util.MyConverters;

public class Tile {

	public Layer originLayer;
	public int spriteX;
	public int spriteY;
	public int outX;
	public int outY;
	public ImageFlip flip;
	public int rotate;
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
		originLayer = tile.originLayer;
		if ((tileTags = tile.tileTags) != null)
			reloadTags(true);
		else
			tileTagsFrameSet = null;
		disabledTileTags = tile.disabledTileTags;
		opacity = tile.opacity;
		effects = tile.effects == null ? null : new DrawImageEffects(tile.effects);
	}
	
	public Tile(Layer originLayer, int spriteX, int spriteY, int outX, int outY)
		{ this(originLayer, spriteX, spriteY, outX, outY, ImageFlip.NONE, 0, 1, Color.TRANSPARENT, null); }

	public Tile(Layer originLayer, int spriteX, int spriteY, int outX, int outY, ImageFlip flip, int rotate, double opacity)
		{ this(originLayer, spriteX, spriteY, outX, outY, flip, rotate, opacity, Color.TRANSPARENT, null); }

	public Tile(Layer originLayer, int spriteX, int spriteY, int outX, int outY, ImageFlip flip, int rotate, double opacity, Color tint)
		{ this(originLayer, spriteX, spriteY, outX, outY, flip, rotate, opacity, tint, null); }
	
	public Tile(Layer originLayer, int spriteX, int spriteY, int outX, int outY, ImageFlip flip, int rotate, double opacity, Color tint, DrawImageEffects effects) {
		this.spriteX = spriteX;
		this.spriteY = spriteY;
		this.outX = outX;
		this.outY = outY;
		this.flip = flip;
		this.rotate = rotate;
		this.opacity = opacity;
		this.effects = effects;
		this.originLayer = originLayer;
	}
	
	public Tile(Layer originLayer, String strFromIni) {
		this.originLayer = originLayer;
		tileTagsFrameSet = null;
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
			if (!originLayer.haveTilesOnCoord(getTileCoord()) || !originLayer.tileHaveProps(getTileCoord()))
				for (String s : split2) {
					int v = Integer.parseInt(s);
					if (TileProp.getPropFromValue(v) != null) {
						TileProp prop = TileProp.getPropFromValue(v);
						originLayer.addTileProp(getTileCoord(), prop);
					}
				}
			opacity = Double.parseDouble(split[++n]);
			effects = Tools.loadEffectsFromString(MyConverters.arrayToString(split, ++n));
			if (split.length > 9)
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
		{ return new TileCoord(outX / Main.TILE_SIZE, outY / Main.TILE_SIZE); }
	
	public int getTileX()
		{ return outX / Main.TILE_SIZE; }
	
	public int getTileY()
		{ return outY / Main.TILE_SIZE; }

	public static void addTileShadow(Position shadowType, TileCoord coord)
		{ addTileShadow(coord, shadowType, true); }
	
	public static void addTileShadow(TileCoord coord, Position shadowType, boolean updateLayer) {
		Tile tile = !MapSet.haveTilesOnCoord(coord) ? null : MapSet.getTopTileFromCoord(coord);
		Position groundTile = MapSet.getGroundTile();
		if (tile == null || (tile.spriteX == groundTile.getX() && tile.spriteY == groundTile.getY())) {
			if (tile != null)
				MapSet.getCurrentLayer().removeFirstTileFromCoord(coord);
			tile = new Tile(MapSet.getCurrentLayer(), (int)shadowType.getX(), (int)shadowType.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY());
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
					tile = new Tile(tile.originLayer, (int)groundTile.getX(), (int)groundTile.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY());
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
