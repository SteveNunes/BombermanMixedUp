package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.TileCoord;
import enums.SpriteLayerType;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import tools.Materials;

public class Layer {
	
	private Map<TileCoord, List<Tile>> tilesMap;
	private List<Tile> tileList;
	private WritableImage layerImage;
	private int layer;
	private SpriteLayerType layerType;
	
	public Layer(List<String> tileInfos) {
		tilesMap = new HashMap<>();
		tileList = new ArrayList<>();
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
			Tile tile = new Tile(s);
			addTile(tile);
		}
	}
	
	public void buildLayer() {
		int w, h, width = 0, height = 0;
		for (TileCoord coord : tilesMap.keySet())
			for (Tile tile : tilesMap.get(coord)) {
				if ((w = tile.getTileX() * Main.TILE_SIZE + 16) > width)
					width = w;
				if ((h = tile.getTileY() * Main.TILE_SIZE + 16) > height)
					height = h;
			}
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		tilesMap.values().forEach(tiles ->
			tiles.forEach(tile ->
				ImageUtils.drawImage(gc, MapSet.getTileSetImage(),
														 tile.spriteX, tile.spriteY, 16, 16,
														 tile.outX, tile.outY, 16, 16,
														 tile.flip, tile.rotate,
														 tile.opacity, tile.effects)));
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		layerImage = canvas.snapshot(params, null);
		Materials.tempSprites.put("Layer" + layer, layerImage);
	}
	
	public WritableImage getLayerImage()
		{ return layerImage; }
	
	public void setLayerImage(WritableImage image)
		{ layerImage = image; }
	
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

	public void addTile(Tile tile) {
		if (!tilesMap.containsKey(tile.getTileCoord()))
			tilesMap.put(tile.getTileCoord(), new ArrayList<>());
		else
			tile.tileProp = new ArrayList<>(Arrays.asList(TileProp.NOTHING));
		tilesMap.get(tile.getTileCoord()).add(tile);
		tileList.add(tile);
	}
	
	public boolean haveTilesOnCoord(TileCoord coord)
		{ return tilesMap.containsKey(coord); } 
	
	public Tile getTopTileFromCoord(TileCoord coord)
		{ return getTileFromCoord(coord, 0); }
	
	public Tile getFirstBottomTileFromCoord(TileCoord coord)
		{ return getTileFromCoord(coord, tilesMap.get(coord).size() - 1); }

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
		{ return (int)layerImage.getWidth(); }

	public int getHeight()
		{ return (int)layerImage.getHeight(); }
	
	public SpriteLayerType getSpriteLayerType()
		{ return layerType; }

	public void setSpriteLayerType(SpriteLayerType layerType)
		{ this.layerType = layerType; }

}