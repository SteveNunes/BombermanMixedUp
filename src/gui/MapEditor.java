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
import damage.Explosion;
import damage.TileDamage;
import entities.Bomb;
import entities.BomberMan;
import entities.Effect;
import entities.Entity;
import enums.BombType;
import enums.Direction;
import enums.GameInput;
import enums.GameInputMode;
import enums.Icons;
import enums.ImageFlip;
import enums.ItemType;
import enums.SpriteLayerType;
import enums.StageClearCriteria;
import enums.TileProp;
import frameset.Tags;
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
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
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
import javafx.util.Pair;
import maps.Brick;
import maps.Item;
import maps.Layer;
import maps.MapSet;
import maps.Tile;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderOptmize;
import player.Player;
import tools.Draw;
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
	private Canvas canvasRollingTile;
	@FXML
	private Canvas canvasMain;
	@FXML
	private Canvas canvasTileSet;
	@FXML
	private CheckBox checkBoxShowTilesWith2Sprites;
	@FXML
	private CheckBox checkBoxShowAim;
	@FXML
	private CheckBox checkBoxShowGrid;
	@FXML
	private CheckBox checkBoxShowBlockType;
	@FXML
	private CheckBox checkBoxShowBricks;
	@FXML
	private CheckBox checkBoxShowItems;
	@FXML
	private CheckBox checkBoxMarkEntity;
	@FXML
	private CheckBox checkBoxMarkBomb;
	@FXML
	private CheckBox checkBoxMarkBrick;
	@FXML
	private CheckBox checkBoxMarkItem;
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
	private HBox hBoxFrameSetButtons;
	@FXML
	private VBox vBoxTileSet;
	@FXML
	private RadioButton radioCtrlModeTiles;
	@FXML
	private RadioButton radioCtrlModeProps;
	@FXML
	private RadioButton radioCtrlModeTags;

	private KeyCode[] keysInputs = { KeyCode.W, KeyCode.D, KeyCode.S, KeyCode.A, KeyCode.NUMPAD1, KeyCode.NUMPAD2, KeyCode.NUMPAD4, KeyCode.NUMPAD5, KeyCode.SPACE, KeyCode.ENTER };
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
	private PathFinder pathFinder;
	private List<KeyCode> holdedKeys;
	private List<TileProp> copyProps;
	private Tags copyTags;
	private List<CopiedTile> copiedTiles;
	private List<Map<TileCoord, List<Tile>>> backupTiles;
	private Font font;
	private CanvasMouse canvasMouseDraw;
	private CanvasMouse canvasMouseTileSet;
	private ItemType itemType;
	private BombType bombType;
	public static int zoomMain;
	private int zoomTileSet;
	private int ctrlZPos;
	private long resetBricks;
	private Set<TileCoord> alreadySetTiles;
	private String defaultMap = "TikTok-Small-Battle-01";
	public boolean playing;
	public boolean editable;
	private int controlledBomberIndex;
	private int bomberColor;
	private TileCoord hoveredInitialCoord;
	private Layer copyLayer;
	private int clickToSetNewInitialCoord;

	public void init() {
		canvasMouseDraw = new CanvasMouse(true);
		canvasMouseTileSet = new CanvasMouse();
		tileSelection = new Rectangle(0, 0, 1, 1);
		copiedTiles = new ArrayList<>();
		holdedKeys = new ArrayList<>();
		backupTiles = new ArrayList<>();
		alreadySetTiles = new HashSet<>();
		font = new Font("Lucida Console", 15);
		resetBricks = System.currentTimeMillis();
		zoomMain = 3;
		zoomTileSet = 1;
		controlledBomberIndex = 0;
		bomberColor = -1;
		playing = false;
		clickToSetNewInitialCoord = -1;
		editable = true;
		pathFinder = null;
		copyLayer = null;
		hoveredInitialCoord = null;
		MapSet.setCurrentLayerIndex(26);
		ctrlZPos = -1;
		copyProps = null;
		selection = null;
		copyTags = null;
		itemType = ItemType.BOMB_UP;
		bombType = BombType.NORMAL;
		canvasMain.setWidth(320 * zoomMain - 16 * zoomMain * 3);
		canvasMain.setHeight(240 * zoomMain - 16 * zoomMain);
		Main.setMainCanvas(canvasMain);
		listenerHandleComboBoxMapFrameSets = new ListenerHandle<>(comboBoxMapFrameSets.valueProperty(), (o, oldValue, newValue) -> MapSet.mapFrameSets.setFrameSet(comboBoxMapFrameSets.getSelectionModel().getSelectedItem()));
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		setAllCanvas();
		defineControls();
		setKeyboardEvents();
		setMainCanvasMouseEvents();
		rebuildTileSetCanvas();
		updateTileSelectionArray();
		mainLoop();
	}

	void setAllCanvas() {
		canvasList = new Canvas[] { canvasBrickStand, canvasBrickBreaking, canvasBrickRegen, canvasWallSprite, canvasGroundSprite, canvasGroundWithWallShadow, canvasGroundWithBrickShadow, canvasRollingTile };
		for (Canvas canvas : canvasList) {
			canvas.getGraphicsContext2D().setImageSmoothing(false);
			canvas.getParent().requestFocus();
		}
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
		buttonReloadFromDisk.setOnAction(e -> {
			Brick.clearBricks();
			setPlayButton();
			reloadCurrentMap();
		});
		buttonSaveToDisk.setOnAction(e -> saveCurrentMap());
		checkBoxShowBricks.setSelected(true);
		checkBoxShowItems.setSelected(true);
		buttonAddLayer.setOnAction(e -> {
			String str = Alerts.textPrompt("Prompt", "Adicionar camada", "Digite o indice da camada á ser adicionada");
			if (str != null) {
				int layer;
				try {
					layer = Integer.parseInt(str);
				}
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
			if (playing) {
				if (!checkBoxShowBricks.isSelected())
					Brick.clearBricks();
				else
					MapSet.setBricks();
			}
			checkBoxShowItems.setDisable(!checkBoxShowBricks.isSelected());
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
							brick.replaceFrameSetFromString(brick, frameSetName, str);
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
			MapSet.setGroundTile(new Position(tileSelection.getMinX() * Main.TILE_SIZE, tileSelection.getMinY() * Main.TILE_SIZE));
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetGroundWithWallShadow.setOnAction(e -> {
			MapSet.setGroundWithWallShadow(new Position(tileSelection.getMinX() * Main.TILE_SIZE, tileSelection.getMinY() * Main.TILE_SIZE));
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetGroundWithBrickShadow.setOnAction(e -> {
			MapSet.setGroundWithBrickShadow(new Position(tileSelection.getMinX() * Main.TILE_SIZE, tileSelection.getMinY() * Main.TILE_SIZE));
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		buttonSetFrameSetWallSprite.setOnAction(e -> {
			MapSet.setWallTile(new Position(tileSelection.getMinX() * Main.TILE_SIZE, tileSelection.getMinY() * Main.TILE_SIZE));
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		List<File> list = FindFile.findFile("./appdata/tileset/", "*.tiles");
		list.forEach(file -> comboBoxTileSets.getItems().add(file.getName().replace(".tiles", "")));
		comboBoxTileSets.valueProperty().addListener((obs, oldV, newV) -> {
			MapSet.setTileSet(newV);
			MapSet.rebuildAllLayers();
			setSampleTiles();
		});
		for (TileProp type : TileProp.getList())
			comboBoxTileType.getItems().add(type);
		comboBoxTileType.getSelectionModel().select(0);
		comboBoxMapList.getItems().addAll(IniFiles.stages.getSectionList());
		comboBoxMapList.valueProperty().addListener((obs, oldV, newV) -> loadMap(newV));
		comboBoxMapList.getSelectionModel().select(0);
		for (ImageFlip flip : ImageFlip.values())
			comboBoxTileFlip.getItems().add(flip);
		comboBoxTileFlip.getSelectionModel().select(0);
		for (int n = 0; n < 360; n += 90)
			comboBoxTileRotate.getItems().add(n);
		comboBoxTileRotate.getSelectionModel().select(0);
		ControllerUtils.setNodeFont(buttonPlay, "Lucida Console", 14);
		setPlayButton();
		canvasBrickStand.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickStandFrameSet")));
		canvasBrickBreaking.setOnMouseClicked(e -> Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickBreakFrameSet")));
		canvasBrickRegen.setOnMouseClicked(e -> {
			Brick.getBricks().forEach(brick -> brick.setFrameSet("BrickRegenFrameSet"));
		});
	}
	
	private void setPlayButton() {
		playing = false;
		editable = true;
		vBoxLayerList.setDisable(false);
		hBoxFrameSetButtons.setDisable(false);
		vBoxTileSet.setDisable(false);
		buttonSaveToDisk.setDisable(false);
		buttonPlay.setText("►");
		buttonPlay.setTooltip(new Tooltip("Ao rodar o framese, a edição do mapa e o botão\nde salvar serão desativados permanentemente,\naté que você recarregue o mapa, pois ao reproduzir\no frameset do mapa pode fazer com que alguns\nelementos do mapa sejam alterados em tempo real."));
		buttonPlay.setOnAction(e -> {
			playing = !playing;
			buttonPlay.setText(playing ? "■" : "►");
			if (playing)
				reloadCurrentMap();
			vBoxLayerList.setDisable(true);
			hBoxFrameSetButtons.setDisable(true);
			vBoxTileSet.setDisable(true);
			buttonSaveToDisk.setDisable(true);
			editable = false;
		});
	}

	void mainLoop() {
		try {
			drawDrawCanvas();
			drawMainCanvas();
			Tools.getFPSHandler().fpsCounter();
			if (!Main.close)
				Platform.runLater(() -> {
					String title = "Map Editor" + "     FPS: " + Tools.getFPSHandler().getFPS() + "     " + canvasMouseDraw.tileCoord + "     " + canvasMouseDraw.tileCoord.getPosition() + "     (Sprites: " + (getCurrentLayer().getTilesFromCoord(canvasMouseDraw.tileCoord) == null ? "0" : getCurrentLayer().getTilesFromCoord(canvasMouseDraw.tileCoord).size()) + "," + "     " + (MapSet.tileIsFree(canvasMouseDraw.tileCoord) ? "FREE" : "BRICKED") + ")" + "     Zoom: x" + zoomMain + "     Tileset Zoom: x" + zoomTileSet + "     Sobrecarga: " + Tools.getFPSHandler().getFreeTaskTicks();
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
		}
	}
	
	public int getCurrentLayerIndex() {
		return MapSet.getCurrentLayerIndex();
	}

	void reloadCurrentMap() {
		loadMap(MapSet.getMapName());
	}

	void loadMap(String mapName) {
		loadMap(mapName, true);
	}

	void setLayer(Integer newValue) {
		MapSet.setCurrentLayerIndex(newValue);
	}

	void loadMap(String mapName, boolean resetCurrentLayerIndex) {
		if (mapName == null)
			throw new RuntimeException("Unable to load map because 'mapName' is null");
		if (defaultMap != null) {
			mapName = defaultMap;
			defaultMap = null;
		}
		MapSet.loadMap(mapName);
		setLayersListView();
		resetBricks = System.currentTimeMillis() + 1500;
		canvasMouseDraw = new CanvasMouse(true);
		canvasMouseTileSet = new CanvasMouse();
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
			ControllerUtils.setListToComboBox(comboBox, Arrays.asList(SpriteLayerType.values()));
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

	Canvas getDrawCanvas() {
		return Draw.getTempCanvas();
	}

	GraphicsContext getDrawGc() {
		return Draw.getTempGc();
	}

	void setSampleTiles() {
		tilePosition = new Position[] { MapSet.getWallTile(), MapSet.getGroundTile(), MapSet.getGroundWithWallShadow(), MapSet.getGroundWithBrickShadow() };
		bricks = new Brick[] { new Brick(), new Brick(), new Brick(), new Brick() };
		bricks[0].setFrameSet("BrickStandFrameSet");
		bricks[1].setFrameSet("BrickBreakFrameSet");
		bricks[2].setFrameSet("BrickRegenFrameSet");
		if (MapSet.mapFrameSets.haveFrameSet("BrickRollingFrameSet"))
			bricks[3].setFrameSet("BrickRollingFrameSet");
	}

	void setKeyboardEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			Player.convertOnKeyPressEvent(e);
			if (e.getCode() == KeyCode.I && MapSet.tileIsFree(canvasMouseDraw.tileCoord) && !Item.haveItemAt(canvasMouseDraw.tileCoord) && !MapSet.tileContainsProp(canvasMouseDraw.tileCoord, TileProp.GROUND_NO_ITEM))
				Item.addItem(canvasMouseDraw.tileCoord, itemType);
			else if (e.getCode() == KeyCode.B && MapSet.tileIsFree(canvasMouseDraw.tileCoord) && !Item.haveItemAt(canvasMouseDraw.tileCoord) && !MapSet.tileContainsProp(canvasMouseDraw.tileCoord, TileProp.GROUND_NO_BOMB))
				Bomb.addBomb(canvasMouseDraw.tileCoord, bombType, 5);
			else if (e.getCode() == KeyCode.T && MapSet.tileIsFree(canvasMouseDraw.tileCoord) && !Item.haveItemAt(canvasMouseDraw.tileCoord) && !MapSet.tileContainsProp(canvasMouseDraw.tileCoord, TileProp.GROUND_NO_BRICK)) {
				if (itemType != null)
					Brick.addBrick(canvasMouseDraw.tileCoord, itemType);
				else
					Brick.addBrick(canvasMouseDraw.tileCoord);
			}
			else if (e.getCode() == KeyCode.Q || e.getCode() == KeyCode.E) {
				if (e.getCode() == KeyCode.Q && --controlledBomberIndex == -1)
					controlledBomberIndex = Player.getTotalPlayers() - 1;
				else if (e.getCode() == KeyCode.E && ++controlledBomberIndex == Player.getTotalPlayers())
					controlledBomberIndex = 0;
				Player.getPlayer(controlledBomberIndex).getBomberMan().setBlinkingFrames(45);
			}
			else if (e.getCode() == KeyCode.P && MapSet.tileIsFree(canvasMouseDraw.tileCoord) && !Entity.haveAnyEntityAtCoord(canvasMouseDraw.tileCoord))
				addPlayerAtCursor();
			else if (e.getCode() == KeyCode.F) {
				if (pathFinder == null)
					pathFinder = new PathFinder(getCurrentBomber().getTileCoordFromCenter(), canvasMouseDraw.tileCoord, getCurrentBomber().getDirection(), PathFinderOptmize.OPTIMIZED, t -> MapSet.tileIsFree(t));
				else
					pathFinder = null;
			}
			if (!editable)
				return;
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
				if (isCtrlHold()) {
					if (selection == null) {
						if (MapSet.haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
							if (radioCtrlModeProps.isSelected())
								copyProps = new ArrayList<>(MapSet.getTileProps(canvasMouseDraw.tileCoord));
							else if (radioCtrlModeTags.isSelected())
								copyTags = new Tags(MapSet.getTileTags(canvasMouseDraw.tileCoord));
						}
					}
					else if (radioCtrlModeTiles.isSelected())
						copySelectedTiles();
				}
			}
			else if (e.getCode() == KeyCode.V) {
				if (isCtrlHold()) {
					if (radioCtrlModeTiles.isSelected())
						pasteCopiedTiles();
					else if (MapSet.haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
						if (radioCtrlModeProps.isSelected()) {
							if (isShiftHold())
								MapSet.setTileProps(canvasMouseDraw.tileCoord, new ArrayList<>(copyProps));
							else
								MapSet.getTileProps(canvasMouseDraw.tileCoord).addAll(copyProps);
						}
						else if (radioCtrlModeTags.isSelected()) {
							if (isShiftHold())
								MapSet.setTileTags(canvasMouseDraw.tileCoord, new Tags(copyTags));
							else if (MapSet.tileHaveTags(canvasMouseDraw.tileCoord))
								MapSet.getTileTags(canvasMouseDraw.tileCoord).getTags().addAll(copyTags.getTags());
							else
								MapSet.setTileTags(canvasMouseDraw.tileCoord, new Tags(copyTags));
						}
					}
				}
			}
			else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.S || e.getCode() == KeyCode.A || e.getCode() == KeyCode.D) {
				Direction dir = e.getCode() == KeyCode.W ? Direction.UP : e.getCode() == KeyCode.S ? Direction.DOWN : e.getCode() == KeyCode.A ? Direction.LEFT : Direction.RIGHT;
				if (isCtrlHold()) {
					if (selection == null) {
						Map<TileCoord, List<Tile>> tilesMap = new HashMap<>();
						for (TileCoord coord : getCurrentLayer().getTilesMap().keySet()) {
							List<Tile> tiles = getCurrentLayer().getTilesFromCoord(coord);
							TileCoord tileCoord = coord.getNewInstance();
							tileCoord.incCoordsByDirection(dir);
							for (Tile tile : tiles)
								tile.setCoords(tileCoord);
							tilesMap.put(new TileCoord(tileCoord), new ArrayList<>(tiles));
						}
						List<Brick> bricks = new ArrayList<>(Brick.getBricks());
						bricks.forEach(brick -> Brick.removeBrick(brick.getTileCoordFromCenter()));
						bricks.forEach(brick -> {
							Brick.addBrick(brick.getTileCoordFromCenter(), false);
						});
						getCurrentLayer().setTilesMap(tilesMap);
					}
					else {
						Map<TileCoord, List<Tile>> tilesMap = new HashMap<>();
						List<Brick> bricks = new ArrayList<>();
						iterateAllSelectedCoords(coord -> {
							if (getCurrentLayer().haveTilesOnCoord(coord)) {
								TileCoord tileCoord = coord.getNewInstance();
								tileCoord.incCoordsByDirection(dir);
								tilesMap.put(new TileCoord(tileCoord), getCurrentLayer().getTilesFromCoord(coord));
								if (Brick.haveBrickAt(coord)) {
									Brick brick = Brick.getBrickAt(coord);
									bricks.add(brick);
									brick.incPositionByDirection(dir);
									Brick.removeBrick(coord);
								}
								getCurrentLayer().removeAllTilesFromCoord(coord);
							}
						});
						for (TileCoord coord : tilesMap.keySet()) {
							if (getCurrentLayer().haveTilesOnCoord(coord))
								getCurrentLayer().removeAllTilesFromCoord(coord);
							for (Tile tile : tilesMap.get(coord))
								getCurrentLayer().addTile(tile, coord);
						}
						bricks.forEach(brick -> Brick.addBrick(brick.getTileCoordFromCenter()));
						int x = (int) selection.getX(), y = (int) selection.getY(), w = (int) selection.getWidth(), h = (int) selection.getHeight();
						x += dir == Direction.LEFT ? -1 : dir == Direction.RIGHT ? 1 : 0;
						y += dir == Direction.UP ? -1 : dir == Direction.DOWN ? 1 : 0;
						selection.setBounds(x, y, w, h);
					}
					getCurrentLayer().buildLayer();
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
			Player.convertOnKeyReleaseEvent(e);
			for (int n = 0; n < keysInputs.length; n++) {
				if (e.getCode() == keysInputs[n])
					getCurrentBomber().keyRelease(GameInput.values()[n]);
			}
		});
	}

	private BomberMan getCurrentBomber() {
		return Player.getPlayer(controlledBomberIndex).getBomberMan();
	}

	void decLayerIndex() {
		incLayerIndex(-1);
	}

	void incLayerIndex() {
		incLayerIndex(1);
	}

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
		scrollPane.requestFocus();
		setTileSetCanvasMouseEvents();
	}

	void setTileSetCanvasMouseEvents() {
		canvasTileSet.setOnMouseDragged(e -> {
			canvasMouseTileSet.tileCoord.setCoords((int) e.getX() / (16 * zoomTileSet), (int) e.getY() / (16 * zoomTileSet));
			if (e.getButton() == MouseButton.PRIMARY)
				tileSelection.setFrameFromDiagonal(canvasMouseTileSet.startDragDX < canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX(), canvasMouseTileSet.startDragDY < canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY(), (canvasMouseTileSet.startDragDX > canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX()) + 1, (canvasMouseTileSet.startDragDY > canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY()) + 1);
		});

		canvasTileSet.setOnMouseMoved(e -> {
			canvasMouseTileSet.x = (int) e.getX();
			canvasMouseTileSet.y = (int) e.getY();
			canvasMouseTileSet.tileCoord.setCoords((int) e.getX() / (16 * zoomTileSet), (int) e.getY() / (16 * zoomTileSet));
		});

		canvasTileSet.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				tileSelection = new Rectangle();
				canvasMouseTileSet.startDragDX = canvasMouseTileSet.getCoordX();
				canvasMouseTileSet.startDragDY = canvasMouseTileSet.getCoordY();
				tileSelection.setFrameFromDiagonal(canvasMouseTileSet.startDragDX < canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX(), canvasMouseTileSet.startDragDY < canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY(), (canvasMouseTileSet.startDragDX > canvasMouseTileSet.getCoordX() ? canvasMouseTileSet.startDragDX : canvasMouseTileSet.getCoordX()) + 1, (canvasMouseTileSet.startDragDY > canvasMouseTileSet.getCoordY() ? canvasMouseTileSet.startDragDY : canvasMouseTileSet.getCoordY()) + 1);
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

	private void updateTileSetText() {
		textTileSetCoord.setText("Tile: " + canvasMouseTileSet.tileCoord.getX() + "," + canvasMouseTileSet.tileCoord.getY() + " (" + (int) tileSelection.getWidth() + " x " + (int) tileSelection.getHeight() + ")     Position: " + canvasMouseTileSet.tileCoord.getX() * Main.TILE_SIZE + "," + canvasMouseTileSet.tileCoord.getY() * Main.TILE_SIZE + " (" + (int) tileSelection.getWidth() * Main.TILE_SIZE + " x " + (int) tileSelection.getHeight() * Main.TILE_SIZE + ")");
	}

	void setMainCanvasMouseEvents() {
		canvasMain.setOnScroll(e -> {
			int inc = (isShiftHold() ? e.getDeltaX() : e.getDeltaY()) < 0 ? -1 : 1;
			if (isNoHolds() && (zoomMain + inc <= 10 && zoomMain + inc >= 1))
				zoomMain += inc;
		});
		canvasMain.setOnMouseDragged(e -> {
			if (contextMenu != null && contextMenu.isShowing())
				return;
			canvasMouseDraw.x = (int) e.getX() + offsetX();
			canvasMouseDraw.y = (int) e.getY() + offsetY();
			TileCoord prevCoord = canvasMouseDraw.tileCoord.getNewInstance();
			canvasMouseDraw.tileCoord.setCoords(((int) e.getX() - offsetX()) / (Main.TILE_SIZE * zoomMain), ((int) e.getY() - offsetY()) / (Main.TILE_SIZE * zoomMain));
			if (editable && e.getButton() == MouseButton.PRIMARY) {
				if (selection == null && !prevCoord.equals(canvasMouseDraw.tileCoord))
					addSelectedTileOnCurrentCursorPosition();
				if (isShiftHold()) {
					if (selection == null)
						selection = new Rectangle();
					selection.setFrameFromDiagonal(canvasMouseDraw.startDragDX < canvasMouseDraw.getCoordX() ? canvasMouseDraw.startDragDX : canvasMouseDraw.getCoordX(), canvasMouseDraw.startDragDY < canvasMouseDraw.getCoordY() ? canvasMouseDraw.startDragDY : canvasMouseDraw.getCoordY(), (canvasMouseDraw.startDragDX > canvasMouseDraw.getCoordX() ? canvasMouseDraw.startDragDX : canvasMouseDraw.getCoordX()) + 1, (canvasMouseDraw.startDragDY > canvasMouseDraw.getCoordY() ? canvasMouseDraw.startDragDY : canvasMouseDraw.getCoordY()) + 1);
				}
			}
			else if (e.getButton() == MouseButton.SECONDARY) {
				canvasMouseDraw.dragX = ((int) e.getX() - canvasMouseDraw.startDragX);
				canvasMouseDraw.dragY = ((int) e.getY() - canvasMouseDraw.startDragY);
			}
		});

		canvasMain.setOnMouseMoved(e -> {
			if (contextMenu != null && contextMenu.isShowing())
				return;
			canvasMouseDraw.x = (int) e.getX() + offsetX();
			canvasMouseDraw.y = (int) e.getY() + offsetY();
			canvasMouseDraw.tileCoord.setCoords(((int) e.getX() - offsetX()) / (Main.TILE_SIZE * zoomMain), ((int) e.getY() - offsetY()) / (Main.TILE_SIZE * zoomMain));
		});
		canvasMain.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY && selection != null) {
				selection = null;
				copyLayer = null;
				copiedTiles.clear();
				return;
			}
			alreadySetTiles.clear();
			canvasMouseDraw.startDragX = (int) e.getX();
			canvasMouseDraw.startDragY = (int) e.getY();
			canvasMouseDraw.startDragDX = canvasMouseDraw.getCoordX();
			canvasMouseDraw.startDragDY = canvasMouseDraw.getCoordY();
			if (contextMenu != null && contextMenu.isShowing()) {
				contextMenu.hide();
				return;
			}
			if (clickToSetNewInitialCoord != -1) {
				int n = clickToSetNewInitialCoord >= 1000 ? clickToSetNewInitialCoord - 1000 : clickToSetNewInitialCoord;
				if (clickToSetNewInitialCoord < 1000)
					MapSet.getInitialPlayerPositions().set(n, canvasMouseDraw.tileCoord.getNewInstance());
				else
					MapSet.getInitialMonsterPositions().set(n, canvasMouseDraw.tileCoord.getNewInstance());
				clickToSetNewInitialCoord = -1;
				return;
			}
			if (e.getButton() == MouseButton.PRIMARY && !isAltHold()) {
				selection = null;
				if (editable && isNoHolds())
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
			alreadySetTiles.clear();
			canvasMouseDraw.tileCoord.setCoords(((int) e.getX() - offsetX()) / (Main.TILE_SIZE * zoomMain), ((int) e.getY() - offsetY()) / (Main.TILE_SIZE * zoomMain));
		});
	}

	void addSelectedTileOnCurrentCursorPosition() {
		updateTileSelectionArray(true);
	}

	void updateTileSelectionArray() {
		updateTileSelectionArray(false);
	}

	void updateTileSelectionArray(boolean fixTilesOnLayer) {
		ImageFlip flip = comboBoxTileFlip.getSelectionModel().getSelectedItem();
		int w, h, rotate = comboBoxTileRotate.getSelectionModel().getSelectedItem();
		if (rotate == 90 || rotate == 270) {
			h = (int) tileSelection.getWidth();
			w = (int) tileSelection.getHeight();
		}
		else {
			w = (int) tileSelection.getWidth();
			h = (int) tileSelection.getHeight();
		}
		tileSelectionArray = new Tile[h > w ? h : w][h > w ? h : w];
		for (int y = 0; y < tileSelection.getHeight(); y++)
			for (int x = 0; x < tileSelection.getWidth(); x++) {
				Tile tile = new Tile(getCurrentLayer(), (int) tileSelection.getMinX() * Main.TILE_SIZE + x * Main.TILE_SIZE, (int) tileSelection.getMinY() * Main.TILE_SIZE + y * Main.TILE_SIZE, canvasMouseDraw.getCoordX() * Main.TILE_SIZE + x * Main.TILE_SIZE, canvasMouseDraw.getCoordY() * Main.TILE_SIZE + y * Main.TILE_SIZE, flip, rotate, sliderTileOpacity.getValue());
				tileSelectionArray[y][x] = tile;
			}
		for (int y = 0; y < tileSelectionArray.length; y++)
			for (int x = 0; x < tileSelectionArray.length; x++) {
				int a, b;
				if (rotate == 0) {
					a = y;
					b = x;
				}
				else if (rotate == 90) {
					a = h - x - 1;
					b = y;
				}
				else if (rotate == 180) {
					a = w - y - 1;
					b = w - x - 1;
				}
				else {
					a = x;
					b = h - y - 1;
				}
				Tile tile = tileSelectionArray[a][b];
				if (tile != null) {
					boolean vf = tile.flip == ImageFlip.VERTICAL || tile.flip == ImageFlip.BOTH, hf = tile.flip == ImageFlip.HORIZONTAL || tile.flip == ImageFlip.BOTH;
					tile.setCoords(new TileCoord(canvasMouseDraw.getCoordX() + (hf ? w - 1 - x : x), canvasMouseDraw.getCoordY() + (vf ? h - 1 - y : y)));
					if (fixTilesOnLayer && !alreadySetTiles.contains(tile.getTileCoord())) {
						alreadySetTiles.add(tile.getTileCoord().getNewInstance());
						getCurrentLayer().addTile(tile);
						if (!MapSet.tileHaveProps(tile.getTileCoord()))
							MapSet.addTileProp(tile.getTileCoord().getNewInstance(), comboBoxTileType.getSelectionModel().getSelectedItem());
					}
				}
			}
		w = getCurrentLayer().getWidth();
		h = getCurrentLayer().getHeight();
		rebuildCurrentLayer(false);
		if (w != getCurrentLayer().getWidth() || h != getCurrentLayer().getHeight())
			setLayersListView();
	}

	Layer getCurrentLayer() {
		return MapSet.getCurrentLayer();
	}

	Map<TileCoord, List<Tile>> getTileMapFromCurrentLayer() {
		return getCurrentLayer().getTilesMap();
	}

	boolean haveTilesOnCoord(TileCoord coord) {
		return getCurrentLayer().haveTilesOnCoord(coord);
	}

	void drawDrawCanvas() {
		getDrawGc().setFill(Color.DIMGRAY);
		getDrawGc().fillRect(0, 0, 1000, 1000);
		getDrawGc().setFill(Color.BLACK);
		if (playing) {
			getDrawGc().fillRect(0, 0, MapSet.getLayer(26).getWidth(), MapSet.getLayer(26).getHeight());
			MapSet.run();
		}
		else {
			if (MapSet.getLayersMap().containsKey(MapSet.getCurrentLayerIndex())) {
				getDrawGc().fillRect(0, 0, getCurrentLayer().getWidth(), getCurrentLayer().getHeight());
				Draw.addDrawQueue(SpriteLayerType.GROUND, getCurrentLayer().getLayerImage(), 0, 0);
			}
			Explosion.drawExplosions();
			Bomb.drawBombs();
			Item.drawItems();
			TileDamage.runTileDamages();
			Effect.drawEffects();
			BomberMan.drawBomberMans();
		}
		if (copyLayer != null)
			Draw.addDrawQueue(SpriteLayerType.CEIL, copyLayer.getLayerImage(), canvasMouseDraw.getCoordX() * Main.TILE_SIZE, canvasMouseDraw.getCoordY() * Main.TILE_SIZE);
		if (checkBoxShowBricks.isSelected()) {
			if (!playing)
				Brick.drawBricks();
			if (checkBoxShowItems.isSelected() && Misc.blink(200))
				for (Brick brick : Brick.getBricks())
					if (brick.getItem() != null)
						Draw.addDrawQueue(SpriteLayerType.CEIL, Materials.mainSprites, (brick.getItem().getValue() - 1) * Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE, brick.getTileCoord().getX() * Main.TILE_SIZE, brick.getTileCoord().getY() * Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE);
		}
		updateTileSelectionArray();
		if (editable) {
			for (int y = 0; y < tileSelectionArray.length; y++)
				for (int x = 0; x < tileSelectionArray[0].length; x++) {
					Tile tile = tileSelectionArray[y][x];
					if (tile != null)
						Draw.addDrawQueue(SpriteLayerType.GROUND, MapSet.getTileSetImage(), tile.spriteX, tile.spriteY, Main.TILE_SIZE, Main.TILE_SIZE, tile.outX, tile.outY, Main.TILE_SIZE, Main.TILE_SIZE, tile.flip, tile.rotate, tile.opacity, null);
				}
		}
	}

	int offsetX() {
		return canvasMouseDraw.movedX + canvasMouseDraw.dragX;
	}

	int offsetY() {
		return canvasMouseDraw.movedY + canvasMouseDraw.dragY;
	}

	void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
		Draw.applyAllDraws(canvasMain, Color.DIMGRAY, zoomMain, offsetX(), offsetY());

		if (pathFinder != null) {
			pathFinder.recalculatePath(getCurrentBomber().getTileCoordFromCenter(), canvasMouseDraw.tileCoord, getCurrentBomber().getDirection());
			if (pathFinder.getCurrentPath() != null)
				for (Pair<TileCoord, Direction> path : pathFinder.getCurrentPath()) {
					Direction dir = path.getValue();
					int x = (int) path.getKey().getPosition().getX(), y = (int) path.getKey().getPosition().getY();
					gcMain.drawImage(Materials.hud, 1025 + dir.get4DirValue() * 16, 760, 16, 16, x * zoomMain + offsetX(), y * zoomMain + offsetY(), Main.TILE_SIZE * zoomMain, Main.TILE_SIZE * zoomMain);
				}
		}
		if (checkBoxMarkEntity.isSelected()) {
			gcMain.setLineWidth(4);
			gcMain.setStroke(Color.RED);
			for (Entity entity : Entity.getEntityList())
				gcMain.strokeRect(entity.getX() * zoomMain + offsetX(), entity.getY() * zoomMain + offsetY(), Main.TILE_SIZE * zoomMain, Main.TILE_SIZE * zoomMain);
			gcMain.setStroke(Color.MEDIUMVIOLETRED);
		}
		if (checkBoxMarkBomb.isSelected()) {
			gcMain.setStroke(Color.ORANGE);
			for (Bomb bomb : Bomb.getBombMap().values())
				gcMain.strokeRect(bomb.getX() * zoomMain + offsetX(), bomb.getY() * zoomMain + offsetY(), Main.TILE_SIZE * zoomMain, Main.TILE_SIZE * zoomMain);
		}
		if (checkBoxMarkBrick.isSelected()) {
			gcMain.setStroke(Color.GREENYELLOW);
			for (Brick brick : Brick.getBrickMap().values())
				gcMain.strokeRect(brick.getX() * zoomMain + offsetX(), brick.getY() * zoomMain + offsetY(), Main.TILE_SIZE * zoomMain, Main.TILE_SIZE * zoomMain);
		}
		if (checkBoxMarkItem.isSelected()) {
			gcMain.setStroke(Color.ALICEBLUE);
			for (Item item : Item.getItemMap().values())
				gcMain.strokeRect(item.getX() * zoomMain + offsetX(), item.getY() * zoomMain + offsetY(), Main.TILE_SIZE * zoomMain, Main.TILE_SIZE * zoomMain);
		}
		drawBlockTypeMark();
		drawGridAndAim();
		drawTileTagsOverCursor();
		drawTileSetCanvas();
		if (checkBoxShowBlockType.isSelected() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
			Tile tile = MapSet.getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord);
			if (tile != null) {
				int x, y = tile.outY * zoomMain + (Main.TILE_SIZE * zoomMain) / 2 - 20 + offsetY();
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				while (y + MapSet.getTotalTileProps(tile.getTileCoord()) * 20 >= canvasMain.getHeight() - 10)
					y -= 10;
				for (TileProp prop : MapSet.getTileProps(tile.getTileCoord())) {
					String s = prop.name();
					Text text = new Text(s);
					ControllerUtils.setNodeFont(text, "Lucida Console", 15);
					x = tile.outX * zoomMain + offsetX();
					while (x + (int) text.getBoundsInLocal().getWidth() + 60 >= canvasMain.getWidth())
						x -= 20;
					gcMain.strokeText(s, x, y += 20);
					gcMain.fillText(s, x, y);
				}
			}
		}
		if (playing)
			return;
		Arrays.asList(selection, tileSelection).forEach(rect -> {
			if (rect != null) {
				GraphicsContext gc = rect == selection ? gcMain : gcTileSet;
				gc.setStroke(Misc.blink(50) ? Color.GREEN : Color.YELLOW);
				int z = rect == selection ? zoomMain : zoomTileSet, x = (int) rect.getMinX() * Main.TILE_SIZE * z, y = (int) rect.getMinY() * Main.TILE_SIZE * z, w = (int) rect.getWidth() * Main.TILE_SIZE * z, h = (int) rect.getHeight() * Main.TILE_SIZE * z;
				gc.strokeRect(x + (rect == selection ? offsetX() : 0), y + (rect == selection ? offsetY() : 0), w, h);
			}
		});
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
		drawBrickSample(canvasList[7], bricks[3]);
		if (System.currentTimeMillis() >= resetBricks)
			resetBricks += 1500;
		gcTileSet.setFill(Color.BLACK);
		gcTileSet.fillRect(0, 0, canvasTileSet.getWidth(), canvasTileSet.getHeight());
		Image i = MapSet.getTileSetImage();
		gcTileSet.drawImage(i, 0, 0, i.getWidth(), i.getHeight(), 0, 0, i.getWidth() * zoomTileSet, i.getHeight() * zoomTileSet);
	}

	void drawTileTagsOverCursor() {
		for (int y = 0; Misc.blink(50) && y < 200; y++)
			for (int x = 0; x < 200; x++) {
				TileCoord coord = new TileCoord(x, y);
				if (haveTilesOnCoord(coord) && getCurrentLayer().tileHaveTags(coord)) {
					gcMain.setStroke(Color.WHITESMOKE);
					gcMain.setLineWidth(2);
					gcMain.strokeRect(x * zoomMain * Main.TILE_SIZE + offsetX(), y * zoomMain * Main.TILE_SIZE + offsetY(), 16 * zoomMain, 16 * zoomMain);
				}
			}
		if (!checkBoxShowBlockType.isSelected() && getCurrentLayer().haveTilesOnCoord(canvasMouseDraw.tileCoord)) {
			String tileTags;
			if (getCurrentLayer().tileHaveTags(canvasMouseDraw.tileCoord))
				tileTags = getCurrentLayer().getTileTags(canvasMouseDraw.tileCoord).toString();
			else if (getCurrentLayer().getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord).getStringTags() != null)
				tileTags = getCurrentLayer().getFirstBottomTileFromCoord(canvasMouseDraw.tileCoord).getStringTags();
			else
				return;
			gcMain.setFill(Color.LIGHTBLUE);
			gcMain.setStroke(Color.BLACK);
			gcMain.setFont(font);
			gcMain.setLineWidth(3);
			String str[] = tileTags.split(tileTags.charAt(0) == '{' ? "\\," : " ");
			int x, y = canvasMouseDraw.y + 20 - offsetY(), yy = str.length * 20;
			while (y + yy >= canvasMain.getHeight() - 30)
				y -= 20;
			yy = 0;
			for (String s : str) {
				Text text = new Text(s);
				ControllerUtils.setNodeFont(text, "Lucida Console", 15);
				int w = (int) text.getBoundsInLocal().getWidth();
				x = canvasMouseDraw.x - w / 2 - offsetX();
				while (x + w >= canvasMain.getWidth() - 130)
					x -= 10;
				gcMain.strokeText(s, x, y + (yy += 20));
				gcMain.fillText(s, x, y + yy);
			}
			gcMain.setStroke(Color.GREENYELLOW);
			gcMain.setLineWidth(2);
			gcMain.strokeRect(canvasMouseDraw.getCoordX() * zoomMain * Main.TILE_SIZE + offsetX(), canvasMouseDraw.getCoordY() * zoomMain * Main.TILE_SIZE + offsetY(), 16 * zoomMain, 16 * zoomMain);
		}
	}

	void setContextMenu() {
		contextMenu = new ContextMenu();
		if (selection != null) {
			MenuItem menuItem = new MenuItem(selection.getWidth() + selection.getHeight() > 2 ? "Copiar tiles selecionados" : "Copiar tile selecionado");
			contextMenu.getItems().add(menuItem);
			menuItem.setOnAction(e -> copySelectedTiles());
		}
		MenuItem menuItem = new Menu("Colar tiles copiados");
		menuItem.setDisable(copiedTiles.isEmpty());
		contextMenu.getItems().add(menuItem);
		menuItem.setOnAction(e -> pasteCopiedTiles());
		menuItem = new MenuItem("Remover");
		contextMenu.getItems().add(menuItem);
		menuItem.setOnAction(e -> removeAllSelectedTiles());
		contextMenu.getItems().add(new SeparatorMenuItem());
		menuItem = new MenuItem("Resetar offsetamento do mapa");
		contextMenu.getItems().add(menuItem);
		menuItem.setOnAction(e -> {
			canvasMouseDraw = new CanvasMouse(true);
			canvasMouseTileSet = new CanvasMouse();
		});
		final TileCoord coord = canvasMouseDraw.tileCoord.getNewInstance();
		if (getCurrentLayer().haveTilesOnCoord(coord)) {
			contextMenu.getItems().add(new SeparatorMenuItem());
			Menu menu = new Menu("Posição inicial");
			contextMenu.getItems().add(menu);
			for (int n = 0; n < 2; n++) {
				List<TileCoord> list = n == 0 ? MapSet.getInitialPlayerPositions() : MapSet.getInitialMonsterPositions();
				Menu menu2 = new Menu(n == 0 ? "Jogador" : "Monstro");
				menu.getItems().add(menu2);
				menuItem = new MenuItem("Adicionar");
				menu2.getItems().add(menuItem);
				menuItem.setOnAction(e -> list.add(coord.getNewInstance()));
				menuItem = new MenuItem("Remover");
				menuItem.setDisable(list.contains(coord));
				menu2.getItems().add(menuItem);
				menuItem.setOnAction(e -> list.remove(coord));
				Menu menu3 = new Menu("Editar da lista geral");
				menu2.getItems().add(menu3);
				for (TileCoord t : list) {
					Label label = new Label(t.toString());
					CustomMenuItem cMenuItem = new CustomMenuItem(label);
					final int n3 = list.indexOf(t) + 1000 * n;
					cMenuItem.setOnAction(e -> clickToSetNewInitialCoord = n3);
					label.setOnMouseEntered(e -> hoveredInitialCoord = t.getNewInstance());
					label.setOnMouseExited(e -> hoveredInitialCoord = null);
					menu3.getItems().add(cMenuItem);
				}
				menu3 = new Menu("Remover da lista geral");
				menu2.getItems().add(menu3);
				for (TileCoord t : list) {
					Label label = new Label(t.toString());
					CustomMenuItem cMenuItem = new CustomMenuItem(label);
					cMenuItem.setOnAction(e -> list.remove(t));
					label.setOnMouseEntered(e -> hoveredInitialCoord = t.getNewInstance());
					label.setOnMouseExited(e -> hoveredInitialCoord = null);
					menu3.getItems().add(cMenuItem);
				}
			}
			contextMenu.getItems().add(new SeparatorMenuItem());
			menu = new Menu("Adicionar");
			contextMenu.getItems().add(menu);
			menuItem = new MenuItem("Player");
			menuItem.setDisable(Player.getTotalPlayers() == 17);
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> addPlayerAtCursor());
			Menu menu2 = new Menu("Bomba");
			menu2.setDisable(!MapSet.tileIsFree(coord) || Item.haveItemAt(coord) || Bomb.haveBombAt(null, coord) || MapSet.tileContainsProp(coord, TileProp.GROUND_NO_BOMB));
			menu.getItems().add(menu2);
			for (BombType type : BombType.values()) {
				menuItem = new MenuItem(type.name());
				menu2.getItems().add(menuItem);
				menuItem.setOnAction(e -> {
					Bomb.addBomb(canvasMouseDraw.tileCoord, type, 5);
					bombType = type;
				});
			}
			menu2 = new Menu("Tijolo");
			menu2.setDisable(!MapSet.tileIsFree(coord) || Item.haveItemAt(coord) || Bomb.haveBombAt(null, coord) || MapSet.tileContainsProp(coord, TileProp.GROUND_NO_BOMB));
			menu.getItems().add(menu2);
			for (int n = -1; n < ItemType.values().length; n++) {
				ItemType type = n == -1 ? null : ItemType.values()[n];
				menuItem = new MenuItem(type == null ? "Sem item" : "Com item: " + type.name());
				menu2.getItems().add(menuItem);
				menuItem.setOnAction(e -> {
					if ((itemType = type) != null)
						Brick.addBrick(canvasMouseDraw.tileCoord, type);
					else
						Brick.addBrick(canvasMouseDraw.tileCoord);
				});
			}
			menu2 = new Menu("Item");
			menu2.setDisable(!MapSet.tileIsFree(coord) || Item.haveItemAt(coord));
			menu.getItems().add(menu2);
			for (ItemType type : ItemType.values()) {
				menuItem = new MenuItem(type.name());
				menu2.getItems().add(menuItem);
				menuItem.setOnAction(e -> {
					Item.addItem(coord, type);
					itemType = type;
				});
			}
			if (!editable) {
				contextMenu.getItems().add(new MenuItem("Recarregue o mapa para editá-lo"));
				return;
			}
			contextMenu.getItems().add(new SeparatorMenuItem());
			Menu mainMenu = new Menu("TileProps");
			contextMenu.getItems().add(mainMenu);
			menu = new Menu("Adicionar");
			mainMenu.getItems().add(menu);
			for (TileProp prop : TileProp.getList()) {
				menuItem = new MenuItem(prop.name());
				menuItem.setOnAction(e -> MapSet.addTileProp(coord, prop));
				menu.getItems().addAll(menuItem);
			}
			menuItem = new MenuItem("Copiar");
			mainMenu.getItems().add(menuItem);
			menuItem.setOnAction(e -> copyProps = new ArrayList<>(MapSet.getTileProps(coord)));
			menuItem = new MenuItem("Colar (Adicionar)");
			mainMenu.getItems().add(menuItem);
			menuItem.setDisable(copyProps == null);
			menuItem.setOnAction(e -> MapSet.getTileProps(coord).addAll(copyProps));
			menuItem = new MenuItem("Colar (Substituir)");
			mainMenu.getItems().add(menuItem);
			menuItem.setDisable(copyProps == null);
			menuItem.setOnAction(e -> MapSet.setTileProps(coord, new ArrayList<>(copyProps)));
			menu = new Menu("Remover");
			mainMenu.getItems().add(menu);
			menu.setDisable(!MapSet.tileHaveProps(coord));
			for (TileProp prop : MapSet.getTileProps(coord)) {
				menuItem = new MenuItem(prop.name());
				menu.getItems().addAll(menuItem);
				menuItem.setOnAction(e -> MapSet.removeTileProp(coord, prop));
			}
			contextMenu.getItems().add(new SeparatorMenuItem());
			menu = new Menu("Tile Tags");
			contextMenu.getItems().add(menu);
			menuItem = new MenuItem("Editar");
			menu.getItems().add(menuItem);
			final Tile tile = getCurrentLayer().getFirstBottomTileFromCoord(coord);
			menuItem.setOnAction(e -> {
				String str = Alerts.textPrompt("Prompt", "Editar Tile Frame Tag", tile.getStringTags(), "Digite o novo Frame Tag para o tile atual");
				if (str != null) {
					String backupTags = tile.getStringTags();
					try {
						tile.setTileTagsFromString(str);
					}
					catch (Exception ex) {
						tile.setTileTagsFromString(backupTags);
						Alerts.exception("Erro", "Erro ao definir tag do tile", ex);
						return;
					}
				}
			});
			menuItem = new MenuItem("Copiar");
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> copyTags = new Tags(MapSet.getTileTags(coord)));
			menuItem = new MenuItem("Colar (Adicionar)");
			menu.getItems().add(menuItem);
			menuItem.setDisable(copyTags == null);
			menuItem.setOnAction(e -> {
				if (MapSet.tileHaveTags(coord))
					MapSet.getTileTags(coord).getTags().addAll(copyTags.getTags());
				else
					MapSet.setTileTags(coord, new Tags(copyTags));
			});
			menuItem = new MenuItem("Colar (Substituir)");
			menu.getItems().add(menuItem);
			menuItem.setDisable(copyTags == null);
			menuItem.setOnAction(e -> MapSet.setTileTags(coord, new Tags(copyTags)));
			menuItem = new MenuItem("Remover");
			menu.getItems().add(menuItem);
			menuItem.setOnAction(e -> tile.clearTileTags());
		}
	}

	private void addPlayerAtCursor() {
		Player.addPlayer();
		BomberMan bomber = new BomberMan(Player.getTotalPlayers(), 1, getNextBomberColor());
		Player.getPlayer(Player.getTotalPlayers() - 1).setBomberMan(bomber);
		bomber.setPosition(canvasMouseDraw.tileCoord.getPosition());
	}

	private int getNextBomberColor() {
		if (++bomberColor == 17)
			bomberColor = 0;
		return bomberColor;
	}

	void setContextMenuTileSet() {
		contextMenuTileSet = new ContextMenu();
		MenuItem menuItemSelectionToString = new MenuItem("Copiar tile coord da seleção atual");
		menuItemSelectionToString.setOnAction(e -> Misc.putTextOnClipboard((int) tileSelection.getX() + ";" + (int) tileSelection.getY() + ";" + (int) tileSelection.getWidth() + ";" + (int) tileSelection.getHeight()));
		contextMenuTileSet.getItems().add(menuItemSelectionToString);
		menuItemSelectionToString = new MenuItem("Copiar position coord da seleção atual");
		menuItemSelectionToString.setOnAction(e -> Misc.putTextOnClipboard((int) tileSelection.getX() * Main.TILE_SIZE + ";" + (int) tileSelection.getY() * Main.TILE_SIZE + ";" + (int) tileSelection.getWidth() * Main.TILE_SIZE + ";" + (int) tileSelection.getHeight() * Main.TILE_SIZE));
		contextMenuTileSet.getItems().add(menuItemSelectionToString);
	}

	void copySelectedTiles() {
		copiedTiles.clear();
		iterateAllSelectedCoords(coord -> copiedTiles.add(new CopiedTile(coord)));
		copyLayer = new Layer(999999);
		pasteCopiedTiles(copyLayer, new TileCoord(0, 0));
	}
	
	void pasteCopiedTiles() {
		pasteCopiedTiles(getCurrentLayer(), canvasMouseDraw.tileCoord);
	}

	void pasteCopiedTiles(Layer layer, TileCoord targetTile) {
		Integer x = Integer.MAX_VALUE, xx = targetTile.getX(), 
						y = Integer.MAX_VALUE, yy = targetTile.getY();
		for (CopiedTile cTile : copiedTiles) {
			if (cTile.getCoord().getX() < x)
				x = cTile.getCoord().getX();
			if (cTile.getCoord().getY() < y)
				y = cTile.getCoord().getY();
		}
		x = xx > x ? xx - x : -(x - xx);
		y = yy > y ? yy - y : -(y - yy);
		for (CopiedTile cTile : copiedTiles) {
			TileCoord coord = cTile.getCoord().getNewInstance();
			coord.incCoords(x, y);
			if (layer.haveTilesOnCoord(coord))
				layer.removeAllTilesFromCoord(coord);
			if (cTile.getTiles() != null) {
				for (Tile tile : cTile.getTiles())
					layer.addTile(new Tile(tile, coord));
				layer.setTileProps(coord, cTile.getProps());
				if (cTile.getTags() != null)
					layer.setTileTags(coord, cTile.getTags());
			}
		}
		layer.buildLayer();
	}

	void replaceTile(TileCoord coord, Tile newTile) {
		replaceTile(coord, Arrays.asList(newTile));
	}

	void replaceTile(TileCoord coord, List<Tile> newTiles) {
		removeTile(coord);
		addTile(coord, newTiles);
	}

	void addTile(TileCoord coord, Tile newTile) {
		addTile(coord, Arrays.asList(newTile));
	}

	void addTile(TileCoord coord, List<Tile> newTiles) {
		for (Tile tile : newTiles)
			getCurrentLayer().addTile(tile);
		rebuildCurrentLayer();
	}

	void removeTile(TileCoord coord) {
		removeTile(coord, true);
	}

	void removeTile(TileCoord coord, boolean rebuild) {
		removeTileMaster(coord, false);
		if (rebuild)
			rebuildCurrentLayer();
	}

	void removeFirstTileSprite(TileCoord coord) {
		removeFirstTileSprite(coord, true);
	}

	void removeFirstTileSprite(TileCoord coord, boolean rebuild) {
		removeTileMaster(coord, true);
		if (rebuild)
			rebuildCurrentLayer();
	}

	void removeTileMaster(TileCoord coord, boolean removeOnlyTopSprite) {
		if (Brick.haveBrickAt(coord))
			Brick.removeBrick(coord);
		else if (getCurrentLayer().haveTilesOnCoord(coord)) {
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

	void rebuildCurrentLayer() {
		rebuildCurrentLayer(true);
	}

	void rebuildCurrentLayer(boolean saveCtrlz) {
		getCurrentLayer().buildLayer();
		if (saveCtrlz)
			saveCtrlZ();
	}
	
	void drawBlockTypeMark() {
		if (checkBoxShowBlockType.isSelected())
			Draw.drawBlockTypeMarks(gcMain, offsetX(), offsetY(), zoomMain, checkBoxShowTilesWith2Sprites.isSelected(), t -> {
				if (hoveredInitialCoord != null && t.getTileCoord().equals(hoveredInitialCoord) && Misc.blink(100))
					return Color.GOLD;
				return null;
			});
	}

	void iterateAllSelectedCoords(Consumer<TileCoord> consumer) {
		if (selection != null)
			for (int y = 0; y < selection.getHeight(); y++)
				for (int x = 0; x < selection.getWidth(); x++)
					consumer.accept(new TileCoord((int) selection.getMinX() + x, (int) selection.getMinY() + y));
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
		return ((shift == 0 && !isShiftHold()) || (shift == 1 && isShiftHold())) && ((ctrl == 0 && !isCtrlHold()) || (ctrl == 1 && isCtrlHold())) && ((alt == 0 && !isAltHold()) || (alt == 1 && isAltHold()));
	}

	boolean isCtrlHold() {
		return holdedKeys.contains(KeyCode.CONTROL);
	}

	boolean isShiftHold() {
		return holdedKeys.contains(KeyCode.SHIFT);
	}

	boolean isAltHold() {
		return holdedKeys.contains(KeyCode.ALT);
	}

	boolean isNoHolds() {
		return !isAltHold() && !isCtrlHold() && !isShiftHold();
	}

	void saveCurrentMap() {
		MapSet.getMapIniFile().clearSection("TILES");
		Map<Integer, Set<TileCoord>> ok = new HashMap<>();
		int n = 0;
		for (Layer layer : MapSet.getLayersMap().values()) {
			if (!ok.containsKey(layer.getLayer()))
				ok.put(layer.getLayer(), new HashSet<>());
			for (Tile tile : layer.getTileList()) {
				boolean first = !ok.get(layer.getLayer()).contains(tile.getTileCoord());
				String props = "", layerType = !first ? "-" : "" + layer.getSpriteLayerType(), tileTags = !first || tile.getStringTags() == null ? "" : tile.getStringTags();
				if (first) {
					for (TileProp prop : layer.getTileProps(tile.getTileCoord())) {
						if (!props.isEmpty())
							props += "!";
						props += prop.getValue();
					}
					ok.get(layer.getLayer()).add(tile.getTileCoord());
				}
				else
					props = "" + TileProp.NOTHING.getValue();
				String s = layer.getLayer() + " " + layerType + " " + (tile.spriteX / Main.TILE_SIZE) + "!" + (tile.spriteY / Main.TILE_SIZE) + " " + (tile.outX / Main.TILE_SIZE) + "!" + (tile.outY / Main.TILE_SIZE) + " " + tile.flip.name() + " " + tile.rotate + " " + props + " " + tile.opacity + " " + Tools.SpriteEffectsToString(tile.effects) + " " + tileTags;
				MapSet.getMapIniFile().write("TILES", "" + n++, s);
			}
		}
		MapSet.getMapIniFile().write("SETUP", "CopyImageLayer", "" + MapSet.getCopyImageLayerIndex());
		MapSet.getMapIniFile().write("SETUP", "TileSet", MapSet.getTileSetName());
		String criterias = "";
		for (StageClearCriteria criteria : MapSet.getStageClearCriterias()) {
			if (!criterias.isBlank())
				criterias += " ";
			criterias += criteria.name();
		}
		MapSet.getMapIniFile().write("SETUP", "PortalCriteria", criterias);
		StringBuilder sb = new StringBuilder();
		if (!MapSet.getInitialPlayerPositions().isEmpty()) {
			for (TileCoord coord : MapSet.getInitialPlayerPositions()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(coord.getX() + "!" + coord.getY());
			}
			MapSet.getMapIniFile().write("SETUP", "PlayerInitialCoordsOrder", sb.toString());
		}
		if (!MapSet.getInitialMonsterPositions().isEmpty()) {
			sb = new StringBuilder();
			for (TileCoord coord : MapSet.getInitialMonsterPositions()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(coord.getX() + "!" + coord.getY());
			}
			MapSet.getMapIniFile().write("SETUP", "MonsterInitialCoordsOrder", sb.toString());
		}
	}

	public static boolean isPlaying() {
		return Main.mapEditor.playing;
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

	CanvasMouse()
		{ this(false); }
	
	CanvasMouse(boolean offset) {
		tileCoord = new TileCoord();
		x = 0;
		y = 0;
		dragX = 0;
		dragY = 0;
		movedX = offset ? -32 * MapEditor.zoomMain : 0;
		movedY = offset ? -32 * MapEditor.zoomMain : 0;
		startDragX = offset ? 32 * MapEditor.zoomMain : 0;
		startDragY = offset ? 32 * MapEditor.zoomMain : 0;
		startDragDX = 0;
		startDragDY = 0;
	}

	int getCoordX() {
		return tileCoord.getX();
	}

	int getCoordY() {
		return tileCoord.getY();
	}
	
}

class CopiedTile {
	
	private TileCoord coord;
	private List<Tile> tiles;
	private List<TileProp> props;
	private Tags tags;
	
	public CopiedTile(TileCoord coord) {
		this.coord = coord.getNewInstance();
		if (MapSet.haveTilesOnCoord(coord)) {
			tiles = new ArrayList<>(MapSet.getTileListFromCoord(coord));
			props = new ArrayList<>(MapSet.getTileProps(coord));
			tags = !MapSet.tileHaveTags(coord) ? null : new Tags(MapSet.getTileTags(coord));
		}
		else {
			tiles = null;
			props = null;
			tags = null;
		}
	}

	public TileCoord getCoord() {
		return coord;
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	public List<TileProp> getProps() {
		return props;
	}

	public Tags getTags() {
		return tags;
	}
	
}
