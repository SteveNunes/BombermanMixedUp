package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.TileCoord;
import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import tools.GameMisc;

public class Layer {
	
	private Map<TileCoord, List<Tile>> tilesMap;
	private List<Tile> tileList;
	private WritableImage layerImage;
	private MapSet originMapSet;
	private int layer;
	
	public Layer(MapSet originMapSet, List<String> tileInfos) {
		this.originMapSet = originMapSet;
		tilesMap = new HashMap<>();
		tileList = new ArrayList<>();
		for (String s : tileInfos) {
			Tile tile = new Tile(originMapSet, s);
			addTile(tile.getTileCoord(), tile);
		}
		buildLayer();
	}
	
	public void buildLayer() {
		int w, h, width = 0, height = 0;
		for (TileCoord coord : tilesMap.keySet())
			for (Tile tile : tilesMap.get(coord)) {
				if ((w = tile.getTileX() * Main.tileSize + 16) > width)
					width = w;
				if ((h = tile.getTileY() * Main.tileSize + 16) > height)
					height = h;
			}
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().setImageSmoothing(false);
		for (TileCoord coord : tilesMap.keySet())
			for (Tile tile : tilesMap.get(coord))
				ImageUtils.drawImage(gc, originMapSet.getTileSetImage(),
														 tile.spriteX, tile.spriteY, 16, 16,
														 tile.outX, tile.outY, 16, 16,
														 tile.flip, tile.rotate, tile.opacity, tile.effects);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		layerImage = canvas.snapshot(params, null);
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

	public void setTilesMap(Map<TileCoord, List<Tile>> tilesMap)
		{ this.tilesMap = new HashMap<>(tilesMap); }	

	public void addTile(TileCoord coord, Tile tile) {
		if (!tilesMap.containsKey(coord))
			tilesMap.put(coord, new ArrayList<>());
		tilesMap.get(coord).add(tile);
		tileList.add(tile);
	}
	
	public boolean haveTilesOnCoord(TileCoord coord)
		{ return tilesMap.containsKey(coord); } 
	
	public Tile getFirstTileFromCoord(TileCoord coord)
		{ return getTileFromCoord(coord, 0); }
	
	public Tile getTileFromCoord(TileCoord coord, int tileIndex) {
		if (tileIndex < 0)
			GameMisc.throwRuntimeException("Index must be 0 or higher");
		if (!tilesMap.containsKey(coord))
			GameMisc.throwRuntimeException(coord + " - Invalid tile coordinate");
		if (tileIndex >= tilesMap.get(coord).size())
			GameMisc.throwRuntimeException(tileIndex + " - Invalid index (Min 0, Max " + (tilesMap.get(coord).size() - 1) + ")");
		return tilesMap.get(coord).get(tileIndex);
	}
	
	public void removeFirstTileFromCoord(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			GameMisc.throwRuntimeException(coord + " - Invalid tile coordinate");
		int i = tilesMap.get(coord).size() - 1;
		tileList.remove(tilesMap.get(coord).get(i));
		if (tilesMap.get(coord).size() == 1)
			tilesMap.remove(coord);
		else
			tilesMap.get(coord).remove(i);
	}
	
	public void removeAllTilesFromCoord(TileCoord coord) {
		if (!tilesMap.containsKey(coord))
			GameMisc.throwRuntimeException(coord + " - Invalid tile coordinate");
		tileList.removeAll(tilesMap.get(coord));
		tilesMap.remove(coord);
	}
	
	public MapSet getOriginMapSet()
		{ return originMapSet; }

	public int getLayer()
		{ return layer; }

	public int getWidth()
		{ return (int)layerImage.getWidth(); }

	public int getHeight()
		{ return (int)layerImage.getHeight(); }

}