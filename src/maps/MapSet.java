package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Bomb;
import entities.Effect;
import entities.TileCoord;
import enums.Direction;
import enums.Elevation;
import enums.ItemType;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.TileProp;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import objmoveutils.Position;
import pathfinder.PathFinder;
import pathfinder.PathFinderTileCoord;
import tools.Tools;
import tools.IniFiles;
import tools.Materials;
import util.IniFile;
import util.Misc;
import util.MyMath;

public class MapSet {
	
	public static Map<SpriteLayerType, Map<Integer, WritableImage>> layerImages = null;
	private static Map<Integer, Layer> layers;
	private static Map<TileCoord, Integer> initialPlayerCoords;
	private static Map<TileCoord, Integer> initialMonsterCoords;
	private static IniFile iniFileMap;
	private static IniFile iniFileTileSet;
	private static Image tileSetImage;
	private static Integer copyImageLayer;
	private static String mapName;
	private static String iniMapName;
	private static String tileSetName;
	private static Position groundTile;
	private static Position wallTile;
	private static Position groundWithBrickShadow;
	private static Position groundWithWallShadow;
	private static Position fragileGround;
	
	public static void loadMap(String iniMapName) {
		long ct = System.currentTimeMillis();
		System.out.println("Carregando mapa " + iniMapName + " ...");
		Effect.clearPreloadedEffects();
		MapSet.iniMapName = iniMapName;
		layerImages = new HashMap<>();
		layers = new HashMap<>();
		initialPlayerCoords = new HashMap<>();
		initialMonsterCoords = new HashMap<>();
		mapName = IniFiles.stages.read(iniMapName, "File");
		iniFileMap = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		if (iniFileMap == null)
			throw new RuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
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
			Layer layer = new Layer(tileInfos.get(i));
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
	
	public static void setTileSet(String tileSetName) {
		MapSet.tileSetName = tileSetName; 
		iniFileTileSet = IniFile.getNewIniFileInstance("appdata/tileset/" + tileSetName + ".tiles");
		if (iniFileTileSet == null)
			throw new RuntimeException("Unable to load map \"" + mapName + "\" (Invalid TileSet (" + tileSetName + ".tiles))");
		tileSetImage = Materials.tileSets.get(tileSetName);
	}

	public static void setBricks() {
		Brick.clearBricks();
		List<Brick> addBricks = new ArrayList<>();
		getLayer(26).getTileList().forEach(tile ->
			tile.tileProp.forEach(prop -> {
				if (prop == TileProp.FIXED_BRICK)
					addBricks.add(new Brick(tile.getTileCoord(), null));
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
				{ throw new RuntimeException(IniFiles.stages.read(iniMapName, "Blocks") + " - Wrong data for this item"); }
			done:
			while (totalBricks > 0) {
				for (Tile t : getLayer(26).getTileList())
					for (TileProp p : t.tileProp)
						if (p == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(t.getTileCoord())) {
							if ((int)MyMath.getRandom(0, 3) == 0) {
								addBricks.add(new Brick(t.getTileCoord(), null));
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
	
	private static void addItensToBricks() {
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
				{ throw new RuntimeException(IniFiles.stages.read(iniMapName, "Items") + " - Wrong data for this item"); }
		}		
	}
	
	public static void setRandomWalls() {
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
				{ throw new RuntimeException(IniFiles.stages.read(iniMapName, "FixedBlocks") + " - Wrong data for this item"); }
			Map<Tile, List<TileProp>> recProp = new HashMap<>();
			done:
			while (totalWalls > 0) {
				List<TileCoord> coords = new ArrayList<>();
				for (TileCoord coord : getLayer(26).getTilesMap().keySet())
					coords.add(coord.getNewInstance());
				for (TileCoord coord : coords) {
					Tile tile = getLayer(26).getTopTileFromCoord(coord);
					if (tile.tileProp.contains(TileProp.GROUND) && (int)MyMath.getRandom(0, 9) == 0) {
						Tile tile2 = new Tile((int)wallTile.getX(), (int)wallTile.getY(), coord.getX() * Main.TILE_SIZE, coord.getY() * Main.TILE_SIZE, new ArrayList<>(Arrays.asList(TileProp.WALL)));
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
				Tile.addTileShadow(groundWithWallShadow, coord);
			});
			getLayer(26).buildLayer();
		}
	}
	
	private static boolean testCoordForInsertFixedBlock(TileCoord coord) {
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

	public static TileCoord getInitialPlayerPosition(int playerIndex) {
		for (TileCoord coord : initialPlayerCoords.keySet())
			if (initialPlayerCoords.get(coord) == playerIndex)
				return coord;
		return null;
	}
	
	public static TileCoord getInitialMonsterPosition(int monsterIndex) {
		for (TileCoord coord : initialMonsterCoords.keySet())
			if (initialMonsterCoords.get(coord) == monsterIndex)
				return coord;
		return null;
	}
	
	public static List<TileProp> getTilePropsFromCoord(TileCoord coord)
		{ return getLayer(26).getFirstBottomTileFromCoord(coord).tileProp; }
	
	public static boolean tileContainsProp(TileCoord coord, TileProp prop)
		{ return getTilePropsFromCoord(coord).contains(prop); }
	
	public static void addPropToTile(TileCoord coord, TileProp prop)
		{ getLayer(26).getFirstBottomTileFromCoord(coord).tileProp.add(prop); }

	public static void removePropFromTile(TileCoord coord, TileProp prop)
		{ getLayer(26).getFirstBottomTileFromCoord(coord).tileProp.remove(prop); }

	public static IniFile getMapIniFile()
		{ return iniFileMap; }
	
	public static IniFile getTileSetIniFile()
		{ return iniFileTileSet; }

	private static Position getTilePositionFromIni(IniFile ini, String tileStr) {
		Position position = new Position();
		if (iniFileTileSet.read("CONFIG", tileStr) == null)
			return null;
		String[] split2 = iniFileTileSet.read("CONFIG", tileStr).split(" ");
		if (split2.length > 0) {
			try
				{ position.setPosition(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])); }
			catch (Exception e)
				{ throw new RuntimeException(iniFileTileSet.read("CONFIG", tileStr) + " - Invalid data on file \"" + iniFileTileSet.getFilePath().getFileName() + "\""); }
		}
		return position;
	}

	public static Position getGroundTile()
		{ return groundTile; }
	
	public static Position getWallTile()
		{ return wallTile; }
	
	public static Position getGroundWithBrickShadow()
		{ return groundWithBrickShadow; }
	
	public static Position getGroundWithWallShadow()
		{ return groundWithWallShadow; }

	public static Position getFragileGround()
		{ return fragileGround; }
	
	public static void rebuildAllLayers()
		{ layers.values().forEach(layer -> layer.buildLayer()); }
	
	public static void setGroundTile(Position groundTile)
		{ MapSet.groundTile = new Position(groundTile); }

	public static void setWallTile(Position wallTile)
		{ MapSet.wallTile = new Position(wallTile); }

	public static void setGroundWithBrickShadow(Position groundWithBrickShadow)
		{ MapSet.groundWithBrickShadow = new Position(groundWithBrickShadow); }

	public static void setGroundWithWallShadow(Position groundWithWallShadow)
		{ MapSet.groundWithWallShadow = new Position(groundWithWallShadow); }

	public static void setFragileGround(Position fragileGround)
		{ MapSet.fragileGround = new Position(fragileGround); }

	public static Map<Integer, Layer> getLayersMap()
		{ return layers; }
	
	public static Layer getLayer(int layerIndex) {
		if (!layers.containsKey(layerIndex))
			throw new RuntimeException(layerIndex + " - Invalid layer index");
		return layers.get(layerIndex);
	}
	
	public static Integer getCopyLayer()
		{ return copyImageLayer; }

	public static Image getTileSetImage()
		{ return tileSetImage; }
	
	public static void setTileSetImage(Image image)
		{ tileSetImage = image; }
	
	public static String getTileSetName()
		{ return tileSetName; }
		
	public static String getMapName()
		{ return mapName; }

	public static String getIniMapName()
		{ return iniMapName; }

	public static boolean tileIsFree(TileCoord coord)
		{ return tileIsFree(coord, null); }
	
	public static boolean tileIsFree(TileCoord coord, List<PassThrough> passThrough) {
		if (!haveTilesOnCoord(coord))
			return false;
		for (TileProp prop : getTilePropsFromCoord(coord))
			if (TileProp.getCantCrossList(Elevation.ON_GROUND).contains(prop) ||
				 (Brick.haveBrickAt(coord) && (passThrough == null || !passThrough.contains(PassThrough.BRICK))) ||
				 (Bomb.haveBombAt(coord) && (passThrough == null || !passThrough.contains(PassThrough.BOMB))))
						return false;
		return true;
	}

	public static boolean haveTilesOnCoord(TileCoord coord)
		{ return getLayer(26).haveTilesOnCoord(coord); }

	public static void run() {
		// NOTA: implementar (criar um Entity no mapa que fará o desenho do mapa usando os sprites das layers
	}
	
}
