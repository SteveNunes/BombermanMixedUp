package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.GameMode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import tools.MapEditor;
import tools.Materials;
import util.IniFile;

public class MapSet {
	
	private Map<Integer, Layer> layers;
	private List<Brick> bricks;
	private Image tileSetImage;
	private Integer copyImageLayer;
	private String mapName;
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
			throw new RuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
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
	}

	public Map<Integer, Layer> getLayersMap()
		{ return layers; }
	
	public Integer getCopyLayer()
		{ return copyImageLayer; }

	public Image getTileSetImage()
		{ return tileSetImage; }
	
	public String getMapName()
		{ return mapName; }

	public void draw(GraphicsContext gc) {
		if (Main.mode != GameMode.MAP_EDITOR) {
			for (int l = minLayer; l <= maxLayer; l++)
				if (layers.containsKey(l) && l != copyImageLayer)
					layers.get(l).draw(gc);
		}
		else if (layers.containsKey(MapEditor.getCurrentLayer()))
			layers.get(MapEditor.getCurrentLayer()).draw(gc);
	}
	
}
