package gui;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.TileProp;
import gui.util.ListenerHandle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import util.FindFile;

public class MapEditor {

	private final static int WIN_W = 320;
	private final static int WIN_H = 240;
	private final static int TILE_SIZE = 16;
	

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
  private Canvas canvasGroundWithWallShadow;
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
	private ListView<Integer> listViewLayers;

	private ListenerHandle<Integer> listenerHandleLayersListView;
	private Rectangle selection;
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
	private Canvas canvasDraw;
	private GraphicsContext gcDraw;
	private Brick[] bricks;
	private Position[] tilePosition;
	private Canvas[] canvasList;
	private List<KeyCode> holdedKeys;
	private List<Tile> copiedTiles;
	private List<List<Tile>> backupTiles;
	private MapSet currentMapSet;
	private Font font;
	private ContextMenu defaultContextMenu;
	private Position tileSetPos;
	private int currentMapPos;
	private int currentLayerIndex;
	private int mouseX;
	private int mouseY;
	private int mouseDX;
	private int mouseDY;
	private int mouseStartDragDX;
	private int mouseStartDragDY;
	private int zoom;
	private int ctrlZPos;
	private long resetBricks;
	
	public void init(Scene scene) {
		resetBricks = System.currentTimeMillis();
		canvasList = new Canvas[] {canvasBrickStand, canvasBrickBreaking, canvasBrickRegen, canvasWallSprite, canvasGroundSprite, canvasGroundWithBrickShadow, canvasGroundWithWallShadow};
		gcBrickStand = canvasBrickStand.getGraphicsContext2D();
		gcBrickBreaking = canvasBrickBreaking.getGraphicsContext2D();
		gcBrickRegen = canvasBrickRegen.getGraphicsContext2D();
		gcWallSprite = canvasWallSprite.getGraphicsContext2D();
		gcGroundSprite = canvasGroundSprite.getGraphicsContext2D();
		gcGroundWithBrickShadow = canvasGroundWithBrickShadow.getGraphicsContext2D();
		gcGroundWithWallShadow = canvasGroundWithWallShadow.getGraphicsContext2D();
		gcBrickStand.setImageSmoothing(false);
		gcBrickBreaking.setImageSmoothing(false);
		gcBrickRegen.setImageSmoothing(false);
		gcWallSprite.setImageSmoothing(false);
		gcGroundSprite.setImageSmoothing(false);
		gcGroundWithBrickShadow.setImageSmoothing(false);
		gcGroundWithWallShadow.setImageSmoothing(false);
	  sceneMain = scene;
		canvasDraw = new Canvas(WIN_W, WIN_H);
		gcDraw = canvasDraw.getGraphicsContext2D();
		gcMain = canvasMain.getGraphicsContext2D();
		gcTileSet = canvasTileSet.getGraphicsContext2D();
	  gcMain.setImageSmoothing(false);
		gcDraw.setImageSmoothing(false);
		gcTileSet.setImageSmoothing(false);
		selection = null;
		holdedKeys = new ArrayList<>();
		copiedTiles = new ArrayList<>();
		backupTiles = new ArrayList<>();
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
		mouseX = 0;
		mouseY = 0;
		mouseDX = 0;
		mouseDY = 0;
		ctrlZPos = -1;
		mouseStartDragDX = 0;
		mouseStartDragDY = 0;
		currentMapPos = 24; // TEMP
		loadNextMap();
		setListeners();
		setKeyboardEvents(sceneMain);
		setMainCanvasMouseEvents();
		setTileSetCanvasMouseEvents();
		saveCtrlZ();
		mainLoop();
		listenerHandleLayersListView.attach();
	}
	
	private void setListeners() {
	}

	private void mainLoop() {
		drawDrawCanvas();
		drawMainCanvas();
		GameMisc.getFPSHandler().fpsCounter();
		if (!Main.close )
			Platform.runLater(() -> {
				String title = "Map Editor \t FPS: " + GameMisc.getFPSHandler().getFPS() + " \t "
						 + " Map: " + currentMapSet.getMapName() + " Layer: " + currentLayerIndex
						 + "Tile " + mouseDX + "," + mouseDY;
				Main.stageMain.setTitle(title);
				mainLoop();
			});
	}
	
	private void saveCtrlZ() {
		backupTiles.add(new ArrayList<>(getTileListFromCurrentLayer()));
		ctrlZPos = backupTiles.size() - 1;
	}
	
	private void ctrlZ() {
		if (--ctrlZPos == -1)
			ctrlZPos = backupTiles.size() - 1;
		getCurrentLayer().setTiles(backupTiles.get(ctrlZPos));
		rebuildCurrentLayer(false);
	}
	
	private void ctrlY() {
		if (++ctrlZPos == backupTiles.size())
			ctrlZPos = 0;
		getCurrentLayer().setTiles(backupTiles.get(ctrlZPos));
		rebuildCurrentLayer(false);
	}
	
	public MapSet getCurrentMapSet()
		{ return currentMapSet; }
	
	public int getCurrentLayerIndex()
		{ return currentLayerIndex; }
	
	private void loadMap(String mapName)
		{ loadMap(mapName, true); }
	
	private void loadMap(String mapName, boolean resetCurrentLayerIndex) {
		listenerHandleLayersListView.detach();
		Tile.tags = new String[200][200];
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
		bricks = new Brick[] { new Brick(currentMapSet, 0, 0), new Brick(currentMapSet, 0, 0), new Brick(currentMapSet, 0, 0) };
		bricks[0].setFrameSet("BrickStandFrameSet");
		bricks[1].setFrameSet("BrickBreakFrameSet");
		bricks[2].setFrameSet("BrickRegenFrameSet");
		resetBricks = System.currentTimeMillis() + 1500;
		tilePosition = new Position[] {currentMapSet.getWallTile(), currentMapSet.getGroundTile(), currentMapSet.getGroundWithBlockShadow(), currentMapSet.getGroundWithWallShadow()};
	}
	
	public void loadPrevMap() {
		List<File> maps = FindFile.findFile("appdata/maps/", "*", 0);
		if (--currentMapPos == -1)
			currentMapPos = maps.size() - 1;
		loadMap(maps.get(currentMapPos).getName().replace(".map", ""));
	}

	public void loadNextMap() {
		List<File> maps = FindFile.findFile("appdata/maps/", "*", 0);
		if (++currentMapPos == maps.size())
			currentMapPos = 0;
		loadMap(maps.get(currentMapPos).getName().replace(".map", ""));
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
		canvasTileSet.setOnMouseMoved(e -> {
			
		});
		canvasTileSet.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				tileSetPos.setPosition((int)(e.getX() / 16), (int)(e.getY() / 16));
			}
			else if (e.getButton() == MouseButton.SECONDARY) {
				
			}
		});
	}

	private void setMainCanvasMouseEvents() {
		canvasMain.setOnMouseDragged(e -> {
			mouseX = (int)e.getX();
			mouseY = (int)e.getY();
			mouseDX = mouseX / (Main.tileSize * zoom);
			mouseDY = mouseY / (Main.tileSize * zoom);
			if (e.getButton() == MouseButton.PRIMARY) {
				if (selection == null)
					selection = new Rectangle();
				selection.setFrameFromDiagonal(mouseStartDragDX < mouseDX ? mouseStartDragDX : mouseDX,
																				mouseStartDragDY < mouseDY ? mouseStartDragDY : mouseDY,
																				(mouseStartDragDX > mouseDX ? mouseStartDragDX : mouseDX) + 1,
																				(mouseStartDragDY > mouseDY ? mouseStartDragDY : mouseDY) + 1);
			}
		});

		canvasMain.setOnMouseMoved(e -> {
			mouseX = (int)e.getX();
			mouseY = (int)e.getY();
			mouseDX = mouseX / (Main.tileSize * zoom);
			mouseDY = mouseY / (Main.tileSize * zoom);
		});
		canvasMain.setOnMousePressed(e -> {
			mouseStartDragDX = mouseDX;
			mouseStartDragDY = mouseDY;
			if (e.getButton() == MouseButton.PRIMARY)
				selection = null;
		});
		canvasMain.setOnMouseReleased(e -> {
			if (selection == null) {
				if (e.getButton() == MouseButton.PRIMARY) {
					Map<TileProp, Integer> map = new HashMap<>();
					map.put(TileProp.GROUND, 1);
					Tile tile = new Tile(currentMapSet, (int)tileSetPos.getX() * 16, (int)tileSetPos.getY() * 16, mouseDX * 16, mouseDY * 16, map);
					getCurrentLayer().getTiles().add(tile);
					rebuildCurrentLayer();
				}
			}
		});
		canvasMain.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				if (defaultContextMenu != null)
					defaultContextMenu.hide();
				setContextMenu();
				defaultContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
			}
		});
	}
	
	private Layer getCurrentLayer()
		{ return currentMapSet.getLayer(currentLayerIndex); }

	private List<Tile> getTileListFromCurrentLayer()
		{ return getCurrentLayer().getTiles(); }

	public void drawDrawCanvas() {
		gcDraw.setFill(Color.BLACK);
		gcDraw.fillRect(0, 0, WIN_W, WIN_H);
		if (currentMapSet.getLayersMap().containsKey(currentLayerIndex))
			currentMapSet.getLayer(currentLayerIndex).draw(gcDraw);
		gcDraw.drawImage(currentMapSet.getTileSetImage(), tileSetPos.getX() * 16, tileSetPos.getY() * 16, 16, 16, mouseDX * Main.tileSize, mouseDY * Main.tileSize, Main.tileSize, Main.tileSize);
	}
	
	public void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
    gcMain.drawImage(canvasDraw.snapshot(null, null), 0, 0, WIN_W, WIN_H, 0, 0, WIN_W * zoom, WIN_H * zoom);
    drawBlockTypeMark();
    drawGridAndAim();
    drawTileTagsOverCursor();
    drawTileSetCanvas();
		if (selection != null) {
	  	gcMain.setStroke(Color.ORANGE);
	  	int x = (int)selection.getMinX() * Main.tileSize * zoom,
	  			y = (int)selection.getMinY() * Main.tileSize * zoom,
	  			w = (int)selection.getWidth() * Main.tileSize * zoom,
	  			h = (int)selection.getHeight() * Main.tileSize * zoom;
			gcMain.strokeRect(x, y, w, h);
		}
		if (checkBoxShowBlockType.isSelected()) {
	    Tile tile = currentMapSet.getTileAt(currentLayerIndex, mouseDX, mouseDY);
	    if (tile != null) {
	    	int x = tile.outX * zoom, y = tile.outY * zoom + (Main.tileSize * zoom) / 2 - 20;
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				for (TileProp prop : tile.tileProp.keySet()) {
					gcMain.strokeText(prop.name() + " (" + tile.tileProp.get(prop) + ")", x, y += 20);
					gcMain.fillText(prop.name() + " (" + tile.tileProp.get(prop) + ")", x, y);
				}
	    }
    }
 	}
	
	private void drawBrickSample(Canvas canvas, Brick brick) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 48, 48);
		brick.run(gc, false);
		gc.drawImage(canvas.snapshot(null, null), 0, 0, 16, 16, 0, 0, 48, 48);
		if (System.currentTimeMillis() > resetBricks && brick.getCurrentFrameSet() != null && brick.getCurrentFrameSet().getCurrentFrameIndex() == brick.getCurrentFrameSet().getTotalFrames())
			brick.getCurrentFrameSet().setCurrentFrameIndex(0);
	}

	private void drawBrickSample(Canvas canvas, Position tilePosition) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 48, 48);
		gc.drawImage(currentMapSet.getTileSetImage(), tilePosition.getX(), tilePosition.getY(), 16, 16, 0, 0, 48, 48);
	}
	
	public void drawTileSetCanvas() {
		for (int n = 0; n < 3; n++)
			drawBrickSample(canvasList[n], bricks[n]);
		for (int n = 3; n < 6; n++)
			drawBrickSample(canvasList[n], tilePosition[n - 3]);
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
			if (Tile.tags[mouseDY][mouseDX] != null) {
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				String str[] = Tile.tags[mouseDY][mouseDX].split(" ");
				int x = mouseX + 30, y = mouseY - 10, yy = str.length * 20;
				while (y + yy >= WIN_H * zoom - 30)
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
				gcMain.strokeRect(mouseDX * zoom * TILE_SIZE, mouseDY * zoom * TILE_SIZE, 16 * zoom, 16 * zoom);
			}
			for (int y = 0; blink && y < 200; y++)
				for (int x = 0; x < 200; x++)
					if (Tile.tags[y][x] != null) {
						gcMain.setStroke(Color.WHITESMOKE);
						gcMain.setLineWidth(2);
						gcMain.strokeRect(x * zoom * TILE_SIZE, y * zoom * TILE_SIZE, 16 * zoom, 16 * zoom);
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
			List<Tile> tiles = getTileListFromCurrentLayer();
			for (int y = (int)selection.getMinY(); y < selection.getMaxY(); y++)
				for (int x = (int)selection.getMinX(); x < selection.getMaxX(); x++) {
					boolean ok = false;
					for (int n = 0; !ok && n < tiles.size(); n++) {
						Tile tile = tiles.get(n);
						if (tile.getTileDX() == x && tile.getTileDY() == y) {
							copiedTiles.add(tile);
							ok = copyOnlyFirstSprite;
						}
					}
				}
		}
	}
	
	private void copySelectedTiles()
		{ copySelectedTiles(false); }
	
	private void replaceTile(int x, int y, Tile newTile)
		{ replaceTile(x, y, Arrays.asList(newTile)); }

	private void replaceTile(int x, int y, List<Tile> newTiles) {
		removeTile(x, y);
		addTile(x, y, newTiles);
	}
	
	private void addTile(int x, int y, Tile newTile)
		{ addTile(x, y, Arrays.asList(newTile)); }

	private void addTile(int x, int y, List<Tile> newTiles) {
		for (Tile tile : newTiles)
			getCurrentLayer().getTiles().add(tile);
		rebuildCurrentLayer();
	}

	private void removeTile(int x, int y)
		{ removeTile(x, y, true); }
	
	private void removeTile(int x, int y, boolean rebuild) {
		removeTileMaster(x, y, false);
		if (rebuild)
			rebuildCurrentLayer();
	}

	private void removeFirstTileSprite(int x, int y)
		{ removeFirstTileSprite(x, y, true); }
	
	private void removeFirstTileSprite(int x, int y, boolean rebuild) {
		removeTileMaster(x, y, true);
		if (rebuild)
			rebuildCurrentLayer();
	}

	private void removeTileMaster(int x, int y, boolean removeOnlyTopSprite) {
		List<Tile> tiles = getTileListFromCurrentLayer();
		for (int n = tiles.size() - 1; n >= 0; n--) {
			Tile tile = tiles.get(n);
			if (tile.getTileDX() == x && tile.getTileDY() == y) {
				tiles.remove(n);
				if (removeOnlyTopSprite)
					return;
			}
		}
	}
	
	private void removeAllSelectedTiles() {
		if (selection != null) {
			for (int y = (int)selection.getMinY(); y < selection.getMaxY(); y++)
				for (int x = (int)selection.getMinX(); x < selection.getMaxX(); x++)
					removeTile(x, y, false);
			rebuildCurrentLayer();
		}
	}

	private void removeFirstSpriteFromSelectedTiles() {
		if (selection != null) {
			for (int y = (int)selection.getMinY(); y < selection.getMaxY(); y++)
				for (int x = (int)selection.getMinX(); x < selection.getMaxX(); x++)
					removeTile(x, y, true);
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
		if (checkBoxShowBlockType.isSelected()) {
    	for (Tile tile : getTileListFromCurrentLayer()) {
    		Color color;
    		if (tile.tileProp.containsKey(TileProp.GROUND))
    			color = Color.YELLOW;
    		else if (tile.tileProp.containsKey(TileProp.GROUND_NO_MOB) || tile.tileProp.containsKey(TileProp.GROUND_NO_PLAYER) || tile.tileProp.containsKey(TileProp.GROUND_NO_BOMB) || tile.tileProp.containsKey(TileProp.GROUND_NO_FIRE))
    			color = Color.LIGHTGOLDENRODYELLOW;
    		else if (tile.tileProp.containsKey(TileProp.FRAGILE_GROUND_LV1) || tile.tileProp.containsKey(TileProp.FRAGILE_GROUND_LV2))
    			color = Color.LIGHTPINK;
    		else if (tile.tileProp.containsKey(TileProp.WALL) || tile.tileProp.containsKey(TileProp.HIGH_WALL))
    			color = Color.RED;
    		else if (tile.tileProp.containsKey(TileProp.BRICK_RANDOM_SPAWNER))
    			color = Color.LIGHTGREEN;
    		else if (tile.tileProp.containsKey(TileProp.FIXED_BRICK))
    			color = Color.GREEN;
    		else if (tile.tileProp.containsKey(TileProp.GROUND_HOLE) || tile.tileProp.containsKey(TileProp.MOVING_BLOCK_HOLE))
    			color = Color.ALICEBLUE;
    		else if (tile.tileProp.containsKey(TileProp.DEEP_HOLE))
    			color = Color.GRAY;
    		else if (tile.tileProp.containsKey(TileProp.WATER))
    			color = Color.LIGHTBLUE;
    		else
    			color = Color.ORANGE;
    		gcMain.save();
    		gcMain.setFill(color);
    		gcMain.setLineWidth(1);
    		gcMain.setGlobalAlpha(0.6);
	    	gcMain.fillRect(tile.getTileDX() * Main.tileSize * zoom, tile.getTileDY() * Main.tileSize * zoom, Main.tileSize * zoom, Main.tileSize * zoom);
    		gcMain.restore();
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
    	int x = mouseDX * Main.tileSize * zoom, y = mouseDY * Main.tileSize * zoom;
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
