package maps;

import java.util.List;
import java.util.Map;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Entity;
import enums.ImageFlip;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Tags;
import frameset_tags.FrameTag;
import javafx.scene.paint.Color;
import objmoveutils.Position;
import objmoveutils.TileCoord;
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
	public TileCoord tileCoord;
	
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
		tileCoord = tile.tileCoord.getNewInstance();
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
		tileCoord = new TileCoord(outX / Main.TILE_SIZE, outY / Main.TILE_SIZE);
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
			tileCoord = new TileCoord(outX / Main.TILE_SIZE, outY / Main.TILE_SIZE);
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
				setTileTagsFromString(MyConverters.arrayToString(split, 9));
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public TileCoord getTileCoord()
		{ return tileCoord; }
	
	public static void addTileShadow(Position shadowType, TileCoord coord)
		{ addTileShadow(coord, shadowType, true); }
	
	public static void addTileShadow(TileCoord coord, Position shadowType, boolean updateLayer) {
		Tile tile = !MapSet.haveTilesOnCoord(coord) ? null : MapSet.getTopTileFromCoord(coord);
		Position groundTile = MapSet.getGroundTile();
		if (tile == null || (tile.spriteX == groundTile.getX() && tile.spriteY == groundTile.getY())) {
			if (tile != null)
				MapSet.getCurrentLayer().removeFirstTileFromCoord(coord);
			tile = new Tile(MapSet.getCurrentLayer(), (int)shadowType.getX(), (int)shadowType.getY(), (int)coord.getPosition().getX(), (int)coord.getPosition().getY());
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
					tile = new Tile(MapSet.getCurrentLayer(), (int)groundTile.getX(), (int)groundTile.getY(), (int)coord.getPosition().getX(), (int)coord.getPosition().getY());
					MapSet.getCurrentLayer().addTile(tile);
					if (updateLayer)
						MapSet.getCurrentLayer().buildLayer();
		}
	}
	
	public Layer getOriginLayer()
		{ return originLayer; }
	
	public int getOriginLayerIndex()
		{ return getOriginLayer().getLayer(); }
	
	public void runTags(Entity whoTriggered, TileCoord triggeredTileCoord) {
		if (!tileTagsIsDisabled() && getTileTags() != null) {
			FrameSet frameSet = new FrameSet(getTileTagsFrameSet());
			frameSet.setEntity(whoTriggered);
			Position pos = triggeredTileCoord.getPosition();
			if (!whoTriggered.getTileCoordFromCenter().equals(triggeredTileCoord)) {
				frameSet.setPosition(-whoTriggered.getX(), -whoTriggered.getY());
				frameSet.incPosition(pos.getX(), pos.getY());
			}
			frameSet.getSprite(0).setPosition(0, 0);
			frameSet.getSprite(0).setOutputSize(Main.TILE_SIZE, Main.TILE_SIZE);
			String key = "" + hashCode(), key2 = key;
			for (int n = 1; MapSet.runningStageTags.containsKey(key); key = key2 + n++);
			MapSet.runningStageTags.put(key, frameSet);
		}		
	}

	public void setCoord(TileCoord coord) {
		TileCoord oldCoord = getTileCoord().getNewInstance();
		outX = coord.getX() * Main.TILE_SIZE;
		outY = coord.getY() * Main.TILE_SIZE;
		tileCoord.setCoords(coord);
		if (getOriginLayer().tileHaveTags(oldCoord)) {
			getOriginLayer().getTileTagsFrameSet(oldCoord).getSprite(0).setX(outX);
			getOriginLayer().getTileTagsFrameSet(oldCoord).getSprite(0).setY(outY);
		}
	}
	
	// ================ Metodos relacionados a TileProps ==============

	public boolean tileContainsProp(TileProp prop)
		{ return getOriginLayer().tileContainsProp(getTileCoord(), prop); }
	
	public Map<TileCoord, List<TileProp>> getTilePropsMap()
		{ return getOriginLayer().getTilePropsMap(); }

	public boolean tileHaveProps()
		{ return getOriginLayer().tileHaveProps(getTileCoord()); }

	public List<TileProp> getTileProps()
		{ return getOriginLayer().getTileProps(getTileCoord()); }
	
	public int getTotalTileProps()
		{ return getOriginLayer().getTotalTileProps(getTileCoord()); }
	
	public void setTileProps(List<TileProp> tileProps)
		{ getOriginLayer().setTileProps(getTileCoord(), tileProps); }

	public void addTileProp(TileProp ... props)
		{ getOriginLayer().addTileProp(getTileCoord(), props); }
	
	public void removeTileProp(TileProp ... props)
		{ getOriginLayer().removeTileProp(getTileCoord(), props); }
	
	public void clearTileProps()
		{ getOriginLayer().clearTileProps(getTileCoord()); }
	
	// ================ Metodos relacionados a TileTags ==============
	
	public boolean tileHaveTags()
		{ return getOriginLayer().tileHaveTags(getTileCoord()); }
	
	public void disableTileTags()
		{ getOriginLayer().disableTileTags(getTileCoord()); }
	
	public void enableTileTags()
		{ getOriginLayer().enableTileTags(getTileCoord()); }
	
	public boolean tileTagsIsDisabled()
		{ return getOriginLayer().tileTagsIsDisabled(getTileCoord()); }

	public Tags getTileTags()
		{ return getOriginLayer().getTileTags(getTileCoord()); }
	
	public String getStringTags() {
		if (getOriginLayer().tileHaveTags(getTileCoord()))
			return getOriginLayer().getTileTags(getTileCoord()).toString();
		return stringTileTags;
	}

	public FrameSet getTileTagsFrameSet()
		{ return getOriginLayer().getTileTagsFrameSet(getTileCoord()); }

	public void setTileTagsFromString(String stringTileTags)
		{ getOriginLayer().setTileTagsFromString(getTileCoord(), stringTileTags, this); }
	
	public void setTileTags(Tags tags)
		{ getOriginLayer().setTileTags(getTileCoord(), tags); }
	
	public void removeTileTag(String tagStr)
		{ getOriginLayer().removeTileTag(getTileCoord(), tagStr); }
	
	public void removeTileTag(FrameTag tag)
		{ getOriginLayer().removeTileTag(getTileCoord(), tag); }
	
	public void clearTileTags()
		{ getOriginLayer().clearTileTags(getTileCoord()); }
}
