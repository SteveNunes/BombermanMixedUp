package gui;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import application.Main;
import entities.Entity;
import entities.TileCoord;
import enums.Direction;
import enums.Icons;
import enums.PathFindDistance;
import enums.PathFindIgnoreInitialBackDirection;
import enums.SpriteLayerType;
import enums.TileProp;
import gui.util.ControllerUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import maps.Brick;
import maps.Layer;
import maps.MapSet;
import maps.Tile;
import objmoveutils.Position;
import tools.GameMisc;
import tools.IniFiles;
import tools.Materials;
import tools.PathFinder;
import util.FindFile;

public class MapEditor {

	@FXML
	private VBox vBoxTileSet;
	@FXML
	private Button buttonPlay;
  @FXML
  private Button buttonAddMap;
  @FXML
  private Button buttonRenameMap;
  @FXML
  private Button buttonRemoveMap;
  @FXML
  private Button buttonReloadFromDisk;
  @FXML
  private Button buttonSaveToDisk;
  @FXML
  private Button buttonAddLayer;
  @FXML
  private Button buttonSetFrameSetBrickStand;
  @FXML
  private Button buttonSetFrameSetBrickBreaking;
  @FXML
  private Button buttonSetFrameSetBrickRegen;
  @FXML
  private Button buttonSetFrameSetWallSprite;
  @FXML
  private Button buttonSetFrameSetGroundSprite;
  @FXML
  private Button buttonSetFrameSetGroundWithBrickShadow;
  @FXML
  private Button buttonSetFrameSetGroundWithWallShadow;
  @FXML
  private Button buttonSetFrameSetFragileGround;
  @FXML
  private Button buttonTileSetZoom1;
  @FXML
  private Button buttonTileSetZoom2;
  @FXML
  private Canvas canvasBrickStand;
  @FXML
  private Canvas canvasBrickBreaking;
  @FXML
  private Canvas canvasBrickRegen;
  @FXML
  private Canvas canvasWallSprite;
  @FXML
  private Canvas canvasGroundSprite;
  @FXML
  private Canvas canvasGroundWithWallShadow;
  @FXML
  private Canvas canvasGroundWithBrickShadow;
  @FXML
  private Canvas canvasFragileGround;
  @FXML
  private Canvas canvasMain;
  @FXML
  private Canvas canvasTileSet;
  @FXML
  private CheckBox checkBoxShowAim;
  @FXML
  private CheckBox checkBoxShowGrid;
  @FXML
  private CheckBox checkBoxShowBlockType;
  @FXML
  private CheckBox checkBoxShowBricks;
  @FXML
  private CheckBox checkBoxShowItens;
  @FXML
	private ListView<HBox> listViewLayers;
  @FXML
  private ComboBox<String> comboBoxTileSets;
  @FXML
  private ComboBox<String> comboBoxMapFrameSets;
  @FXML
  private ComboBox<String> comboBoxMapList;

	private Map<SpriteLayerType, Canvas> canvas;
	private Map<SpriteLayerType, GraphicsContext> gcs;
	private Rectangle selection;
	private Rectangle tileSelection;
	private SnapshotParameters params;
	private Scene sceneMain;
	private GraphicsContext gcMain;
	private GraphicsContext gcTileSet;
	private GraphicsContext gcBrickStand;
	private GraphicsContext gcBrickBreaking;
	private GraphicsContext gcBrickRegen;
	private GraphicsContext gcWallSprite;
	private GraphicsContext gcGroundSprite;
	private GraphicsContext gcGroundWithBrickShadow;
	private GraphicsContext gcGroundWithWallShadow;
	private GraphicsContext gcFragileGround;
	private Canvas canvasDraw;
	private GraphicsContext gcDraw;
	private Brick[] bricks;
	private Position[] tilePosition;
	private Canvas[] canvasList;
	private List<KeyCode> holdedKeys;
	private Map<TileCoord, Entity> fragileTiles;
	private Entity sampleFragileTile;
	private Map<TileCoord, List<Tile>> copiedTiles;
	private List<Map<TileCoord, List<Tile>>> backupTiles;
	private MapSet currentMapSet;
	private Font font;
	private ContextMenu defaultContextMenu;
	private int currentLayerIndex;
	private CanvasMouse canvasMouseDraw;
	private CanvasMouse canvasMouseTileSet;
	private int zoomMain;
	private int zoomTileSet;
	private int ctrlZPos;
	private long resetBricks;
	private boolean playing;
	private PathFinder pathFinder;
	private TileCoord pathFinderTarget;
	private Function<TileCoord, Boolean> pathFinderFunc;
	private Direction pathFinderDirection;
	private PathFindIgnoreInitialBackDirection pathFinderIgnoreBackDir;
	private PathFindDistance pathFinderDistance;
	public static GraphicsContext gcTemp;

	public void init(Scene scene) {
		sceneMain = scene;
		canvasMouseDraw = new CanvasMouse();
		canvasMouseTileSet = new CanvasMouse();
		tileSelection = new Rectangle(0, 0, 1, 1);
		holdedKeys = new ArrayList<>();
		copiedTiles = new HashMap<>();
		backupTiles = new ArrayList<>();
		fragileTiles = new HashMap<>();
		font = new Font("Lucida Console", 15);
		resetBricks = System.currentTimeMillis();
		zoomMain = 3;
		zoomTileSet = 1;
		currentLayerIndex = 26;
		ctrlZPos = -1;
		playing = false;
		selection = null;
		setAllCanvas();
		defineControls();
		setKeyboardEvents();
		setMainCanvasMouseEvents();
		rebuildTileSetCanvas();
		initPathFinderTest();
		gcTemp = canvasMain.getGraphicsContext2D();
		mainLoop();
	}
	
	private void initPathFinderTest() {
		pathFinder = null;
		pathFinderDirection = Direction.LEFT;
		pathFinderIgnoreBackDir = PathFindIgnoreInitialBackDirection.NO_IGNORE;
		pathFinderDistance = PathFindDistance.RANDOM;
		pathFinderTarget = new TileCoord();
		pathFinderFunc = new Function<TileCoord, Boolean>() {
			@Override
			public Boolean apply(TileCoord coord) {
				Tile tile = currentMapSet == null || getCurrentLayer() == null || !getCurrentLayer().haveTilesOnCoord(coord) ? null :
										 getCurrentLayer().getFirstTileFromCoord(coord);
				return tile != null && tile.tileProp.contains(TileProp.GROUND) && !Brick.haveBrickAt(coord);
			}
		};
	}

	void setAllCanvas() {
		gcs = new HashMap<>();
		canvas = new HashMap<>();
		for (SpriteLayerType layerType : SpriteLayerType.getList()) {
			canvas.put(layerType, new Canvas(canvasMain.getWidth(), canvasMain.getHeight()));
			gcs.put(layerType, canvas.get(layerType).getGraphicsContext2D());
			gcs.get(layerType).setImageSmoothing(false);
		}
		canvasList = new Canvas[] {canvasBrickStand, canvasBrickBreaking, canvasBrickRegen, canvasWallSprite, canvasGroundSprite, canvasGroundWithWallShadow, canvasGroundWithBrickShadow, canvasFragileGround};
		canvasDraw = new Canvas(canvasMain.getWidth(), canvasMain.getHeight());
		gcBrickStand = canvasBrickStand.getGraphicsContext2D();
		gcBrickStand.setImageSmoothing(false);
		gcBrickBreaking = canvasBrickBreaking.getGraphicsContext2D();
		gcBrickBreaking.setImageSmoothing(false);
		gcBrickRegen = canvasBrickRegen.getGraphicsContext2D();
		gcBrickRegen.setImageSmoothing(false);
		gcWallSprite = canvasWallSprite.getGraphicsContext2D();
		gcWallSprite.setImageSmoothing(false);
		gcGroundSprite = canvasGroundSprite.getGraphicsContext2D();
		gcGroundSprite.setImageSmoothing(false);
		gcGroundWithBrickShadow = canvasGroundWithBrickShadow.getGraphicsContext2D();
		gcGroundWithBrickShadow.setImageSmoothing(false);
		gcGroundWithWallShadow = canvasGroundWithWallShadow.getGraphicsContext2D();
		gcGroundWithWallShadow.setImageSmoothing(false);
		gcFragileGround = canvasFragileGround.getGraphicsContext2D();
		gcFragileGround.setImageSmoothing(false);
		gcDraw = canvasDraw.getGraphicsContext2D();
		gcDraw.setImageSmoothing(false);
		gcMain = canvasMain.getGraphicsContext2D();
	  gcMain.setImageSmoothing(false);
	}
	
	void defineControls() {
		ControllerUtils.addIconToButton(buttonAddMap, Icons.NEW_FILE.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRenameMap, Icons.EDIT.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRemoveMap, Icons.DELETE.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonReloadFromDisk, Icons.REFRESH.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonSaveToDisk, Icons.SAVE.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonAddLayer, Icons.NEW_FILE.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonTileSetZoom1, Icons.ZOOM_PLUS.getValue(), 20, 20, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonTileSetZoom2, Icons.ZOOM_MINUS.getValue(), 20, 20, Color.WHITE, 150);
		checkBoxShowBricks.setSelected(true);
		checkBoxShowItens.setSelected(true);
		checkBoxShowBricks.setOnAction(e -> {
			if (!checkBoxShowBricks.isSelected())
				Brick.clearBricks();
			else
				currentMapSet.setBricks();
			checkBoxShowItens.setDisable(!checkBoxShowBricks.isSelected());
		});
		buttonTileSetZoom1.setOnAction(e -> {
			if (zoomTileSet < 3) {
				zoomTileSet++;
				rebuildTileSetCanvas();
			}
		});
		buttonTileSetZoom2.setOnAction(e -> {
			if (zoomTileSet > 1) {
				zoomTileSet--;
				rebuildTileSetCanvas();
			}
		});
		buttonSetFrameSetGroundSprite.setOnAction(e -> {
			currentMapSet.setGroundTile(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			setSampleTiles();
		});
		buttonSetFrameSetGroundWithWallShadow.setOnAction(e -> {
			currentMapSet.setGroundWithWallShadow(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			currentMapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetGroundWithBrickShadow.setOnAction(e -> {
			currentMapSet.setGroundWithBrickShadow(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			currentMapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetWallSprite.setOnAction(e -> {
			currentMapSet.setWallTile(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			setSampleTiles();
		});
		buttonSetFrameSetFragileGround.setOnAction(e -> {
			currentMapSet.setFragileGround(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			setSampleTiles();
		});
		List<File> list = FindFile.findFile("./appdata/tileset/", "*.tiles");
		list.forEach(file -> comboBoxTileSets.getItems().add(file.getName().replace(".tiles", "")));
		comboBoxTileSets.valueProperty().addListener((obs, oldV, newV) -> {
			currentMapSet.setTileSet(newV);
			currentMapSet.setBricks();
			currentMapSet.rebuildAllLayers();
			setSampleTiles();
		});
		comboBoxMapList.getItems().addAll(IniFiles.stages.getSectionList());
		comboBoxMapList.valueProperty().addListener((obs, oldV, newV) -> {
			loadMap(newV);
		});
		comboBoxMapList.getSelectionModel().select(0);
		GameMisc.setNodeFont(buttonPlay, "Lucida Console", 14);
		buttonPlay.setText("►");
		buttonPlay.setOnAction(e -> {
			reloadCurrentMap();
			playing = !playing;
			buttonPlay.setText(playing ? "■" : "►");
		});
		canvasBrickStand.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickStandFrameSet")));
		canvasBrickBreaking.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickBreakFrameSet")));
		canvasBrickRegen.setOnMouseClicked(e -> {
			Brick.getBricks().forEach(brick -> {
				if (!brick.haveFrameSet("BrickRegenFrameSet2"))
					brick.addNewFrameSetFromString("BrickRegenFrameSet2", currentMapSet.getTileSetIniFile().read("CONFIG", "BrickRegenFrameSet") + "|{SetFrameSet;BrickStandFrameSet}");
				brick.setFrameSet("BrickRegenFrameSet2");
			});
		});
	}

	void mainLoop() {
		drawDrawCanvas();
		drawMainCanvas();
		GameMisc.getFPSHandler().fpsCounter();
		if (!Main.close )
			Platform.runLater(() -> {
				String title = "Map Editor"
						+ "     FPS: " + GameMisc.getFPSHandler().getFPS()
						+ "     " + canvasMouseDraw.tileCoord + " (Free: " + pathFinderFunc.apply(canvasMouseDraw.tileCoord) + ")"
						+ "     Zoom: x" + zoomMain
						+ "     Tileset Zoom: x" + zoomTileSet;
				Main.stageMain.setTitle(title);
				mainLoop();
			});
	}
	
	void saveCtrlZ() { System.out.println("saveCtrlZ " + System.currentTimeMillis());
		backupTiles.add(new HashMap<>(getTileMapFromCurrentLayer()));
		ctrlZPos = backupTiles.size() - 1;
	}
	
	void ctrlZ() {
		if (!backupTiles.isEmpty()) {
			if (--ctrlZPos == -1)
				ctrlZPos = backupTiles.size() - 1;
			getCurrentLayer().setTilesMap(backupTiles.get(ctrlZPos));
			rebuildCurrentLayer(false);
			currentMapSet.setBricks();
		}
	}
	
	void ctrlY() {
		if (!backupTiles.isEmpty()) {
			if (++ctrlZPos == backupTiles.size())
				ctrlZPos = 0;
			getCurrentLayer().setTilesMap(backupTiles.get(ctrlZPos));
			rebuildCurrentLayer(false);
			currentMapSet.setBricks();
		}
	}
	
	MapSet getCurrentMapSet()
		{ return currentMapSet; }
	
	public int getCurrentLayerIndex()
		{ return currentLayerIndex; }
	
	void reloadCurrentMap()
		{ loadMap(currentMapSet.getMapName()); }
	
	void loadMap(String mapName)
		{ loadMap(mapName, true); }
	
	void setLayer(Integer newValue)
		{ currentLayerIndex = newValue; }

	void loadMap(String mapName, boolean resetCurrentLayerIndex) {
		if (mapName == null)
			GameMisc.throwRuntimeException("Unable to load map because 'mapName' is null");
		Tile.tags = new HashMap<>();
		currentMapSet = new MapSet(mapName);
		listViewLayers.getItems().clear();
		List<Integer> list = new ArrayList<Integer>(currentMapSet.getLayersMap().keySet());
		list.sort((n1, n2) -> n2 - n1);
		for (int layer : list) {
			HBox hBox = new HBox(new Text("" + layer));
			ComboBox<SpriteLayerType> comboBox = new ComboBox<>();
			ControllerUtils.setListToComboBox(comboBox, Arrays.asList(SpriteLayerType.getList()));
			comboBox.getSelectionModel().select(currentMapSet.getLayer(layer).getSpriteLayerType());
			comboBox.valueProperty().addListener((o, oldV, newV) -> currentMapSet.getLayer(layer).setSpriteLayerType(newV));
			hBox.setSpacing(5);
			hBox.setAlignment(Pos.CENTER_LEFT);
			hBox.getChildren().add(comboBox);
			hBox.setOnMouseClicked(event -> setLayer(layer));
			listViewLayers.getItems().add(hBox);
		}
		listViewLayers.getSelectionModel().select(Integer.valueOf(currentLayerIndex));
		canvasTileSet.setWidth(currentMapSet.getTileSetImage().getWidth());
		canvasTileSet.setHeight(currentMapSet.getTileSetImage().getHeight());
		resetBricks = System.currentTimeMillis() + 1500;
		canvasMouseDraw.movedX = 0;
		canvasMouseDraw.movedY = 0;
		selection = null;
		params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, canvasDraw.getWidth(), canvasDraw.getHeight()));
		currentLayerIndex = resetCurrentLayerIndex ? 26 : currentLayerIndex;
		ctrlZPos = -1;
		backupTiles.clear();
		comboBoxTileSets.getSelectionModel().select(currentMapSet.getTileSetName());
		setSampleTiles();
		saveCtrlZ();
	}
	
	void setSampleTiles() {
		tilePosition = new Position[] {currentMapSet.getWallTile(), currentMapSet.getGroundTile(), currentMapSet.getGroundWithWallShadow(), currentMapSet.getGroundWithBrickShadow(), currentMapSet.getFragileGround()};
		bricks = new Brick[] {new Brick(currentMapSet), new Brick(currentMapSet), new Brick(currentMapSet), new Brick(currentMapSet)};
		bricks[0].setFrameSet("BrickStandFrameSet");
		bricks[1].setFrameSet("BrickBreakFrameSet");
		bricks[2].setFrameSet("BrickRegenFrameSet");
		fragileTiles.clear();
		sampleFragileTile = null;
		if (currentMapSet.getFragileGround() != null) {
			int x1 = (int)currentMapSet.getGroundTile().getX(), y1 = (int)currentMapSet.getGroundTile().getY();
			int x2 = (int)currentMapSet.getFragileGround().getX(), y2 = (int)currentMapSet.getFragileGround().getY();
			String fragileGroundFrameSet = "{SetSprSource;/tileset/" + currentMapSet.getTileSetName() + ";" + x1 + ";" + y1 + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;30},{SetSprIndex;0}|{SetSprSource;/tileset/" + currentMapSet.getTileSetName() + ";" + x2 + ";" + y2 + ";16;16;0;0;0;0;16;16},{SetSprIndex;0}|{SetSprIndex;1}|{Goto;0}";
			sampleFragileTile = new Entity();
			sampleFragileTile.addNewFrameSetFromString("FragileGroundFrameSet", fragileGroundFrameSet);
			sampleFragileTile.setFrameSet("FragileGroundFrameSet");
		}
		for (Tile tile : currentMapSet.getLayer(26).getTileList())
			if (tile.tileProp.contains(TileProp.FRAGILE_GROUND_LV1)) {
				Entity fragileTile = new Entity(sampleFragileTile);
				fragileTiles.put(tile.getTileCoord(), fragileTile);
				fragileTile.setFrameSet("FragileGroundFrameSet");
				fragileTile.setPosition(tile.getTileX() * Main.tileSize, tile.getTileY() * Main.tileSize);
			}
	}
	
	void setKeyboardEvents() {
		sceneMain.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.PAGE_UP || e.getCode() == KeyCode.PAGE_DOWN) {
				if (isNoHolds())
					incLayerIndex(e.getCode() == KeyCode.PAGE_UP ? -1 : 1);
				else if (isCtrlHold()) {
					int i = comboBoxMapList.getSelectionModel().getSelectedIndex() + (e.getCode() == KeyCode.PAGE_UP ? 1 : -1);
					if (i == -1)
						i = comboBoxMapList.getItems().size() - 1;
					else if (i == comboBoxMapList.getItems().size())
						i = 0;
					comboBoxMapList.getSelectionModel().select(i);
				}
			}
			else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.S || e.getCode() == KeyCode.D || e.getCode() == KeyCode.W) {
				pathFinderTarget.incByDirection(e.getCode() == KeyCode.W ? Direction.UP : e.getCode() == KeyCode.S ? Direction.DOWN : e.getCode() == KeyCode.A ? Direction.LEFT : Direction.RIGHT);
				pathFinder.recalculatePath(canvasMouseDraw.tileCoord, pathFinderTarget, pathFinderDirection);
			}
			else if (e.getCode() == KeyCode.DELETE) {
				if (selection == null) {
					if (isShiftHold())
						removeTile(canvasMouseDraw.tileCoord, true);
					else
						removeFirstTileSprite(canvasMouseDraw.tileCoord, true);
				}
				else {
					if (isShiftHold())
						iterateAllSelectedCoords(coord -> removeTile(coord, false));
					else
						iterateAllSelectedCoords(coord -> removeFirstTileSprite(coord, false));
					rebuildCurrentLayer();
				}
			}
			else if (isCtrlHold()) {
				if (e.getCode() == KeyCode.Z)
					ctrlZ();
				else if (e.getCode() == KeyCode.Y)
					ctrlY();
			}
		});
		sceneMain.setOnKeyReleased(e -> {
			holdedKeys.remove(e.getCode());
		});
	}
	
	void decLayerIndex()
		{ incLayerIndex(-1); }
	
	void incLayerIndex()
		{ incLayerIndex(1); }

	void incLayerIndex(int inc) {
		currentLayerIndex += inc;
		while (!currentMapSet.getLayersMap().containsKey(currentLayerIndex)) {
			if (inc == -1)
				currentLayerIndex--;
			else
				currentLayerIndex++;
			if (currentLayerIndex > 10000)
				currentLayerIndex = -10000;
			else if (currentLayerIndex < -10000)
				currentLayerIndex = 10000;
		}
		listViewLayers.getSelectionModel().select(Integer.valueOf(currentLayerIndex));
	}
	
	void rebuildTileSetCanvas() {
		vBoxTileSet.getChildren().remove(1);
		Image i = currentMapSet.getTileSetImage();
		canvasTileSet = new Canvas(i.getWidth() * zoomTileSet, i.getHeight() * zoomTileSet);
		gcTileSet = canvasTileSet.getGraphicsContext2D();
		gcTileSet.setImageSmoothing(false);
		ScrollPane scrollPane = new ScrollPane(canvasTileSet);
		int w = 300, h = 200;
		scrollPane.setMinSize(w, h);
		scrollPane.setPrefSize(w, h);
		scrollPane.setMaxSize(w, h);
		vBoxTileSet.getChildren().add(scrollPane);
		setTileSetCanvasMouseEvents();
	}

	void setTileSetCanvasMouseEvents() {
		canvasTileSet.setOnMouseDragged(e -> {
			canvasMouseTileSet.tileCoord.setCoord((int)e.getX() / (16 * zoomTileSet), (int)e.getY() / (16 * zoomTileSet));
			if (e.getButton() == MouseButton.PRIMARY) {
				tileSelection.setFrameFromDiagonal(canvasMouseTileSet.startDragDX < canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX(),
																					 canvasMouseTileSet.startDragDY < canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY(),
																					(canvasMouseTileSet.startDragDX > canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX()) + 1,
																					(canvasMouseTileSet.startDragDY > canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY()) + 1);
			}
		});

		canvasTileSet.setOnMouseMoved(e -> {
			canvasMouseTileSet.x = (int)e.getX();
			canvasMouseTileSet.y = (int)e.getY();
			canvasMouseTileSet.tileCoord.setCoord((int)e.getX() / (16 * zoomTileSet), (int)e.getY() / (16 * zoomTileSet));
		});

		canvasTileSet.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY)
				tileSelection = new Rectangle();
			canvasMouseTileSet.startDragDX = canvasMouseTileSet.getCoordX();
			canvasMouseTileSet.startDragDY = canvasMouseTileSet.getCoordY();
			tileSelection.setFrameFromDiagonal(canvasMouseTileSet.startDragDX < canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX(),
					 canvasMouseTileSet.startDragDY < canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY(),
					(canvasMouseTileSet.startDragDX > canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX()) + 1,
					(canvasMouseTileSet.startDragDY > canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY()) + 1);
		});
	}
	
	void setMainCanvasMouseEvents() {
		canvasMain.setOnScroll(e -> {
			int inc = (isShiftHold() ? e.getDeltaX() : e.getDeltaY()) < 0 ? -1 : 1;
			if (zoomMain + inc <= 10 && zoomMain + inc >= 1)
				zoomMain += inc;
		});
		canvasMain.setOnMouseDragged(e -> {
			canvasMouseDraw.x = (int)e.getX() + deslocX();
			canvasMouseDraw.y = (int)e.getY() + deslocY();
			TileCoord prevCoord = canvasMouseDraw.tileCoord.getNewInstance();
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.tileSize * zoomMain), ((int)e.getY() - deslocY()) / (Main.tileSize * zoomMain));
			if (!isAltHold()) {
				if (e.getButton() == MouseButton.PRIMARY) {
					if (selection == null && !prevCoord.equals(canvasMouseDraw.tileCoord))
						addSelectedTileOnCurrentCursorPosition();
					if (isShiftHold()) {
						if (selection == null)
							selection = new Rectangle();
						selection.setFrameFromDiagonal(canvasMouseDraw.startDragDX < canvasMouseDraw.getCoordX() ? canvasMouseDraw.startDragDX : canvasMouseDraw.getCoordX(),
																					 canvasMouseDraw.startDragDY < canvasMouseDraw.getCoordY() ? canvasMouseDraw.startDragDY : canvasMouseDraw.getCoordY(),
																					 (canvasMouseDraw.startDragDX > canvasMouseDraw.getCoordX() ? canvasMouseDraw.startDragDX : canvasMouseDraw.getCoordX()) + 1,
																					 (canvasMouseDraw.startDragDY > canvasMouseDraw.getCoordY() ? canvasMouseDraw.startDragDY : canvasMouseDraw.getCoordY()) + 1);
					}
				}
			}
			else {
				canvasMouseDraw.dragX = ((int)e.getX() - canvasMouseDraw.startDragX);
				canvasMouseDraw.dragY = ((int)e.getY() - canvasMouseDraw.startDragY);
			}
		});

		canvasMain.setOnMouseMoved(e -> {
			canvasMouseDraw.x = (int)e.getX() + deslocX();
			canvasMouseDraw.y = (int)e.getY() + deslocY();
			TileCoord oldPos = canvasMouseDraw.tileCoord.getNewInstance(); 
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.tileSize * zoomMain), ((int)e.getY() - deslocY()) / (Main.tileSize * zoomMain));
			if (pathFinder != null && !oldPos.equals(canvasMouseDraw.tileCoord)) {
				Direction dir = oldPos.getX() == canvasMouseDraw.tileCoord.getX() ?
											 (oldPos.getY() < canvasMouseDraw.tileCoord.getY() ? Direction.DOWN : Direction.UP) :
												oldPos.getX() < canvasMouseDraw.tileCoord.getX() ? Direction.RIGHT : Direction.LEFT;
				pathFinder.recalculatePath(canvasMouseDraw.tileCoord, pathFinderTarget, dir);
			}
		});
		canvasMain.setOnMousePressed(e -> {
			canvasMouseDraw.startDragX = (int)e.getX();
			canvasMouseDraw.startDragY = (int)e.getY();
			canvasMouseDraw.startDragDX = canvasMouseDraw.getCoordX();
			canvasMouseDraw.startDragDY = canvasMouseDraw.getCoordY();
			if (e.getButton() == MouseButton.PRIMARY && !isAltHold()) {
					selection = null;
				if (isNoHolds())
					addSelectedTileOnCurrentCursorPosition();
			}
		});
		canvasMain.setOnMouseReleased(e -> {
			canvasMouseDraw.movedX += canvasMouseDraw.dragX;
			canvasMouseDraw.movedY += canvasMouseDraw.dragY;
			canvasMouseDraw.dragX = 0;
			canvasMouseDraw.dragY = 0;
			if (e.getButton() == MouseButton.PRIMARY)
				saveCtrlZ();
		});
		canvasMain.setOnMouseClicked(e -> {
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.tileSize * zoomMain), ((int)e.getY() - deslocY()) / (Main.tileSize * zoomMain));
			if (isCtrlHold()) {
				if (e.getButton() == MouseButton.PRIMARY) {
					pathFinderTarget = canvasMouseDraw.tileCoord.getNewInstance();
					setNewPathFinder();
				}
			}
			else if (e.getButton() == MouseButton.SECONDARY) {
				if (defaultContextMenu != null)
					defaultContextMenu.hide();
				setContextMenu();
				defaultContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
			}
		});
	}
	
	private void setNewPathFinder()
		{ pathFinder = new PathFinder(canvasMouseDraw.tileCoord, pathFinderTarget, pathFinderDirection, pathFinderIgnoreBackDir, pathFinderDistance, pathFinderFunc); }

	void addSelectedTileOnCurrentCursorPosition() {
		for (int y = 0; y < tileSelection.getHeight(); y++)
			for (int x = 0; x < tileSelection.getWidth(); x++) {
				Tile tile = new Tile(currentMapSet, (int)tileSelection.getMinX() * 16 + x * 16, (int)tileSelection.getMinY() * 16 + y * 16, canvasMouseDraw.getCoordX() * 16 + x * 16, canvasMouseDraw.getCoordY() * 16 + y * 16, new ArrayList<>());
				getCurrentLayer().addTile(tile);
			}
		rebuildCurrentLayer(false);
	}

	Layer getCurrentLayer()
		{ return currentMapSet.getLayer(currentLayerIndex); }

	Map<TileCoord, List<Tile>> getTileMapFromCurrentLayer()
		{ return getCurrentLayer().getTilesMap(); }
	
	List<Tile> getTilesFromCurrentLayer()
		{ return getCurrentLayer().getTileList(); }

	List<Tile> getTilesFromCoord(TileCoord coord)
		{ return getCurrentLayer().getTilesFromCoord(coord); }

	Tile getFirstTileFromCoord(TileCoord coord)
		{ return getCurrentLayer().getFirstTileFromCoord(coord); }

	void drawDrawCanvas() {
		gcDraw.setFill(Color.DIMGRAY);
		gcDraw.fillRect(0, 0, canvasDraw.getWidth(), canvasDraw.getHeight());
		gcDraw.setFill(Color.BLACK);
		gcDraw.fillRect(0, 0, getCurrentLayer().getLayerImage().getWidth(), getCurrentLayer().getLayerImage().getHeight());
		if (playing)
			currentMapSet.run(gcs);
		else if (currentMapSet.getLayersMap().containsKey(currentLayerIndex))
			gcDraw.drawImage(getCurrentLayer().getLayerImage(), 0, 0);
		if (checkBoxShowBricks.isSelected() && currentLayerIndex == 26) {
			Brick.drawBricks(gcDraw);
			if (checkBoxShowItens.isSelected() && GameMisc.blink(200))
				for (Brick brick : Brick.getBricks())
					if (brick.getItem() != null)
						gcDraw.drawImage(Materials.mainSprites, (brick.getItem().getValue() - 1) * 16, 16, 16, 16, brick.getTileX() * Main.tileSize, brick.getTileY() * Main.tileSize, Main.tileSize, Main.tileSize);
		}
		fragileTiles.values().forEach(e -> e.run(gcDraw));
		for (int y = 0; y < tileSelection.getHeight(); y++)
			for (int x = 0; x < tileSelection.getWidth(); x++)
				gcDraw.drawImage(currentMapSet.getTileSetImage(), (int)tileSelection.getMinX() * 16 + x * 16, (int)tileSelection.getMinY() * 16 + y * 16, 16, 16, canvasMouseDraw.getCoordX() * 16 + x * 16, canvasMouseDraw.getCoordY() * 16 + y * 16, Main.tileSize, Main.tileSize);
    drawBlockTypeMark();
    pathFinderTest();
	}
	
	private void pathFinderTest() {
		if (pathFinder != null) {
	    gcDraw.save();
	    gcDraw.setLineWidth(2);
	    if (pathFinder.getCurrentPath() != null) { // Mostra o pontilhado do caminho encontrado (Apertar F e R para alterar o caminho visualizado)
		    List<Pair<TileCoord, Direction>> dirs = pathFinder.getCurrentPath();
	      TileCoord coord = canvasMouseDraw.tileCoord.getNewInstance();
		    for (Pair<TileCoord, Direction> dir : dirs) {
		    	gcDraw.setFill(Color.GREEN);
		    	gcDraw.fillRect(coord.getX() * 16 + 6, coord.getY() * 16 + 6, 4 , 4);
		    	coord.incByDirection(dir.getValue());
		    	gcDraw.setFill(Color.GREEN);
		    	gcDraw.fillRect(coord.getX() * 16 + 6, coord.getY() * 16 + 6, 4 , 4);
		    }
	    }
    	gcDraw.setStroke(GameMisc.blink() ? Color.YELLOW : Color.LIGHTGREEN);
    	gcDraw.strokeRect(pathFinderTarget.getX() * 16, pathFinderTarget.getY() * 16, 16 , 16);
	    gcDraw.restore();
		}
	}

	int deslocX()
		{ return canvasMouseDraw.movedX + canvasMouseDraw.dragX; }

	int deslocY()
		{ return canvasMouseDraw.movedY + canvasMouseDraw.dragY; }

	void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
		gcMain.setFill(Color.DIMGRAY);
		gcMain.fillRect(0, 0, canvasMain.getWidth(), canvasMain.getHeight());
    gcMain.drawImage(canvasDraw.snapshot(params, null), 0, 0, canvasDraw.getWidth(), canvasDraw.getHeight(), deslocX(), deslocY(), canvasDraw.getWidth() * zoomMain, canvasDraw.getHeight() * zoomMain);
    drawGridAndAim();
    drawTileTagsOverCursor();
    drawTileSetCanvas();
    for (Rectangle rect : Arrays.asList(selection, tileSelection))
			if (rect != null) {
				GraphicsContext gc = rect == selection ? gcMain : gcTileSet;
		  	gc.setStroke(GameMisc.blink() ? Color.GREEN : Color.YELLOW);
		  	int z = rect == selection ? zoomMain : zoomTileSet, 
		  			x = (int)rect.getMinX() * Main.tileSize * z,
		  			y = (int)rect.getMinY() * Main.tileSize * z,
		  			w = (int)rect.getWidth() * Main.tileSize * z,
		  			h = (int)rect.getHeight() * Main.tileSize * z;
				gc.strokeRect(x + deslocX(), y + deslocY(), w, h);
			}
		if (checkBoxShowBlockType.isSelected() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
	    Tile tile = getCurrentLayer().getFirstTileFromCoord(canvasMouseDraw.tileCoord);
	    if (tile != null) {
	    	int x = tile.outX * zoomMain + deslocX(),
	    			y = tile.outY * zoomMain + (Main.tileSize * zoomMain) / 2 - 20 + deslocY();
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				for (TileProp prop : tile.tileProp) {
					String s = prop.name();
					Text text = new Text(s);
					GameMisc.setNodeFont(text, "Lucida Console", 15);
					while (x + (int)text.getBoundsInLocal().getWidth() + 20 >= canvasMain.getWidth())
						x -= 20;
					gcMain.strokeText(s, x, y += 20);
					gcMain.fillText(s, x, y);
				}
	    }
    }
		gcMain.save();
		gcMain.setFont(new Font("Lucida Console", 30));
    gcMain.restore();
 	}
	
	void drawBrickSample(Canvas canvas, Entity entity) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 48, 48);
		if (entity != null) {
			entity.run(gc, false);
			gc.drawImage(canvas.snapshot(null, null), 0, 0, 16, 16, 0, 0, 48, 48);
			if (System.currentTimeMillis() > resetBricks && entity.getCurrentFrameSet() != null && entity.getCurrentFrameSet().getCurrentFrameIndex() == entity.getCurrentFrameSet().getTotalFrames())
				entity.getCurrentFrameSet().setCurrentFrameIndex(0);
		}
	}

	void drawBrickSample(Canvas canvas, Position tilePosition) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 48, 48);
		if (tilePosition != null)
			gc.drawImage(currentMapSet.getTileSetImage(), tilePosition.getX(), tilePosition.getY(), 16, 16, 0, 0, 48, 48);
	}
	
	void drawTileSetCanvas() {
		for (int n = 0; n < 3; n++)
			drawBrickSample(canvasList[n], bricks[n]);
		for (int n = 3; n < 7; n++)
			drawBrickSample(canvasList[n], tilePosition[n - 3]);
		drawBrickSample(canvasList[7], sampleFragileTile);
		if (System.currentTimeMillis() >= resetBricks)
			resetBricks += 1500;
		gcTileSet.setFill(Color.BLACK);
		gcTileSet.fillRect(0, 0, canvasTileSet.getWidth(), canvasTileSet.getHeight());
		Image i = currentMapSet.getTileSetImage();
		//gcTileSet.drawImage(i, 0, 0);
		gcTileSet.drawImage(i, 0, 0, i.getWidth(), i.getHeight(), 0, 0, i.getWidth() * zoomTileSet, i.getHeight() * zoomTileSet);
	}

	void drawTileTagsOverCursor() {
		if (!checkBoxShowBlockType.isSelected()) {
			boolean blink = System.currentTimeMillis() / 50 % 2 == 0;
			if (Tile.getStringTag(currentLayerIndex, canvasMouseDraw.getCoordX(), canvasMouseDraw.getCoordY()) != null) {
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				String str[] = Tile.getStringTag(currentLayerIndex, canvasMouseDraw.getCoordX(), canvasMouseDraw.getCoordY()).split(" ");
				int x = canvasMouseDraw.x + 30 - deslocX(), y = canvasMouseDraw.y - 10 - deslocY(), yy = str.length * 20;
				while (y + yy >= canvasMain.getWidth() - 30)
					y -= 20;
				yy = 0;
				for (String s : str) {
					Text text = new Text(s);
					GameMisc.setNodeFont(text, "Lucida Console", 15);
					while (x + (int)text.getBoundsInLocal().getWidth() + 20 >= canvasMain.getWidth())
						x -= 10;
				}
				for (String s : str) {
					gcMain.strokeText(s, x, y + (yy += 20));
					gcMain.fillText(s, x, y + yy);
				}
				gcMain.setStroke(Color.GREENYELLOW);
				gcMain.setLineWidth(2);
				gcMain.strokeRect(canvasMouseDraw.getCoordX() * zoomMain * Main.tileSize + deslocX(), canvasMouseDraw.getCoordY() * zoomMain * Main.tileSize + deslocY(), 16 * zoomMain, 16 * zoomMain);
			}
			for (int y = 0; blink && y < 200; y++)
				for (int x = 0; x < 200; x++)
					if (Tile.getStringTag(currentLayerIndex, x, y) != null) {
						gcMain.setStroke(Color.WHITESMOKE);
						gcMain.setLineWidth(2);
						gcMain.strokeRect(x * zoomMain * Main.tileSize + deslocX(), y * zoomMain * Main.tileSize + deslocY(), 16 * zoomMain, 16 * zoomMain);
					}
		}
	}
	
	void setContextMenu() {
		defaultContextMenu = new ContextMenu();
		if (selection != null) {
			Menu menuSelecao = new Menu("Seleção");
			MenuItem menuItemRemoveTiles = new MenuItem("Remover tile(s) selecionado(s)");
			menuItemRemoveTiles.setOnAction(e -> removeAllSelectedTiles());
			menuSelecao.getItems().addAll(menuItemRemoveTiles);
			defaultContextMenu.getItems().add(menuSelecao);
		}
	}
	
	void copySelectedTiles(boolean copyOnlyFirstSprite) {
		copiedTiles.clear();
		iterateAllSelectedCoords(coord -> copiedTiles.put(coord, getCurrentLayer().getTilesFromCoord(coord)));
	}
	
	void copySelectedTiles()
		{ copySelectedTiles(false); }
	
	void replaceTile(TileCoord coord, Tile newTile)
		{ replaceTile(coord, Arrays.asList(newTile)); }

	void replaceTile(TileCoord coord, List<Tile> newTiles) {
		removeTile(coord);
		addTile(coord, newTiles);
	}
	
	void addTile(TileCoord coord, Tile newTile)
		{ addTile(coord, Arrays.asList(newTile)); }

	void addTile(TileCoord coord, List<Tile> newTiles) {
		for (Tile tile : newTiles)
			getCurrentLayer().addTile(tile);
		rebuildCurrentLayer();
	}

	void removeTile(TileCoord coord)
		{ removeTile(coord, true); }
	
	void removeTile(TileCoord coord, boolean rebuild) {
		if (Brick.haveBrickAt(coord))
			Brick.removeBrick(coord);
		removeTileMaster(coord, false);
		if (rebuild)
			rebuildCurrentLayer();
	}

	void removeFirstTileSprite(TileCoord coord)
		{ removeFirstTileSprite(coord, true); }
	
	void removeFirstTileSprite(TileCoord coord, boolean rebuild) {
		removeTileMaster(coord, true);
		if (rebuild)
			rebuildCurrentLayer();
	}

	void removeTileMaster(TileCoord coord, boolean removeOnlyTopSprite) {
		if (Brick.haveBrickAt(coord))
			Brick.removeBrick(coord);
		else if (getCurrentLayer().haveTilesOnCoord(coord)) {
			if (getFirstTileFromCoord(coord).tileProp.contains(TileProp.FRAGILE_GROUND_LV1) && fragileTiles.containsKey(coord))
				fragileTiles.remove(coord);
			if (!removeOnlyTopSprite)
				getCurrentLayer().removeAllTilesFromCoord(coord);
			else
				getCurrentLayer().removeFirstTileFromCoord(coord);
		}
	}
	
	void removeAllSelectedTiles() {
		iterateAllSelectedCoords(coord -> removeTile(coord, false));
		rebuildCurrentLayer();
	}

	void removeFirstSpriteFromSelectedTiles() {
		iterateAllSelectedCoords(coord -> removeTile(coord, true));
		rebuildCurrentLayer();
	}

	void rebuildCurrentLayer()
		{ rebuildCurrentLayer(true); }
	
	void rebuildCurrentLayer(boolean saveCtrlz) {
		getCurrentLayer().buildLayer();
		if (saveCtrlz)
			saveCtrlZ();
	}
	
	void drawBlockTypeMark() {
		Map<TileCoord, Boolean> ok = new HashMap<>();
		if (checkBoxShowBlockType.isSelected()) {
    	for (Tile tile : getTilesFromCurrentLayer()) {
    		Color color;
    		if (!ok.containsKey(tile.getTileCoord())) {
	    		if (tile.tileProp.contains(TileProp.PLAYER_INITIAL_POSITION))
	    			color = Color.DEEPPINK;
	    		else if (tile.tileProp.contains(TileProp.MOB_INITIAL_POSITION))
	    			color = Color.INDIANRED;
	    		else if (tile.tileProp.contains(TileProp.REDIRECT_BOMB_TO_DOWN) ||
	    						 tile.tileProp.contains(TileProp.REDIRECT_BOMB_TO_RIGHT) ||
	    						 tile.tileProp.contains(TileProp.REDIRECT_BOMB_TO_UP) ||
	    						 tile.tileProp.contains(TileProp.REDIRECT_BOMB_TO_LEFT))
	    							 color = Color.MEDIUMPURPLE;
	    		else if (tile.tileProp.contains(TileProp.RAIL_DL) ||
	    						 tile.tileProp.contains(TileProp.RAIL_DR) ||
	    						 tile.tileProp.contains(TileProp.RAIL_UL) ||
	    						 tile.tileProp.contains(TileProp.RAIL_UR) ||
	    						 tile.tileProp.contains(TileProp.RAIL_H) ||
	    						 tile.tileProp.contains(TileProp.RAIL_V) ||
	    						 tile.tileProp.contains(TileProp.RAIL_JUMP) ||
	    						 tile.tileProp.contains(TileProp.RAIL_START) ||
	    						 tile.tileProp.contains(TileProp.RAIL_END) ||
	    						 tile.tileProp.contains(TileProp.TREADMILL_TO_LEFT) ||
	    						 tile.tileProp.contains(TileProp.TREADMILL_TO_UP) ||
	    						 tile.tileProp.contains(TileProp.TREADMILL_TO_RIGHT) ||
	    						 tile.tileProp.contains(TileProp.TREADMILL_TO_DOWN))
	    							 color = Color.SADDLEBROWN;
	    		else if (tile.tileProp.contains(TileProp.GROUND_NO_MOB) ||
	    						 tile.tileProp.contains(TileProp.GROUND_NO_PLAYER) ||
	    						 tile.tileProp.contains(TileProp.GROUND_NO_BOMB) ||
	    						 tile.tileProp.contains(TileProp.GROUND_NO_FIRE))
	    							 color = Color.LIGHTGOLDENRODYELLOW;
	    		else if (tile.tileProp.contains(TileProp.FRAGILE_GROUND_LV1) ||
	    						 tile.tileProp.contains(TileProp.FRAGILE_GROUND_LV2))
	    							 color = Color.LIGHTPINK;
	    		else if (tile.tileProp.contains(TileProp.TRIGGER_BY_BLOCK) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_BOMB) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_EXPLOSION) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_ITEM) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_MOB) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_PLAYER) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_RIDE) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_UNRIDE_PLAYER) ||
	    						 tile.tileProp.contains(TileProp.TRIGGER_BY_STOPPED_BOMB) ||
	    						 tile.tileProp.contains(TileProp.NO_TRIGGER_WHILE_HAVE_BOMB) ||
	    						 tile.tileProp.contains(TileProp.NO_TRIGGER_WHILE_HAVE_BRICK) ||
	    						 tile.tileProp.contains(TileProp.NO_TRIGGER_WHILE_HAVE_ITEM) ||
	    						 tile.tileProp.contains(TileProp.NO_TRIGGER_WHILE_HAVE_MOB) ||
	    						 tile.tileProp.contains(TileProp.NO_TRIGGER_WHILE_HAVE_PLAYER))
	    							 color = Color.DARKORANGE;
	    		else if (tile.tileProp.contains(TileProp.BRICK_RANDOM_SPAWNER))
	    			color = Color.LIGHTGREEN;
	    		else if (tile.tileProp.contains(TileProp.MAGNET_D) ||
	    						 tile.tileProp.contains(TileProp.MAGNET_R) ||
	    						 tile.tileProp.contains(TileProp.MAGNET_U) ||
	    						 tile.tileProp.contains(TileProp.MAGNET_L))
	    							 color = Color.LIGHTSLATEGRAY;
	    		else if (tile.tileProp.contains(TileProp.FIXED_BRICK))
	    			color = Color.GREEN;
	    		else if (tile.tileProp.contains(TileProp.MOVING_BLOCK))
	    			color = Color.PALEVIOLETRED;
	    		else if (tile.tileProp.contains(TileProp.GROUND_HOLE))
	    			color = Color.ALICEBLUE;
	    		else if (tile.tileProp.contains(TileProp.DEEP_HOLE))
	    			color = Color.GRAY;
	    		else if (tile.tileProp.contains(TileProp.JUMP_OVER))
	    			color = Color.CORAL;
	    		else if (tile.tileProp.contains(TileProp.MAP_EDGE))
	    			color = Color.SADDLEBROWN;
	    		else if (tile.tileProp.contains(TileProp.WATER))
	    			color = Color.LIGHTBLUE;
	    		else if (tile.tileProp.contains(TileProp.DEEP_WATER))
	    			color = Color.DARKBLUE;
	    		else if (tile.tileProp.contains(TileProp.TELEPORT_FROM_FLOATING_PLATFORM))
	    			color = Color.ROSYBROWN;
	    		else if (tile.tileProp.contains(TileProp.STAGE_CLEAR))
	    			color = Color.AQUA;
	    		else if (tile.tileProp.contains(TileProp.GROUND))
	    			color = Color.YELLOW;
	    		else if (tile.tileProp.contains(TileProp.WALL) ||
	    						 tile.tileProp.contains(TileProp.HIGH_WALL))
	    							 color = Color.RED;
	    		else
	    			color = Color.ORANGE;
	    		gcDraw.save();
	    		gcDraw.setFill(color);
	    		gcDraw.setLineWidth(1);
	    		gcDraw.setGlobalAlpha(0.6);
		    	gcDraw.fillRect(tile.getTileX() * Main.tileSize,
		    									tile.getTileY() * Main.tileSize,
		    									Main.tileSize, Main.tileSize);
	    		gcDraw.restore();
	    		ok.put(tile.getTileCoord(), true);
    		}
    	}
		}
	}
	
	void iterateAllSelectedCoords(Consumer<TileCoord> consumer) {
		if (selection != null)
			for (int y = 0; y < selection.getHeight(); y++)
				for (int x = 0; x < selection.getWidth(); x++)
					consumer.accept(new TileCoord((int)selection.getMinX() + x, (int)selection.getMinY() + y));
	}
	
	void drawGridAndAim() {
		gcMain.setLineWidth(1);
    if (checkBoxShowGrid.isSelected()) {
    	gcMain.setStroke(Color.RED);
    	for (int y = 0; y < canvasMain.getHeight(); y += Main.tileSize * zoomMain)
    		gcMain.strokeRect(0, y, canvasMain.getWidth(), y + Main.tileSize * zoomMain - 1);
    	for (int x = 0; x < canvasMain.getWidth(); x += Main.tileSize * zoomMain)
    		gcMain.strokeRect(x, 0, x + Main.tileSize * zoomMain - 1, canvasMain.getHeight());
    }
    if (checkBoxShowAim.isSelected()) {
    	gcMain.setStroke(Color.YELLOW);
    	int x = canvasMouseDraw.getCoordX() * Main.tileSize * zoomMain, y = canvasMouseDraw.getCoordY() * Main.tileSize * zoomMain;
  		gcMain.strokeRect(x, 0, Main.tileSize * zoomMain - 1, canvasMain.getHeight());
  		gcMain.strokeRect(0, y, canvasMain.getWidth(), Main.tileSize * zoomMain - 1);
    	gcMain.setStroke(Color.LIGHTBLUE);
  		gcMain.strokeRect(x, y, Main.tileSize * zoomMain - 1, Main.tileSize * zoomMain - 1);
    }
	}

	boolean isHold(int shift, int ctrl, int alt) {
		return ((shift == 0 && !isShiftHold()) || (shift == 1 && isShiftHold())) &&
					 ((ctrl == 0 && !isCtrlHold()) || (ctrl == 1 && isCtrlHold())) &&
					 ((alt == 0 && !isAltHold()) || (alt == 1 && isAltHold()));
	}
	
	boolean isCtrlHold()
		{ return holdedKeys.contains(KeyCode.CONTROL); }
	
	boolean isShiftHold()
		{ return holdedKeys.contains(KeyCode.SHIFT); }

	boolean isAltHold()
		{ return holdedKeys.contains(KeyCode.ALT); }
	
	boolean isNoHolds()
		{ return !isAltHold() && !isCtrlHold() && !isShiftHold(); }
	
}

class CanvasMouse {
	
	TileCoord tileCoord;
	int x;
	int y;
	int dragX;
	int dragY;
	int movedX;
	int movedY;
	int startDragX;
	int startDragY;
	int startDragDX;
	int startDragDY;
	
	CanvasMouse() {
		tileCoord = new TileCoord();
		x = 0;
		y = 0;
		dragX = 0;
		dragY = 0;
		movedX = 0;
		movedY = 0;
		startDragX = 0;
		startDragY = 0;
		startDragDX = 0;
		startDragDY = 0;
	}
	
	int getCoordX()
		{ return tileCoord.getX(); }
	
	int getCoordY()
		{ return tileCoord.getY(); }

}
