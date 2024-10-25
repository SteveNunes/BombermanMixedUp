package maps;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import application.Main;
import enums.SpriteLayerType;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Tags;
import frameset_tags.FrameTag;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import tools.Materials;
import tools.Tools;

public class Layer {
	
	private Map<TileCoord, List<Tile>> tilesMap;
	private Map<TileCoord, List<TileProp>> tilesProps;
	private Set<TileCoord> disabledTileTags;
	private Map<TileCoord, FrameSet> tilesTags;
	private List<Tile> tileList;
	private int layer;
	private int width;
	private int height;
	private SpriteLayerType layerType;
	
	public Layer(int layerIndex)
		{ this(layerIndex, SpriteLayerType.GROUND); }
	
	public Layer(int layerIndex, SpriteLayerType layerType) {
		this(null);
		this.layerType = layerType;
		layer = layerIndex;
		buildLayer();
	}
	
	public Layer(List<String> tileInfos) {
		tilesMap = new HashMap<>();
		tilesProps = new HashMap<>();
		tilesTags = new HashMap<>();
		disabledTileTags = new HashSet<>();
		tileList = new ArrayList<>();
		if (tileInfos != null)
			for (String s : tileInfos) {
				String[] split = s.split(" ");
				if (!split[1].equals("-")) {
					try
						{ layerType = SpriteLayerType.valueOf(split[1]); }
					catch (Exception e)
						{ throw new RuntimeException(split[1] + " - Invalid SpriteLayerType param"); }
				}
				try
					{ layer = Integer.parseInt(split[0]); }
				catch (Exception e)
					{ throw new RuntimeException(split[0] + " - Invalid Layer param"); }
				Tile tile = new Tile(this, s);
				addTile(tile);
			}
	}
	
	public void buildLayer() {
		if (tilesMap.isEmpty()) {
			width = Main.TILE_SIZE * 3;
			height = Main.TILE_SIZE * 3;
			setLayerImage(new WritableImage(width, height));
			return;
		}
		int w, h, width = 0, height = 0;
		for (TileCoord coord : tilesMap.keySet())
			for (Tile tile : tilesMap.get(coord)) {
				if ((w = tile.getTileCoord().getX() * Main.TILE_SIZE + 16) > width)
					width = w;
				if ((h = tile.getTileCoord().getY() * Main.TILE_SIZE + 16) > height)
					height = h;
			}
		this.width = width;
		this.height = height;
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		gc.clearRect(0, 0, width, height);
		tilesMap.values().forEach(tiles ->
			tiles.forEach(tile -> {
				ImageUtils.drawImage(gc, MapSet.getTileSetImage(), tile.spriteX, tile.spriteY, 16, 16, tile.outX, tile.outY,
														 16, 16, tile.flip, tile.rotate, tile.opacity, tile.effects);
		}));
		if (getLayerImage() == null || (int)getLayerImage().getWidth() != width || (int)getLayerImage().getHeight() != height)
			setLayerImage(Tools.getCanvasSnapshot(canvas));
		else
			Tools.getCanvasSnapshot(canvas, getLayerImage());
	}
	
	public WritableImage getLayerImage()
		{ return Materials.tempSprites.get("Layer" + layer); }
	
	public void setLayerImage(WritableImage image)
		{ Materials.tempSprites.put("Layer" + layer, image); }
	
	public Map<TileCoord, List<Tile>> getTilesMap()
		{ return tilesMap; }

	public List<Tile> getTilesFromCoord(TileCoord coord)
		{ return tilesMap.get(coord); }
	
	public List<Tile> getTileList()
		{ return tileList; }

	public void setTilesMap(Map<TileCoord, List<Tile>> tilesMap) {
		this.tilesMap = new HashMap<>(tilesMap);
		tileList.clear();
		tilesMap.values().forEach(tiles ->
			tiles.forEach(tile -> tileList.add(tile)));
	}

	public void addTile(Tile tile)
		{ addTile(tile, tile.getTileCoord()); }
	
	public void addTile(Tile tile, TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			tilesMap.put(coord, new ArrayList<>());
		tilesMap.get(coord).add(tile);
		tileList.add(tile);
		tile.setCoord(coord);
	}
	
	public boolean haveTilesOnCoord(TileCoord coord)
		{ return tilesMap.containsKey(coord); } 
	
	public Tile getTopTileFromCoord(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		if (tilesMap.get(coord).isEmpty())
			throw new RuntimeException(coord + " - Tile is empty at this coordinate");
		return getTileFromCoord(coord, tilesMap.get(coord).size() - 1);
	}
	
	public Tile getFirstBottomTileFromCoord(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		if (tilesMap.get(coord).isEmpty())
			throw new RuntimeException(coord + " - Tile is empty at this coordinate");
		return getTileFromCoord(coord, 0);
	}

	public Tile getTileFromCoord(TileCoord coord, int tileIndex) {
		if (tileIndex < 0)
			throw new RuntimeException("Index must be 0 or higher");
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		if (tileIndex >= tilesMap.get(coord).size())
			throw new RuntimeException(tileIndex + " - Invalid index (Min 0, Max " + (tilesMap.get(coord).size() - 1) + ")");
		return tilesMap.get(coord).get(tileIndex);
	}
	
	public void removeFirstTileFromCoord(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		int i = tilesMap.get(coord).size() - 1;
		tileList.remove(tilesMap.get(coord).get(i));
		if (tilesMap.get(coord).size() == 1)
			tilesMap.remove(coord);
		else
			tilesMap.get(coord).remove(i);
	}
	
	public void removeAllTilesFromCoord(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		tileList.removeAll(tilesMap.get(coord));
		tilesMap.remove(coord);
	}
	
	public int getLayer()
		{ return layer; }
	
	public int getWidth()
		{ return width; }
	
	public int getHeight()
		{ return height	; }
	
	public SpriteLayerType getSpriteLayerType()
		{ return layerType; }
	
	public void setSpriteLayerType(SpriteLayerType layerType)
		{ this.layerType = layerType; }

	// ================ Metodos relacionados a TileProps ==============

	public boolean tileContainsProp(TileCoord coord, TileProp prop)
		{ return tilesProps.containsKey(coord) && tilesProps.get(coord).contains(prop); }
	
	public Map<TileCoord, List<TileProp>> getTilePropsMap()
		{ return tilesProps; }

	public boolean tileHaveProps(TileCoord coord)
		{ return getTotalTileProps(coord) > 0; }

	public List<TileProp> getTileProps(TileCoord coord) {
		if (!tilesProps.containsKey(coord))
			return null;
		return tilesProps.get(coord);
	}
	
	public int getTotalTileProps(TileCoord coord) {
		if (!tilesProps.containsKey(coord))
			return 0;
		return tilesProps.get(coord).size();
	} 
	
	public void setTileProps(TileCoord coord, List<TileProp> tileProps)
		{ tilesProps.put(coord, new ArrayList<>(tileProps)); }

	public void addTileProp(TileCoord coord, TileProp ... props) {
		if (!tilesProps.containsKey(coord))
			tilesProps.put(coord.getNewInstance(), new ArrayList<>());
		for (TileProp prop : props)
			tilesProps.get(coord).add(prop);
	}
	
	public void removeTileProp(TileCoord coord, TileProp ... props) {
		for (TileProp prop : props)
			tilesProps.get(coord).remove(prop);
		if (tilesProps.get(coord).isEmpty())
			tilesProps.get(coord).add(TileProp.NOTHING);
	}
	
	public void clearTileProps(TileCoord coord)
		{ tilesProps.put(coord, new ArrayList<>(Arrays.asList(TileProp.NOTHING))); }
	
	// ================ Metodos relacionados a TileTags ==============
	
	public boolean tileHaveTags(TileCoord coord)
		{ return tilesMap.containsKey(coord) && tilesTags.containsKey(coord); }
	
	public void disableTileTags(TileCoord coord)
		{ disabledTileTags.add(coord); }
	
	public void enableTileTags(TileCoord coord)
		{ disabledTileTags.remove(coord); }
	
	public boolean tileTagsIsDisabled(TileCoord coord)
		{ return disabledTileTags.contains(coord); }

	public Tags getTileTags(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		if (!tilesTags.containsKey(coord))
			throw new RuntimeException(coord + " - Tile have no tags");
		return tilesTags.get(coord).getFrameSetTagsFrom(0);
	}
	
	public String getStringTags(TileCoord coord) {
		if (MapSet.haveTilesOnCoord(coord))
			return MapSet.getFirstBottomTileFromCoord(coord).getStringTags();
		return null;
	}

	public FrameSet getTileTagsFrameSet(TileCoord coord)
		{ return tilesTags.get(coord); }

	public void setTileTagsFromString(TileCoord coord, String stringTileTags)
		{ setTileTagsFromString(coord, stringTileTags, getFirstBottomTileFromCoord(coord)); }
	
	public void setTileTagsFromString(TileCoord coord, String stringTileTags, Tile tile) {
		tilesTags.remove(coord);
		int x = coord.getX() * Main.TILE_SIZE, y = coord.getY() * Main.TILE_SIZE;
		FrameSet frameSet = new FrameSet(new Position(x, y));
		frameSet.loadFromString(stringTileTags);
		frameSet.getSprite(0).setOutputSpritePos(new Rectangle(x, y, 0, 0));
		tilesTags.put(coord.getNewInstance(), frameSet);
		if (tile != null)
			tile.stringTileTags = stringTileTags;
	}
	
	public void setTileTags(TileCoord coord, Tags tags) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		setTileTagsFromString(coord, tags.toString());
	}
	
	public void removeTileTag(TileCoord coord, String tagStr) {
		FrameTag tag = FrameTag.getFrameTagClassFromString(tilesTags.get(coord).getFrameSetTagsFrom(0).getTags(), tagStr);
		if (tag != null)
			removeTileTag(coord, tag);
	}
	
	public void removeTileTag(TileCoord coord, FrameTag tag) {
		if (!tilesMap.containsKey(coord))
			throw new RuntimeException(coord + " - Invalid tile coordinate");
		if (!tilesTags.containsKey(coord))
			throw new RuntimeException(coord + " - Tile have no tags");
		tilesTags.get(coord).getFrameSetTagsFrom(0).removeTag(tag);
		if (tilesTags.get(coord).getFrameSetTagsFrom(0).getTotalTags() == 0)
			clearTileTags(coord);
	}
	
	public void clearTileTags(TileCoord coord) {
		getFirstBottomTileFromCoord(coord).stringTileTags = null;
		tilesTags.remove(coord);
	}

}