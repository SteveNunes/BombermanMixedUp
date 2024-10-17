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
import enums.Direction;
import enums.Icons;
import enums.ImageFlip;
import enums.SpriteLayerType;
import enums.StageClearCriteria;
import enums.TileProp;
import gui.util.Alerts;
import gui.util.ControllerUtils;
import gui.util.ListenerHandle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
  private Button buttonSetFrameSetBrickRolling;
  @FXML
  private Button buttonTileSetZoom1;
  @FXML
  private Button buttonTileSetZoom2;
  @FXML
  private Button buttonAddFrameSet;
  @FXML
  private Button buttonRenameFrameSet;
  @FXML
  private Button buttonEditFrameSet;
  @FXML
  private Button buttonRemoveFrameSet;
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
  private Text textTileSetCoord;
  @FXML
  private VBox vBoxLayerList;
  @FXML
  private HBox hBoxCheckBoxes;
  @FXML
  private HBox hBoxFrameSetButtons;
  @FXML
  private VBox vBoxTileSet;

	private ListenerHandle<String> listenerHandleComboBoxMapFrameSets;
	private ContextMenu contextMenu;
	private ContextMenu contextMenuTileSet;
	private Rectangle selection;
	private Rectangle tileSelection;
	private GraphicsContext gcMain;
	private GraphicsContext gcTileSet;
	private Brick[] bricks;
	private Position[] tilePosition;
	private Canvas[] canvasList;
	private Tile[][] tileSelectionArray;
	private List<KeyCode> holdedKeys;
	private List<TileProp> copyProps;
	private Map<TileCoord, Entity> fragileTiles;
	private Entity sampleFragileTile;
	private Map<TileCoord, List<Tile>> copiedTiles;
	private List<Map<TileCoord, List<Tile>>> backupTiles;
	private Font font;
	private CanvasMouse canvasMouseDraw;
	private CanvasMouse canvasMouseTileSet;
	int zoomMain;
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
		MapSet.setCurrentLayerIndex(26);
		ctrlZPos = -1;
		playing = false;
		copyProps = null;
		selection = null;
		canvasMain.setWidth(320 * zoomMain - 16 * zoomMain * 3);
		canvasMain.setHeight(240 * zoomMain - 16 * zoomMain);
		listenerHandleComboBoxMapFrameSets = new ListenerHandle<>(comboBoxMapFrameSets.valueProperty(), (o, oldValue, newValue) ->
			MapSet.mapFrameSets.setFrameSet(comboBoxMapFrameSets.getSelectionModel().getSelectedItem()));
		Tools.loadTools();
		setAllCanvas();
		defineControls();
		setKeyboardEvents();
		setMainCanvasMouseEvents();
		rebuildTileSetCanvas();
		updateTileSelectionArray();
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
		ControllerUtils.addIconToButton(buttonAddMap, Icons.NEW_FILE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRenameMap, Icons.EDIT.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRemoveMap, Icons.DELETE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonReloadFromDisk, Icons.REFRESH.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonSaveToDisk, Icons.SAVE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonAddLayer, Icons.NEW_FILE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonTileSetZoom1, Icons.ZOOM_PLUS.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonTileSetZoom2, Icons.ZOOM_MINUS.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonAddFrameSet, Icons.NEW_FILE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRenameFrameSet, Icons.EDIT.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonEditFrameSet, Icons.EDIT.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRemoveFrameSet, Icons.DELETE.getValue(), 16, 16, Color.WHITE, 150);
		buttonAddMap.setTooltip(new Tooltip("Adicionar mapa"));
		buttonRenameMap.setTooltip(new Tooltip("Renomear mapa"));
		buttonRemoveMap.setTooltip(new Tooltip("Remover mapa"));
		buttonReloadFromDisk.setTooltip(new Tooltip("Recarregar mapa do disco"));
		buttonSaveToDisk.setTooltip(new Tooltip("Salvar para o disco"));
		buttonAddLayer.setTooltip(new Tooltip("Adicionar camada"));
		buttonTileSetZoom1.setTooltip(new Tooltip("Aumentar zoom do TileSet"));
		buttonTileSetZoom2.setTooltip(new Tooltip("Diminuir zoom do TileSet"));
		buttonAddFrameSet.setTooltip(new Tooltip("Adicionar FrameSet"));
		buttonRenameFrameSet.setTooltip(new Tooltip("Renomear FrameSet"));
		buttonEditFrameSet.setTooltip(new Tooltip("Editar FrameSet"));
		buttonRemoveFrameSet.setTooltip(new Tooltip("Excluir FrameSet"));
		buttonReloadFromDisk.setOnAction(e -> reloadCurrentMap());
		buttonSaveToDisk.setOnAction(e -> saveCurrentMap());
		checkBoxShowBricks.setSelected(true);
		checkBoxShowItens.setSelected(true);
		buttonAddLayer.setOnAction(e -> {
			String str = Alerts.textPrompt("Prompt", "Adicionar camada", "Digite o indice da camada á ser adicionada");
			if (str != null) {
				int layer;
				try
					{ layer = Integer.parseInt(str); }
				catch (Exception ex) {
					Alerts.error("Erro", "Valor inválido para o indice da nova camada");
					return;
				}
				if (MapSet.isValidLayer(layer)) {
					Alerts.error("Erro", "Já existe uma camada na lista com o valor informado");
					return;
				}
				MapSet.addLayer(layer);
				setLayersListView();
				Alerts.information("Info", "Camada adicionada com sucesso");
			}
		});
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
		List<String> frameSetNames = Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet", "BrickRollingFrameSet");
		List<Button> buttons = Arrays.asList(buttonSetFrameSetBrickStand, buttonSetFrameSetBrickBreaking, buttonSetFrameSetBrickRegen, buttonSetFrameSetBrickRolling);
		for (int n = 0; n < 3; n++) {
			final String frameSetName = frameSetNames.get(n);
			buttons.get(n).setOnAction(e -> {
				String str = Alerts.textPrompt("Prompt", "Editar FrameSet", MapSet.getTileSetIniFile().read("CONFIG", frameSetName), "Editar FrameSet '" + frameSetName + "'");
				if (str != null) {
					try {
						for (Brick brick : Brick.getBricks())
							brick.replaceFrameSetFromString(frameSetName, str);
					}
					catch (Exception ex) {
						Alerts.error("Erro", "O FrameSet informado é inválido ou contém erros");
						return;
					}
					MapSet.getTileSetIniFile().write("CONFIG", frameSetName, str);
					setSampleTiles();
				}
			});
		}
		buttonSetFrameSetGroundSprite.setOnAction(e -> {
			MapSet.setGroundTile(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			MapSet.rebuildAllLayers();
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
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetFragileGround.setOnAction(e -> {
			MapSet.setFragileGround(new Position(tileSelection.getMinX() * 16, tileSelection.getMinY() * 16));
			MapSet.rebuildAllLayers();
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
			vBoxLayerList.setDisable(playing);
			hBoxCheckBoxes.setDisable(playing);
			hBoxFrameSetButtons.setDisable(playing);
			vBoxTileSet.setDisable(playing);
		});
		canvasBrickStand.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickStandFrameSet")));
		canvasBrickBreaking.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickBreakFrameSet")));
		canvasBrickRegen.setOnMouseClicked(e -> {
			Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickRegenFrameSet"));
		});
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
							+ "     " + canvasMouseDraw.tileCoord
							+ "     " + canvasMouseDraw.tileCoord.positionString(Main.TILE_SIZE)
							+ "     (" + (MapSet.tileIsFree(canvasMouseDraw.tileCoord) ? "FREE" : "BLOCKED") + ")"
							+ "     Zoom: x" + zoomMain
							+ "     Tileset Zoom: x" + zoomTileSet
							+ "     Sobrecarga: " + Tools.getFPSHandler().getFreeTaskTicks();
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
		{ return MapSet.getCurrentLayerIndex(); }
	
	void reloadCurrentMap()
		{ loadMap(MapSet.getMapName()); }
	
	void loadMap(String mapName)
		{ loadMap(mapName, true); }
	
	void setLayer(Integer newValue)
		{ MapSet.setCurrentLayerIndex(newValue); }

	void loadMap(String mapName, boolean resetCurrentLayerIndex) {
		if (mapName == null)
			throw new RuntimeException("Unable to load map because 'mapName' is null");
		if (defaultMap != null) {
			mapName = defaultMap;
			defaultMap = null;
		}
		MapSet.loadMap(mapName);
		MapSet.setBricksRegenTime(5);
		setLayersListView();
		canvasTileSet.setWidth(MapSet.getTileSetImage().getWidth());
		canvasTileSet.setHeight(MapSet.getTileSetImage().getHeight());
		resetBricks = System.currentTimeMillis() + 1500;
		canvasMouseDraw.movedX = 0;
		canvasMouseDraw.movedY = 0;
		selection = null;
		MapSet.setCurrentLayerIndex(resetCurrentLayerIndex ? 26 : MapSet.getCurrentLayerIndex());
		ctrlZPos = -1;
		backupTiles.clear();
		comboBoxTileSets.getSelectionModel().select(MapSet.getTileSetName());
		updateTileSetText();
		listenerHandleComboBoxMapFrameSets.detach();
		comboBoxMapFrameSets.getItems().clear();
		for (String frameSet : MapSet.mapFrameSets.getFrameSetsNames())
			comboBoxMapFrameSets.getItems().add(frameSet);
		listenerHandleComboBoxMapFrameSets.attach();
		comboBoxMapFrameSets.getSelectionModel().select(0);
		setSampleTiles();
		saveCtrlZ();
	}

	private void setLayersListView() {
		listViewLayers.getItems().clear();
		listViewLayers.setFixedCellSize(26);
		List<Integer> list = new ArrayList<Integer>(MapSet.getLayersMap().keySet());
		list.sort((n1, n2) -> n2 - n1);
		list.forEach(layer -> {
			HBox textHBox = new HBox(new Text((layer == MapSet.getCopyImageLayerIndex() ? "📌  " : "") + layer + " "));
			textHBox.setPrefWidth(35);
			textHBox.setAlignment(Pos.CENTER_RIGHT);
			HBox hBox = new HBox(textHBox);
			HBox hBox2 = new HBox(new Text(MapSet.getLayer(layer).getWidth() + " x " + MapSet.getLayer(layer).getHeight()));
			ComboBox<SpriteLayerType> comboBox = new ComboBox<>();
			ControllerUtils.setListToComboBox(comboBox, Arrays.asList(SpriteLayerType.getList()));
			comboBox.getSelectionModel().select(MapSet.getLayer(layer).getSpriteLayerType());
			comboBox.valueProperty().addListener((o, oldV, newV) -> MapSet.getLayer(layer).setSpriteLayerType(newV));
			ControllerUtils.forceComboBoxSize(comboBox, 125, 23);
			hBox.setSpacing(5);
			hBox.setAlignment(Pos.CENTER_LEFT);
			hBox2.setAlignment(Pos.CENTER);
			hBox2.setPrefWidth(85);
			hBox.getChildren().add(comboBox);
			hBox.getChildren().add(hBox2);
			hBox.setOnMouseClicked(event -> setLayer(layer));
			if (layer != 26) {
				Button button = new Button();
				ControllerUtils.forceButtonSize(button, 19, 19);
				button.setTooltip(new Tooltip("Excluir camada"));
				button.setOnAction(e -> {
					if (Alerts.confirmation("Confirmation", "Deseja mesmo excluir a camada " + layer + "?")) {
						MapSet.removeLayer(layer);
						setLayersListView();
					}
				});
				ControllerUtils.addIconToButton(button, Icons.DELETE.getValue(), 12, 12, Color.WHITE, 50);
				hBox.getChildren().add(button);
			}
			if (MapSet.getCopyImageLayerIndex() != layer) {
				Button button = new Button();
				ControllerUtils.forceButtonSize(button, 19, 19);
				button.setTooltip(new Tooltip("Definir como 'CopyImageLayer'"));
				button.setOnAction(e -> {
					MapSet.setCopyImageLayerIndex(layer);
					setLayersListView();
				});
				ControllerUtils.addIconToButton(button, Icons.PIN.getValue(), 12, 12, Color.WHITE, 50);
				hBox.getChildren().add(button);
			}
			listViewLayers.getItems().add(hBox);
		});
		listViewLayers.getSelectionModel().select(Integer.valueOf(MapSet.getCurrentLayerIndex()));
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
		MapSet.getTileListFromCurrentLayer().forEach(tile -> {
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
			else if (e.getCode() == KeyCode.C) {
				if (selection == null) {
					
				}
				else if (isCtrlHold()) {
					if (isShiftHold())
						copySelectedTiles();
					else
						copySelectedTiles(true);
				}
			}
			else if (e.getCode() == KeyCode.V) {
				if (selection == null) {
					
				}
				else if (isCtrlHold()) {
					if (isShiftHold())
						pasteCopiedTiles(true);
					else
						pasteCopiedTiles();
				}
			}
			else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.S || e.getCode() == KeyCode.A || e.getCode() == KeyCode.D) {
				Direction dir = e.getCode() == KeyCode.W ? Direction.UP :
												e.getCode() == KeyCode.S ? Direction.DOWN :
												e.getCode() == KeyCode.A ? Direction.LEFT : Direction.RIGHT;
				if (isCtrlHold()) {
					if (selection == null) {
						Map<TileCoord, List<Tile>> tilesMap = new HashMap<>();
						for (TileCoord coord : getCurrentLayer().getTilesMap().keySet()) {
							List<Tile> tiles = getCurrentLayer().getTilesFromCoord(coord);
							TileCoord tileCoord = coord.getNewInstance();
							tileCoord.incByDirection(dir);
							for (Tile tile : tiles)
								tile.setCoord(tileCoord);
							tilesMap.put(new TileCoord(tileCoord), new ArrayList<>(tiles));
						}
						List<Brick> bricks = Brick.getBricks();
						bricks.forEach(brick -> Brick.removeBrick(brick));
						bricks.forEach(brick -> {
							brick.incPositionByDirection(dir, Main.TILE_SIZE);
							Brick.addBrick(brick);
						});
						MapSet.getCurrentLayer().setTilesMap(tilesMap);
					}
					else {
						Map<TileCoord, List<Tile>> tilesMap = new HashMap<>();
						List<Brick> bricks = new ArrayList<>();
						iterateAllSelectedCoords(coord -> {
							if (MapSet.getCurrentLayer().haveTilesOnCoord(coord)) {
								TileCoord tileCoord = coord.getNewInstance();
								tileCoord.incByDirection(dir);
								tilesMap.put(new TileCoord(tileCoord), getCurrentLayer().getTilesFromCoord(coord));
								if (Brick.haveBrickAt(coord)) {
									Brick brick = Brick.getBrickAt(coord);
									bricks.add(brick);
									brick.incPositionByDirection(dir, Main.TILE_SIZE);
									Brick.removeBrick(coord);
								}
								MapSet.getCurrentLayer().removeAllTilesFromCoord(coord);
							}
						});
						for (TileCoord coord : tilesMap.keySet()) {
							if (MapSet.getCurrentLayer().haveTilesOnCoord(coord))
								MapSet.getCurrentLayer().removeAllTilesFromCoord(coord);
							for (Tile tile : tilesMap.get(coord))
								MapSet.getCurrentLayer().addTile(tile, coord);
						}
						bricks.forEach(brick -> Brick.addBrick(brick));
						int x = (int)selection.getX(), y = (int)selection.getY(),
								w = (int)selection.getWidth(), h = (int)selection.getHeight();
						x += dir == Direction.LEFT ? -1 : dir == Direction.RIGHT ? 1 : 0;
						y += dir == Direction.UP ? -1 : dir == Direction.DOWN ? 1 : 0;
						selection.setBounds(x, y, w, h);
					}
					MapSet.getCurrentLayer().buildLayer();
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
		MapSet.setCurrentLayerIndex(MapSet.getCurrentLayerIndex() + inc);
		while (!MapSet.getLayersMap().containsKey(MapSet.getCurrentLayerIndex())) {
			if (inc == -1)
				MapSet.setCurrentLayerIndex(MapSet.getCurrentLayerIndex() - 1);
			else
				MapSet.setCurrentLayerIndex(MapSet.getCurrentLayerIndex() + 1);
			if (MapSet.getCurrentLayerIndex() > 10000)
				MapSet.setCurrentLayerIndex(-10000);
			else if (MapSet.getCurrentLayerIndex() < -10000)
				MapSet.setCurrentLayerIndex(10000);
		}
		listViewLayers.getSelectionModel().select(Integer.valueOf(MapSet.getCurrentLayerIndex()));
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
			if (e.getButton() == MouseButton.PRIMARY)
				tileSelection.setFrameFromDiagonal(canvasMouseTileSet.startDragDX < canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX(),
																					 canvasMouseTileSet.startDragDY < canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY(),
																					(canvasMouseTileSet.startDragDX > canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX()) + 1,
																					(canvasMouseTileSet.startDragDY > canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY()) + 1);
		});

		canvasTileSet.setOnMouseMoved(e -> {
			canvasMouseTileSet.x = (int)e.getX();
			canvasMouseTileSet.y = (int)e.getY();
			canvasMouseTileSet.tileCoord.setCoord((int)e.getX() / (16 * zoomTileSet), (int)e.getY() / (16 * zoomTileSet));
		});

		canvasTileSet.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				tileSelection = new Rectangle();
				canvasMouseTileSet.startDragDX = canvasMouseTileSet.getCoordX();
				canvasMouseTileSet.startDragDY = canvasMouseTileSet.getCoordY();
				tileSelection.setFrameFromDiagonal(canvasMouseTileSet.startDragDX < canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX(),
						 canvasMouseTileSet.startDragDY < canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY(),
						(canvasMouseTileSet.startDragDX > canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX()) + 1,
						(canvasMouseTileSet.startDragDY > canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY()) + 1);
				if (contextMenuTileSet != null && contextMenuTileSet.isShowing()) {
					contextMenuTileSet.hide();
					return;
				}
				updateTileSetText();
			}
			else if (e.getButton() == MouseButton.SECONDARY) {
				if (contextMenuTileSet != null && contextMenuTileSet.isShowing())
					contextMenuTileSet.hide();
				setContextMenuTileSet();
				contextMenuTileSet.show(canvasTileSet, e.getScreenX(), e.getScreenY());
			}
		});
	}
	
	private void updateTileSetText()
		{ textTileSetCoord.setText("Tile: " + canvasMouseTileSet.tileCoord.getX() + "," + canvasMouseTileSet.tileCoord.getY() + " (" + (int)tileSelection.getWidth() + " x " + (int)tileSelection.getHeight() + ")     Position: " + canvasMouseTileSet.tileCoord.getX() * Main.TILE_SIZE + "," + canvasMouseTileSet.tileCoord.getY() * Main.TILE_SIZE + " (" + (int)tileSelection.getWidth() * Main.TILE_SIZE + " x " + (int)tileSelection.getHeight() * Main.TILE_SIZE + ")");	}
	
	void setMainCanvasMouseEvents() {
		canvasMain.setOnScroll(e -> {
			int inc = (isShiftHold() ? e.getDeltaX() : e.getDeltaY()) < 0 ? -1 : 1;
			if (isNoHolds() && (zoomMain + inc <= 10 && zoomMain + inc >= 1))
				zoomMain += inc;
		});
		canvasMain.setOnMouseDragged(e -> {
			if (contextMenu != null && contextMenu.isShowing())
				return;
			canvasMouseDraw.x = (int)e.getX() + deslocX();
			canvasMouseDraw.y = (int)e.getY() + deslocY();
			TileCoord prevCoord = canvasMouseDraw.tileCoord.getNewInstance();
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.TILE_SIZE * zoomMain), ((int)e.getY() - deslocY()) / (Main.TILE_SIZE * zoomMain));
			if (!playing && e.getButton() == MouseButton.PRIMARY) {
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
			else if (e.getButton() == MouseButton.SECONDARY) {
				canvasMouseDraw.dragX = ((int)e.getX() - canvasMouseDraw.startDragX);
				canvasMouseDraw.dragY = ((int)e.getY() - canvasMouseDraw.startDragY);
			}
		});

		canvasMain.setOnMouseMoved(e -> {
			if (contextMenu != null && contextMenu.isShowing())
				return;
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
			if (contextMenu != null && contextMenu.isShowing()) {
				contextMenu.hide();
				return;
			}
			if (e.getButton() == MouseButton.PRIMARY && !isAltHold()) {
					selection = null;
				if (!playing && isNoHolds())
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
			else if (e.isDragDetect() && e.getButton() == MouseButton.SECONDARY) {
				if (contextMenu != null && contextMenu.isShowing())
					contextMenu.hide();
				setContextMenu();
				contextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
			}
		});
		canvasMain.setOnMouseClicked(e -> {
			canvasMouseDraw.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.TILE_SIZE * zoomMain), ((int)e.getY() - deslocY()) / (Main.TILE_SIZE * zoomMain));
			if (e.getButton() == MouseButton.PRIMARY) {
				if (playing && isAltHold() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord))
					Bomb.addBomb(new Bomb(null, canvasMouseDraw.tileCoord, BombType.NORMAL, 5));
			}
		});
	}
	
	void addSelectedTileOnCurrentCursorPosition()
		{ updateTileSelectionArray(true); }
	
	void updateTileSelectionArray()
		{ updateTileSelectionArray(false); }

	void updateTileSelectionArray(boolean fixTilesOnLayer) {
		ImageFlip flip = comboBoxTileFlip.getSelectionModel().getSelectedItem();
		int w, h, rotate = comboBoxTileRotate.getSelectionModel().getSelectedItem();
		if (rotate == 90 || rotate == 270) {
			h = (int)tileSelection.getWidth();
			w = (int)tileSelection.getHeight();
		}
		else {
			w = (int)tileSelection.getWidth();
			h = (int)tileSelection.getHeight();
		}
		tileSelectionArray = new Tile[h > w ? h : w][h > w ? h : w];
		for (int y = 0; y < tileSelection.getHeight(); y++)
			for (int x = 0; x < tileSelection.getWidth(); x++) {
				Tile tile = new Tile((int)tileSelection.getMinX() * 16 + x * 16,
						(int)tileSelection.getMinY() * 16 + y * 16,
						canvasMouseDraw.getCoordX() * 16 + x * 16,
						canvasMouseDraw.getCoordY() * 16 + y * 16,
						new ArrayList<>(Arrays.asList(comboBoxTileType.getSelectionModel().getSelectedItem())),
						flip, rotate, sliderTileOpacity.getValue());
				tileSelectionArray[y][x] = tile;
			}
		
		for (int y = 0; y < tileSelectionArray.length; y++)
			for (int x = 0; x < tileSelectionArray.length; x++) {
				int a, b;
				if (rotate == 0)
					{ a = y; b = x; }
				else if (rotate == 90)
					{ a = h - x - 1; b = y; }
				else if (rotate == 180)
					{ a = w - y - 1; b = w - x - 1; }
				else
					{ a = x; b = h - y - 1; }
				Tile tile = tileSelectionArray[a][b];
				if (tile != null) {
					boolean vf = tile.flip == ImageFlip.VERTICAL || tile.flip == ImageFlip.BOTH,
									hf = tile.flip == ImageFlip.HORIZONTAL || tile.flip == ImageFlip.BOTH;
					tile.setCoord(new TileCoord(canvasMouseDraw.getCoordX() + (hf ? w - 1 - x : x), canvasMouseDraw.getCoordY() + (vf ? h - 1 - y : y)));
					if (fixTilesOnLayer)
						getCurrentLayer().addTile(tile);
				}
			}
		w = getCurrentLayer().getWidth();
		h = getCurrentLayer().getHeight();
		rebuildCurrentLayer(false);
		if (w != getCurrentLayer().getWidth() || h != getCurrentLayer().getHeight())
			setLayersListView();
	}

	Layer getCurrentLayer()
		{ return MapSet.getCurrentLayer(); }

	Map<TileCoord, List<Tile>> getTileMapFromCurrentLayer()
		{ return getCurrentLayer().getTilesMap(); }
	
	boolean haveTilesOnCoord(TileCoord coord)
		{ return getCurrentLayer().haveTilesOnCoord(coord); }

	void drawDrawCanvas() {
		getDrawGc().setFill(Color.DIMGRAY);
		getDrawGc().fillRect(0, 0, 1000, 1000);
		getDrawGc().setFill(Color.BLACK);
		if (playing) {
			getDrawGc().fillRect(0, 0, MapSet.getLayer(26).getWidth(), MapSet.getLayer(26).getHeight());
			MapSet.run();
		}
		else if (MapSet.getLayersMap().containsKey(MapSet.getCurrentLayerIndex())) {
			getDrawGc().fillRect(0, 0, getCurrentLayer().getWidth(), getCurrentLayer().getHeight());
			Tools.addDrawImageQueue(SpriteLayerType.GROUND, getCurrentLayer().getLayerImage(), 0, 0);
		}
		Explosion.drawExplosions();
		if (checkBoxShowBricks.isSelected() && MapSet.getCurrentLayerIndex() == 26) {
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
		updateTileSelectionArray();
		if (!playing) {
			for (int y = 0; y < tileSelectionArray.length; y++)
				for (int x = 0; x < tileSelectionArray[0].length; x++) {
					Tile tile = tileSelectionArray[y][x];
					if (tile != null)
						Tools.addDrawImageQueue(SpriteLayerType.GROUND, MapSet.getTileSetImage(),
								tile.spriteX, tile.spriteY,
								Main.TILE_SIZE, Main.TILE_SIZE,
								tile.outX, tile.outY,
								Main.TILE_SIZE, Main.TILE_SIZE,
								tile.flip, tile.rotate, tile.opacity, null);
				}
		}
	}
	
	int deslocX()
		{ return canvasMouseDraw.movedX + canvasMouseDraw.dragX; }

	int deslocY()
		{ return canvasMouseDraw.movedY + canvasMouseDraw.dragY; }

	void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
    Tools.applyAllDraws(canvasMain, Color.DIMGRAY, zoomMain, deslocX(), deslocY());
    if (playing)
    	return;
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
	    Tile tile = MapSet.getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord);
	    if (tile != null) {
	    	int x, y = tile.outY * zoomMain + (Main.TILE_SIZE * zoomMain) / 2 - 20 + deslocY();
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				while (y + tile.tileProp.size() * 20 >= canvasMain.getHeight() - 10)
					y -= 10;
				for (TileProp prop : tile.tileProp) {
					String s = prop.name();
					Text text = new Text(s);
					ControllerUtils.setNodeFont(text, "Lucida Console", 15);
					x = tile.outX * zoomMain + deslocX();
					while (x + (int)text.getBoundsInLocal().getWidth() + 60 >= canvasMain.getWidth())
						x -= 20;
					gcMain.strokeText(s, x, y += 20);
					gcMain.fillText(s, x, y);
				}
	    }
    }
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
		if (!checkBoxShowBlockType.isSelected() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
			String tileTags = MapSet.getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord).getStringTags();
			if (tileTags != null) {
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				String str[] = tileTags.split(tileTags.charAt(0) == '{' ? "\\," : "\s");
				int x, y = canvasMouseDraw.y + 20 - deslocY(), yy = str.length * 20;
				while (y + yy >= canvasMain.getHeight() - 30)
					y -= 20;
				yy = 0;
				for (String s : str) {
					Text text = new Text(s);
					ControllerUtils.setNodeFont(text, "Lucida Console", 15);
					int w = (int)text.getBoundsInLocal().getWidth();
					x = canvasMouseDraw.x - w / 2 - deslocX();
					while (x + w >= canvasMain.getWidth() - 130)
						x -= 10;
					gcMain.strokeText(s, x, y + (yy += 20));
					gcMain.fillText(s, x, y + yy);
				}
				gcMain.setStroke(Color.GREENYELLOW);
				gcMain.setLineWidth(2);
				gcMain.strokeRect(canvasMouseDraw.getCoordX() * zoomMain * Main.TILE_SIZE + deslocX(), canvasMouseDraw.getCoordY() * zoomMain * Main.TILE_SIZE + deslocY(), 16 * zoomMain, 16 * zoomMain);
			}
		}
		for (int y = 0; Misc.blink(50) && y < 200; y++)
			for (int x = 0; x < 200; x++) {
				TileCoord coord = new TileCoord(x, y);
				if (haveTilesOnCoord(coord) && MapSet.getFirstBottomTileFromCoord(coord).getStringTags() != null) {
					gcMain.setStroke(Color.WHITESMOKE);
					gcMain.setLineWidth(2);
					gcMain.strokeRect(x * zoomMain * Main.TILE_SIZE + deslocX(), y * zoomMain * Main.TILE_SIZE + deslocY(), 16 * zoomMain, 16 * zoomMain);
				}
			}
	}
	
	void setContextMenu() {
		contextMenu = new ContextMenu();
		if (selection != null) {
			Menu menuSelecao = new Menu(selection.getWidth() + selection.getHeight() > 2 ? "Tiles selecionados" : "Tile selecionado");
			contextMenu.getItems().add(menuSelecao);
			Menu menu = new Menu("Copiar");
			menuSelecao.getItems().add(menu);
			MenuItem menuItem = new MenuItem("Apenas o primeiro sprite de cada tile");
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> copySelectedTiles(true));
			menuItem = new MenuItem("Cópia completa dos tiles");
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> copySelectedTiles(true));
			menu = new Menu("Colar");
			menu.setDisable(copiedTiles.isEmpty());
			menuSelecao.getItems().add(menu);
			menuItem = new MenuItem("Por cima dos tiles atuais");
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> pasteCopiedTiles(false));
			menuItem = new MenuItem("Remover tiles antes de colar");
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> pasteCopiedTiles());
			menuItem = new MenuItem("Remover");
			menuSelecao.getItems().add(menuItem);
			menuItem.setOnAction(e -> removeAllSelectedTiles());
		}
		else {
			MenuItem menuItem;
			if (getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
				final Tile tile = MapSet.getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord);
				Menu mainMenu = new Menu("TileProps");
				contextMenu.getItems().add(mainMenu);
				Menu menu = new Menu("Adicionar");
				mainMenu.getItems().add(menu);
				for (TileProp prop : TileProp.getList()) {
					menuItem = new MenuItem(prop.name());
					menuItem.setOnAction(e -> tile.tileProp.add(prop));
					menu.getItems().addAll(menuItem);
				}
				menuItem = new MenuItem("Copiar");
				mainMenu.getItems().add(menuItem);
				menuItem.setOnAction(e -> copyProps = new ArrayList<>(tile.tileProp));
				menuItem = new MenuItem("Colar (Adicionar)");
				mainMenu.getItems().add(menuItem);
				menuItem.setDisable(copyProps == null);
				menuItem.setOnAction(e -> tile.tileProp.addAll(copyProps));
				menuItem = new MenuItem("Colar (Substituir)");
				mainMenu.getItems().add(menuItem);
				menuItem.setDisable(copyProps == null);
				menuItem.setOnAction(e -> tile.tileProp = new ArrayList<>(copyProps));
				menu = new Menu("Remover");
				mainMenu.getItems().add(menu);
				menu.setDisable(tile.tileProp.isEmpty());
				for (TileProp prop : tile.tileProp) {
					menuItem = new MenuItem(prop.name());
					menu.getItems().addAll(menuItem);
					menuItem.setOnAction(e -> {
						Tile tile2 = MapSet.getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord);
						tile2.tileProp.remove(prop);
						if (tile2.tileProp.isEmpty())
							tile2.tileProp.add(TileProp.NOTHING);
					});
				}
				contextMenu.getItems().add(new SeparatorMenuItem());
				menu = new Menu("Tile Tags");
				contextMenu.getItems().add(menu);
				menuItem = new MenuItem("Editar");
				menu.getItems().add(menuItem);
				menuItem.setOnAction(e -> {
					String str = Alerts.textPrompt("Prompt", "Editar Tile Frame Tag", tile.getStringTags(), "Digite o novo Frame Tag para o tile atual");
					if (str != null)
						tile.setTileTagsFrameSetFromString(str);
				});
				menuItem = new MenuItem("Remover");
				menu.getItems().add(menuItem);
				menuItem.setOnAction(e -> tile.removeTileTagsFrameSet());
			}
		}
	}
	
	void setContextMenuTileSet() {
		contextMenuTileSet = new ContextMenu();
		MenuItem menuItemSelectionToString = new MenuItem("Copiar tile coord da seleção atual");
		menuItemSelectionToString.setOnAction(e ->
			Misc.putTextOnClipboard((int)tileSelection.getX() + ";" + (int)tileSelection.getY() + ";" + (int)tileSelection.getWidth() + ";" + (int)tileSelection.getHeight()));
		contextMenuTileSet.getItems().add(menuItemSelectionToString);
		menuItemSelectionToString = new MenuItem("Copiar position coord da seleção atual");
		menuItemSelectionToString.setOnAction(e ->
			Misc.putTextOnClipboard((int)tileSelection.getX() * Main.TILE_SIZE + ";" + (int)tileSelection.getY() * Main.TILE_SIZE + ";" + (int)tileSelection.getWidth() * Main.TILE_SIZE + ";" + (int)tileSelection.getHeight() * Main.TILE_SIZE));
		contextMenuTileSet.getItems().add(menuItemSelectionToString);
	}
	
	void copySelectedTiles(boolean copyOnlyFirstSprite) {
		copiedTiles.clear();
		iterateAllSelectedCoords(coord -> copiedTiles.put(coord, MapSet.getTileListFromCoord(coord)));
	}
	
	void copySelectedTiles()
		{ copySelectedTiles(false); }
	
	void pasteCopiedTiles()
		{ pasteCopiedTiles(true); }
	
	void pasteCopiedTiles(boolean removeTilesBeforePaste) {
  	int x = canvasMouseDraw.tileCoord.getX(),
  			y = canvasMouseDraw.tileCoord.getY();
  	int[] minPos = {Integer.MAX_VALUE, Integer.MAX_VALUE};
  	copiedTiles.keySet().forEach(t -> {
  		if (t.getX() < minPos[0])
  			minPos[0] = t.getX();
  		if (t.getY() < minPos[1])
  			minPos[1] = t.getY();
  	});
		int xx = x - minPos[0], yy = y - minPos[1];
  	for (TileCoord coord : copiedTiles.keySet()) {
  		TileCoord coord2 = new TileCoord(coord.getX() + xx, coord.getY() + yy);
  		if (removeTilesBeforePaste)
  			MapSet.getCurrentLayer().removeAllTilesFromCoord(coord2);
  		for (Tile tile : copiedTiles.get(coord))
  			MapSet.getCurrentLayer().addTile(new Tile(tile), coord2);
  	}
  	MapSet.getCurrentLayer().buildLayer();
	}
	
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
			if (MapSet.getTopTileFromCoord(coord).tileProp.contains(TileProp.FRAGILE_GROUND_LV1) && fragileTiles.containsKey(coord))
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
		Set<TileCoord> ok = new HashSet<>();
		if (checkBoxShowBlockType.isSelected()) {
			MapSet.getTileListFromCurrentLayer().forEach(tile -> {
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
	    		else if (tile.tileProp.contains(TileProp.PUSH_BOMB_TO_DOWN) ||
	    						 tile.tileProp.contains(TileProp.PUSH_BOMB_TO_LEFT) ||
	    						 tile.tileProp.contains(TileProp.PUSH_BOMB_TO_RIGHT) ||
	    						 tile.tileProp.contains(TileProp.PUSH_BOMB_TO_UP))
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
				String s = layer.getLayer() + " " + layer.getSpriteLayerType() + " " + (tile.outX / 16) + " " + (tile.outY / 16) + " " + (tile.spriteX / 16) + " " + (tile.spriteY / 16) + " " + tile.flip.getValue() + " " + tile.rotate / 90 + " " + props + " " + tile.opacity + " " + Tools.SpriteEffectsToString(tile.effects) + " " + (tile.getStringTags() == null ? "" : tile.getStringTags());
				MapSet.getMapIniFile().write("TILES", "" + n++, s);
			}
		MapSet.getMapIniFile().write("SETUP", "CopyImageLayer", "" + MapSet.getCopyImageLayerIndex());
		MapSet.getMapIniFile().write("SETUP", "Tiles", MapSet.getTileSetName());
		String criterias = "";
		for (StageClearCriteria criteria : MapSet.getStageClearCriterias()) {
			if (!criterias.isBlank())
				criterias += " ";
			criterias += criteria.name();
		}
		MapSet.getMapIniFile().write("SETUP", "PortalCriteria", criterias);
	}
	
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