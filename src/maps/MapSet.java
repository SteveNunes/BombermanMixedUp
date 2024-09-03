package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import tools.MapEditor;
import tools.Materials;
import util.IniFile;
import util.Misc;

public class MapSet {
	
	private Map<Integer, Layer> layers;
	private List<Bricks> bricks;
	private Image tileSetImage;
	private WritableImage copyImageLayer;
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
		Map<Integer, LayerInfo> layerInfos = new HashMap<>();
		Map<Integer, List<String>> tileInfos = new HashMap<>();
		Integer imageLayer = ini.readAsInteger("SETUP", "ImageLayer", null);
		for (String item : ini.getItemList("TILES")) {
			String line = ini.read("TILES", item);
			TileSpriteInfos info = new TileSpriteInfos(line);
			if (!layerInfos.containsKey(info.layer)) {
				layerInfos.put(info.layer, new LayerInfo(info.layer, ini.read("WININFO", "" + info.layer)));
				tileInfos.put(info.layer, new ArrayList<>());
			}
			layerInfos.get(info.layer).update(info);
			tileInfos.get(info.layer).add(line);
			if (info.layer > maxLayer)
				maxLayer = info.layer;
			if (info.layer < minLayer)
				minLayer = info.layer;
		}
		for (Integer i : tileInfos.keySet()) {
			Layer layer = new Layer(this, layerInfos.get(i), tileInfos.get(i));
			layers.put(i, layer);
			if (imageLayer != null && i == imageLayer)
				copyImageLayer = layer.getLayerImage();
		}
	}

	public Map<Integer, Layer> getLayersMap()
		{ return layers; }

	public Image getTileSetImage()
		{ return tileSetImage; }

	public void draw(GraphicsContext gc) {
		if (!Misc.alwaysTrue())
			for (int l = minLayer; l <= maxLayer; l++)
				if (layers.containsKey(l) && layers.get(l).getLayerImage() != copyImageLayer)
					layers.get(l).draw(gc);
		if (layers.containsKey(MapEditor.getCurrentLayer()))
			layers.get(MapEditor.getCurrentLayer()).draw(gc);
	}
	
}
