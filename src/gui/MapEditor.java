package gui;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import application.Main;
import entities.Bomb;
import entities.Effect;
import entities.Entity;
import entities.Explosion;
import entities.TileCoord;
import enums.BombType;
import enums.Icons;
import enums.ImageFlip;
import enums.SpriteLayerType;
import enums.TileProp;
import gui.util.ControllerUtils;
import gui.util.ImageUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import maps.Brick;
import maps.Item;
import maps.Layer;
import maps.MapSet;
import maps.Tile;
import objmoveutils.Position;
import tools.IniFiles;
import tools.Materials;
import tools.Tools;
import util.FindFile;
import util.Misc;

public class MapEditor {

	@FXML
	private HBox hBoxTileSet;
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
  private Button buttonSetFrameSetRollingTile;
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
  private Canvas canvasRollingTile;
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
  @FXML
  private ComboBox<TileProp> comboBoxTileType;
  @FXML
  private ComboBox<Integer> comboBoxTileRotate;
  @FXML
  private ComboBox<ImageFlip> comboBoxTileFlip;
  @FXML
  private Slider sliderTileOpacity;
  @FXML
  private ColorPicker colorPickerTileTint;
  @FXML
  private CheckBox checkBoxTintSprite;

	private Rectangle selection;
	private Rectangle tileSelection;
	private GraphicsContext gcMain;
	private GraphicsContext gcTileSet;
	private GraphicsContext gcTileSel;
	private Brick[] bricks;
	private Position[] tilePosition;
	private Canvas[] canvasList;
	private Canvas canvasTileSel;
	private List<KeyCode> holdedKeys;
	private Map<TileCoord, Entity> fragileTiles;
	private Entity sampleFragileTile;
	private Map<TileCoord, List<Tile>> copiedTiles;
	private List<Map<TileCoord, List<Tile>>> backupTiles;
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
	private String defaultMap = "SBM2_1-1";
	
	public void init() {
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
		canvasTileSel = null;
		gcTileSel = null;
		canvasMain.setWidth(320 * zoomMain - 16 * zoomMain * 3);
		canvasMain.setHeight(240 * zoomMain - 16 * zoomMain);
		Tools.loadTools();
		redefineTileSelectionCanvas();
		setAllCanvas();
		defineControls();
		setKeyboardEvents();
		setMainCanvasMouseEvents();
		rebuildTileSetCanvas();
		mainLoop();
  }
	
	void setAllCanvas() {
		canvasList = new Canvas[] {canvasBrickStand, canvasBrickBreaking, canvasBrickRegen, canvasWallSprite, canvasGroundSprite, canvasGroundWithWallShadow, canvasGroundWithBrickShadow, canvasFragileGround, canvasRollingTile};
		for (Canvas canvas : canvasList)
			canvas.getGraphicsContext2D().setImageSmoothing(false);
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
		buttonReloadFromDisk.setOnAction(e -> reloadCurrentMap());
		buttonSaveToDisk.setOnAction(e -> saveCurrentMap());
		checkBoxShowBricks.setSelected(true);
		checkBoxShowItens.setSelected(true);
		checkBoxShowBricks.setOnAction(e -> {
			if (!checkBoxShowBricks.isSelected())
				Brick.clearBricks();
			else
				MapSet.setBricks();
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
			MapSet.setGroundTile(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			setSampleTiles();
		});
		buttonSetFrameSetGroundWithWallShadow.setOnAction(e -> {
			MapSet.setGroundWithWallShadow(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetGroundWithBrickShadow.setOnAction(e -> {
			MapSet.setGroundWithBrickShadow(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetWallSprite.setOnAction(e -> {
			MapSet.setWallTile(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			setSampleTiles();
		});
		buttonSetFrameSetFragileGround.setOnAction(e -> {
			MapSet.setFragileGround(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			setSampleTiles();
		});
		List<File> list = FindFile.findFile("./appdata/tileset/", "*.tiles");
		list.forEach(file -> comboBoxTileSets.getItems().add(file.getName().replace(".tiles", "")));
		comboBoxTileSets.valueProperty().addListener((obs, oldV, newV) -> {
			MapSet.setTileSet(newV);
			MapSet.setBricks();
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		for (TileProp type : TileProp.getList())
			comboBoxTileType.getItems().add(type);
		comboBoxTileType.getSelectionModel().select(0);
		comboBoxMapList.getItems().addAll(IniFiles.stages.getSectionList());
		comboBoxMapList.valueProperty().addListener((obs, oldV, newV) -> {
			loadMap(newV);
		});
		comboBoxMapList.getSelectionModel().select(0);
		for (ImageFlip flip : ImageFlip.values())
			comboBoxTileFlip.getItems().add(flip);
		comboBoxTileFlip.getSelectionModel().select(0);
		for (int n = 0; n < 360; n += 90)
			comboBoxTileRotate.getItems().add(n);
		comboBoxTileRotate.getSelectionModel().select(0);
		ControllerUtils.setNodeFont(buttonPlay, "Lucida Console", 14);
		buttonPlay.setText("►");
		buttonPlay.setOnAction(e -> {
			playing = !playing;
			buttonPlay.setText(playing ? "■" : "►");
			if (playing)
				MapSet.resetMapFrameSets();
		});
		canvasBrickStand.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickStandFrameSet")));
		canvasBrickBreaking.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickBreakFrameSet")));
		canvasBrickRegen.setOnMouseClicked(e -> {
			Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickRegenFrameSet"));
		});
		colorPickerTileTint.setDisable(true);
		checkBoxTintSprite.selectedProperty().addListener((o, pastState, nowState) ->
			colorPickerTileTint.setDisable(!nowState));
	}

	void mainLoop() {
		try {
			drawDrawCanvas();
			drawMainCanvas();
			Tools.getFPSHandler().fpsCounter();
			if (!Main.close )
				Platform.runLater(() -> {
					String title = "Map Editor"
							+ "     FPS: " + Tools.getFPSHandler().getFPS()
							+ "     " + canvasMouseDraw.tileCoord + "     (" + (MapSet.tileIsFree(canvasMouseDraw.tileCoord) ? "FREE" : "BLOCKED") + ")"
							+ "     Zoom: x" + zoomMain
							+ "     Tileset Zoom: x" + zoomTileSet
							+ "     Sobrecarga: " + Tools.getFPSHandler().getFreeTaskTicks()
							+ "     A: " + (MapSet.getLayer(26).getTilesFromCoord(canvasMouseDraw.tileCoord) == null ? 0 : MapSet.getLayer(26).getTilesFromCoord(canvasMouseDraw.tileCoord).size())
							
							;
					Main.stageMain.setTitle(title);
					mainLoop();
				});
		}
		catch (Exception e) {
			e.printStackTrace();
			Main.close();
		}
	}
	
	void saveCtrlZ() {
		if (Misc.alwaysTrue())
			return;
		System.out.println("saveCtrlZ " + System.currentTimeMillis());
		backupTiles.add(new HashMap<>(getTileMapFromCurrentLayer()));
		ctrlZPos = backupTiles.size() - 1;
	}
	
	void ctrlZ() {
		if (Misc.alwaysTrue())
			return;
		if (!backupTiles.isEmpty()) {
			if (--ctrlZPos == -1)
				ctrlZPos = backupTiles.size() - 1;
			getCurrentLayer().setTilesMap(backupTiles.get(ctrlZPos));
			rebuildCurrentLayer(false);
			MapSet.setBricks();
		}
	}
	
	void ctrlY() {
		if (Misc.alwaysTrue())
			return;
		if (!backupTiles.isEmpty()) {
			if (++ctrlZPos == backupTiles.size())
				ctrlZPos = 0;
			getCurrentLayer().setTilesMap(backupTiles.get(ctrlZPos));
			rebuildCurrentLayer(false);
			MapSet.setBricks();
		}
	}
	
	public int getCurrentLayerIndex()
		{ return currentLayerIndex; }
	
	void reloadCurrentMap()
		{ loadMap(MapSet.getMapName()); }
	
	void loadMap(String mapName)
		{ loadMap(mapName, true); }
	
	void setLayer(Integer newValue)
		{ currentLayerIndex = newValue; }

	void loadMap(String mapName, boolean resetCurrentLayerIndex) {
		if (mapName == null)
			throw new RuntimeException("Unable to load map because 'mapName' is null");
		if (defaultMap != null) {
			mapName = defaultMap;
			defaultMap = null;
		}
		Tile.tags = new HashMap<>();
		MapSet.loadMap(mapName);
		MapSet.setBricksRegenTime(5);
		setLayersListView();
		canvasTileSet.setWidth(MapSet.getTileSetImage().getWidth());
		canvasTileSet.setHeight(MapSet.getTileSetImage().getHeight());
		resetBricks = System.currentTimeMillis() + 1500;
		canvasMouseDraw.movedX = 0;
		canvasMouseDraw.movedY = 0;
		selection = null;
		currentLayerIndex = resetCurrentLayerIndex ? 26 : currentLayerIndex;
		ctrlZPos = -1;
		backupTiles.clear();
		comboBoxTileSets.getSelectionModel().select(MapSet.getTileSetName());
		setSampleTiles();
		saveCtrlZ();
	}

	private void setLayersListView() {
		listViewLayers.getItems().clear();
		List<Integer> list = new ArrayList<Integer>(MapSet.getLayersMap().keySet());
		list.sort((n1, n2) -> n2 - n1);
		list.forEach(layer -> {
			HBox hBox = new HBox(new Text("" + layer));
			HBox hBox2 = new HBox(new Text(MapSet.getLayer(layer).getWidth() + " x " + MapSet.getLayer(layer).getHeight()));
			ComboBox<SpriteLayerType> comboBox = new ComboBox<>();
			ControllerUtils.setListToComboBox(comboBox, Arrays.asList(SpriteLayerType.getList()));
			comboBox.getSelectionModel().select(MapSet.getLayer(layer).getSpriteLayerType());
			comboBox.valueProperty().addListener((o, oldV, newV) -> MapSet.getLayer(layer).setSpriteLayerType(newV));
			hBox.setSpacing(5);
			hBox.setAlignment(Pos.CENTER_LEFT);
			hBox2.setAlignment(Pos.CENTER);
			hBox2.setPrefWidth(70);
			hBox.getChildren().add(comboBox);
			hBox.getChildren().add(hBox2);
			hBox.setOnMouseClicked(event -> setLayer(layer));
			listViewLayers.getItems().add(hBox);
		});
		listViewLayers.getSelectionModel().select(Integer.valueOf(currentLayerIndex));
	}

	Canvas getDrawCanvas()
		{ return Tools.getTempCanvas(); }
	
	GraphicsContext getDrawGc()
		{ return Tools.getTempGc(); }

	void setSampleTiles() {
		tilePosition = new Position[] {MapSet.getWallTile(), MapSet.getGroundTile(), MapSet.getGroundWithWallShadow(), MapSet.getGroundWithBrickShadow(), MapSet.getFragileGround()};
		bricks = new Brick[] {new Brick(), new Brick(), new Brick(), new Brick()};
		bricks[0].setFrameSet("BrickStandFrameSet");
		bricks[1].setFrameSet("BrickBreakFrameSet");
		bricks[2].setFrameSet("BrickRegenFrameSet");
		bricks[3].setFrameSet("BrickRollingFrameSet");
		fragileTiles.clear();
		sampleFragileTile = null;
		if (MapSet.getFragileGround() != null) {
			int x1 = (int)MapSet.getGroundTile().getX(), y1 = (int)MapSet.getGroundTile().getY();
			int x2 = (int)MapSet.getFragileGround().getX(), y2 = (int)MapSet.getFragileGround().getY();
			String fragileGroundFrameSet = "{SetSprSource;/tileset/" + MapSet.getTileSetName() + ";" + x1 + ";" + y1 + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;30},{SetSprIndex;0}|{SetSprSource;/tileset/" + MapSet.getTileSetName() + ";" + x2 + ";" + y2 + ";16;16;0;0;0;0;16;16},{SetSprIndex;0}|{SetSprIndex;1}|{Goto;0}";
			sampleFragileTile = new Entity();
			sampleFragileTile.addNewFrameSetFromString("FragileGroundFrameSet", fragileGroundFrameSet);
			sampleFragileTile.setFrameSet("FragileGroundFrameSet");
		}
		MapSet.getLayer(26).getTileList().forEach(tile -> {
			if (tile.tileProp.contains(TileProp.FRAGILE_GROUND_LV1)) {
				Entity fragileTile = new Entity(sampleFragileTile);
				fragileTiles.put(tile.getTileCoord(), fragileTile);
				fragileTile.setFrameSet("FragileGroundFrameSet");
				fragileTile.setPosition(tile.getTileX() * Main.TILE_SIZE, tile.getTileY() * Main.TILE_SIZE);
			}
		});
	}
	
	void setKeyboardEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
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
		Main.sceneMain.setOnKeyReleased(e -> {
			holdedKeys.remove(e.getCode());
		});
	}
	
	void decLayerIndex()
		{ incLayerIndex(-1); }
	
	void incLayerIndex()
		{ incLayerIndex(1); }

	void incLayerIndex(int inc) {
		currentLayerIndex += inc;
		while (!MapSet.getLayersMap().containsKey(currentLayerIndex)) {
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
		hBoxTileSet.getChildren().clear();
		Image i = MapSet.getTileSetImage();
		canvasTileSet = new Canvas(i.getWidth() * zoomTileSet, i.getHeight() * zoomTileSet);
		gcTileSet = canvasTileSet.getGraphicsContext2D();
		gcTileSet.setImageSmoothing(false);
		ScrollPane scrollPane = new ScrollPane(canvasTileSet);
		int w = 300, h = 200;
		scrollPane.setMinSize(w, h);
		scrollPane.setPrefSize(w, h);
		scrollPane.setMaxSize(w, h);
		hBoxTileSet.getChildren().add(scrollPane);
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
				redefineTileSelectionCanvas();
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
			redefineTileSelectionCanvas();
		});
	}
	
	private void redefineTileSelectionCanvas() {
		int w = (int)tileSelection.getWidth() * 16, h = (int)tileSelection.getHeight() * 16;
		if (canvasTileSel == null || (int)canvasTileSel.getWidth() != w || (int)canvasTileSel.getHeight() != h) {
			canvasTileSel = new Canvas(w, h);
			gcTileSel = canvasTileSel.getGraphicsContext2D();
			gcTileSel.setImageSmoothing(false);
		}
	}

	void setMainCanvasMouseEvents() {
		canvasMain.setOnScroll(e -> {
			int inc = (isShiftHold() ? e.getDeltaX() : e.getDeltaY()) < 0 ? -1 : 1;
			if (isNoHolds() && (zoomMain + inc <= 10 && zoomMain + inc >= 1))
				zoomMain += inc;
		});
		canvasMain.setOnMouseDragged(e -> {
			canvasMouseDraw.x = (int)e.getX() + deslocX();
			canvasMouseDraw.y = (int)e.getY() + deslocY();
			TileCoord prevCoord = canvasMouseDraw.tileCoord.getNewInstance();
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.TILE_SIZE * zoomMain), ((int)e.getY() - deslocY()) / (Main.TILE_SIZE * zoomMain));
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
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.TILE_SIZE * zoomMain), ((int)e.getY() - deslocY()) / (Main.TILE_SIZE * zoomMain));
			if (Item.haveItemAt(canvasMouseDraw.tileCoord))
				Item.getItemAt(canvasMouseDraw.tileCoord).pick();
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
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.TILE_SIZE * zoomMain), ((int)e.getY() - deslocY()) / (Main.TILE_SIZE * zoomMain));
			if (e.getButton() == MouseButton.PRIMARY) {
				if (isAltHold() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord))
					Bomb.addBomb(new Bomb(null, canvasMouseDraw.tileCoord, BombType.NORMAL, 5));
			}
			else if (e.getButton() == MouseButton.SECONDARY) {
				if (defaultContextMenu != null)
					defaultContextMenu.hide();
				setContextMenu();
				defaultContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
			}
		});
	}
	
	void addSelectedTileOnCurrentCursorPosition() {
		for (int y = 0; y < tileSelection.getHeight(); y++)
			for (int x = 0; x < tileSelection.getWidth(); x++) {
				Tile tile = new Tile((int)tileSelection.getMinX() * 16 + x * 16,
						(int)tileSelection.getMinY() * 16 + y * 16,
						canvasMouseDraw.getCoordX() * 16 + x * 16,
						canvasMouseDraw.getCoordY() * 16 + y * 16,
						new ArrayList<>(Arrays.asList(comboBoxTileType.getSelectionModel().getSelectedItem())),
						comboBoxTileFlip.getSelectionModel().getSelectedItem(),
						comboBoxTileRotate.getSelectionModel().getSelectedItem(),
						sliderTileOpacity.getValue(),
						!checkBoxTintSprite.isSelected() ? Color.TRANSPARENT : colorPickerTileTint.getValue());
				getCurrentLayer().addTile(tile);
			}
		int w = getCurrentLayer().getWidth(), h = getCurrentLayer().getHeight();
		rebuildCurrentLayer(false);
		if (w != getCurrentLayer().getWidth() || h != getCurrentLayer().getHeight())
			setLayersListView();
	}

	Layer getCurrentLayer()
		{ return MapSet.getLayer(currentLayerIndex); }

	Map<TileCoord, List<Tile>> getTileMapFromCurrentLayer()
		{ return getCurrentLayer().getTilesMap(); }
	
	List<Tile> getTilesFromCurrentLayer()
		{ return getCurrentLayer().getTileList(); }

	List<Tile> getTilesFromCoord(TileCoord coord)
		{ return getCurrentLayer().getTilesFromCoord(coord); }

	Tile getTopTileFromCoord(TileCoord coord)
		{ return getCurrentLayer().getTopTileFromCoord(coord); }

	void drawDrawCanvas() {
		getDrawGc().setFill(Color.DIMGRAY);
		getDrawGc().fillRect(0, 0, 1000, 1000);
		getDrawGc().setFill(Color.BLACK);
		if (playing) {
			getDrawGc().fillRect(0, 0, MapSet.getLayer(26).getWidth(), MapSet.getLayer(26).getHeight());
			MapSet.run();
		}
		else if (MapSet.getLayersMap().containsKey(currentLayerIndex)) {
			getDrawGc().fillRect(0, 0, getCurrentLayer().getWidth(), getCurrentLayer().getHeight());
			Tools.addDrawImageQueue(SpriteLayerType.GROUND, getCurrentLayer().getLayerImage(), 0, 0);
		}
		Explosion.drawExplosions();
		if (checkBoxShowBricks.isSelected() && currentLayerIndex == 26) {
			Brick.drawBricks();
			if (checkBoxShowItens.isSelected() && Misc.blink(200))
				for (Brick brick : Brick.getBricks())
					if (brick.getItem() != null)
						Tools.addDrawImageQueue(SpriteLayerType.GROUND, Materials.mainSprites, (brick.getItem().getValue() - 1) * 16, 16, 16, 16, brick.getTileX() * Main.TILE_SIZE, brick.getTileY() * Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE);
		}
		Bomb.drawBombs();
		Item.drawItems();
		Effect.drawEffects();
		fragileTiles.values().forEach(e -> e.run());
		gcTileSel.clearRect(0, 0, canvasTileSel.getWidth(), canvasTileSel.getHeight());
		for (int y = 0; y < tileSelection.getHeight(); y++)
			for (int x = 0; x < tileSelection.getWidth(); x++)
				ImageUtils.drawImage(gcTileSel, MapSet.getTileSetImage(), (int)tileSelection.getMinX() * 16 + x * 16, (int)tileSelection.getMinY() * 16 + y * 16, 16, 16, x * 16, y * 16, Main.TILE_SIZE, Main.TILE_SIZE);
		if (checkBoxTintSprite.isSelected()) {
			Color color = colorPickerTileTint.getValue();
			gcTileSel.setFill(color);
			gcTileSel.fillRect(0, 0, (int)tileSelection.getWidth() * Main.TILE_SIZE, (int)tileSelection.getHeight() * Main.TILE_SIZE);
		}
		Tools.addDrawImageQueue(SpriteLayerType.GROUND, Tools.getCanvasSnapshot(canvasTileSel), canvasMouseDraw.getCoordX() * Main.TILE_SIZE, canvasMouseDraw.getCoordY() * Main.TILE_SIZE, comboBoxTileFlip.getSelectionModel().getSelectedItem(), comboBoxTileRotate.getSelectionModel().getSelectedItem(), sliderTileOpacity.getValue(), null);
	}
	
	int deslocX()
		{ return canvasMouseDraw.movedX + canvasMouseDraw.dragX; }

	int deslocY()
		{ return canvasMouseDraw.movedY + canvasMouseDraw.dragY; }

	void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
    Tools.applyAllDraws(canvasMain, Color.DIMGRAY, zoomMain, deslocX(), deslocY());
    drawBlockTypeMark();
    drawGridAndAim();
    drawTileTagsOverCursor();
    drawTileSetCanvas();
    Arrays.asList(selection, tileSelection).forEach(rect -> {
			if (rect != null) {
				GraphicsContext gc = rect == selection ? gcMain : gcTileSet;
		  	gc.setStroke(Misc.blink(50) ? Color.GREEN : Color.YELLOW);
		  	int z = rect == selection ? zoomMain : zoomTileSet, 
		  			x = (int)rect.getMinX() * Main.TILE_SIZE * z,
		  			y = (int)rect.getMinY() * Main.TILE_SIZE * z,
		  			w = (int)rect.getWidth() * Main.TILE_SIZE * z,
		  			h = (int)rect.getHeight() * Main.TILE_SIZE * z;
				gc.strokeRect(x + deslocX(), y + deslocY(), w, h);
			}
    });
		if (checkBoxShowBlockType.isSelected() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
	    Tile tile = getCurrentLayer().getTopTileFromCoord(canvasMouseDraw.tileCoord);
	    if (tile != null) {
	    	int x = tile.outX * zoomMain + deslocX(),
	    			y = tile.outY * zoomMain + (Main.TILE_SIZE * zoomMain) / 2 - 20 + deslocY();
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				for (TileProp prop : tile.tileProp) {
					String s = prop.name();
					Text text = new Text(s);
					ControllerUtils.setNodeFont(text, "Lucida Console", 15);
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
			gc.drawImage(MapSet.getTileSetImage(), tilePosition.getX(), tilePosition.getY(), 16, 16, 0, 0, 48, 48);
	}
	
	void drawTileSetCanvas() {
		for (int n = 0; n < 3; n++)
			drawBrickSample(canvasList[n], bricks[n]);
		for (int n = 3; n < 7; n++)
			drawBrickSample(canvasList[n], tilePosition[n - 3]);
		drawBrickSample(canvasFragileGround, sampleFragileTile);
		drawBrickSample(canvasRollingTile, sampleFragileTile);
		drawBrickSample(canvasList[8], bricks[3]);
		if (System.currentTimeMillis() >= resetBricks)
			resetBricks += 1500;
		gcTileSet.setFill(Color.BLACK);
		gcTileSet.fillRect(0, 0, canvasTileSet.getWidth(), canvasTileSet.getHeight());
		Image i = MapSet.getTileSetImage();
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
					ControllerUtils.setNodeFont(text, "Lucida Console", 15);
					while (x + (int)text.getBoundsInLocal().getWidth() + 20 >= canvasMain.getWidth())
						x -= 10;
				}
				for (String s : str) {
					gcMain.strokeText(s, x, y + (yy += 20));
					gcMain.fillText(s, x, y + yy);
				}
				gcMain.setStroke(Color.GREENYELLOW);
				gcMain.setLineWidth(2);
				gcMain.strokeRect(canvasMouseDraw.getCoordX() * zoomMain * Main.TILE_SIZE + deslocX(), canvasMouseDraw.getCoordY() * zoomMain * Main.TILE_SIZE + deslocY(), 16 * zoomMain, 16 * zoomMain);
			}
			for (int y = 0; blink && y < 200; y++)
				for (int x = 0; x < 200; x++)
					if (Tile.getStringTag(currentLayerIndex, x, y) != null) {
						gcMain.setStroke(Color.WHITESMOKE);
						gcMain.setLineWidth(2);
						gcMain.strokeRect(x * zoomMain * Main.TILE_SIZE + deslocX(), y * zoomMain * Main.TILE_SIZE + deslocY(), 16 * zoomMain, 16 * zoomMain);
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
			if (getTopTileFromCoord(coord).tileProp.contains(TileProp.FRAGILE_GROUND_LV1) && fragileTiles.containsKey(coord))
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
	
	void saveCurrentMap() {
		MapSet.getMapIniFile().clearSection("TILES");
		int n = 0;
		for (Layer layer : MapSet.getLayersMap().values())
			for (Tile tile : layer.getTileList()) {
				String props = "";
				for (TileProp prop : tile.tileProp) {
					if (!props.isEmpty())
						props += "!";
					props += prop.getValue();
				}
				int[] rgba = tile.tint == null ? new int[] {0, 0, 0, 0} : ImageUtils.getRgbaArray(ImageUtils.colorToArgb(tile.tint));
				String s = layer.getLayer() + " " + layer.getSpriteLayerType() + " " + (tile.outX / 16) + " " + (tile.outY / 16) + " " + tile.spriteX + " " + tile.spriteY + " " + tile.flip.getValue() + " " + tile.rotate / 90 + " " + props + " " + tile.opacity + " " + rgba[0] + " " + rgba[1] + " " + rgba[2] + " " + rgba[3] + " " + Tools.SpriteEffectsToString(tile.effects) + " " + (tile.oldTags == null ? "" : tile.oldTags);
				MapSet.getMapIniFile().write("TILES", "" + n++, s);
			}
		System.out.println(MapSet.getMapIniFile().fileName());
	}
	
	void drawBlockTypeMark() {
		Set<TileCoord> ok = new HashSet<>();
		if (checkBoxShowBlockType.isSelected()) {
			getTilesFromCurrentLayer().forEach(tile -> {
    		Color color;
    		if (!ok.contains(tile.getTileCoord())) {
	    		if (tile.tileProp.contains(TileProp.EXPLOSION))
	    			color = Color.INDIANRED;
	    		else if (tile.tileProp.contains(TileProp.PLAYER_INITIAL_POSITION))
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
	    		gcMain.save();
	    		gcMain.setFill(color);
	    		gcMain.setLineWidth(1);
	    		gcMain.setGlobalAlpha(0.6);
		    	gcMain.fillRect(tile.getTileX() * Main.TILE_SIZE * zoomMain,
		    									tile.getTileY() * Main.TILE_SIZE * zoomMain,
		    									Main.TILE_SIZE * zoomMain, Main.TILE_SIZE * zoomMain);
	    		gcMain.restore();
	    		ok.add(tile.getTileCoord());
    		}
    	});
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
    	for (int y = 0; y < canvasMain.getHeight(); y += Main.TILE_SIZE * zoomMain)
    		gcMain.strokeRect(0, y, canvasMain.getWidth(), y + Main.TILE_SIZE * zoomMain - 1);
    	for (int x = 0; x < canvasMain.getWidth(); x += Main.TILE_SIZE * zoomMain)
    		gcMain.strokeRect(x, 0, x + Main.TILE_SIZE * zoomMain - 1, canvasMain.getHeight());
    }
    if (checkBoxShowAim.isSelected()) {
    	gcMain.setStroke(Color.YELLOW);
    	int x = canvasMouseDraw.getCoordX() * Main.TILE_SIZE * zoomMain, y = canvasMouseDraw.getCoordY() * Main.TILE_SIZE * zoomMain;
  		gcMain.strokeRect(x, 0, Main.TILE_SIZE * zoomMain - 1, canvasMain.getHeight());
  		gcMain.strokeRect(0, y, canvasMain.getWidth(), Main.TILE_SIZE * zoomMain - 1);
    	gcMain.setStroke(Color.LIGHTBLUE);
  		gcMain.strokeRect(x, y, Main.TILE_SIZE * zoomMain - 1, Main.TILE_SIZE * zoomMain - 1);
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