package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Entity;
import enums.TileProp;
import javafx.scene.image.Image;
import objmoveutils.Position;
import tools.GameMisc;
import tools.Materials;
import util.IniFile;

public class MapSet extends Entity{
	
	private Map<Integer, Layer> layers;
	private Image tileSetImage;
	private Integer copyImageLayer;
	private String mapName;
	private String iniMapName;
	private String tileSetName;
	private Position groundTile;
	private Position wallTile;
	private Position groundWithBrickShadow;
	private Position fragileGround;
	private int minLayer;
	private int maxLayer;
	
	public MapSet(String iniMapName) {
		Brick.clearBricks();
		setTileSize(Main.tileSize);
		this.iniMapName = iniMapName;
		layers = new HashMap<>();
		minLayer = 9999;
		maxLayer = 0;
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Stages.cfg");
		mapName = ini.read(iniMapName, "File");
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		if (ini2 == null)
			GameMisc.throwRuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
		tileSetName = ini2.read("SETUP", "Tiles");
		tileSetImage = Materials.tileSets.get(tileSetName);
		Map<Integer, List<String>> tileInfos = new HashMap<>();
		copyImageLayer = ini2.readAsInteger("SETUP", "ImageLayer", null);
		for (String item : ini2.getItemList("TILES")) {
			String line = ini2.read("TILES", item);
			int layer = Integer.parseInt(line.split(" ")[0]);
			if (!tileInfos.containsKey(layer))
				tileInfos.put(layer, new ArrayList<>());
			tileInfos.get(layer).add(line);
			if (layer > maxLayer)
				maxLayer = layer;
			if (layer < minLayer)
				minLayer = layer;
		}
		for (Integer i : tileInfos.keySet()) {
			Layer layer = new Layer(this, tileInfos.get(i));
			layers.put(i, layer);
		}
		groundTile = getTilePositionFromIni(ini2, "GroundTile");
		groundWithBrickShadow = getTilePositionFromIni(ini2, "GroundWithBrickShadow");
		wallTile = getTilePositionFromIni(ini2, "WallTile");
		fragileGround = getTilePositionFromIni(ini2, "FragileGround");
		//setFrameSet("DefaultFrameSet"); // REABILITAR LINHA DEPOIS DE IMPLEMENTAR FRAMESET DE MAPA
		if (ini.read(iniMapName, "Blocks") != null && !ini.read(iniMapName, "Blocks").equals("0")) {
			String[] split = ini.read(iniMapName, "Blocks").split("!");
			int minBricks = Integer.parseInt(split[0]);
			int maxBricks = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
			int totalBricks = GameMisc.getRandom(minBricks, maxBricks);
			int bricksQuant = 0;
			while (totalBricks > 0) {
				int totalBrickSpawners = 0;
				System.out.println(mapName);
				for (Tile t : getLayer(26).getTileList())
					for (TileProp p : t.tileProp)
						if (p == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(t.getTileCoord())) {
							totalBrickSpawners++;
							if (GameMisc.getRandom(0, 3) == 0) {
								Brick.addBrick(this, t.getTileCoord(), null);
								if (++bricksQuant == totalBricks)
									return;
							}
						}
				if (bricksQuant >= totalBrickSpawners)
					return;
			}
		}
	}
	
	private Position getTilePositionFromIni(IniFile ini, String tileStr) {
		Position position = new Position();
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/tileset/" + ini.read("SETUP", "Tiles") + ".tiles");
		if (ini2.read("CONFIG", tileStr) == null)
			return null;
		String[] split2 = ini2.read("CONFIG", tileStr).split(" ");
		if (split2.length > 0) {
			try
				{ position.setPosition(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])); }
			catch (Exception e)
				{ GameMisc.throwRuntimeException(ini2.read("CONFIG", tileStr) + " - Invalid data on file \"" + ini2.getFilePath().getFileName() + "\""); }
		}
		return position;
	}

	public Position getGroundTile()
		{ return groundTile; }
	
	public Position getWallTile()
		{ return wallTile; }
	
	public Position getGroundWithBlockShadow()
		{ return groundWithBrickShadow; }
	
	public Position getFragileGround()
		{ return fragileGround; }
	
	public Map<Integer, Layer> getLayersMap()
		{ return layers; }
	
	public Layer getLayer(int layerIndex) {
		if (!layers.containsKey(layerIndex))
			GameMisc.throwRuntimeException(layerIndex + " - Invalid layer index");
		return layers.get(layerIndex);
	}
	
	public Integer getCopyLayer()
		{ return copyImageLayer; }

	public Image getTileSetImage()
		{ return tileSetImage; }
	
	public void setTileSetImage(Image image)
		{ tileSetImage = image; }
	
	public String getTileSetName()
		{ return tileSetName; }
		
	public String getMapName()
		{ return mapName; }

	public String getIniMapName()
		{ return iniMapName; }

}
