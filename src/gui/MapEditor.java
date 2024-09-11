package gui;

import java.awt.Rectangle;
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
import gui.util.ListenerHandle;
import javafx.application.Platform;
import javafx.fxml.FXML;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import maps.Brick;
import maps.Layer;
import maps.MapSet;
import maps.Tile;
import objmoveutils.Position;
import tools.GameMisc;
import util.IniFile;

public class MapEditor {

	private final static int TILE_SIZE = 16;
	private static IniFile iniMapsCfg;
	
	@FXML
	private Button buttonPlay;
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
	private ListView<Integer> listViewLayers;
  @FXML
  private ComboBox<String> comboBoxMapFrameSets;

	private Map<SpriteLayerType, Canvas> canvas;
	private Map<SpriteLayerType, GraphicsContext> gcs;
	private ListenerHandle<Integer> listenerHandleLayersListView;
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
	private GraphicsContext gcFragileGround;
	private Canvas canvasDraw;
	private GraphicsContext gcDraw;
	private Brick[] bricks;
	private Position[] tilePosition;
	private Canvas[] canvasList;
	private List<KeyCode> holdedKeys;
	private Map<TileCoord, Entity> fragileTiles;
	private Entity sampleFragileTile;
	private Map<TileCoord, Tile> copiedTiles;
	private List<Map<TileCoord, List<Tile>>> backupTiles;
	private MapSet currentMapSet;
	private Font font;
	private ContextMenu defaultContextMenu;
	private Position tileSetPos;
	private int currentMapPos;
	private int currentLayerIndex;
	private CanvasMouse drawCanvasMouse;
	private CanvasMouse tileCanvasMouse;
	private int zoom;
	private int ctrlZPos;
	private long resetBricks;
	private boolean playing;

	public void init(Scene scene) {
		iniMapsCfg = IniFile.getNewIniFileInstance("appdata/configs/Stages.cfg");
		gcs = new HashMap<>();
		canvas = new HashMap<>();
		for (SpriteLayerType layerType : SpriteLayerType.getList()) {
			canvas.put(layerType, new Canvas(1000, 1000));
			gcs.put(layerType, canvas.get(layerType).getGraphicsContext2D());
			gcs.get(layerType).setImageSmoothing(false);
		}
		checkBoxShowBricks.setSelected(true);
		resetBricks = System.currentTimeMillis();
		canvasList = new Canvas[] {canvasBrickStand, canvasBrickBreaking, canvasBrickRegen, canvasWallSprite, canvasGroundSprite, canvasGroundWithBrickShadow, canvasFragileGround};
		gcBrickStand = canvasBrickStand.getGraphicsContext2D();
		gcBrickBreaking = canvasBrickBreaking.getGraphicsContext2D();
		gcBrickRegen = canvasBrickRegen.getGraphicsContext2D();
		gcWallSprite = canvasWallSprite.getGraphicsContext2D();
		gcGroundSprite = canvasGroundSprite.getGraphicsContext2D();
		gcGroundWithBrickShadow = canvasGroundWithBrickShadow.getGraphicsContext2D();
		gcFragileGround = canvasFragileGround.getGraphicsContext2D();
		gcBrickStand.setImageSmoothing(false);
		gcBrickBreaking.setImageSmoothing(false);
		gcBrickRegen.setImageSmoothing(false);
		gcWallSprite.setImageSmoothing(false);
		gcGroundSprite.setImageSmoothing(false);
		gcGroundWithBrickShadow.setImageSmoothing(false);
		gcFragileGround.setImageSmoothing(false);
	  sceneMain = scene;
		canvasDraw = new Canvas(1000, 1000);
		gcDraw = canvasDraw.getGraphicsContext2D();
		gcMain = canvasMain.getGraphicsContext2D();
		gcTileSet = canvasTileSet.getGraphicsContext2D();
	  gcMain.setImageSmoothing(false);
		gcDraw.setImageSmoothing(false);
		gcTileSet.setImageSmoothing(false);
		selection = null;
		tileSelection = null;
		holdedKeys = new ArrayList<>();
		copiedTiles = new HashMap<>();
		backupTiles = new ArrayList<>();
		fragileTiles = new HashMap<>();
		tileSetPos = new Position();
		listenerHandleLayersListView = new ListenerHandle<>(listViewLayers.getSelectionModel().selectedItemProperty(), (obs, oldValue, newValue) -> {
			if (newValue != null) {
				currentLayerIndex = newValue;
				loadMap(currentMapSet.getMapName(), false);
			}
		});
		font = new Font("Lucida Console", 15);
		zoom = 3;
		currentLayerIndex = 26;
		currentMapPos = -1;
		drawCanvasMouse = new CanvasMouse();
		tileCanvasMouse = new CanvasMouse();
		ctrlZPos = -1;
		playing = false;
		currentMapPos = -1; // TEMP
		loadNextMap();
		setListeners();
		setKeyboardEvents(sceneMain);
		setMainCanvasMouseEvents();
		setTileSetCanvasMouseEvents();
		saveCtrlZ();
		mainLoop();
	}
	
	private void setListeners() {
		listenerHandleLayersListView.attach();
		buttonPlay.setFont(new Font("Lucida Console", 14));
		buttonPlay.setText("►");
		buttonPlay.setOnAction(e -> {
			reloadCurrentMap();
			playing = !playing;
			buttonPlay.setText(playing ? "■" : "►");
		});
	}

	private void mainLoop() {
		drawDrawCanvas();
		drawMainCanvas();
		GameMisc.getFPSHandler().fpsCounter();
		if (!Main.close )
			Platform.runLater(() -> {
				String title = "Map Editor"
						+ "\tFPS: " + GameMisc.getFPSHandler().getFPS()
						+ "\tMap: " + currentMapSet.getMapName()
						+ "\tLayer: " + currentLayerIndex
						+ "\tTile " + drawCanvasMouse.tileCoord;
				Main.stageMain.setTitle(title);
				mainLoop();
			});
	}
	
	private void saveCtrlZ() {
		backupTiles.add(new HashMap<>(getTileMapFromCurrentLayer()));
		ctrlZPos = backupTiles.size() - 1;
	}
	
	private void ctrlZ() {
		if (--ctrlZPos == -1)
			ctrlZPos = backupTiles.size() - 1;
		getCurrentLayer().setTilesMap(backupTiles.get(ctrlZPos));
		rebuildCurrentLayer(false);
	}
	
	private void ctrlY() {
		if (++ctrlZPos == backupTiles.size())
			ctrlZPos = 0;
		getCurrentLayer().setTilesMap(backupTiles.get(ctrlZPos));
		rebuildCurrentLayer(false);
	}
	
	public MapSet getCurrentMapSet()
		{ return currentMapSet; }
	
	public int getCurrentLayerIndex()
		{ return currentLayerIndex; }
	
	private void reloadCurrentMap()
		{ loadMap(currentMapSet.getMapName()); }
	
	private void loadMap(String mapName)
		{ loadMap(mapName, true); }
	
	private void loadMap(String mapName, boolean resetCurrentLayerIndex) {
		if (mapName == null)
			GameMisc.throwRuntimeException("Unable to load map because 'mapName' is null");
		listenerHandleLayersListView.detach();
		Tile.tags = new HashMap<>();
		currentLayerIndex = resetCurrentLayerIndex ? 26 : currentLayerIndex;
		currentMapSet = new MapSet(mapName);
		listViewLayers.getItems().clear();
		List<Integer> list = new ArrayList<Integer>(currentMapSet.getLayersMap().keySet());
		list.sort((n1, n2) -> n2 - n1);
		for (int layer : list)
			listViewLayers.getItems().add(0, layer);
		listViewLayers.getSelectionModel().select(Integer.valueOf(currentLayerIndex));
		listenerHandleLayersListView.attach();
		tileSetPos.setPosition(0, 0);
		canvasTileSet.setWidth(currentMapSet.getTileSetImage().getWidth());
		canvasTileSet.setHeight(currentMapSet.getTileSetImage().getHeight());
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
		resetBricks = System.currentTimeMillis() + 1500;
		tilePosition = new Position[] {currentMapSet.getWallTile(), currentMapSet.getGroundTile(), currentMapSet.getGroundWithBlockShadow(), currentMapSet.getFragileGround()};
		drawCanvasMouse.movedX = 0;
		drawCanvasMouse.movedY = 0;
		selection = null;
		params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, canvasDraw.getWidth(), canvasDraw.getHeight()));
		for (Tile tile : currentMapSet.getLayer(26).getTileList())
			if (tile.tileProp.contains(TileProp.FRAGILE_GROUND_LV1)) {
				Entity fragileTile = new Entity(sampleFragileTile);
				fragileTiles.put(tile.getTileCoord(), fragileTile);
				fragileTile.setFrameSet("FragileGroundFrameSet");
				fragileTile.setPosition(tile.getTileX() * Main.tileSize, tile.getTileY() * Main.tileSize);
			}
	}
	
	public void loadPrevMap() {
		if (--currentMapPos == -1)
			currentMapPos = iniMapsCfg.getIniSize() - 1;
		loadMap(iniMapsCfg.getSectionAtPos(currentMapPos));
	}

	public void loadNextMap() {
		if (++currentMapPos == iniMapsCfg.getIniSize())
			currentMapPos = 0;
		loadMap(iniMapsCfg.getSectionAtPos(currentMapPos));
	}
	
	private void setKeyboardEvents(Scene scene) {
		scene.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			if (isNoHolds()) {
				if (e.getCode() == KeyCode.F7 || e.getCode() == KeyCode.F8) {
					if (e.getCode() == KeyCode.F7)
						currentLayerIndex--;
					else
						currentLayerIndex++;
					while (!currentMapSet.getLayersMap().containsKey(currentLayerIndex)) {
						if (e.getCode() == KeyCode.F7)
							currentLayerIndex--;
						else
							currentLayerIndex++;
						if (currentLayerIndex > 10000)
							currentLayerIndex = -10000;
						else if (currentLayerIndex < -10000)
							currentLayerIndex = 10000;
					}
					loadMap(currentMapSet.getMapName(), false);
				}
				else if (e.getCode() == KeyCode.DELETE) {
					if (isNoHolds() && drawCanvasMouse.getCoordX() >= 0 && drawCanvasMouse.getCoordY() >= 0 && (drawCanvasMouse.getCoordX() * 16 + 16) <= getCurrentLayer().getWidth() && (drawCanvasMouse.getCoordY() * 16 + 16) <= getCurrentLayer().getHeight())
						removeFirstTileSprite(drawCanvasMouse.tileCoord, true);
				}
				else if (e.getCode() == KeyCode.F10)
					loadNextMap();
				else if (e.getCode() == KeyCode.F9)
					loadPrevMap();
			}
			else if (isCtrlHold()) {
				if (e.getCode() == KeyCode.Z)
					ctrlZ();
				else if (e.getCode() == KeyCode.Y)
					ctrlY();
			}
		});
		scene.setOnKeyReleased(e -> {
			holdedKeys.remove(e.getCode());
		});
	}
	
	private void setTileSetCanvasMouseEvents() {
		canvasTileSet.setOnMouseDragged(e -> {
			tileCanvasMouse.tileCoord.setCoord((int)e.getX() / 16, (int)e.getY() / 16);
			if (e.getButton() == MouseButton.PRIMARY) {
				if (isShiftHold()) {
					if (tileSelection == null)
						tileSelection = new Rectangle();
					tileSelection.setFrameFromDiagonal(tileCanvasMouse.startDragDX < tileCanvasMouse.getCoordX() ? tileCanvasMouse.startDragDX : tileCanvasMouse.getCoordX(),
																						 tileCanvasMouse.startDragDY < tileCanvasMouse.getCoordY() ? tileCanvasMouse.startDragDY : tileCanvasMouse.getCoordY(),
																						(tileCanvasMouse.startDragDX > tileCanvasMouse.getCoordX() ? tileCanvasMouse.startDragDX : tileCanvasMouse.getCoordX()) + 1,
																						(tileCanvasMouse.startDragDY > tileCanvasMouse.getCoordY() ? tileCanvasMouse.startDragDY : tileCanvasMouse.getCoordY()) + 1);
				}
			}
		});

		canvasTileSet.setOnMouseMoved(e -> {
			tileCanvasMouse.x = (int)e.getX();
			tileCanvasMouse.y = (int)e.getY();
			tileCanvasMouse.tileCoord.setCoord((int)e.getX() / 16, (int)e.getY() / 16);
		});

		canvasTileSet.setOnMousePressed(e -> {
			tileCanvasMouse.startDragDX = tileCanvasMouse.getCoordX();
			tileCanvasMouse.startDragDY = tileCanvasMouse.getCoordY();
			if (e.getButton() == MouseButton.PRIMARY && !isAltHold())
					tileSelection = null;
		});
		canvasTileSet.setOnMouseClicked(e -> {
			if (tileSelection == null) {
				if (e.getButton() == MouseButton.PRIMARY)
					tileSetPos.setPosition((int)(e.getX() / 16), (int)(e.getY() / 16));
				System.out.println(e.getX() + "," + e.getY());
			}
		});
	}
	
	private void setMainCanvasMouseEvents() {
		canvasMain.setOnMouseDragged(e -> {
			drawCanvasMouse.x = (int)e.getX() + deslocX();
			drawCanvasMouse.y = (int)e.getY() + deslocY();
			TileCoord prevCoord = new TileCoord(drawCanvasMouse.tileCoord);
			drawCanvasMouse.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.tileSize * zoom), ((int)e.getY() - deslocY()) / (Main.tileSize * zoom));
			if (selection == null && !prevCoord.equals(drawCanvasMouse.tileCoord))
				addSelectedTileOnCurrentCursorPosition();
			if (!isAltHold()) {
				if (e.getButton() == MouseButton.PRIMARY) {
					if (isShiftHold()) {
						if (selection == null)
							selection = new Rectangle();
						selection.setFrameFromDiagonal(drawCanvasMouse.startDragDX < drawCanvasMouse.getCoordX() ? drawCanvasMouse.startDragDX : drawCanvasMouse.getCoordX(),
																					 drawCanvasMouse.startDragDY < drawCanvasMouse.getCoordY() ? drawCanvasMouse.startDragDY : drawCanvasMouse.getCoordY(),
																					 (drawCanvasMouse.startDragDX > drawCanvasMouse.getCoordX() ? drawCanvasMouse.startDragDX : drawCanvasMouse.getCoordX()) + 1,
																					 (drawCanvasMouse.startDragDY > drawCanvasMouse.getCoordY() ? drawCanvasMouse.startDragDY : drawCanvasMouse.getCoordY()) + 1);
					}
				}
			}
			else {
				drawCanvasMouse.dragX = ((int)e.getX() - drawCanvasMouse.startDragX);
				drawCanvasMouse.dragY = ((int)e.getY() - drawCanvasMouse.startDragY);
			}
		});

		canvasMain.setOnMouseMoved(e -> {
			drawCanvasMouse.x = (int)e.getX() + deslocX();
			drawCanvasMouse.y = (int)e.getY() + deslocY();
			drawCanvasMouse.tileCoord.setCoord(((int)e.getX() - deslocX()) / (Main.tileSize * zoom), ((int)e.getY() - deslocY()) / (Main.tileSize * zoom));
		});
		canvasMain.setOnMousePressed(e -> {
			drawCanvasMouse.startDragX = (int)e.getX();
			drawCanvasMouse.startDragY = (int)e.getY();
			drawCanvasMouse.startDragDX = drawCanvasMouse.getCoordX();
			drawCanvasMouse.startDragDY = drawCanvasMouse.getCoordY();
			if (e.getButton() == MouseButton.PRIMARY && !isAltHold()) {
					selection = null;
				if (isNoHolds())
					addSelectedTileOnCurrentCursorPosition();
			}
		});
		canvasMain.setOnMouseReleased(e -> {
			drawCanvasMouse.movedX += drawCanvasMouse.dragX;
			drawCanvasMouse.movedY += drawCanvasMouse.dragY;
			drawCanvasMouse.dragX = 0;
			drawCanvasMouse.dragY = 0;
		});
		canvasMain.setOnMouseClicked(e -> {
			if (selection == null) {
				if (e.getButton() == MouseButton.PRIMARY) {
				}
			}
			if (e.getButton() == MouseButton.SECONDARY) {
				if (defaultContextMenu != null)
					defaultContextMenu.hide();
				setContextMenu();
				defaultContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
			}
		});
	}
	
	private void addSelectedTileOnCurrentCursorPosition() {
		if (tileSelection != null) {
			for (int y = 0; y < tileSelection.getHeight(); y++)
				for (int x = 0; x < tileSelection.getWidth(); x++) {
					Tile tile = new Tile(currentMapSet, (int)tileSelection.getMinX() * 16 + x * 16, (int)tileSelection.getMinY() * 16 + x * 16, drawCanvasMouse.getCoordX() * 16 + x * 16, drawCanvasMouse.getCoordY() * 16 + y * 16, new ArrayList<>());
					getCurrentLayer().addTile(tile.getTileCoord(), tile);
				}
		}
		else if (drawCanvasMouse.getCoordX() >= 0 && drawCanvasMouse.getCoordY() >= 0 && (drawCanvasMouse.getCoordX() * 16 + 16) <= getCurrentLayer().getWidth() && (drawCanvasMouse.getCoordY() * 16 + 16) <= getCurrentLayer().getHeight()) {
			Tile tile = new Tile(currentMapSet, (int)tileSetPos.getX() * 16, (int)tileSetPos.getY() * 16, drawCanvasMouse.getCoordX() * 16, drawCanvasMouse.getCoordY() * 16, new ArrayList<>());
			getCurrentLayer().addTile(tile.getTileCoord(), tile);
		}
		rebuildCurrentLayer();
	}

	private Layer getCurrentLayer()
		{ return currentMapSet.getLayer(currentLayerIndex); }

	private Map<TileCoord, List<Tile>> getTileMapFromCurrentLayer()
		{ return getCurrentLayer().getTilesMap(); }
	
	private List<Tile> getTilesFromCurrentLayer()
		{ return getCurrentLayer().getTileList(); }

	private List<Tile> getTilesFromCoord(TileCoord coord)
		{ return getCurrentLayer().getTilesFromCoord(coord); }

	private Tile getFirstTileFromCoord(TileCoord coord)
		{ return getCurrentLayer().getFirstTileFromCoord(coord); }

	public void drawDrawCanvas() {
		gcDraw.setFill(Color.BLACK);
		gcDraw.fillRect(0, 0, canvasDraw.getWidth(), canvasDraw.getHeight());
		if (playing)
			currentMapSet.run(gcs);
		else if (currentMapSet.getLayersMap().containsKey(currentLayerIndex))
			gcDraw.drawImage(getCurrentLayer().getLayerImage(), 0, 0);
		if (checkBoxShowBricks.isSelected() && currentLayerIndex == 26)
			Brick.drawBricks(gcDraw);
		if (tileSelection != null) {
			for (int y = 0; y < tileSelection.getHeight(); y++)
				for (int x = 0; x < tileSelection.getWidth(); x++)
					gcDraw.drawImage(currentMapSet.getTileSetImage(), (int)tileSelection.getMinX() * 16 + x * 16, (int)tileSelection.getMinY() * 16 + x * 16, 16, 16, drawCanvasMouse.getCoordX() * 16 + x * 16, drawCanvasMouse.getCoordY() * 16 + y * 16, Main.tileSize, Main.tileSize);
		}
		else
			gcDraw.drawImage(currentMapSet.getTileSetImage(), tileSetPos.getX() * 16, tileSetPos.getY() * 16, 16, 16, drawCanvasMouse.getCoordX() * Main.tileSize, drawCanvasMouse.getCoordY() * Main.tileSize, Main.tileSize, Main.tileSize);
		fragileTiles.values().forEach(e -> e.run(gcDraw));
    drawBlockTypeMark();
	}
	
	private int deslocX()
		{ return drawCanvasMouse.movedX + drawCanvasMouse.dragX; }

	private int deslocY()
		{ return drawCanvasMouse.movedY + drawCanvasMouse.dragY; }

	public void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
		gcMain.setFill(Color.BLACK);
		gcMain.fillRect(0, 0, canvasMain.getWidth(), canvasMain.getHeight());
    gcMain.drawImage(canvasDraw.snapshot(params, null), 0, 0, canvasDraw.getWidth(), canvasDraw.getHeight(), deslocX(), deslocY(), canvasDraw.getWidth() * zoom, canvasDraw.getHeight() * zoom);
    drawGridAndAim();
    drawTileTagsOverCursor();
    drawTileSetCanvas();
    for (Rectangle rect : Arrays.asList(selection, tileSelection))
			if (rect != null) {
				GraphicsContext gc = rect == selection ? gcMain : gcTileSet;
		  	gc.setStroke(GameMisc.blink() ? Color.GREEN : Color.YELLOW);
		  	int z = rect == selection ? zoom : 1, 
		  			x = (int)rect.getMinX() * Main.tileSize * z,
		  			y = (int)rect.getMinY() * Main.tileSize * z,
		  			w = (int)rect.getWidth() * Main.tileSize * z,
		  			h = (int)rect.getHeight() * Main.tileSize * z;
				gc.strokeRect(x + deslocX(), y + deslocY(), w, h);
			}
		if (checkBoxShowBlockType.isSelected()) {
	    Tile tile = getCurrentLayer().getFirstTileFromCoord(drawCanvasMouse.tileCoord);
	    if (tile != null) {
	    	int x = tile.outX * zoom + deslocX(),
	    			y = tile.outY * zoom + (Main.tileSize * zoom) / 2 - 20 + deslocY();
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				for (TileProp prop : tile.tileProp) {
					String s = prop.name();
					Text text = new Text(s);
					text.setFont(font);
					while (x + (int)text.getBoundsInLocal().getWidth() + 20 >= canvasMain.getWidth())
						x -= 20;
					gcMain.strokeText(s, x, y += 20);
					gcMain.fillText(s, x, y);
				}
	    }
    }
 	}
	
	private void drawBrickSample(Canvas canvas, Entity entity) {
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

	private void drawBrickSample(Canvas canvas, Position tilePosition) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 48, 48);
		if (tilePosition != null)
			gc.drawImage(currentMapSet.getTileSetImage(), tilePosition.getX(), tilePosition.getY(), 16, 16, 0, 0, 48, 48);
	}
	
	public void drawTileSetCanvas() {
		for (int n = 0; n < 3; n++)
			drawBrickSample(canvasList[n], bricks[n]);
		for (int n = 3; n < 6; n++)
			drawBrickSample(canvasList[n], tilePosition[n - 3]);
		drawBrickSample(canvasList[6], sampleFragileTile);
		if (System.currentTimeMillis() >= resetBricks)
			resetBricks += 1500;
		gcTileSet.setFill(Color.BLACK);
		gcTileSet.fillRect(0, 0, canvasTileSet.getWidth(), canvasTileSet.getHeight());
		gcTileSet.drawImage(currentMapSet.getTileSetImage(), 0, 0);
		if (GameMisc.blink()) {
			gcMain.setLineWidth(1);
			gcTileSet.setStroke(Color.YELLOW);
			gcTileSet.strokeRect(tileSetPos.getX() * 16, tileSetPos.getY() * 16, 16, 16);
		}
	}

	private void drawTileTagsOverCursor() {
		if (!checkBoxShowBlockType.isSelected()) {
			boolean blink = System.currentTimeMillis() / 50 % 2 == 0;
			if (Tile.getStringTag(drawCanvasMouse.getCoordX(), drawCanvasMouse.getCoordY()) != null) {
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				String str[] = Tile.getStringTag(drawCanvasMouse.getCoordX(), drawCanvasMouse.getCoordY()).split(" ");
				int x = drawCanvasMouse.x + 30 - deslocX(), y = drawCanvasMouse.y - 10 - deslocY(), yy = str.length * 20;
				while (y + yy >= canvasMain.getWidth() - 30)
					y -= 20;
				yy = 0;
				for (String s : str) {
					Text text = new Text(s);
					text.setFont(font);
					while (x + (int)text.getBoundsInLocal().getWidth() + 20 >= canvasMain.getWidth())
						x -= 10;
				}
				for (String s : str) {
					gcMain.strokeText(s, x, y + (yy += 20));
					gcMain.fillText(s, x, y + yy);
				}
				gcMain.setStroke(Color.GREENYELLOW);
				gcMain.setLineWidth(2);
				gcMain.strokeRect(drawCanvasMouse.getCoordX() * zoom * TILE_SIZE + deslocX(), drawCanvasMouse.getCoordY() * zoom * TILE_SIZE + deslocY(), 16 * zoom, 16 * zoom);
			}
			for (int y = 0; blink && y < 200; y++)
				for (int x = 0; x < 200; x++)
					if (Tile.getStringTag(x, y) != null) {
						gcMain.setStroke(Color.WHITESMOKE);
						gcMain.setLineWidth(2);
						gcMain.strokeRect(x * zoom * TILE_SIZE + deslocX(), y * zoom * TILE_SIZE + deslocY(), 16 * zoom, 16 * zoom);
					}
		}
	}
	
	private void setContextMenu() {
		defaultContextMenu = new ContextMenu();
		if (selection != null) {
			Menu menuSelecao = new Menu("Seleção");
			MenuItem menuItemRemoveTiles = new MenuItem("Remover tile(s) selecionado(s)");
			menuItemRemoveTiles.setOnAction(e -> removeAllSelectedTiles());
			menuSelecao.getItems().addAll(menuItemRemoveTiles);
			defaultContextMenu.getItems().add(menuSelecao);
		}
	}
	
	private void copySelectedTiles(boolean copyOnlyFirstSprite) {
		if (selection != null) {
			copiedTiles.clear();
			List<Tile> tiles = getTilesFromCurrentLayer();
			for (int y = (int)selection.getMinY(); y < selection.getMaxY(); y++)
				for (int x = (int)selection.getMinX(); x < selection.getMaxX(); x++) {
					boolean ok = false;
					for (int n = 0; !ok && n < tiles.size(); n++) {
						Tile tile = tiles.get(n);
						if (tile.getTileX() == x && tile.getTileY() == y) {
							copiedTiles.put(new TileCoord(x, y), tile);
							ok = copyOnlyFirstSprite;
						}
					}
				}
		}
	}
	
	private void copySelectedTiles()
		{ copySelectedTiles(false); }
	
	private void replaceTile(TileCoord coord, Tile newTile)
		{ replaceTile(coord, Arrays.asList(newTile)); }

	private void replaceTile(TileCoord coord, List<Tile> newTiles) {
		removeTile(coord);
		addTile(coord, newTiles);
	}
	
	private void addTile(TileCoord coord, Tile newTile)
		{ addTile(coord, Arrays.asList(newTile)); }

	private void addTile(TileCoord coord, List<Tile> newTiles) {
		for (Tile tile : newTiles)
			getCurrentLayer().addTile(coord, tile);
		rebuildCurrentLayer();
	}

	private void removeTile(TileCoord coord)
		{ removeTile(coord, true); }
	
	private void removeTile(TileCoord coord, boolean rebuild) {
		removeTileMaster(coord, false);
		if (rebuild)
			rebuildCurrentLayer();
	}

	private void removeFirstTileSprite(TileCoord coord)
		{ removeFirstTileSprite(coord, true); }
	
	private void removeFirstTileSprite(TileCoord coord, boolean rebuild) {
		removeTileMaster(coord, true);
		if (rebuild)
			rebuildCurrentLayer();
	}

	private void removeTileMaster(TileCoord coord, boolean removeOnlyTopSprite) {
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
	
	private void removeAllSelectedTiles() {
		if (selection != null) {
			for (int y = (int)selection.getMinY(); y < selection.getMaxY(); y++)
				for (int x = (int)selection.getMinX(); x < selection.getMaxX(); x++)
					removeTile(new TileCoord(x, y), false);
			rebuildCurrentLayer();
		}
	}

	private void removeFirstSpriteFromSelectedTiles() {
		if (selection != null) {
			for (int y = (int)selection.getMinY(); y < selection.getMaxY(); y++)
				for (int x = (int)selection.getMinX(); x < selection.getMaxX(); x++)
					removeTile(new TileCoord(x, y), true);
			rebuildCurrentLayer();
		}
	}

	private void rebuildCurrentLayer()
		{ rebuildCurrentLayer(true); }
	
	private void rebuildCurrentLayer(boolean saveCtrlz) {
		getCurrentLayer().buildLayer();
		if (saveCtrlz)
			saveCtrlZ();
	}
	
	private void drawBlockTypeMark() {
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
	    						 tile.tileProp.contains(TileProp.RAIL_END))
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
	
	private void drawGridAndAim() {
		gcMain.setLineWidth(1);
    if (checkBoxShowGrid.isSelected()) {
    	gcMain.setStroke(Color.RED);
    	for (int y = 0; y < canvasMain.getHeight(); y += Main.tileSize * zoom)
    		gcMain.strokeRect(0, y, canvasMain.getWidth(), y + Main.tileSize * zoom - 1);
    	for (int x = 0; x < canvasMain.getWidth(); x += Main.tileSize * zoom)
    		gcMain.strokeRect(x, 0, x + Main.tileSize * zoom - 1, canvasMain.getHeight());
    }
    if (checkBoxShowAim.isSelected()) {
    	gcMain.setStroke(Color.YELLOW);
    	int x = drawCanvasMouse.getCoordX() * Main.tileSize * zoom, y = drawCanvasMouse.getCoordY() * Main.tileSize * zoom;
  		gcMain.strokeRect(x, 0, Main.tileSize * zoom - 1, canvasMain.getHeight());
  		gcMain.strokeRect(0, y, canvasMain.getWidth(), Main.tileSize * zoom - 1);
    	gcMain.setStroke(Color.LIGHTBLUE);
  		gcMain.strokeRect(x, y, Main.tileSize * zoom - 1, Main.tileSize * zoom - 1);
    }
	}

	public boolean isHold(int shift, int ctrl, int alt) {
		return ((shift == 0 && !isShiftHold()) || (shift == 1 && isShiftHold())) &&
					 ((ctrl == 0 && !isCtrlHold()) || (ctrl == 1 && isCtrlHold())) &&
					 ((alt == 0 && !isAltHold()) || (alt == 1 && isAltHold()));
	}
	
	public boolean isCtrlHold()
		{ return holdedKeys.contains(KeyCode.CONTROL); }
	
	public boolean isShiftHold()
		{ return holdedKeys.contains(KeyCode.SHIFT); }

	public boolean isAltHold()
		{ return holdedKeys.contains(KeyCode.ALT); }
	
	public boolean isNoHolds()
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
	
	public int getCoordX()
		{ return tileCoord.getX(); }
	
	public int getCoordY()
		{ return tileCoord.getY(); }

}
