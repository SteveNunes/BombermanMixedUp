package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Bomb;
import entities.Entity;
import entities.TileCoord;
import enums.Direction;
import enums.Elevation;
import enums.ItemType;
import enums.SpriteLayerType;
import enums.TileProp;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import objmoveutils.Position;
import pathfinder.PathFinder;
import pathfinder.PathFinderTileCoord;
import tools.GameMisc;
import tools.IniFiles;
import tools.Materials;
import util.IniFile;
import util.Misc;
import util.MyMath;

public class MapSet extends Entity{
	
	public static Map<SpriteLayerType, Map<Integer, WritableImage>> layerImages = null;

	private Map<Integer, Layer> layers;
	private Map<TileCoord, Integer> initialPlayerCoords;
	private Map<TileCoord, Integer> initialMonsterCoords;
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
		iniFileMap.getItemList("TILES").forEach(item -> {
			String line = iniFileMap.read("TILES", item);
			int layer = Integer.parseInt(line.split(" ")[0]);
			if (!tileInfos.containsKey(layer))
				tileInfos.put(layer, new ArrayList<>());
			tileInfos.get(layer).add(line);
		});
		tileInfos.keySet().forEach(i -> {
			Layer layer = new Layer(this, tileInfos.get(i));
			layers.put(i, layer);
			if (!layerImages.containsKey(layer.getSpriteLayerType()))
				layerImages.put(layer.getSpriteLayerType(), new HashMap<>());
			if (!layerImages.get(layer.getSpriteLayerType()).containsKey(i))
				layerImages.get(layer.getSpriteLayerType()).put(i, layer.getLayerImage());
			layer.getTileList().forEach(tile -> {
				if (tile.tileProp.contains(TileProp.PLAYER_INITIAL_POSITION))
					initialPlayerCoords.put(tile.getTileCoord(), initialPlayerCoords.size());
				else if (tile.tileProp.contains(TileProp.MOB_INITIAL_POSITION))
					initialMonsterCoords.put(tile.getTileCoord(), initialMonsterCoords.size());
			});
		});
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
		getLayer(26).getTileList().forEach(tile ->
			tile.tileProp.forEach(prop -> {
				if (prop == TileProp.FIXED_BRICK)
					addBricks.add(new Brick(this, tile.getTileCoord(), null));
			}));
		if (IniFiles.stages.read(iniMapName, "Blocks") != null && !IniFiles.stages.read(iniMapName, "Blocks").equals("0")) {
			int totalBricks = 0,
					bricksQuant = 0;
			int[] totalBrickSpawners = {0};
			try {
				String[] split = IniFiles.stages.read(iniMapName, "Blocks").split("!");
				int minBricks = Integer.parseInt(split[0]),
						maxBricks = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
						totalBricks = (int)MyMath.getRandom(minBricks, maxBricks);
						getLayer(26).getTileList().forEach(tile ->
							tile.tileProp.forEach(prop -> {
							if (prop == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(tile.getTileCoord()))
								totalBrickSpawners[0]++;
							}));
			}
			catch (Exception e)
				{ GameMisc.throwRuntimeException(IniFiles.stages.read(iniMapName, "Blocks") + " - Wrong data for this item"); }
			done:
			while (totalBricks > 0) {
				for (Tile t : getLayer(26).getTileList())
					for (TileProp p : t.tileProp)
						if (p == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(t.getTileCoord())) {
							if ((int)MyMath.getRandom(0, 3) == 0) {
								addBricks.add(new Brick(this, t.getTileCoord(), null));
								if (--totalBricks == 0 || ++bricksQuant >= totalBrickSpawners[0])
									break done;
							}
						}
			}
		}
		addBricks.sort((b1, b2) -> (int)b2.getY() - (int)b1.getY());
		addBricks.forEach(brick -> Brick.addBrick(brick, false));
		getLayer(26).buildLayer();
		addItensToBricks();
	}
	
	private void addItensToBricks() {
		if (IniFiles.stages.read(iniMapName, "Items") != null && !IniFiles.stages.read(iniMapName, "Items").equals("0")) {
			String[] split = IniFiles.stages.read(iniMapName, "Items").split(" ");
			try {
				for (int n = 0; n < split.length && n < Brick.totalBricks(); n++) {
					int itemId = Integer.parseInt(split[n]);
					Brick brick = null;
					do
						{ brick = Brick.getBricks().get((int)MyMath.getRandom(0, Brick.totalBricks() - 1));	}
					while (brick.getItem() != null);
					brick.setItem(ItemType.getItemById(itemId));
				}
			}
			catch (Exception e)
				{ GameMisc.throwRuntimeException(IniFiles.stages.read(iniMapName, "Items") + " - Wrong data for this item"); }
		}		
	}
	
	public void setRandomWalls() {
		if (IniFiles.stages.read(iniMapName, "FixedBlocks") != null && !IniFiles.stages.read(iniMapName, "FixedBlocks").equals("0")) {
			List<Tile> addWalls = new ArrayList<>();
			int totalWalls = 0;
			try {
				String[] split = IniFiles.stages.read(iniMapName, "FixedBlocks").split("!");
				int minWalls = Integer.parseInt(split[0]),
						maxWalls = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
				totalWalls = (int)MyMath.getRandom(minWalls, maxWalls);
			}
			catch (Exception e)
				{ GameMisc.throwRuntimeException(IniFiles.stages.read(iniMapName, "FixedBlocks") + " - Wrong data for this item"); }
			Map<Tile, List<TileProp>> recProp = new HashMap<>();
			done:
			while (totalWalls > 0) {
				List<TileCoord> coords = new ArrayList<>();
				for (TileCoord coord : getLayer(26).getTilesMap().keySet())
					coords.add(coord.getNewInstance());
				for (TileCoord coord : coords) {
					Tile tile = getLayer(26).getTopTileFromCoord(coord);
					if (tile.tileProp.contains(TileProp.GROUND) && (int)MyMath.getRandom(0, 9) == 0) {
						Tile tile2 = new Tile(this, (int)wallTile.getX(), (int)wallTile.getY(), coord.getX() * Main.tileSize, coord.getY() * Main.tileSize, new ArrayList<>(Arrays.asList(TileProp.WALL)));
						Tile tile3 = getLayer(26).getFirstBottomTileFromCoord(coord);
						List<TileProp> backupProps = tile3.tileProp;
						tile3.tileProp = Arrays.asList(TileProp.WALL);
						if (testCoordForInsertFixedBlock(coord)) {
							recProp .put(tile3, tile3.tileProp);
							addWalls.add(tile2);
							if (--totalWalls == 0)
								break done;
						}
						else
							tile3.tileProp = backupProps;
					}
				}
			}
			recProp.forEach((tile, props) -> tile.tileProp = props);
			addWalls.sort((t1, t2) -> (int)t2.outY - (int)t1.outY);
			addWalls.forEach(tile -> {
				TileCoord coord = tile.getTileCoord();
				getLayer(26).removeFirstTileFromCoord(coord);
				getLayer(26).addTile(tile);
				coord.setY(coord.getY() + 1);
				Tile.addTileShadow(this, groundWithWallShadow, coord);
			});
			getLayer(26).buildLayer();
		}
	}
	
	private boolean testCoordForInsertFixedBlock(TileCoord coord) {
		PathFinderTileCoord coord1 = new PathFinderTileCoord(),
												coord2 = new PathFinderTileCoord(),
												coord3 = new PathFinderTileCoord(coord.getX(), coord.getY());
		Direction dir1 = Direction.LEFT;
		for (int n = 0; n < 4; n++) {
			dir1 = dir1.getNext4WayClockwiseDirection();
			coord1.setCoord(coord3);
			coord1.incByDirection(dir1);
			Direction dir2 = Direction.LEFT;
			if (tileIsFree(new TileCoord(coord1.getX(), coord1.getY())))
				for (int n2 = 0; n2 < 4; n2++) {
					dir2 = dir2.getNext4WayClockwiseDirection();
					coord2.setCoord(coord3);
					coord2.incByDirection(dir2);
					if (dir1 != dir2 && tileIsFree(new TileCoord(coord2.getX(), coord2.getY()))) {
						PathFinder pf = new PathFinder(coord1, coord2, dir1, t -> tileIsFree(new TileCoord(t.getX(), t.getY())));
						if (!pf.pathWasFound())
							return false;
					}
				}
		}
		return true;
	}

	public TileCoord getInitialPlayerPosition(int playerIndex) {
		for (TileCoord coord : initialPlayerCoords.keySet())
			if (initialPlayerCoords.get(coord) == playerIndex)
				return coord;
		return null;
	}
	
	public TileCoord getInitialMonsterPosition(int monsterIndex) {
		for (TileCoord coord : initialMonsterCoords.keySet())
			if (initialMonsterCoords.get(coord) == monsterIndex)
				return coord;
		return null;
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

	public boolean tileIsFree(TileCoord coord) {
		if (!getLayer(26).haveTilesOnCoord(coord))
			return false;
		Tile tile = getLayer(26).getTopTileFromCoord(coord);
		for (TileProp prop : tile.tileProp)
			if (TileProp.getCantCrossList(Elevation.ON_GROUND).contains(prop) ||
					Brick.haveBrickAt(coord) || Bomb.haveBombAt(coord))
						return false;
		return true;
	}
	
}
