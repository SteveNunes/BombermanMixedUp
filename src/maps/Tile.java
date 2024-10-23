package maps;

import java.util.List;
import java.util.Map;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Entity;
import entities.TileCoord;
import enums.ImageFlip;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Tags;
import frameset_tags.FrameTag;
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
	public String stringTileTags;
	
	public Tile(Tile tile)
		{ this(tile, tile.getOriginLayer()); }
	
	public Tile(Tile tile, Layer originLayer) {
		spriteX = tile.spriteX;
		spriteY = tile.spriteY;
		outX = tile.outX;
		outY = tile.outY;
		flip = tile.flip;
		rotate = tile.rotate;
		this.originLayer = originLayer;
		opacity = tile.opacity;
		effects = tile.effects == null ? null : new DrawImageEffects(tile.effects);
		stringTileTags = tile.stringTileTags;
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
		this.stringTileTags = null;
	}
	
	public Tile(Layer originLayer, String strFromIni) {
		this.originLayer = originLayer;
		String[] split = strFromIni.split(" ");
		if (split.length < 8)
			throw new RuntimeException(strFromIni + " - Too few parameters");
		int n = 2; // Os 2 primeiros parametros são ignorados pq so eh util no construtor da classe Layer
		try {
			String[] split2 = split[n].split("!");
			spriteX = Integer.parseInt(split2[0]) * Main.TILE_SIZE;
			spriteY = Integer.parseInt(split2[1]) * Main.TILE_SIZE;
			split2 = split[++n].split("!");
			outX = Integer.parseInt(split2[0]) * Main.TILE_SIZE;
			outY = Integer.parseInt(split2[1]) * Main.TILE_SIZE;
			flip = ImageFlip.valueOf(split[++n]);
			rotate = Integer.parseInt(split[++n]);
			split2 = split[++n].split("!");
			if (!getOriginLayer().haveTilesOnCoord(getTileCoord()) || !getOriginLayer().tileHaveProps(getTileCoord()))
				for (String s : split2) {
					int v = Integer.parseInt(s);
					if (TileProp.getPropFromValue(v) != null) {
						TileProp prop = TileProp.getPropFromValue(v);
						getOriginLayer().addTileProp(getTileCoord(), prop);
					}
				}
			opacity = Double.parseDouble(split[++n]);
			effects = Tools.loadEffectsFromString(MyConverters.arrayToString(split, ++n));
			stringTileTags = null;
			if (split.length > 9 && !getOriginLayer().haveTilesOnCoord(getTileCoord()))
				loadTagsFromString(MyConverters.arrayToString(split, 9));
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
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
					tile = new Tile(MapSet.getCurrentLayer(), (int)groundTile.getX(), (int)groundTile.getY(), (int)coord.getPosition(Main.TILE_SIZE).getX(), (int)coord.getPosition(Main.TILE_SIZE).getY());
					MapSet.getCurrentLayer().addTile(tile);
					if (updateLayer)
						MapSet.getCurrentLayer().buildLayer();
		}
	}
	
	public Layer getOriginLayer()
		{ return originLayer; }
	
	public int getOriginLayerIndex()
		{ return getOriginLayer().getLayer(); }
	
	public void runTags(Entity owner) {
		if (!tileTagsIsDisabled() && getTileTags() != null) {
			FrameSet frameSet = new FrameSet(getTileTagsFrameSet());
			frameSet.setEntity(owner);
			String key = getTileCoord().toString(), key2 = key;
			for (int n = 1; MapSet.runningStageTags.containsKey(key); key = key2 + n++);
			MapSet.runningStageTags.put(key, frameSet);
		}		
	}

	public void setCoord(TileCoord coord) {
		TileCoord oldCoord = getTileCoord().getNewInstance();
		outX = coord.getX() * Main.TILE_SIZE;
		outY = coord.getY() * Main.TILE_SIZE;
		if (getOriginLayer().tileHaveTags(oldCoord)) {
			getOriginLayer().getTileTagsFrameSet(oldCoord).getSprite(0).setX(outX);
			getOriginLayer().getTileTagsFrameSet(oldCoord).getSprite(0).setY(outY);
		}
	}
	
	public void addTileProp(TileCoord coord, TileProp ... props)
		{ getOriginLayer().addTileProp(coord, props); }
	
	public void removeTileProp(TileCoord coord, TileProp ... props)
		{ getOriginLayer().removeTileProp(coord, props); }
	
	public List<TileProp> getTileProps(TileCoord coord)
		{ return getOriginLayer().getTileProps(coord); }
	
	public int getTotalTileProps(TileCoord coord)
		{ return getOriginLayer().getTotalTileProps(coord); }
	
	public void replaceTileProps(TileCoord coord, List<TileProp> newTileProps)
		{ getOriginLayer().replaceTileProps(coord, newTileProps); }
	
	public Map<TileCoord, List<TileProp>> getTilePropsMap()
		{	return getOriginLayer().getTilePropsMap(); }
	
	public boolean tileContainsProp(TileCoord coord, TileProp prop)
		{ return getOriginLayer().tileContainsProp(coord, prop); }
	
	public boolean tileHaveProps(TileCoord coord)
		{ return getOriginLayer().tileHaveProps(coord); }

	public void loadTagsFromString(String stringTileTags) // Passar stringTileTags é temporario eqnuanto eu nao terminar de converter os tile tags antigos
		{ getOriginLayer().setTileTagsFromString(getTileCoord(), stringTileTags, this); }

	public boolean tileHaveTags()
		{ return getOriginLayer().tileHaveTags(getTileCoord()); }
	
	public String getStringTags() {
		if (tileHaveTags())
			return getTileTags().toString();
		return stringTileTags;
	}
	
	public Tags getTileTags()
		{ return getOriginLayer().getTileTags(getTileCoord()); }

	public void setTileTags(TileCoord coord, Tags tags)
		{ getOriginLayer().setTileTags(coord, tags); }
	
	public void removeTileTag(TileCoord coord, String tagStr)
		{ getOriginLayer().removeTileTag(coord, tagStr); }
	
	public void removeTileTag(TileCoord coord, FrameTag tag)
		{ getOriginLayer().removeTileTag(coord, tag); }
	
	public void clearTileTags(TileCoord coord)
		{ getOriginLayer().clearTileTags(coord); }

	public void disableTileTags()
		{ getOriginLayer().disableTileTags(getTileCoord()); }

	public void enableTileTags()
		{ getOriginLayer().enableTileTags(getTileCoord()); }

	public boolean tileTagsIsDisabled()
		{ return getOriginLayer().tileTagsIsDisabled(getTileCoord()); }
	
	private FrameSet getTileTagsFrameSet()
		{ return getOriginLayer().getTileTagsFrameSet(getTileCoord()); }

}
