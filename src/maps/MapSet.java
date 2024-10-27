package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import application.Main;
import entities.Bomb;
import entities.BomberMan;
import entities.Effect;
import entities.Entity;
import entities.Monster;
import entities.Shake;
import enums.BombType;
import enums.Direction;
import enums.Elevation;
import enums.GameMode;
import enums.ItemType;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.StageClearCriteria;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Tags;
import frameset_tags.FrameTag;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderTileCoord;
import tools.IniFiles;
import tools.Materials;
import tools.Sound;
import util.IniFile;
import util.Misc;
import util.MyMath;

public abstract class MapSet {
	
	private static Map<Integer, Layer> layers;
	private static Map<TileCoord, Integer> initialPlayerCoords;
	private static Map<TileCoord, Integer> initialMonsterCoords;
	private static Shake shake;
	public static Entity mapFrameSets;
	private static IniFile iniFileMap;
	private static IniFile iniFileTileSet;
	private static Image tileSetImage;
	private static Integer copyImageLayerIndex;
	private static Integer currentLayerIndex;
	private static String mapName;
	private static String iniMapName;
	private static String tileSetName;
	private static Position groundTile;
	private static Position wallTile;
	private static Position groundWithBrickShadow;
	private static Position groundWithWallShadow;
	private static Position mapMove;
	public static Map<String, FrameSet> runningStageTags;
	private static Map<String, FrameSet> preLoadedStageTags;
	private static int bricksRegenTimeInFrames;
	private static Map<String, Integer> switches;
	private static boolean stageIsCleared;
	private static List<StageClearCriteria> stageClearCriterias;
	private static List<StageClearCriteria> leftStageClearCriterias;
	
	public static void loadMap(String iniMapName) {
		long ct = System.currentTimeMillis();
		if (!IniFiles.stages.isSection(iniMapName))
			throw new RuntimeException("Unable to load map \"" + iniMapName + "\" (Map not found on Stages.cfg)");
		System.out.println("Carregando mapa " + iniMapName + " ...");
		Effect.clearTempEffects();
		Materials.tempSprites.clear();
		MapSet.iniMapName = iniMapName;
		bricksRegenTimeInFrames = -1;
		layers = new HashMap<>();
		switches = new HashMap<>();
		initialPlayerCoords = new HashMap<>();
		initialMonsterCoords = new HashMap<>();
		runningStageTags = new HashMap<>();
		preLoadedStageTags = new HashMap<>();
		mapMove = new Position();
		mapName = IniFiles.stages.read(iniMapName, "File");
		iniFileMap = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		shake = null;
		if (iniFileMap == null)
			throw new RuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
		setTileSet(iniFileMap.read("SETUP", "TileSet"));
		Map<Integer, List<String>> tileInfos = new HashMap<>();
		copyImageLayerIndex = iniFileMap.readAsInteger("SETUP", "CopyImageLayer", null);
		if (iniFileMap.read("SETUP", "BrickRegenTimer") != null)
			MapSet.setBricksRegenTime(Integer.parseInt(iniFileMap.getLastReadVal()));
		if (iniFileMap.read("SETUP", "PortalCriteria") != null) {
			stageClearCriterias = new ArrayList<>();
			leftStageClearCriterias = new ArrayList<>();
			for (String s : iniFileMap.getLastReadVal().split(" ")) {
				stageClearCriterias.add(StageClearCriteria.valueOf(s));
				leftStageClearCriterias.add(StageClearCriteria.valueOf(s));
			}
		}
		else
			leftStageClearCriterias = null;
		iniFileMap.getItemList("TILES").forEach(item -> {
			int layer = Integer.parseInt(iniFileMap.read("TILES", item).split(" ")[0]);
			if (!tileInfos.containsKey(layer))
				tileInfos.put(layer, new ArrayList<>());
			tileInfos.get(layer).add(iniFileMap.getLastReadVal());
		});
		tileInfos.keySet().forEach(i -> {
			Layer layer = new Layer(tileInfos.get(i));
			layers.put(i, layer);
			for (TileCoord coord : layer.getTilePropsMap().keySet()) {
				if (layer.getTileProps(coord).contains(TileProp.PLAYER_INITIAL_POSITION))
					initialPlayerCoords.put(coord.getNewInstance(), initialPlayerCoords.size());
				else if (layer.getTileProps(coord).contains(TileProp.MOB_INITIAL_POSITION))
					initialMonsterCoords.put(coord.getNewInstance(), initialMonsterCoords.size());
			}
		});
		groundTile = getTilePositionFromIni(iniFileMap, "GroundTile");
		groundWithBrickShadow = Misc.notNull(getTilePositionFromIni(iniFileMap, "GroundWithBrickShadow"), new Position(groundTile));
		groundWithWallShadow = Misc.notNull(getTilePositionFromIni(iniFileMap, "GroundWithWallShadow"), new Position(groundTile));
		wallTile = getTilePositionFromIni(iniFileMap, "WallTile");
		currentLayerIndex = 26;
		if (Main.GAME_MODE != GameMode.MAP_EDITOR || Main.mapEditor.playing)
			setRandomWalls();
		rebuildAllLayers();
		if (Main.GAME_MODE != GameMode.MAP_EDITOR || Main.mapEditor.playing)
			setBricks();
		iniFileMap.getItemList("TAGS").forEach(item -> {
			FrameSet frameSet = new FrameSet();
			frameSet.loadFromString(iniFileMap.read("TAGS", item));
			preLoadedStageTags.put(item, frameSet);
		});
		iniFileMap.getItemList("EFFECTS").forEach(item -> {
			Effect.addNewTempEffect(item, iniFileMap.read("EFFECTS", item));
		});
		resetMapFrameSets();
		System.out.println("... Concluído em " + (System.currentTimeMillis() - ct) + "ms");
	}
	
	/* tileCoord - Coordenada do tile que disparou a tag (se for disparado de algum tile) 
	 * whoTriggered -  A entity que disparou o stageTag (O jogador/mob/bomba que pisou no tile, a bomba que gerou a explosão que acertou o tile)
	 */
	public static void runStageTag(String stageTagsName, TileCoord tileCoord, Entity whoTriggered) {
		if (stageTagsName == null)
			throw new RuntimeException("stageTagsName is null");
		if (tileCoord == null)
			throw new RuntimeException("tileCoord is null");
		if (whoTriggered == null)
			throw new RuntimeException("whoTriggered is null");
		if (!preLoadedStageTags.containsKey(stageTagsName))
			throw new RuntimeException(stageTagsName + " - Invalid stage tag name");
		if (!runningStageTags.containsKey(stageTagsName))
			runningStageTags.put(stageTagsName, new FrameSet(preLoadedStageTags.get(stageTagsName), tileCoord.getPosition(), whoTriggered));
	}

	public static void runStageTag(String stageTagsName, TileCoord tileCoord)
		{ runStageTag(stageTagsName, tileCoord, new Entity()); }
	
	public static void runStageTag(String stageTagsName, Entity whoTriggered)
		{ runStageTag(stageTagsName, new TileCoord(), whoTriggered); }
	
	public static void runStageTag(String stageTagsName)
		{ runStageTag(stageTagsName, new TileCoord(), new Entity()); }
	
	public static void setStageStatusToCleared() {
		if (!stageIsCleared) {
			stageIsCleared = true;
			if (preLoadedStageTags.containsKey("StageClear"))
				runStageTag("StageClear");
			if (mapFrameSets.haveFrameSet("StageClear"))
				mapFrameSets.setFrameSet("StageClear");
			Sound.playWav("StageClear");
		}
	}
	
	public static void addLayer(int layerIndex)
		{ layers.put(layerIndex, new Layer(layerIndex)); }

	public static void addLayer(int layerIndex, SpriteLayerType layerType)
		{ layers.put(layerIndex, new Layer(layerIndex, layerType)); }
	
	public static void removeLayer(int layerIndex) {
		if (!isValidLayer(layerIndex))
			throw new RuntimeException("Invalid layer index");
		if (getTotalLayers() == 1)
			throw new RuntimeException("You can't remove this layer because the map must have at least one layer");
		if (layerIndex == 26)
			throw new RuntimeException("You can't remove this layer because it's the main layer");
		layers.remove(layerIndex);
		if (currentLayerIndex == layerIndex)
			setCurrentLayerIndex(layers.keySet().iterator().next());
	}
	
	public static int getTotalLayers()
		{ return layers.size(); }
	
	public static Layer getCurrentLayer()
		{ return layers.get(currentLayerIndex); }
	
	public static int getCurrentLayerIndex()
		{ return currentLayerIndex; }
	
	public static void setCurrentLayerIndex(int index)
		{ currentLayerIndex = index; }
	
	public static int getSwitchValue(String switchName)
		{ return !switches.containsKey(switchName) ? 0 : switches.get(switchName); }

	public static void setSwitchValue(String switchName, int value) {
		int oldValue = !switches.containsKey(switchName) ? -1 : switches.get(switchName);
		switches.put(switchName, value);
		if (!stageIsCleared && switchName.equals("StageClear")) {
			if (oldValue > 0 && getSwitchValue(switchName) == 0)
				removeStageClearCriteria(StageClearCriteria.ACTIVATING_SWITCHES);
			else if (oldValue == 0 && getSwitchValue(switchName) > 0)
				addStageClearCriteria(StageClearCriteria.ACTIVATING_SWITCHES);
		}
	}
	
	public static List<StageClearCriteria> getStageClearCriterias()
		{ return stageClearCriterias; }

	public static List<StageClearCriteria> getLeftStageClearCriterias()
		{ return leftStageClearCriterias; }
	
	public static void setShake(Double incStrength, Double finalStrength)
		{ shake = new Shake(incStrength, incStrength, finalStrength, finalStrength);	}
	
	public static void setShake(Double startStrength, Double incStrength, Double finalStrength)
		{ shake = new Shake(startStrength, startStrength, incStrength, incStrength, finalStrength, finalStrength); }
	
	public static void setShake(Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY)
		{ shake = new Shake(incStrengthX > 0 ? 0 : finalStrengthX, incStrengthY > 0 ? 0 : finalStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);	}
	
	public static void setShake(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY)
		{ shake = new Shake(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);	}
	
	public static void stopShake()
		{ shake.stop(); }
	
	public static Shake getShake()
		{ return shake; }

	private static void addStageClearCriteria(StageClearCriteria criteria) {
		if (leftStageClearCriterias != null)
			leftStageClearCriterias.add(criteria);
	}

	private static void removeStageClearCriteria(StageClearCriteria criteria) {
		if (leftStageClearCriterias != null) {
			leftStageClearCriterias.remove(criteria);
			boolean temp = leftStageClearCriterias.size() == 1 &&
					leftStageClearCriterias.get(0) == StageClearCriteria.KILLING_ALL_MOBS;
			if (temp || leftStageClearCriterias.isEmpty()) // NOTA: remover temp quando os mobs tiverem sido implementados
				setStageStatusToCleared();
		}
	}

	public static void incSwitchValue(String switchName, int incValue)
		{ setSwitchValue(switchName, getSwitchValue(switchName) + incValue); }
	
	public static void resetMapFrameSets() {
		stageIsCleared = false;
		mapFrameSets = new Entity();
		for (String item : iniFileMap.getItemList("FRAMESETS"))
			mapFrameSets.addNewFrameSetFromString(item, iniFileMap.read("FRAMESETS", item));
		if (mapFrameSets.haveFrameSet("StageIntro"))
			mapFrameSets.setFrameSet("StageIntro");
		else if (mapFrameSets.haveFrameSet("Default"))
			mapFrameSets.setFrameSet("Default");
		if (preLoadedStageTags.containsKey("StageIntro"))
			runStageTag("StageIntro");
	}

	public static void setTileSet(String tileSetName) {
		MapSet.tileSetName = tileSetName; 
		iniFileTileSet = IniFile.getNewIniFileInstance("appdata/tileset/" + tileSetName + ".tiles");
		if (iniFileTileSet == null)
			throw new RuntimeException("Unable to load map \"" + mapName + "\" (Invalid TileSet (" + tileSetName + ".tiles))");
		if (Materials.tempSprites.containsKey(tileSetName))
			Materials.tempSprites.remove(tileSetName);
		tileSetImage = Materials.tileSets.get(tileSetName);
		Materials.tempSprites.put("TileSet", (WritableImage)tileSetImage);
	}

	public static void setBricks() {
		Brick.clearBricks();
		List<Brick> addBricks = new ArrayList<>();
		for (TileCoord coord : getTilePropsMap().keySet())
			if (MapSet.getTileProps(coord).contains(TileProp.FIXED_BRICK))
				addBricks.add(new Brick(coord, null));
		if (IniFiles.stages.read(iniMapName, "Blocks") != null && !IniFiles.stages.read(iniMapName, "Blocks").equals("0")) {
			int totalBricks = 0, bricksQuant = 0;
			int[] totalBrickSpawners = {0};
			try {
				String[] split = IniFiles.stages.read(iniMapName, "Blocks").split("!");
				int minBricks = Integer.parseInt(split[0]),
						maxBricks = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
						totalBricks = (int)MyMath.getRandom(minBricks, maxBricks);
						for (TileCoord coord : getTilePropsMap().keySet()) {
							MapSet.getTileProps(coord).forEach(prop -> {
							if (prop == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(coord))
								totalBrickSpawners[0]++;
							});
						}
			}
			catch (Exception e)
				{ throw new RuntimeException(IniFiles.stages.read(iniMapName, "Blocks") + " - Wrong data for this item"); }
			done:
			while (totalBricks > 0) {
				for (Tile tile : getLayer(26).getTileList())
					for (TileProp p : MapSet.getTileProps(tile.getTileCoord()))
						if (p == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(tile.getTileCoord())) {
							if ((int)MyMath.getRandom(0, 3) == 0) {
								addBricks.add(new Brick(tile.getTileCoord(), null));
								if (--totalBricks == 0 || ++bricksQuant >= totalBrickSpawners[0])
									break done;
							}
						}
			}
		}
		addBricks.sort((b1, b2) -> (int)b2.getY() - (int)b1.getY());
		addBricks.forEach(brick -> Brick.addBrick(brick, false));
		getLayer(26).buildLayer();
		addItemsToBricks();
	}
	
	public static int getBricksRegenTimeInFrames()
		{ return bricksRegenTimeInFrames; }

	public static void setBricksRegenTime(int bricksRegenTimeInSecs) {
		bricksRegenTimeInFrames = bricksRegenTimeInSecs * 60;
		Brick.getBricks().forEach(brick -> brick.setRegenTime(bricksRegenTimeInSecs));
	}

	private static void addItemsToBricks() {
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
			Map<TileCoord, List<TileProp>> recProp = new HashMap<>();
			done:
			while (totalWalls > 0) {
				List<TileCoord> coords = new ArrayList<>();
				for (TileCoord coord : getLayer(26).getTilesMap().keySet())
					coords.add(coord.getNewInstance());
				for (TileCoord coord : coords) {
					if (MapSet.getTileProps(coord).contains(TileProp.GROUND) && (int)MyMath.getRandom(0, 9) == 0) {
						Tile tile = new Tile(getLayer(26), (int)wallTile.getX(), (int)wallTile.getY(), coord.getX() * Main.TILE_SIZE, coord.getY() * Main.TILE_SIZE);
						if (testCoordForInsertFixedBlock(coord)) {
							recProp.put(coord, Arrays.asList(TileProp.WALL));
							addWalls.add(tile);
							if (--totalWalls == 0)
								break done;
						}
					}
				}
			}
			recProp.forEach((coord, props) -> MapSet.setTileProps(coord, new ArrayList<>(props)));
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
			coord1.incCoordsByDirection(dir1);
			Direction dir2 = Direction.LEFT;
			if (tileIsFree(new TileCoord(coord1.getX(), coord1.getY())))
				for (int n2 = 0; n2 < 4; n2++) {
					dir2 = dir2.getNext4WayClockwiseDirection();
					coord2.setCoord(coord3);
					coord2.incCoordsByDirection(dir2);
					if (dir1 != dir2 && tileIsFree(new TileCoord(coord2.getX(), coord2.getY()))) {
						PathFinder pf = new PathFinder(coord1, coord2, dir1, t -> tileIsFree(new TileCoord(t.getX(), t.getY())));
						if (!pf.pathWasFound())
							return false;
					}
				}
		}
		return true;
	}

	public static Position getInitialPlayerPosition(int playerIndex) {
		for (TileCoord coord : initialPlayerCoords.keySet())
			if (initialPlayerCoords.get(coord) == playerIndex)
				return new Position(coord.getX() * Main.TILE_SIZE, coord.getY() * Main.TILE_SIZE);
		return null;
	}
	
	public static Position getInitialMonsterPosition(int monsterIndex) {
		for (TileCoord coord : initialMonsterCoords.keySet())
			if (initialMonsterCoords.get(coord) == monsterIndex)
				return new Position(coord.getX() * Main.TILE_SIZE, coord.getY() * Main.TILE_SIZE);
		return null;
	}
	
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
				{ position.setPosition(Integer.parseInt(split2[0]) * Main.TILE_SIZE, Integer.parseInt(split2[1]) * Main.TILE_SIZE); }
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

	public static void rebuildAllLayers()
		{ layers.keySet().forEach(layer -> layers.get(layer).buildLayer()); }
	
	public static void setGroundTile(Position groundTile)
		{ MapSet.groundTile = new Position(groundTile); }

	public static void setWallTile(Position wallTile)
		{ MapSet.wallTile = new Position(wallTile); }

	public static void setGroundWithBrickShadow(Position groundWithBrickShadow)
		{ MapSet.groundWithBrickShadow = new Position(groundWithBrickShadow); }

	public static void setGroundWithWallShadow(Position groundWithWallShadow)
		{ MapSet.groundWithWallShadow = new Position(groundWithWallShadow); }

	public static Map<Integer, Layer> getLayersMap()
		{ return layers; }
	
	public static boolean isValidLayer(int layer)
		{ return layers.containsKey(layer); } 
	
	public static Layer getLayer(int layerIndex) {
		if (!isValidLayer(layerIndex))
			throw new RuntimeException(layerIndex + " - Invalid layer index");
		return layers.get(layerIndex);
	}
	
	public static void setCopyImageLayerIndex(int index)
		{ copyImageLayerIndex = index; }
	
	public static int getCopyImageLayerIndex()
		{ return copyImageLayerIndex; }
	
	public static Layer getCopyLayer()
		{ return getLayer(copyImageLayerIndex); }

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
	
	public static List<Tile> getTileListFromCurrentLayer()
		{ return getCurrentLayer().getTileList(); }

	public static Tile getFirstBottomTileFromCoord(TileCoord coord)
		{ return getCurrentLayer().getFirstBottomTileFromCoord(coord); }
	
	public static Tile getTopTileFromCoord(TileCoord coord)
		{ return getCurrentLayer().getTopTileFromCoord(coord); }
	
	public static List<Tile> getTileListFromCoord(TileCoord coord)
		{ return getCurrentLayer().getTilesFromCoord(coord); }

	public static void checkTileTrigger(Entity entity, TileCoord coord, TileProp triggerProp)
		{ checkTileTrigger(entity, coord, triggerProp, false); }
	
	public static void checkTileTrigger(Entity entity, TileCoord coord, TileProp triggerProp, boolean isTileStepOut) {
		Tile tile = getFirstBottomTileFromCoord(coord);
		boolean stepTrigger = triggerProp == TileProp.TRIGGER_BY_BLOCK || triggerProp == TileProp.TRIGGER_BY_BOMB || 
				triggerProp == TileProp.TRIGGER_BY_ITEM || triggerProp == TileProp.TRIGGER_BY_MOB || 
				triggerProp == TileProp.TRIGGER_BY_PLAYER ||	triggerProp == TileProp.TRIGGER_BY_UNRIDE_PLAYER;
		boolean containsStepOut = tile.getTileProps().contains(TileProp.TRIGGER_WHEN_STEP_OUT);
		if (stepTrigger && ((isTileStepOut && !containsStepOut) || (!isTileStepOut && containsStepOut)))
			return;
		if (tile.tileHaveTags())
			for (TileProp prop : getTileProps(coord))
				if (prop == triggerProp)
					tile.runTags(entity, coord);
	}

	public static boolean haveTilesOnCoord(TileCoord coord)
		{ return getCurrentLayer().haveTilesOnCoord(coord); }

	public static void run() {
		if (shake != null) {
			shake.proccess();
			if (!shake.isActive())
				shake = null;
		}
		mapFrameSets.run();
		if (!mapFrameSets.getCurrentFrameSet().isRunning()) {
			if (mapFrameSets.getCurrentFrameSetName().equals("StageIntro"))
				mapFrameSets.setFrameSet("Default");
		}
		List<String> removeStageTag = new ArrayList<>();
		List<String> stageTags = new ArrayList<>(runningStageTags.keySet());
		for (String frameSetName : stageTags) {
			FrameSet frameSet = runningStageTags.get(frameSetName);
			frameSet.run();
			if (!frameSet.isRunning())
				removeStageTag.add(frameSetName);
		}
		removeStageTag.forEach(fs -> runningStageTags.remove(fs));
	}

	public static boolean tileIsFree(TileCoord coord)
		{ return tileIsFree(null, coord); }
	
	public static boolean tileIsFree(TileCoord coord, Set<PassThrough> passThrough)
		{ return tileIsFree(null, coord, passThrough); }
	
	public static boolean tileIsFree(Entity entity, TileCoord coord)
		{ return tileIsFree(entity, coord, null); }
	
	public static boolean tileIsFree(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		if (!haveTilesOnCoord(coord))
			return false;
		Entity en = Entity.haveAnyEntityAtCoord(coord) ? Entity.getFirstEntityFromCoord(coord) : null;
		if (getTileProps(coord) != null)
			for (TileProp prop : getTileProps(coord))
				if (TileProp.getCantCrossList(Elevation.ON_GROUND).contains(prop) ||
					 ((prop == TileProp.HOLE || prop == TileProp.GROUND_HOLE || prop == TileProp.DEEP_HOLE) && (passThrough == null || !passThrough.contains(PassThrough.HOLE))) ||
					 ((prop == TileProp.WALL || prop == TileProp.HIGH_WALL) && (passThrough == null || !passThrough.contains(PassThrough.WALL))) ||
					 ((prop == TileProp.WATER || prop == TileProp.DEEP_WATER) && (passThrough == null || !passThrough.contains(PassThrough.WATER))) ||
					 (en != null && en instanceof BomberMan && passThrough != null && !passThrough.contains(PassThrough.PLAYER)) ||
					 (en != null && en instanceof Monster && passThrough != null && !passThrough.contains(PassThrough.MONSTER)) ||
					 (Item.haveItemAt(coord) && passThrough != null && !passThrough.contains(PassThrough.ITEM)) ||
					 (Brick.haveBrickAt(coord) && (passThrough == null || !passThrough.contains(PassThrough.BRICK))) ||
					 (Bomb.haveBombAt(entity, coord) && Bomb.getBombAt(coord).getBombType() != BombType.LAND_MINE && (passThrough == null || !passThrough.contains(PassThrough.BOMB))))
							return false;
		return true;
	}

	public static boolean tileIsOccuped(TileCoord coord, Set<PassThrough> passThrough)
		{ return tileIsOccuped(null, coord, passThrough); }
	
	public static boolean tileIsOccuped(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		// NOTA: Implementar retornando se tem monstro ou player em cima
		return !tileIsFree(entity, coord, passThrough) || Item.haveItemAt(coord);
	}

	// ================ Metodos relacionados a TileProps ==============

	public static boolean tileContainsProp(TileCoord coord, TileProp prop)
		{ return getCurrentLayer().tileContainsProp(coord, prop); }
	
	public static Map<TileCoord, List<TileProp>> getTilePropsMap()
		{ return getCurrentLayer().getTilePropsMap(); }

	public static boolean tileHaveProps(TileCoord coord)
		{ return getCurrentLayer().tileHaveProps(coord); }

	public static List<TileProp> getTileProps(TileCoord coord)
		{ return getCurrentLayer().getTileProps(coord); }
	
	public static int getTotalTileProps(TileCoord coord)
		{ return getCurrentLayer().getTotalTileProps(coord); }
	
	public static void setTileProps(TileCoord coord, List<TileProp> tileProps)
		{ getCurrentLayer().setTileProps(coord, tileProps); }

	public static void addTileProp(TileCoord coord, TileProp ... props)
		{ getCurrentLayer().addTileProp(coord, props); }
	
	public static void removeTileProp(TileCoord coord, TileProp ... props)
		{ getCurrentLayer().removeTileProp(coord, props); }
	
	public static void clearTileProps(TileCoord coord)
		{ getCurrentLayer().clearTileProps(coord); }
	
	// ================ Metodos relacionados a TileTags ==============
	
	public static boolean tileHaveTags(TileCoord coord)
		{ return getCurrentLayer().tileHaveTags(coord); }
	
	public static void disableTileTags(TileCoord coord)
		{ getCurrentLayer().disableTileTags(coord); }
	
	public static void enableTileTags(TileCoord coord)
		{ getCurrentLayer().enableTileTags(coord); }
	
	public static boolean tileTagsIsDisabled(TileCoord coord)
		{ return getCurrentLayer().tileTagsIsDisabled(coord); }

	public static Tags getTileTags(TileCoord coord)
		{ return getCurrentLayer().getTileTags(coord); }
	
	public static String getStringTags(TileCoord coord)
		{ return getCurrentLayer().getStringTags(coord); }

	public static FrameSet getTileTagsFrameSet(TileCoord coord)
		{ return getCurrentLayer().getTileTagsFrameSet(coord); }

	public static void setTileTagsFromString(TileCoord coord, String stringTileTags)
		{ getCurrentLayer().setTileTagsFromString(coord, stringTileTags); }
	
	public static void setTileTagsFromString(TileCoord coord, String stringTileTags, Tile tile)
		{ getCurrentLayer().setTileTagsFromString(coord, stringTileTags, tile); }
	
	public static void setTileTags(TileCoord coord, Tags tags)
		{ getCurrentLayer().setTileTags(coord, tags); }
	
	public static void removeTileTag(TileCoord coord, String tagStr)
		{ getCurrentLayer().removeTileTag(coord, tagStr); }
	
	public static void removeTileTag(TileCoord coord, FrameTag tag)
		{ getCurrentLayer().removeTileTag(coord, tag); }
	
	public static void clearTileTags(TileCoord coord)
		{ getCurrentLayer().clearTileTags(coord); }
	
}
