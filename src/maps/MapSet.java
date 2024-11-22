package maps;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import application.Main;
import damage.Explosion;
import damage.TileDamage;
import entities.Bomb;
import entities.BomberMan;
import entities.Effect;
import entities.Entity;
import entities.Monster;
import entityTools.ShakeEntity;
import enums.BombType;
import enums.Direction;
import enums.DrawType;
import enums.Elevation;
import enums.GameMode;
import enums.ItemType;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.StageObjectives;
import enums.TileProp;
import fades.DefaultFade;
import frameset.FrameSet;
import frameset.Tags;
import frameset_tags.FrameTag;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import tools.Draw;
import tools.GameFonts;
import tools.Materials;
import tools.Sound;
import util.DurationTimerFX;
import util.IniFile;
import util.Misc;
import util.MyMath;

public abstract class MapSet {

	private static Map<Integer, Layer> layers;
	private static List<TileCoord> initialPlayerCoords;
	private static List<TileCoord> initialMonsterCoords;
	private static ShakeEntity shake;
	public static Entity mapFrameSets;
	private static IniFile iniFile;
	private static IniFile iniFileTileSet;
	private static Image tileSetImage;
	private static Integer copyImageLayerIndex;
	private static Integer currentLayerIndex;
	private static String mapName;
	private static String nextMapName;
	private static String iniMapName;
	private static String tileSetName;
	private static Position groundTile;
	private static Position wallTile;
	private static Position groundWithBrickShadow;
	private static Position groundWithWallShadow;
	private static Position mapMove;
	private static Position mapMinLimit;
	private static Position mapMaxLimit;
	public static Map<String, FrameSet> runningStageTags;
	private static Map<String, FrameSet> preLoadedStageTags;
	private static int bricksRegenTimeInFrames;
	private static Map<String, Integer> switches;
	private static boolean stageObjectiveIsCleared;
	private static boolean stageIsCleared;
	private static List<StageObjectives> stageClearCriterias;
	private static List<StageObjectives> leftStageClearCriterias;
	private static boolean hurryUpIsActive;
	private static int stageTimerPauseDuration;
	static TileCoord hurryUpMinFreeCoord;
	static TileCoord hurryUpMaxFreeCoord;
	private static TileCoord hurryUpNextCoord;
	private static Direction hurryUpDirection;
	private static Integer hurryUpDrawX;
	private static Integer stageTimeInSecs;
	private static Integer hurryUpTimeInSecs;
	private static String stageStartMusic;
	private static String stageBGM;
	private static String stageClearSound;
	private static String stageClearBGM;
	private static Runnable onStageClearEvent = null;
	private static Runnable onStageObjectiveClearEvent = null;
	private static Runnable onBeforeMapLoadEvent = null;
	private static Runnable onAfterMapLoadEvent = null;

	public static void loadMap(String mapName) {
		long ct = System.currentTimeMillis();
		if (!new File("appdata/maps/" + mapName + ".map").exists())
			throw new RuntimeException("Unable to load map file \"appdata/maps/" + mapName + ".map\" (File not found)");
		System.out.println("Carregando mapa " + mapName + " ...");
		if (onBeforeMapLoadEvent != null)
			Platform.runLater(onBeforeMapLoadEvent);
		clearStuffs();
		iniFile = IniFile.getNewIniFileInstance("appdata/maps/" + mapName + ".map");
		Materials.tempSprites.clear();
		MapSet.mapName = mapName;
		nextMapName = iniFile.read("SETUP", "NextMap", mapName);
		bricksRegenTimeInFrames = 0;
		layers = new HashMap<>();
		switches = new HashMap<>();
		initialPlayerCoords = new ArrayList<>();
		initialMonsterCoords = new ArrayList<>();
		runningStageTags = new HashMap<>();
		preLoadedStageTags = new HashMap<>();
		mapMove = new Position();
		mapMinLimit = new Position();
		mapMaxLimit = new Position();
		stageStartMusic = iniFile.read("SETUP", "StageStartMusic");
		stageBGM = iniFile.read("SETUP", "StageBGM");
		if (stageBGM != null)
			Sound.stopAllMp3s();
		stageClearSound = iniFile.read("SETUP", "StageClearSound");
		setStageTimeLimitInSecs(iniFile.readAsInteger("SETUP", "TimeLimitInSecs", null));
		setHurryUpTimeInSecs(iniFile.readAsInteger("SETUP", "HurryUpTime", null));
		shake = null;
		hurryUpIsActive = false;
		stageObjectiveIsCleared = false;
		stageIsCleared = false;
		hurryUpMinFreeCoord = new TileCoord(Integer.MAX_VALUE, Integer.MAX_VALUE);
		hurryUpMaxFreeCoord = new TileCoord();
		hurryUpNextCoord = new TileCoord();
		hurryUpDirection = Direction.RIGHT;
		hurryUpDrawX = null;
		if (iniFile == null)
			throw new RuntimeException("Unable to load map \"" + mapName + "\" (File not found)");
		if (iniFile.read("SETUP", "TileSet") == null)
			throw new RuntimeException("Unable to find \"TileSet\" item at \"SETUP\" section from \"" + iniFile.fileName() + "\"");
		setTileSet(iniFile.getLastReadVal());
		Map<Integer, List<String>> tileInfos = new HashMap<>();
		if (iniFile.read("SETUP", "CopyImageLayer") == null)
			throw new RuntimeException("Unable to find \"CopyImageLayer\" item at \"SETUP\" section from \"" + iniFile.fileName() + "\"");
		copyImageLayerIndex = iniFile.readAsInteger("SETUP", "CopyImageLayer", null);
		if (iniFile.read("SETUP", "BrickRegenTimer") != null)
			setBricksRegenTime(Integer.parseInt(iniFile.getLastReadVal()));
		if (iniFile.read("SETUP", "PortalCriteria") != null) {
			stageClearCriterias = new ArrayList<>();
			leftStageClearCriterias = new ArrayList<>();
			for (String s : iniFile.getLastReadVal().split(" ")) {
				stageClearCriterias.add(StageObjectives.valueOf(s));
				leftStageClearCriterias.add(StageObjectives.valueOf(s));
			}
		}
		else
			leftStageClearCriterias = null;
		if (!iniFile.sectionExists("TILES"))
			throw new RuntimeException("Unable to find \"TILES\" section from \"" + iniFile.fileName() + "\"");
		iniFile.getItemList("TILES").forEach(item -> {
			int layerIndex = Integer.parseInt(iniFile.read("TILES", item).split(" ")[0]);
			if (!tileInfos.containsKey(layerIndex))
				tileInfos.put(layerIndex, new ArrayList<>());
			tileInfos.get(layerIndex).add(iniFile.getLastReadVal());
		});
		tileInfos.keySet().forEach(i -> {
			Layer layer = new Layer(tileInfos.get(i));
			layers.put(i, layer);
		});
		for (int n = 0; n < 2; n++)
			if (iniFile.read("SETUP", n == 0 ? "MonsterInitialCoordsOrder" : "PlayerInitialCoordsOrder") != null) {
				try {
					String[] split = iniFile.getLastReadVal().split(" ");
					for (String s : split) {
						String[] split2 = s.split("!");
						initialPlayerCoords.add(new TileCoord(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])));
					}
				}
				catch (Exception e) {
					throw new RuntimeException("Invalid format at [SETUP], '" + (n == 0 ? "MonsterInitialCoordsOrder" : "PlayerInitialCoordsOrder") + "' -> " + iniFile.getLastReadVal());
				}
			}
		if (initialPlayerCoords.isEmpty())
			throw new RuntimeException("Unable to find any initial player spots on this map");
		groundTile = getTilePositionFromIni(iniFile, "GroundTile");
		groundWithBrickShadow = Misc.notNull(getTilePositionFromIni(iniFile, "GroundWithBrickShadow"), new Position(groundTile));
		groundWithWallShadow = Misc.notNull(getTilePositionFromIni(iniFile, "GroundWithWallShadow"), new Position(groundTile));
		wallTile = getTilePositionFromIni(iniFile, "WallTile");
		currentLayerIndex = 26;
		if (Main.GAME_MODE != GameMode.MAP_EDITOR || Main.mapEditor.playing)
			setRandomWalls();
		rebuildAllLayers();
		if (Main.GAME_MODE != GameMode.MAP_EDITOR || Main.mapEditor.playing)
			setBricks();
		iniFile.getItemList("TAGS").forEach(item -> {
			FrameSet frameSet = new FrameSet();
			frameSet.loadFromString(mapFrameSets, iniFile.read("TAGS", item));
			preLoadedStageTags.put(item, frameSet);
		});
		resetMapFrameSets();
		for (int p = 0; p < initialPlayerCoords.size() && p < BomberMan.getTotalBomberMans(); p++)
			BomberMan.getBomberMan(p).setPosition(initialPlayerCoords.get(p).getPosition());
		if (preLoadedStageTags.containsKey("AfterMapLoad"))
			runStageTag("AfterMapLoad");
		if (onAfterMapLoadEvent != null)
			Platform.runLater(onAfterMapLoadEvent);
		if (preLoadedStageTags.containsKey("AfterMapLoad"))
			runStageTag("AfterMapLoad");
		else {
			if (stageStartMusic != null)
				Main.freezeAll();
			Draw.setFade(new DefaultFade(0.005).fadeIn().setOnFadeDoneEvent(() -> Draw.setFade(null)));
			Runnable event = () -> {
				if (stageBGM != null) {
					String[] split = stageBGM.split(" ");
					if (split.length == 1)
						Sound.playMp3(stageBGM).thenAccept(mp3 -> mp3.setOnEndOfMedia(() -> mp3.play()));
					else {
						try {
							final int end = Integer.parseInt(split[1]);
							final int start = split.length == 2 ? 0 : Integer.parseInt(split[2]);
							Sound.playMp3(split[0], new Pair<>(Duration.millis(end), Duration.millis(start)));
						}
						catch (Exception e) {
							throw new RuntimeException("Invalid format for string: " + stageBGM + "\n\t(Expected: [MP3_NAME] or [MP3_NAME END_POS] or [MP3_NAME END_POS START_POS])");
						}
					}
				}
			};
			if (stageStartMusic != null)
				Sound.playMp3(stageStartMusic).thenAcceptAsync(mp3 ->	mp3.setOnEndOfMedia(() ->	{
					Main.unFreezeAll();
					Platform.runLater(event);
				}));
			else {
				Main.unFreezeAll();
				Platform.runLater(event);
			}
		}
		System.out.println("... Concluído em " + (System.currentTimeMillis() - ct) + "ms");
	}
	
	public static void clearStuffs() {
		Brick.clearBricks();
		Bomb.clearBombs();
		Item.clearItems();
		Effect.clearEffects();
		BomberMan.setBomberAlives(0);
	}

	public static void reloadMap() {
		loadMap(mapName);
	}
	
	public static void setStageTimeLimitInSecs(Integer timeLimit) {
		stageTimeInSecs = timeLimit;
		if (timeLimit != null)
			DurationTimerFX.createTimer("StageTimer", Duration.ZERO, Duration.seconds(1), 0, () -> {
				if (stageTimerPauseDuration > 0)
					stageTimerPauseDuration--;
				else if (stageTimeInSecs > 0)
					stageTimeInSecs--;
				if (getStageClearCriterias().contains(StageObjectives.LAST_PLAYER_SURVIVOR) && !hurryUpIsActive() && stageTimeInSecs != null && hurryUpTimeInSecs != null && getMapTimeLeftInSecs() <= hurryUpTimeInSecs)
					setHurryUpState(true);
			});
		else
			DurationTimerFX.stopTimer("StageTimer");
	}
	
	public static void addStageTimePauseDuration(int timePauseDurationToAdd) {
		stageTimerPauseDuration += timePauseDurationToAdd;
	}
	
	public static boolean stageTimerIspaused() {
		return stageTimerPauseDuration != 0;
	}

	public static void setHurryUpTimeInSecs(Integer time) {
		hurryUpTimeInSecs = time;
	}
	
	public static boolean haveTimeLimit() {
		return stageTimeInSecs != null;
	}
	
	/*
	 * tileCoord - Coordenada do tile que disparou a tag (se for disparado de algum
	 * tile) whoTriggered - A entity que disparou o stageTag (O jogador/mob/bomba
	 * que pisou no tile, a bomba que gerou a explosão que acertou o tile)
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
	
	public static boolean hurryUpIsActive() {
		return hurryUpIsActive;
	}
	
	public static void setHurryUpState(boolean state) {
		if (stageObjectiveIsCleared())
			return;
		if (state != hurryUpIsActive && state) {
			DurationTimerFX.createTimer("HurryUp", Duration.millis(1), () -> {
				Sound.getCurrentMediaPlayer().setRate(1.2);
				Sound.playWav("HurryUp");
				Sound.playWav("voices/HurryUp");
				hurryUpNextCoord.setCoords(hurryUpMinFreeCoord);
				hurryUpDirection = Direction.RIGHT;
				hurryUpDrawX = (int)Main.getMainCanvas().getWidth() / Main.getZoom() + 10;
				dropNextHurryUpBlock();
			});
		}
		hurryUpIsActive = state;
	}

	private static void dropNextHurryUpBlock() {
		if (!MapSet.stageObjectiveIsCleared && !MapSet.stageIsCleared)
			DurationTimerFX.createTimer("NextHurryUp" + Main.uniqueTimerId++, Duration.millis(250), () -> {
				if (hurryUpMinFreeCoord.getY() != hurryUpMaxFreeCoord.getY()) {
					do {
						hurryUpNextCoord.incCoordsByDirection(hurryUpDirection);
						if (hurryUpNextCoord.getX() == hurryUpMaxFreeCoord.getX() && hurryUpDirection == Direction.RIGHT) {
							hurryUpMinFreeCoord.incY(1);
							hurryUpDirection = Direction.DOWN;
						}
						else if (hurryUpNextCoord.getY() == hurryUpMaxFreeCoord.getY() && hurryUpDirection == Direction.DOWN) {
							hurryUpMaxFreeCoord.decX(1);
							hurryUpDirection = Direction.LEFT;
						}
						else if (hurryUpNextCoord.getX() == hurryUpMinFreeCoord.getX() && hurryUpDirection == Direction.LEFT) {
							hurryUpMaxFreeCoord.decY(1);
							hurryUpDirection = Direction.UP;
						}
						else if (hurryUpNextCoord.getY() == hurryUpMinFreeCoord.getY() && hurryUpDirection == Direction.UP) {
							hurryUpMinFreeCoord.incX(1);
							hurryUpDirection = Direction.RIGHT;
						}
						if (hurryUpMinFreeCoord.getY() == hurryUpMaxFreeCoord.getY()) {
							hurryUpDirection = Direction.UP;
							break;
						}
					}
					while (!haveTilesOnCoord(hurryUpNextCoord) || tileContainsProp(hurryUpNextCoord, TileProp.WALL));
				}
				else {
					hurryUpNextCoord.incCoordsByDirection(hurryUpDirection);
					hurryUpDirection = Direction.RIGHT;
					if (hurryUpNextCoord.getX() == hurryUpMaxFreeCoord.getX())
						return;
				}
				dropWallFromSky(hurryUpNextCoord);
				dropNextHurryUpBlock();
			});
	}

	public static int getMapTimeLeftInSecs() {
		return stageTimeInSecs == null ? -1 : stageTimeInSecs;
	}
	
	public static void runStageTag(String stageTagsName, TileCoord tileCoord) {
		runStageTag(stageTagsName, tileCoord, new Entity());
	}

	public static void runStageTag(String stageTagsName, Entity whoTriggered) {
		runStageTag(stageTagsName, new TileCoord(), whoTriggered);
	}

	public static void runStageTag(String stageTagsName) {
		runStageTag(stageTagsName, new TileCoord(), new Entity());
	}

	public static void setStageObjectiveAsClear() {
		if (!stageObjectiveIsCleared) {
			stageObjectiveIsCleared = true;
			if (preLoadedStageTags.containsKey("StageObjectiveCleared"))
				runStageTag("StageObjectiveCleared");
			if (mapFrameSets.haveFrameSet("StageObjectiveCleared"))
				mapFrameSets.setFrameSet("StageObjectiveCleared");
			else if (stageClearSound != null)
				Sound.playWav(stageClearSound).thenAccept(c -> setStageAsClear());
			if (onStageObjectiveClearEvent != null)
				Platform.runLater(onStageObjectiveClearEvent);
		}
	}

	public static void setStageAsClear() {
		if (!stageIsCleared) {
			stageIsCleared = true;
			if (preLoadedStageTags.containsKey("StageCleared"))
				runStageTag("StageCleared");
			if (mapFrameSets.haveFrameSet("StageCleared"))
				mapFrameSets.setFrameSet("StageCleared");
			Runnable event = () -> {
				if (preLoadedStageTags.containsKey("StageClearFadeOut"))
					runStageTag("StageClearFadeOut");
				else
					Draw.setFade(new DefaultFade(0.01).fadeOut().setOnFadeDoneEvent(() -> loadNextMap()));
			};
			if (onStageClearEvent != null)
				Platform.runLater(onStageClearEvent);
			if (stageClearBGM != null)
				Sound.playMp3(stageClearBGM).thenAcceptAsync(mp3 ->	mp3.setOnEndOfMedia(() ->	Platform.runLater(event)));
			else
				Platform.runLater(event);
		}
	}

	private static void loadNextMap() {
		loadMap(nextMapName);
	}

	public static void addLayer(int layerIndex) {
		layers.put(layerIndex, new Layer(layerIndex));
	}

	public static void addLayer(int layerIndex, SpriteLayerType layerType) {
		layers.put(layerIndex, new Layer(layerIndex, layerType));
	}

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

	public static int getTotalLayers() {
		return layers.size();
	}

	public static Layer getCurrentLayer() {
		return layers.get(currentLayerIndex);
	}

	public static int getCurrentLayerIndex() {
		return currentLayerIndex;
	}

	public static void setCurrentLayerIndex(int index) {
		currentLayerIndex = index;
	}

	public static int getSwitchValue(String switchName) {
		return !switches.containsKey(switchName) ? 0 : switches.get(switchName);
	}

	public static void setSwitchValue(String switchName, int value) {
		int oldValue = !switches.containsKey(switchName) ? -1 : switches.get(switchName);
		switches.put(switchName, value);
		if (!stageObjectiveIsCleared && switchName.equals("StageClear")) {
			if (oldValue > 0 && getSwitchValue(switchName) == 0)
				removeStageClearCriteria(StageObjectives.ACTIVATING_SWITCHES);
			else if (oldValue == 0 && getSwitchValue(switchName) > 0)
				addStageClearCriteria(StageObjectives.ACTIVATING_SWITCHES);
		}
	}

	public static Position getMapMinLimit() {
		return mapMinLimit;
	}

	public static void setMapMinLimit(int x, int y) {
		mapMinLimit.setPosition(x, y);
	}

	public static Position getMapMaxLimit() {
		return mapMaxLimit;
	}

	public static void setMapMaxLimit(int x, int y) {
		mapMaxLimit.setPosition(x, y);
	}

	public static List<StageObjectives> getStageClearCriterias() {
		return stageClearCriterias;
	}

	public static List<StageObjectives> getLeftStageClearCriterias() {
		return leftStageClearCriterias;
	}

	public static void setShake(Double incStrength, Double finalStrength) {
		shake = new ShakeEntity(incStrength, incStrength, finalStrength, finalStrength);
	}

	public static void setShake(Double startStrength, Double incStrength, Double finalStrength) {
		shake = new ShakeEntity(startStrength, startStrength, incStrength, incStrength, finalStrength, finalStrength);
	}

	public static void setShake(Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		shake = new ShakeEntity(incStrengthX > 0 ? 0 : finalStrengthX, incStrengthY > 0 ? 0 : finalStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public static void setShake(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		shake = new ShakeEntity(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public static void stopShake() {
		shake.stop();
	}

	public static ShakeEntity getShake() {
		return shake;
	}

	public static void addStageClearCriteria(StageObjectives criteria) {
		if (leftStageClearCriterias != null)
			leftStageClearCriterias.add(criteria);
	}

	public static void removeStageClearCriteria(StageObjectives criteria) {
		if (leftStageClearCriterias != null) {
			leftStageClearCriterias.remove(criteria);
			boolean temp = leftStageClearCriterias.size() == 1 && leftStageClearCriterias.get(0) == StageObjectives.KILLING_ALL_MOBS;
			if (temp || leftStageClearCriterias.isEmpty()) // NOTA: remover temp quando os mobs tiverem sido implementados
				setStageObjectiveAsClear();
		}
	}

	public static void incSwitchValue(String switchName, int incValue) {
		setSwitchValue(switchName, getSwitchValue(switchName) + incValue);
	}

	public static void resetMapFrameSets() {
		stageObjectiveIsCleared = false;
		mapFrameSets = new Entity();
		for (String item : iniFile.getItemList("FRAMESETS"))
			mapFrameSets.addNewFrameSetFromIniFile(mapFrameSets, item, iniFile.fileName(), "FRAMESETS", item);
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
		Materials.tempSprites.put("TileSet", (WritableImage) tileSetImage);
	}

	public static void setBricks() {
		long cTime = System.currentTimeMillis();
		System.out.print("Definindo tijolos aleatórios... ");
		Brick.clearBricks();
		for (TileCoord coord : getTilePropsMap().keySet())
			if (getTileProps(coord).contains(TileProp.FIXED_BRICK))
				Brick.addBrick(coord.getNewInstance());
		if (iniFile.read("SETUP", "Blocks") != null && !iniFile.read("SETUP", "Blocks").equals("0")) {
			int totalBricks = 0, bricksQuant = 0;
			int[] totalBrickSpawners = { 0 };
			try {
				String[] split = iniFile.read("SETUP", "Blocks").split("!");
				int minBricks = Integer.parseInt(split[0]), maxBricks = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
				totalBricks = (int) MyMath.getRandom(minBricks, maxBricks);
				for (TileCoord coord : getTilePropsMap().keySet()) {
					getTileProps(coord).forEach(prop -> {
						if (prop == TileProp.BRICK_RANDOM_SPAWNER && !Brick.haveBrickAt(coord))
							totalBrickSpawners[0]++;
					});
				}
			}
			catch (Exception e) {
				throw new RuntimeException(iniFile.read("SETUP", "Blocks") + " - Wrong data for this item");
			}
			List<TileCoord> coords = new ArrayList<>();
			for (TileCoord coord : getLayer(26).getTilesMap().keySet())
				if (!Brick.haveBrickAt(coord) && tileContainsProp(coord, TileProp.BRICK_RANDOM_SPAWNER) && tileIsFree(coord, Set.of(PassThrough.PLAYER)))
					coords.add(coord.getNewInstance());
			done:
			while (!coords.isEmpty() && totalBricks > 0)
				for (TileCoord coord : new ArrayList<>(coords))
					if ((int)MyMath.getRandom(0, 3) == 0) {
						coords.remove(coord);
						Brick.addBrick(coord.getNewInstance());
						if (coords.isEmpty() || --totalBricks == 0 || ++bricksQuant >= totalBrickSpawners[0])
							break done;
					}
		}
		addItemsToBricks();
		System.out.println("concluido em " + (System.currentTimeMillis() - cTime)  +"ms");
	}

	public static int getBricksRegenTimeInFrames() {
		return bricksRegenTimeInFrames;
	}

	public static void setBricksRegenTime(int bricksRegenTimeInSecs) {
		bricksRegenTimeInFrames = bricksRegenTimeInSecs * 60;
		Brick.getBricks().forEach(brick -> brick.setRegenTime(bricksRegenTimeInSecs));
	}

	private static void addItemsToBricks() {
		if (iniFile.read("SETUP", "Items") != null && !iniFile.read("SETUP", "Items").equals("0")) {
			String[] split = iniFile.read("SETUP", "Items").split(" ");
			try {
				for (int n = 0; n < split.length && n < Brick.totalBricks(); n++) {
					int itemId = Integer.parseInt(split[n]);
					Brick brick = null;
					do {
						brick = Brick.getBricks().get((int) MyMath.getRandom(0, Brick.totalBricks() - 1));
					}
					while (brick.getItem() != null);
					brick.setItem(ItemType.getItemById(itemId));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(iniFile.read("SETUP", "Items") + " - Wrong data for this item");
			}
		}
	}

	public static void setRandomWalls() {
		if (iniFile.read("SETUP", "FixedBlocks") != null && !iniFile.read("SETUP", "FixedBlocks").equals("0")) {
			long cTime = System.currentTimeMillis();
			System.out.print("Definindo paredes aleatórias... ");
			int totalWalls = 0;
			try {
				String[] split = iniFile.read("SETUP", "FixedBlocks").split("!");
				int minWalls = Integer.parseInt(split[0]), maxWalls = Integer.parseInt(split[split.length == 1 ? 0 : 1]);
				totalWalls = (int) MyMath.getRandom(minWalls, maxWalls);
			}
			catch (Exception e) {
				throw new RuntimeException(iniFile.read("SETUP", "FixedBlocks") + " - Wrong data for this item");
			}
			List<TileCoord> coords = new ArrayList<>();
			for (TileCoord coord : getLayer(26).getTilesMap().keySet())
				if (getTileProps(coord).contains(TileProp.GROUND) && tileIsFree(coord, Set.of(PassThrough.PLAYER)))
					coords.add(coord.getNewInstance());
			done:
			while (!coords.isEmpty() && totalWalls > 0) {
				for (TileCoord coord : new ArrayList<>(coords))
					if ((int) MyMath.getRandom(0, 9) == 0) {
						coords.remove(coord);
						if (testCoordForInsertFixedBlock(coord)) {
							getLayer(26).setTileProps(coord.getNewInstance(), new ArrayList<>(Arrays.asList(TileProp.WALL)));
							getFirstBottomTileFromCoord(coord).spriteX = (int) wallTile.getX();
							getFirstBottomTileFromCoord(coord).spriteY = (int) wallTile.getY();
							coord.incY(1);
							if (tileIsFree(coord))
								Tile.addTileShadow(groundWithWallShadow, coord);
							if (coords.isEmpty() || --totalWalls == 0)
								break done;
						}
					}
			}
			getLayer(26).buildLayer();
			System.out.println("concluido em " + (System.currentTimeMillis() - cTime)  +"ms");
		}
	}

	private static boolean testCoordForInsertFixedBlock(TileCoord coord) {
		return testCoordForInsertFixedBlock(coord, Set.of(PassThrough.BRICK, PassThrough.ITEM, PassThrough.BOMB, PassThrough.PLAYER));
	}

	private static boolean testCoordForInsertFixedBlock(TileCoord coord, Set<PassThrough> passThrough) {
		List<TileCoord> checks = new ArrayList<>(initialPlayerCoords);
		getLayer(26).addTileProp(coord, TileProp.WALL);
		TileCoord coord1 = new TileCoord();
		Direction dir1 = Direction.LEFT;
		for (int n = 0; n < 4; n++) {
			dir1 = dir1.getNext4WayClockwiseDirection();
			coord1.setCoords(coord);
			coord1.incCoordsByDirection(dir1);
			if (tileIsFree(coord1, passThrough))
				for (TileCoord target : checks) {
					PathFinder pf = new PathFinder(coord1.getNewInstance(), target.getNewInstance(), dir1, t -> tileIsFree(t, passThrough));
					if (!pf.pathWasFound()) {
						getLayer(26).removeTileProp(coord, TileProp.WALL);
						return false;
					}
				}
		}
		getLayer(26).removeTileProp(coord, TileProp.WALL);
		return true;
	}
	
	public static List<TileCoord> getInitialPlayerPositions() {
		return initialPlayerCoords;
	}

	public static List<TileCoord> getInitialMonsterPositions() {
		return initialMonsterCoords;
	}

	public static Position getInitialPlayerPosition(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= initialPlayerCoords.size())
			return null;
		return initialPlayerCoords.get(playerIndex).getPosition();
	}

	public static Position getInitialMonsterPosition(int monsterIndex) {
		if (monsterIndex < 0 || monsterIndex >= initialMonsterCoords.size())
			return null;
		return initialMonsterCoords.get(monsterIndex).getPosition();
	}

	public static IniFile getMapIniFile() {
		return iniFile;
	}

	public static IniFile getTileSetIniFile() {
		return iniFileTileSet;
	}

	private static Position getTilePositionFromIni(IniFile ini, String tileStr) {
		Position position = new Position();
		if (iniFileTileSet.read("CONFIG", tileStr) == null)
			return null;
		String[] split2 = iniFileTileSet.read("CONFIG", tileStr).split(" ");
		if (split2.length > 0) {
			try {
				position.setPosition(Integer.parseInt(split2[0]) * Main.TILE_SIZE, Integer.parseInt(split2[1]) * Main.TILE_SIZE);
			}
			catch (Exception e) {
				throw new RuntimeException(iniFileTileSet.read("CONFIG", tileStr) + " - Invalid data on file \"" + iniFileTileSet.getFilePath().getFileName() + "\"");
			}
		}
		return position;
	}

	public static Position getGroundTile() {
		return groundTile;
	}

	public static Position getWallTile() {
		return wallTile;
	}

	public static Position getGroundWithBrickShadow() {
		return groundWithBrickShadow;
	}

	public static Position getGroundWithWallShadow() {
		return groundWithWallShadow;
	}

	public static void rebuildAllLayers() {
		layers.keySet().forEach(layer -> layers.get(layer).buildLayer());
	}

	public static void setGroundTile(Position groundTile) {
		groundTile = new Position(groundTile);
	}

	public static void setWallTile(Position wallTile) {
		wallTile = new Position(wallTile);
	}

	public static void setGroundWithBrickShadow(Position groundWithBrickShadow) {
		groundWithBrickShadow = new Position(groundWithBrickShadow);
	}

	public static void setGroundWithWallShadow(Position groundWithWallShadow) {
		groundWithWallShadow = new Position(groundWithWallShadow);
	}

	public static Map<Integer, Layer> getLayersMap() {
		return layers;
	}

	public static boolean isValidLayer(int layer) {
		return layers.containsKey(layer);
	}

	public static Layer getLayer(int layerIndex) {
		if (!isValidLayer(layerIndex))
			throw new RuntimeException(layerIndex + " - Invalid layer index");
		return layers.get(layerIndex);
	}

	public static void setCopyImageLayerIndex(int index) {
		copyImageLayerIndex = index;
	}

	public static int getCopyImageLayerIndex() {
		return copyImageLayerIndex;
	}

	public static Layer getCopyLayer() {
		return getLayer(copyImageLayerIndex);
	}

	public static Image getTileSetImage() {
		return tileSetImage;
	}

	public static void setTileSetImage(Image image) {
		tileSetImage = image;
	}

	public static String getTileSetName() {
		return tileSetName;
	}

	public static String getMapName() {
		return mapName;
	}

	public static String getIniMapName() {
		return iniMapName;
	}

	public static List<Tile> getTileListFromCurrentLayer() {
		return getCurrentLayer().getTileList();
	}

	public static Tile getFirstBottomTileFromCoord(TileCoord coord) {
		return getCurrentLayer().getFirstBottomTileFromCoord(coord);
	}

	public static Tile getTopTileFromCoord(TileCoord coord) {
		return getCurrentLayer().getTopTileFromCoord(coord);
	}

	public static List<Tile> getTileListFromCoord(TileCoord coord) {
		return getCurrentLayer().getTilesFromCoord(coord);
	}

	public static void checkTileTrigger(Entity entity, TileCoord coord, TileProp triggerProp) {
		checkTileTrigger(entity, coord, triggerProp, false);
	}

	public static void checkTileTrigger(Entity entity, TileCoord coord, TileProp triggerProp, boolean isTileStepOut) {
		Tile tile = getFirstBottomTileFromCoord(coord);
		boolean stepTrigger = triggerProp == TileProp.TRIGGER_BY_BRICK || triggerProp == TileProp.TRIGGER_BY_BOMB || triggerProp == TileProp.TRIGGER_BY_ITEM || triggerProp == TileProp.TRIGGER_BY_MOB || triggerProp == TileProp.TRIGGER_BY_PLAYER || triggerProp == TileProp.TRIGGER_BY_UNRIDE_PLAYER;
		boolean containsStepOut = tile.getTileProps().contains(TileProp.TRIGGER_WHEN_STEP_OUT);
		if (stepTrigger && ((isTileStepOut && !containsStepOut) || (!isTileStepOut && containsStepOut)))
			return;
		if (tile.tileHaveTags())
			for (TileProp prop : getTileProps(coord))
				if (prop == triggerProp)
					tile.runTags(entity, coord);
	}

	public static boolean haveTilesOnCoord(TileCoord coord) {
		return getCurrentLayer().haveTilesOnCoord(coord);
	}

	public static void run() {
		if (hurryUpIsActive())
			drawHurryUpMessage();
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
		Explosion.drawExplosions();
		Brick.drawBricks();
		Bomb.drawBombs();
		Item.drawItems();
		TileDamage.runTileDamages();
		Effect.drawEffects();
		BomberMan.drawBomberMans();
		removeStageTag.forEach(fs -> runningStageTags.remove(fs));
	}
	
	public static int getMapLimitWidth() {
		return (int)((MapSet.getMapMaxLimit().getX() - MapSet.getMapMinLimit().getX()) + MapSet.getMapMinLimit().getX() + Main.TILE_SIZE); 
	}

	public static int getMapLimitHeight() {
		return (int)((MapSet.getMapMaxLimit().getY() - MapSet.getMapMinLimit().getY()) + MapSet.getMapMinLimit().getY() + Main.TILE_SIZE); 
	}

	private static void drawHurryUpMessage() {
		if (hurryUpDrawX != null) {
			if (Misc.blink(25)) {
				Draw.addDrawQueue(1, SpriteLayerType.CLOUD, DrawType.SET_GLOBAL_ALPHA, 1);
				Draw.addDrawQueue(1, SpriteLayerType.CLOUD, DrawType.SET_FONT, GameFonts.fontBomberMan60);
				Draw.addDrawQueue(1, SpriteLayerType.CLOUD, DrawType.SET_LINE_WIDTH, 3);
				Draw.addDrawQueue(2, SpriteLayerType.CLOUD, DrawType.SET_STROKE, Color.valueOf("#AAAAAA"));
				Draw.addDrawQueue(3, SpriteLayerType.CLOUD, DrawType.SET_FILL, Color.valueOf("#FFFFFF"));
				Draw.addDrawQueue(4, SpriteLayerType.CLOUD, DrawType.FILL_TEXT, null, null, "HURRY UP!", hurryUpDrawX, Main.getMainCanvas().getHeight() / 2 / Main.getZoom() + 32 * Main.zoom - 30);
				Draw.addDrawQueue(5, SpriteLayerType.CLOUD, DrawType.STROKE_TEXT, null, null, "HURRY UP!", hurryUpDrawX, Main.getMainCanvas().getHeight() / 2 / Main.getZoom() + 32 * Main.zoom - 30);
			}
			Text text = new Text("HURRY UP!");
			text.setFont(GameFonts.fontBomberMan60);
			int x = (int)text.getLayoutBounds().getWidth();
			if ((hurryUpDrawX -= 4) + x < 0)
				hurryUpDrawX = null;
		}
	}

	public static boolean tileIsFree(TileCoord coord) {
		return tileIsFree(null, coord);
	}

	public static boolean tileIsFree(TileCoord coord, Set<PassThrough> passThrough) {
		return tileIsFree(null, coord, passThrough);
	}

	public static boolean tileIsFree(Entity entity, TileCoord coord) {
		return tileIsFree(entity, coord, null);
	}

	public static boolean tileIsFree(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		if (!haveTilesOnCoord(coord))
			return false;
		Entity en = Entity.haveAnyEntityAtCoord(coord, entity) ? Entity.getFirstEntityFromCoord(coord) : null;
		for (TileProp prop : new ArrayList<>(getTileProps(coord))) {
			if (entity != null && entity.getElevation() == Elevation.ON_GROUND) {
				if ((entity instanceof Bomb && prop == TileProp.GROUND_NO_BOMB) ||
						(entity instanceof Brick && prop == TileProp.GROUND_NO_BRICK) ||
						(entity instanceof Item && prop == TileProp.GROUND_NO_ITEM) ||
						(entity instanceof Monster && prop == TileProp.GROUND_NO_MOB) ||
						(entity instanceof BomberMan && prop == TileProp.GROUND_NO_PLAYER))
							return false;
			}
			if ((entity == null || entity.getElevation() == Elevation.ON_GROUND) &&
					(((prop == TileProp.HOLE || prop == TileProp.GROUND_HOLE || prop == TileProp.DEEP_HOLE) && (passThrough == null || !passThrough.contains(PassThrough.HOLE))) ||
					(prop == TileProp.WALL && (passThrough == null || !passThrough.contains(PassThrough.WALL))) ||
					((prop == TileProp.WATER || prop == TileProp.DEEP_WATER) && (passThrough == null || !passThrough.contains(PassThrough.WATER))) ||
					(en != null && en instanceof BomberMan && passThrough != null && !passThrough.contains(PassThrough.PLAYER)) ||
					(en != null && en instanceof Monster && passThrough != null && !passThrough.contains(PassThrough.MONSTER)) ||
					(Item.haveItemAt(coord) && Item.getItemAt(coord) != entity && passThrough != null && !passThrough.contains(PassThrough.ITEM)) ||
					(Brick.haveBrickAt(coord) && Brick.getBrickAt(coord) != entity && (passThrough == null || !passThrough.contains(PassThrough.BRICK))) ||
					(Bomb.haveBombAt(entity, coord) && Bomb.getBombAt(coord) != entity && Bomb.getBombAt(coord).getBombType() != BombType.LAND_MINE && (passThrough == null || !passThrough.contains(PassThrough.BOMB))) ||
					TileProp.getCantCrossList(entity == null ? Elevation.ON_GROUND : entity.getElevation()).contains(prop)))
						return false;
		}
		return true;
	}

	public static boolean tileIsOccuped(TileCoord coord, Set<PassThrough> passThrough) {
		return tileIsOccuped(null, coord, passThrough);
	}

	public static boolean tileIsOccuped(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		// NOTA: Implementar retornando se tem monstro ou player em cima
		return !tileIsFree(entity, coord, passThrough) || Item.haveItemAt(coord);
	}

	// ================ Metodos relacionados a TileProps ==============

	public static boolean tileContainsProp(TileCoord coord, TileProp prop) {
		return getCurrentLayer().tileContainsProp(coord, prop);
	}

	public static Map<TileCoord, List<TileProp>> getTilePropsMap() {
		return getCurrentLayer().getTilePropsMap();
	}

	public static boolean tileHaveProps(TileCoord coord) {
		return getCurrentLayer().tileHaveProps(coord);
	}

	public static List<TileProp> getTileProps(TileCoord coord) {
		return getCurrentLayer().getTileProps(coord);
	}

	public static int getTotalTileProps(TileCoord coord) {
		return getCurrentLayer().getTotalTileProps(coord);
	}

	public static void setTileProps(TileCoord coord, List<TileProp> tileProps) {
		getCurrentLayer().setTileProps(coord, tileProps);
	}

	public static void addTileProp(TileCoord coord, TileProp... props) {
		getCurrentLayer().addTileProp(coord, props);
	}

	public static void removeTileProp(TileCoord coord, TileProp... props) {
		getCurrentLayer().removeTileProp(coord, props);
	}

	public static void clearTileProps(TileCoord coord) {
		getCurrentLayer().clearTileProps(coord);
	}

	// ================ Metodos relacionados a TileTags ==============

	public static boolean tileHaveTags(TileCoord coord) {
		return getCurrentLayer().tileHaveTags(coord);
	}

	public static void disableTileTags(TileCoord coord) {
		getCurrentLayer().disableTileTags(coord);
	}

	public static void enableTileTags(TileCoord coord) {
		getCurrentLayer().enableTileTags(coord);
	}

	public static boolean tileTagsIsDisabled(TileCoord coord) {
		return getCurrentLayer().tileTagsIsDisabled(coord);
	}

	public static Tags getTileTags(TileCoord coord) {
		return getCurrentLayer().getTileTags(coord);
	}

	public static String getStringTags(TileCoord coord) {
		return getCurrentLayer().getStringTags(coord);
	}

	public static FrameSet getTileTagsFrameSet(TileCoord coord) {
		return getCurrentLayer().getTileTagsFrameSet(coord);
	}

	public static void setTileTagsFromString(TileCoord coord, String stringTileTags) {
		getCurrentLayer().setTileTagsFromString(coord, stringTileTags);
	}

	public static void setTileTagsFromString(TileCoord coord, String stringTileTags, Tile tile) {
		getCurrentLayer().setTileTagsFromString(coord, stringTileTags, tile);
	}

	public static void setTileTags(TileCoord coord, Tags tags) {
		getCurrentLayer().setTileTags(coord, tags);
	}

	public static void removeTileTag(TileCoord coord, String tagStr) {
		getCurrentLayer().removeTileTag(coord, tagStr);
	}

	public static void removeTileTag(TileCoord coord, FrameTag tag) {
		getCurrentLayer().removeTileTag(coord, tag);
	}

	public static void clearTileTags(TileCoord coord) {
		getCurrentLayer().clearTileTags(coord);
	}

	public static CompletableFuture<TileCoord> getRandomFreeTileAsync() {
		return getRandomFreeTileAsync(null, false, null);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(boolean test) {
		return getRandomFreeTileAsync(null, test, null);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(Predicate<TileCoord> extraTileIsFreeVerification) {
		return getRandomFreeTileAsync(null, false, extraTileIsFreeVerification);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(boolean test, Predicate<TileCoord> extraTileIsFreeVerification) {
		return getRandomFreeTileAsync(null, test, extraTileIsFreeVerification);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(Set<PassThrough> passThrough) {
		return getRandomFreeTileAsync(passThrough, false, null);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(Set<PassThrough> passThrough, boolean test) {
		return getRandomFreeTileAsync(passThrough, test, null);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(Set<PassThrough> passThrough, Predicate<TileCoord> extraTileIsFreeVerification) {
		return getRandomFreeTileAsync(passThrough, false, extraTileIsFreeVerification);
	}
	
	public static CompletableFuture<TileCoord> getRandomFreeTileAsync(Set<PassThrough> passThrough, boolean test, Predicate<TileCoord> extraTileIsFreeVerification) {
		return CompletableFuture.supplyAsync(() -> {
			return getRandomFreeTile(passThrough, test, extraTileIsFreeVerification);
		});
	}
	
	public static TileCoord getRandomFreeTile() {
		return getRandomFreeTile(null, false, null);
	}
	
	public static TileCoord getRandomFreeTile(boolean test) {
		return getRandomFreeTile(null, test, null);
	}
	
	public static TileCoord getRandomFreeTile(Predicate<TileCoord> extraTileIsFreeVerification) {
		return getRandomFreeTile(null, false, extraTileIsFreeVerification);
	}
	
	public static TileCoord getRandomFreeTile(boolean test, Predicate<TileCoord> extraTileIsFreeVerification) {
		return getRandomFreeTile(null, test, extraTileIsFreeVerification);
	}
	
	public static TileCoord getRandomFreeTile(Set<PassThrough> passThrough) {
		return getRandomFreeTile(passThrough, false, null);
	}
	
	public static TileCoord getRandomFreeTile(Set<PassThrough> passThrough, boolean test) {
		return getRandomFreeTile(passThrough, test, null);
	}
	
	public static TileCoord getRandomFreeTile(Set<PassThrough> passThrough, Predicate<TileCoord> extraTileIsFreeVerification) {
		return getRandomFreeTile(passThrough, false, extraTileIsFreeVerification);
	}
	
	public static TileCoord getRandomFreeTile(Set<PassThrough> passThrough, boolean test, Predicate<TileCoord> extraTileIsFreeVerification) {
		List<Tile> list = new ArrayList<>(MapSet.getTileListFromCurrentLayer());
		for (int n = (int)MyMath.getRandom(0, list.size() - 1); !list.isEmpty();) {
			TileCoord coord = list.get(n).getTileCoord().getNewInstance();
			if ((!test || testCoordForInsertFixedBlock(coord)) &&
					tileIsFree(coord, passThrough) && (extraTileIsFreeVerification == null || extraTileIsFreeVerification.test(coord)))
						return coord;
			list.remove(n);
			n = (int)MyMath.getRandom(0, list.size() - 1);
		}
		return null;
	}

	public static Brick dropWallFromSky(TileCoord coord) {
		return Brick.dropBrickFromSky(coord, null, true);
	}

	public static boolean stageObjectiveIsCleared() {
		return stageObjectiveIsCleared;
	}

	public static boolean stageIsCleared() {
		return stageIsCleared;
	}

	public static void setOnMapLoadEvent(Runnable runnable) {
		onAfterMapLoadEvent = runnable;		
	}

	public static void setOnStageObjectiveClearEvent(Runnable runnable) {
		onStageObjectiveClearEvent = runnable;		
	}

}
