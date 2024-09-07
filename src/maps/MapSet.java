package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import objmoveutils.Position;
import tools.GameMisc;
import tools.Materials;
import util.IniFile;

public class MapSet {
	
	private Map<Integer, Layer> layers;
	private List<Brick> bricks;
	private Image tileSetImage;
	private Integer copyImageLayer;
	private String mapName;
	private Position groundTile;
	private Position wallTile;
	private Position groundWithBlockShadow;
	private Position groundWithWallShadow;
	private int minLayer;
	private int maxLayer;
	
	public MapSet(String mapName) {
		this.mapName = mapName;
		layers = new HashMap<>();
		bricks = new ArrayList<>();
		minLayer = 9999;
		maxLayer = 0;
		IniFile ini = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		if (ini == null)
			GameMisc.throwRuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
		tileSetImage = Materials.tileSets.get(ini.read("SETUP", "Tiles"));
		Map<Integer, String> layerInfos = new HashMap<>();
		Map<Integer, List<String>> tileInfos = new HashMap<>();
		copyImageLayer = ini.readAsInteger("SETUP", "ImageLayer", null);
		for (String item : ini.getItemList("TILES")) {
			String line = ini.read("TILES", item);
			int layer = Integer.parseInt(line.split(" ")[0]);
			if (!layerInfos.containsKey(layer)) {
				layerInfos.put(layer, ini.read("WININFO", "" + layer));
				tileInfos.put(layer, new ArrayList<>());
			}
			tileInfos.get(layer).add(line);
			if (layer > maxLayer)
				maxLayer = layer;
			if (layer < minLayer)
				minLayer = layer;
		}
		for (Integer i : tileInfos.keySet()) {
			Layer layer = new Layer(this, layerInfos.get(i), tileInfos.get(i));
			layers.put(i, layer);
		}
		groundTile = getTilePositionFromIni(ini, "GroundTile");
		groundWithBlockShadow = getTilePositionFromIni(ini, "GroundWithShadowFromBlock");
		groundWithWallShadow = getTilePositionFromIni(ini, "GroundWithShadowFromWall");
		wallTile = getTilePositionFromIni(ini, "HurryUpTile");
	}
	
	private Position getTilePositionFromIni(IniFile ini, String tileStr) {
		Position position = new Position();
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/tileset/" + ini.read("SETUP", "Tiles") + ".tiles");
		String[] split2 = ini2.read("CONFIG", tileStr).split(" ");
		if (split2.length > 0) {
			try
				{ position.setPosition(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])); }
			catch (Exception e) {
				throw new RuntimeException(ini2.read("CONFIG", tileStr) + " - Invalid data on file \"" + ini2.getFilePath().getFileName() + "\"");
			}
		}
		return position;
	}

	public Position getGroundTile()
		{ return groundTile; }
	
	public Position getWallTile()
		{ return wallTile; }
	
	public Position getGroundWithBlockShadow()
		{ return groundWithBlockShadow; }
	
	public Position getGroundWithWallShadow()
		{ return groundWithWallShadow; }

	public Map<Integer, Layer> getLayersMap()
		{ return layers; }
	
	public Layer getLayer(int layerIndex)
		{ return layers.get(layerIndex); }
	
	public Integer getCopyLayer()
		{ return copyImageLayer; }

	public Image getTileSetImage()
		{ return tileSetImage; }
	
	public void setTileSetImage(Image image)
		{ tileSetImage = image; }
	
	public String getMapName()
		{ return mapName; }

	public void draw(GraphicsContext gc) {
		for (int l = minLayer; l <= maxLayer; l++)
			if (layers.containsKey(l) && l != copyImageLayer)
				layers.get(l).draw(gc);
	}

	public Tile getTileAt(int layerIndex, int x, int y) {
		for (Tile tile : getLayer(layerIndex).getTiles())
			if (tile.getTileDX() == x && tile.getTileDY() == y)
				return tile;
		return null;
	}
	
}
