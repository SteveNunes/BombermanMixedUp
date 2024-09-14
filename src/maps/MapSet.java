package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Entity;
import entities.TileCoord;
import enums.SpriteLayerType;
import enums.TileProp;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import objmoveutils.Position;
import tools.GameMisc;
import tools.IniFiles;
import tools.Materials;
import util.IniFile;
import util.Misc;

public class MapSet extends Entity{
	
	public static Map<SpriteLayerType, Map<Integer, WritableImage>> layerImages = null;

	private Map<Integer, Layer> layers;
	private Map<Integer, TileCoord> initialPlayerCoords;
	private Map<Integer, TileCoord> initialMonsterCoords;
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
		long ct = System.currentTimeMillis();
		System.out.println("Carregando mapa " + iniMapName + " ...");
		layerImages = new HashMap<>();
		setTileSize(Main.tileSize);
		this.iniMapName = iniMapName;
		layers = new HashMap<>();
		initialPlayerCoords = new HashMap<>();
		initialMonsterCoords = new HashMap<>();
		mapName = IniFiles.stages.read(iniMapName, "File");
		iniFileMap = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		if (iniFileMap == null)
			GameMisc.throwRuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
		setTileSet(iniFileMap.read("SETUP", "Tiles"));
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
		groundWithBrickShadow = Misc.notNull(getTilePositionFromIni(iniFileMap, "GroundWithBrickShadow"), new Position(groundTile));
		groundWithWallShadow = Misc.notNull(getTilePositionFromIni(iniFileMap, "GroundWithWallShadow"), new Position(groundTile));
		wallTile = getTilePositionFromIni(iniFileMap, "WallTile");
		fragileGround = getTilePositionFromIni(iniFileMap, "FragileGround");
		//setFrameSet("DefaultFrameSet"); // NOTA: REABILITAR LINHA DEPOIS DE IMPLEMENTAR FRAMESET DE MAPA
		setRandomWalls();
		rebuildAllLayers();
		setBricks();
		System.out.println("... Concluído em " + (System.currentTimeMillis() - ct) + "ms");
	}
	
	public boolean entityCanCrossTileAt(Entity entity, TileCoord coord) {
		for (Tile tile : getLayer(26).getTilesFromCoord(coord))
			for (TileProp prop : tile.tileProp)
				if (!prop.isCrossableBy(entity.getElevation()))
					return false;
		return true;
	}

	public void setTileSet(String tileSetName) {
		this.tileSetName = tileSetName; 
		iniFileTileSet = IniFile.getNewIniFileInstance("appdata/tileset/" + tileSetName + ".tiles");
		if (iniFileTileSet == null)
			GameMisc.throwRuntimeException("Unable to load map \"" + mapName + "\" (Invalid TileSet (" + tileSetName + ".tiles))");
		tileSetImage = Materials.tileSets.get(tileSetName);
	}

	public void setBricks() {
		Brick.clearBricks();
		List<Brick> addBricks = new ArrayList<>();
		for (Tile t : getLayer(26).getTileList())
			for (TileProp p : t.tileProp)
				if (p == TileProp.FIXED_BRICK)
					addBricks.add(new Brick(this, t.getTileCoord(), null));
		if (IniFiles.stages.read(iniMapName, "Blocks") != null && !IniFiles.stages.read(iniMapName, "Blocks").equals("0")) {
			String[] split = IniFiles.stages.read(iniMapName, "Blocks").split("!");
			int minBricks = Integer.parseInt(split[0]),
					maxBricks = Integer.parseInt(split[split.length == 1 ? 0 : 1]),
					totalBricks = GameMisc.getRandom(minBricks, maxBricks),
					bricksQuant = 0;
			int totalBrickSpawners = 0;
			for (Tile t : getLayer(26).getTileList())
				for (TileProp p : t.tileProp)
					if (p == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(t.getTileCoord()))
						totalBrickSpawners++;
			done:
			while (totalBricks > 0) {
				for (Tile t : getLayer(26).getTileList())
					for (TileProp p : t.tileProp)
						if (p == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(t.getTileCoord())) {
							if (GameMisc.getRandom(0, 3) == 0) {
								addBricks.add(new Brick(this, t.getTileCoord(), null));
								if (--totalBricks == 0 || ++bricksQuant >= totalBrickSpawners)
									break done;
							}
						}
			}
		}
		addBricks.sort((b1, b2) -> (int)b2.getY() - (int)b1.getY());
		addBricks.forEach(brick -> Brick.addBrick(brick, false));
		getLayer(26).buildLayer();
	}
	
	public void setRandomWalls() {
		if (IniFiles.stages.read(iniMapName, "FixedBlocks") != null && !IniFiles.stages.read(iniMapName, "FixedBlocks").equals("0")) {
			List<Position> addWalls = new ArrayList<>();
			String[] split = IniFiles.stages.read(iniMapName, "FixedBlocks").split("!");
			int minWalls = Integer.parseInt(split[0]),
					maxWalls = Integer.parseInt(split[split.length == 1 ? 0 : 1]),
					totalWalls = GameMisc.getRandom(minWalls, maxWalls);
			done:
			while (totalWalls > 0) {
				for (TileCoord coord : getLayer(26).getTilesMap().keySet()) {
					Tile tile = getLayer(26).getFirstTileFromCoord(coord);
					if (tile.tileProp.contains(TileProp.GROUND) && GameMisc.getRandom(0, 9) == 0) {
						addWalls.add(new Position(tile.outX, tile.outY));
						if (--totalWalls == 0)
							break done;
					}
				}
			}
			addWalls.sort((p1, p2) -> (int)p2.getY() - (int)p1.getY());
			addWalls.forEach(pos -> {
				Position wallTile = getWallTile();
				Tile tile = new Tile(this, (int)wallTile.getX(), (int)wallTile.getY(), (int)pos.getX(), (int)pos.getY(), new ArrayList<>(Arrays.asList(TileProp.WALL)));
				getLayer(26).removeFirstTileFromCoord(tile.getTileCoord());
				getLayer(26).addTile(tile);
				TileCoord coord = new TileCoord(tile.getTileCoord());
				coord.setY(coord.getY() + 1);
				Tile.addTileShadow(this, groundWithWallShadow, coord);
				/* NOTA:
				 * Apos implementar as saidas, criar um metodo que passa o ponto inicial
				 * do jogador e a coordenada da saida, para verificar se tem como chegar
				 * do ponto inicial ate a saida, e realizar esse teste apos definir
				 * cada nova wall, para evitar que bloqueie a saida ao gerar as paredes aleatorias.
				 * Testar tambem acesso aos tijolos, para evitar de gerar tijolo dentro de uma
				 * area inacessivel. O teste do tijolo deve ser realizado ao tentar gerar cada
				 * tijolo aleatorio, verificando se o jogador consegue chegar até o tijolo andando.
				 */
			});
			getLayer(26).buildLayer();
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
	
	public void rebuildAllLayers()
		{ layers.values().forEach(layer -> layer.buildLayer()); }
	
	public void setGroundTile(Position groundTile)
		{ this.groundTile = new Position(groundTile); }

	public void setWallTile(Position wallTile)
		{ this.wallTile = new Position(wallTile); }

	public void setGroundWithBrickShadow(Position groundWithBrickShadow)
		{ this.groundWithBrickShadow = new Position(groundWithBrickShadow); }

	public void setGroundWithWallShadow(Position groundWithWallShadow)
		{ this.groundWithWallShadow = new Position(groundWithWallShadow); }

	public void setFragileGround(Position fragileGround)
		{ this.fragileGround = new Position(fragileGround); }

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
