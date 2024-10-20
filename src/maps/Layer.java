package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.TileCoord;
import enums.SpriteLayerType;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import tools.Materials;
import tools.Tools;

public class Layer {
	
	private Map<TileCoord, List<Tile>> tilesMap;
	private Map<TileCoord, List<TileProp>> tilesProps;
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
		tileList = new ArrayList<>();
		if (tileInfos != null)
			for (String s : tileInfos) {
				String[] split = s.split(" ");
				try
					{ layerType = SpriteLayerType.valueOf(split[1]); }
				catch (Exception e)
					{ throw new RuntimeException(split[1] + " - Invalid SpriteLayerType param"); }
				try
					{ layer = Integer.parseInt(split[0]); }
				catch (Exception e)
					{ throw new RuntimeException(split[0] + " - Invalid Layer param"); }
				Tile tile = new Tile(this, s);
				addTile(tile);
			}
	}
	
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
	
	public void replaceTileProps(TileCoord coord, List<TileProp> newTileProps)
		{ tilesProps.put(coord, new ArrayList<>(newTileProps)); }

	public boolean tileContainsProp(TileCoord coord, TileProp prop)
		{ return tilesProps.containsKey(coord) && tilesProps.get(coord).contains(prop); }
	
	public Map<TileCoord, List<TileProp>> getTilePropsMap()
		{ return tilesProps; }

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
				if ((w = tile.getTileX() * Main.TILE_SIZE + 16) > width)
					width = w;
				if ((h = tile.getTileY() * Main.TILE_SIZE + 16) > height)
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

}