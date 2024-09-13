package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Entity;
import enums.SpriteLayerType;
import enums.TileProp;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import objmoveutils.Position;
import tools.GameMisc;
import tools.IniFiles;
import tools.Materials;
import util.IniFile;

public class MapSet extends Entity{
	
	public static Map<SpriteLayerType, Map<Integer, WritableImage>> layerImages = null;

	private Map<Integer, Layer> layers;
	private IniFile iniFileMap;
	private IniFile iniFileTileSet;
	private Image tileSetImage;
	private Integer copyImageLayer;
	private String mapName;
	private String iniMapName;
	private String tileSetName;
	private Position groundTile;
	private Position wallTile;
	private Position groundWithBrickShadow;
	private Position groundWithWallShadow;
	private Position fragileGround;
	
	public MapSet(String iniMapName) {
		layerImages = new HashMap<>();
		setTileSize(Main.tileSize);
		this.iniMapName = iniMapName;
		layers = new HashMap<>();
		mapName = IniFiles.stages.read(iniMapName, "File");
		iniFileMap = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		if (iniFileMap == null)
			GameMisc.throwRuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
		tileSetName = iniFileMap.read("SETUP", "Tiles");
		iniFileTileSet = IniFile.getNewIniFileInstance("appdata/tileset/" + tileSetName + ".tiles");
		if (iniFileTileSet == null)
			GameMisc.throwRuntimeException("Unable to load map \"" + mapName + "\" (Invalid TileSet (" + tileSetName + ".tiles))");
		tileSetImage = Materials.tileSets.get(tileSetName);
		Map<Integer, List<String>> tileInfos = new HashMap<>();
		copyImageLayer = iniFileMap.readAsInteger("SETUP", "ImageLayer", null);
		for (String item : iniFileMap.getItemList("TILES")) {
			String line = iniFileMap.read("TILES", item);
			int layer = Integer.parseInt(line.split(" ")[0]);
			if (!tileInfos.containsKey(layer))
				tileInfos.put(layer, new ArrayList<>());
			tileInfos.get(layer).add(line);
		}
		for (Integer i : tileInfos.keySet()) {
			Layer layer = new Layer(this, tileInfos.get(i));
			layers.put(i, layer);
			if (!layerImages.containsKey(layer.getSpriteLayerType()))
				layerImages.put(layer.getSpriteLayerType(), new HashMap<>());
			if (!layerImages.get(layer.getSpriteLayerType()).containsKey(i))
				layerImages.get(layer.getSpriteLayerType()).put(i, layer.getLayerImage());
		}
		groundTile = getTilePositionFromIni(iniFileMap, "GroundTile");
		groundWithBrickShadow = getTilePositionFromIni(iniFileMap, "GroundWithBrickShadow");
		groundWithWallShadow = getTilePositionFromIni(iniFileMap, "GroundWithWallShadow");
		wallTile = getTilePositionFromIni(iniFileMap, "WallTile");
		fragileGround = getTilePositionFromIni(iniFileMap, "FragileGround");
		if (groundWithBrickShadow == null && groundWithWallShadow != null)
			groundWithBrickShadow = new Position(groundWithWallShadow);
		if (groundWithWallShadow == null && groundWithBrickShadow != null)
			groundWithWallShadow = new Position(groundWithBrickShadow);
		//setFrameSet("DefaultFrameSet"); // REABILITAR LINHA DEPOIS DE IMPLEMENTAR FRAMESET DE MAPA
		setBricks();
	}
	
	public void setTileSet(String tileSetName) {
		this.tileSetName = tileSetName; 
		tileSetImage = Materials.tileSets.get(tileSetName);
		iniFileTileSet = IniFile.getNewIniFileInstance("appdata/tileset/" + tileSetName + ".tiles");
		for (Layer layer : layers.values())
			layer.buildLayer();
		setBricks();
	}

	public void setBricks() {
		Brick.clearBricks();
		for (Tile t : getLayer(26).getTileList())
			for (TileProp p : t.tileProp)
				if (p == TileProp.FIXED_BRICK)
					Brick.addBrick(this, t.getTileCoord(), null);
		if (IniFiles.stages.read(iniMapName, "Blocks") != null && !IniFiles.stages.read(iniMapName, "Blocks").equals("0")) {
			String[] split = IniFiles.stages.read(iniMapName, "Blocks").split("!");
			int minBricks = Integer.parseInt(split[0]);
			int maxBricks = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
			int totalBricks = GameMisc.getRandom(minBricks, maxBricks);
			int bricksQuant = 0;
			while (totalBricks > 0) {
				int totalBrickSpawners = 0;
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
	
	public IniFile getMapIniFile()
		{ return iniFileMap; }
	
	public IniFile getTileSetIniFile()
		{ return iniFileTileSet; }

	private Position getTilePositionFromIni(IniFile ini, String tileStr) {
		Position position = new Position();
		if (iniFileTileSet.read("CONFIG", tileStr) == null)
			return null;
		String[] split2 = iniFileTileSet.read("CONFIG", tileStr).split(" ");
		if (split2.length > 0) {
			try
				{ position.setPosition(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])); }
			catch (Exception e)
				{ GameMisc.throwRuntimeException(iniFileTileSet.read("CONFIG", tileStr) + " - Invalid data on file \"" + iniFileTileSet.getFilePath().getFileName() + "\""); }
		}
		return position;
	}

	public Position getGroundTile()
		{ return groundTile; }
	
	public Position getWallTile()
		{ return wallTile; }
	
	public Position getGroundWithBrickShadow()
		{ return groundWithBrickShadow; }
	
	public Position getGroundWithWallShadow()
		{ return groundWithWallShadow; }

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
