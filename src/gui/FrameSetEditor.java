package gui;
	
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import application.Main;
import entities.Entity;
import entities.Frame;
import entities.FrameSet;
import entities.Sprite;
import enums.Direction;
import frameset_tags.DecSprAlign;
import frameset_tags.DecSprFlip;
import frameset_tags.FrameTag;
import frameset_tags.IncEntityPos;
import frameset_tags.IncEntityX;
import frameset_tags.IncEntityY;
import frameset_tags.IncObjPos;
import frameset_tags.IncObjX;
import frameset_tags.IncObjY;
import frameset_tags.IncOriginSprHeight;
import frameset_tags.IncOriginSprPos;
import frameset_tags.IncOriginSprSize;
import frameset_tags.IncOriginSprWidth;
import frameset_tags.IncOriginSprX;
import frameset_tags.IncOriginSprY;
import frameset_tags.IncOutputSprHeight;
import frameset_tags.IncOutputSprSize;
import frameset_tags.IncOutputSprWidth;
import frameset_tags.IncSprAlign;
import frameset_tags.IncSprFlip;
import frameset_tags.SetEntityPos;
import frameset_tags.SetEntityX;
import frameset_tags.SetEntityY;
import frameset_tags.SetObjPos;
import frameset_tags.SetObjX;
import frameset_tags.SetObjY;
import frameset_tags.SetOriginSprHeight;
import frameset_tags.SetOriginSprPos;
import frameset_tags.SetOriginSprSize;
import frameset_tags.SetOriginSprWidth;
import frameset_tags.SetOriginSprX;
import frameset_tags.SetOriginSprY;
import frameset_tags.SetOutputSprHeight;
import frameset_tags.SetOutputSprSize;
import frameset_tags.SetOutputSprWidth;
import frameset_tags.SetSprAlign;
import frameset_tags.SetSprAlpha;
import frameset_tags.SetSprFlip;
import frameset_tags.SetSprIndex;
import frameset_tags.SetSprRotate;
import gui.util.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import maps.MapSet;
import tools.GameMisc;
import tools.SquaredBg;
import util.IniFile;
import util.MyFile;


public class FrameSetEditor {
	
	private final int winW = 312;
	private final int winH = 240;
	private final int tileSize = 16;
	private List<Map<String, FrameSet>> backupFrameSetsMap;
	private List<KeyCode> holdedKeys;
	private List<FrameSet> backupFrameSets;
	private List<Sprite> deltaSprites;
	private List<Sprite> selectedSprites;
	private List<Sprite> copiedSprites;
	private List<Entity> entities;
	private Scene sceneMain;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Canvas canvasDraw;
	private GraphicsContext gcDraw;
	private Entity currentEntity;
	private Frame copiedFrame;
	private Sprite focusedSprite;
	private ContextMenu defaultContextMenu;
	private ContextMenu spriteContextMenu;
	private Stage stageHelpWindow;
	private Font font;
	public boolean isPaused;
	private boolean isChangingSprite;
	private int zoomScale;
	private int mouseX;
	private int mouseY;
	private int dragX;
	private int dragY;
	private int backupIndex;
	private int bgType;
	private int zoom = 3;
	private int linkEntityToCursor;
	private int centerX;
	private int centerY;
	private MapSet mapSet;
	
	public void init(Scene scene) {
		sceneMain = scene;
		font = new Font("Lucida Console", 15);
		canvasMain.getGraphicsContext2D().setImageSmoothing(false);
		canvasDraw = new Canvas(winW, winH);
		canvasDraw.getGraphicsContext2D().setImageSmoothing(false);
		canvasMain.setWidth(winW * zoom);
		canvasMain.setHeight(winH * zoom);
		gcDraw = canvasDraw.getGraphicsContext2D();
		gcMain = canvasMain.getGraphicsContext2D();
		mapSet = new MapSet("SBM_1-1");
		entities = new ArrayList<>();
		holdedKeys = new ArrayList<>();
		backupFrameSets = new ArrayList<>();
		backupFrameSetsMap = new ArrayList<>();
		deltaSprites = new ArrayList<>();
		selectedSprites = new ArrayList<>();
		copiedSprites = new ArrayList<>();
		focusedSprite = null;
		copiedFrame = null;
		isPaused = false;
		isChangingSprite = false;
		centerX = tileSize * 10;
		centerY = tileSize * 7;
		linkEntityToCursor = 0;
		zoomScale = 1;
		backupIndex = -1;
		mouseX = 0;
		mouseY = 0;
		dragX = 0;
		dragY = 0;
		bgType = 2;
		currentEntity = new Entity();
		currentEntity.setPosition(centerX, centerY);

		IniFile ini = IniFile.getNewIniFileInstance("./appdata/configs/Monsters.ini");
		currentEntity.addNewFrameSetFromString("MovingFrames.LEFT", ini.read("2", "MovingFrames.LEFT"));
		currentEntity.addNewFrameSetFromString("MovingFrames.RIGHT", ini.read("2", "MovingFrames.RIGHT"));
		currentEntity.addNewFrameSetFromString("MovingFrames.UP", ini.read("2", "MovingFrames.UP"));
		currentEntity.addNewFrameSetFromString("MovingFrames.DOWN", ini.read("2", "MovingFrames.DOWN"));

		currentEntity.addNewFrameSetFromString("IntroFrames", ini.read("1000", "IntroFrames"));
		currentEntity.addNewFrameSetFromString("MovingFrames", ini.read("1000", "MovingFrames"));

		currentEntity.setFrameSet("MovingFrames.LEFT");
		
		setDefaultContextMenu();
		setSpriteContextMenu();
		setMouseEvents();
		setKeyboardEvents(sceneMain);
		
		
		for (int n = 0; n < 0; n++) { // TEMP para desenhar multiplos FrameSets na tela para testar capacidade
			Entity entity = new Entity(currentEntity);
			entity.setFrameSet("MovingFrames");
			entity.setPosition(GameMisc.getRandom(0, 320), GameMisc.getRandom(0, 240));
			entities.add(entity);
		}
		
		mainLoop();
		
	}
	
	private void mainLoop() {
		drawDrawCanvas();
		drawMainCanvas();
		GameMisc.getFPSHandler().fpsCounter();
		if (!Main.close )
			Platform.runLater(() -> {
				String title = "Sprite Editor \t FPS: " + GameMisc.getFPSHandler().getFPS() + " \t ";
				if (getCurrentFrameSetName() != null) {
					title += "Frame Set: " + getCurrentFrameSetName() + 
							" | Frames: " + (getCurrentFrameSet().getCurrentFrameIndex() + 1) +
							"/" + getCurrentFrameSet().getTotalFrames() +
							" | Sprites: " + getCurrentFrameSet().getTotalSprites() +
							(isPaused ? " | (Paused)" : " | (Playing)") +
							" | Scroll mode: " + getScrollMode() +
							" | Move mode: " + getMoveMode();
				}
				Main.stageMain.setTitle(title);
				mainLoop();
			});
	}
	
	private void setDefaultContextMenu() {
		defaultContextMenu = new ContextMenu();
		Menu menu = new Menu("Adicionar FrameSet");
		MenuItem item1 = new MenuItem("FrameSet em branco");
		MenuItem item2 = new MenuItem("Cópia do FrameSet atual");
		item1.setOnAction(e -> addFrameSet(null));
		item2.setOnAction(e -> addFrameSet(getCurrentFrameSet()));
		menu.getItems().addAll(item1, item2);
		defaultContextMenu.getItems().add(menu);
		menu = new Menu("Remover FrameSet");
		for (String frameSet : currentEntity.getFrameSetsNames()) {
			MenuItem i = new MenuItem(frameSet);
			i.setOnAction(e -> {
				if (Alerts.confirmation("Excluir FrameSet", "Deseja mesmo excluir o FrameSet \"" + frameSet + "\"?")) {
					currentEntity.removeFrameSet(frameSet);
					Alerts.information("Info", "FrameSet excluido com sucesso!");
					if (!currentEntity.getFrameSetsMap().isEmpty())
						currentEntity.setFrameSet((String)currentEntity.getFrameSetsNames().toArray()[0]);
				}
			});
			menu.getItems().add(i);
		}
		menu.setDisable(currentEntity.getTotalFrameSets() == 0);
		defaultContextMenu.getItems().add(menu);
		if (currentEntity.getTotalFrameSets() > 0) {
			defaultContextMenu.getItems().add(new SeparatorMenuItem());
			menu = new Menu("Adicionar Frame ao FrameSet atual");
			Menu menu1 = new Menu("Frame em branco");
			MenuItem item11 = new MenuItem("No inicio do FrameSet");
			MenuItem item12 = new MenuItem("Após o Frame atual");
			MenuItem item13 = new MenuItem("No final do FrameSet");
			item11.setOnAction(e -> addFrame(0, null));
			item12.setOnAction(e -> addFrame(getCurrentFrameSet().getCurrentFrameIndex(), null));
			item13.setOnAction(e -> addFrame(getCurrentFrameSet().getTotalFrames(), null));
			menu1.getItems().addAll(item11, item12, item13);
			Menu menu2 = new Menu("Cópia do frame atual");
			MenuItem item21 = new MenuItem("No inicio do FrameSet");
			MenuItem item22 = new MenuItem("Após o Frame atual");
			MenuItem item23 = new MenuItem("No final do FrameSet");
			item21.setOnAction(e -> addFrame(0, getCurrentFrame()));
			item22.setOnAction(e -> addFrame(getCurrentFrameSet().getCurrentFrameIndex(), getCurrentFrame()));
			item23.setOnAction(e -> addFrame(getCurrentFrameSet().getTotalFrames(), getCurrentFrame()));
			menu2.getItems().addAll(item21, item22, item23);
			menu.getItems().addAll(menu1, menu2);
			defaultContextMenu.getItems().add(menu);
			MenuItem item = new MenuItem("Remover Frame atual");
			item.setOnAction(e -> getCurrentFrameSet().removeFrame(getCurrentFrame()));
			item.setDisable(getCurrentFrameSet().isEmptyFrames());
			defaultContextMenu.getItems().add(item);
			if (getCurrentFrame() != null) {
				defaultContextMenu.getItems().add(new SeparatorMenuItem());
				item = new MenuItem("Adicionar Sprite novo");
				item.setOnAction(e -> {
					defaultContextMenu.hide();
					File file = MyFile.selectFile("Selecione o arquivo de imagem");
					Image image = new Image("file:" + file.getAbsolutePath());
					try {
						String s = Alerts.textPrompt("Prompt", "Coordenada inicial", null, "Informe: X Y W H SpritesPerLine SpriteIndex");
						String[] split = s.split(" ");
						int sx = Integer.parseInt(split[0]), sy = Integer.parseInt(split[1]),
								sw = Integer.parseInt(split[2]), sh = Integer.parseInt(split[3]),
								perLine = Integer.parseInt(split[4]), index = Integer.parseInt(split[5]);
						Rectangle rect = new Rectangle(winW / 2 - sw / 2, winH / 2 - sh / 2, sw, sh);
						getCurrentFrameSet().addSpriteAtEnd(new Sprite(getCurrentFrameSet(), image, new Rectangle(sx, sy, sw, sh), rect, perLine, index));
					}
					catch (Exception ex) {
						Alerts.error("Erro", "O valor informado não é válido");
					}
				});
				defaultContextMenu.getItems().add(item);
				if (!getCurrentFrameSet().isEmptySprites()) {
					item = new MenuItem("Adicionar Sprite clonado");
					item.setOnAction(e -> {
						spriteContextMenu.hide();
						Sprite sprite = getCurrentFrameSet().getSprite(0);
						Sprite sprite2 = new Sprite(sprite);
						sprite2.setX((int)(sprite.getX() + sprite.getOutputWidth() / 3));
						sprite2.setY((int)(sprite.getY() + sprite.getOutputHeight() / 3));
						getCurrentFrameSet().addSpriteAtTop(sprite2);
					});
					defaultContextMenu.getItems().add(item);
				}
			}
		}
	}
	
	private String getCurrentFrameSetName()
		{ return currentEntity.getCurrentFrameSetName(); }

	private FrameSet getCurrentFrameSet()
		{ return currentEntity.getCurrentFrameSet(); }

	private Frame getCurrentFrame()
		{ return getCurrentFrameSet().getCurrentFrame(); }
	
	private void addFrameSet(FrameSet frameSet) {
		String name = Alerts.textPrompt("Prompt", "Adicionar FrameSet", null, "Digite o nome do FrameSet á ser adicionado:");
		if (currentEntity.getFrameSetsNames().contains(name))
			Alerts.error("Erro", "Já existe um FrameSet com esse nome!");
		else {
			String t = Alerts.textPrompt("Prompt", "Definir ticks", null, "Quantos ticks por frame terá o FrameSet \"" + name + "\"?");
			try {
				int ticks = Integer.parseInt(t);
				if (ticks < 1)
					Alerts.error("Erro", "O valor informado é menor que 1");
				else {
					currentEntity.addFrameSet(name, frameSet == null ? new FrameSet(currentEntity, ticks) : getCurrentFrameSet());
					Alerts.information("Info", "FrameSet criado com sucesso!");
				}
			}
			catch (Exception ex)
				{ Alerts.error("Erro", "O valor informado não é um número válido"); ex.printStackTrace(); }
		}
	}

	private void addFrame(int index, Frame frame) {
		getCurrentFrameSet().addFrameAt(index, new Frame(frame));
		Alerts.information("Info", "Frame(s) adcionado(s) com sucesso!");
	}

	private void iterateSelectedSprites(Consumer<Sprite> consumer) {
		for (int n = 0, n2 = selectedSprites.size(); n < n2; n++)
			consumer.accept(selectedSprites.get(n));
	}
	
	private void setSpriteContextMenu() {
		spriteContextMenu = new ContextMenu();
		if (!selectedSprites.isEmpty()) {
			MenuItem itemEditFrameTag = new MenuItem("Editar FrameTag do(s) Sprite(s) selecionado(s)");
			itemEditFrameTag.setOnAction(e -> {
				spriteContextMenu.hide();
				String tag = Alerts.textPrompt("Prompt", "Adicionar FrameTag", null, "Digite a FrameTag á ser adicionada");
				if (tag != null)
					iterateSelectedSprites(sprite -> getCurrentFrameSet().addFrameTagToSpriteFromString(sprite, tag));
			});
			MenuItem itemAddFrameTag = new MenuItem("Adicionar FrameTag ao(s) Sprite(s) selecionado(s)");
			itemAddFrameTag.setOnAction(e -> {
				spriteContextMenu.hide();
				String tag = Alerts.textPrompt("Prompt", "Adicionar FrameTag", null, "Digite a FrameTag á ser adicionada");
				if (tag != null)
					iterateSelectedSprites(sprite -> getCurrentFrameSet().addFrameTagToSpriteFromString(sprite, tag));
			});
			MenuItem itemExcluirSprite = new MenuItem("Excluir Sprite(s) selecionado(s)");
			itemExcluirSprite.setOnAction(e -> {
				spriteContextMenu.hide();
				iterateSelectedSprites(sprite -> getCurrentFrameSet().removeSprite(sprite));
				selectedSprites.clear();
				focusedSprite = null;
			});
			spriteContextMenu.getItems().addAll(itemAddFrameTag, itemExcluirSprite);
		}
	}
	
	private void updateFrameSetTags(KeyCode keyCode, int incX, int incY) {
		if (isNoHolds()) {
			if (keyCode == KeyCode.A)
				currentEntity.setDirection(Direction.LEFT);
			else if (keyCode == KeyCode.W)
				currentEntity.setDirection(Direction.UP);
			else if (keyCode == KeyCode.D)
				currentEntity.setDirection(Direction.RIGHT);
			else if (keyCode == KeyCode.S)
				currentEntity.setDirection(Direction.DOWN);
			else {
				boolean added = false;
				List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFromFirstSprite().getFrameSetTags();
				for (int n = 0; n < list.size(); n++) {
					FrameTag tag = list.get(n);
					if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT || keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
						if (tag instanceof SetEntityPos) {
							tag = new SetEntityPos(((SetEntityPos)tag).getX() + (keyCode == KeyCode.LEFT ? -incX : keyCode == KeyCode.RIGHT ? incX : 0),
																	((SetEntityPos)tag).getY() + (keyCode == KeyCode.UP ? -incY : keyCode == KeyCode.DOWN ? incY : 0));
							list.set(n, tag);
							added = true;
						}
						else if (tag instanceof SetEntityX || tag instanceof SetEntityY || tag instanceof IncEntityPos || tag instanceof IncEntityX || tag instanceof IncEntityY)
							list.remove(n--);
					}
				}
				if (added)
					return;
				if (keyCode == KeyCode.LEFT)
					getCurrentFrame().addFrameTagToFirstSprite(new SetEntityPos((int)currentEntity.getX() - incX, (int)currentEntity.getY()));
				else if (keyCode == KeyCode.RIGHT)
					getCurrentFrame().addFrameTagToFirstSprite(new SetEntityPos((int)currentEntity.getX() + incX, (int)currentEntity.getY()));
				else if (keyCode == KeyCode.UP)
					getCurrentFrame().addFrameTagToFirstSprite(new SetEntityPos((int)currentEntity.getX(), (int)currentEntity.getY() - incY));
				else if (keyCode == KeyCode.DOWN)
					getCurrentFrame().addFrameTagToFirstSprite(new SetEntityPos((int)currentEntity.getX(), (int)currentEntity.getY() + incY));
			}
		}
		else if (isHold(1, 0, 0)) {
			boolean added = false;
			isChangingSprite = false;
			List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFromFirstSprite(getCurrentFrame()).getFrameSetTags();
			for (int n = 0; n < list.size(); n++) {
				FrameTag tag = list.get(n);
				if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT || keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
					if (tag instanceof SetObjPos) {
						tag = new SetObjPos(((SetObjPos)tag).getX() + (keyCode == KeyCode.LEFT ? -incX : keyCode == KeyCode.RIGHT ? incX : 0),
																((SetObjPos)tag).getY() + (keyCode == KeyCode.UP ? -incY : keyCode == KeyCode.DOWN ? incY : 0));
						list.set(n, tag);
						added = true;
					}
					else if (tag instanceof SetObjX || tag instanceof SetObjY || tag instanceof IncObjPos || tag instanceof IncObjX || tag instanceof IncObjY)
						list.remove(n--);
				}
			}
			if (added)
				return;
			if (keyCode == KeyCode.LEFT)
				getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos((int)getCurrentFrameSet().getX() - incX, (int)getCurrentFrameSet().getY()));
			else if (keyCode == KeyCode.RIGHT)
				getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos((int)getCurrentFrameSet().getX() + incX, (int)getCurrentFrameSet().getY()));
			else if (keyCode == KeyCode.UP)
				getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos((int)getCurrentFrameSet().getX(), (int)getCurrentFrameSet().getY() - incY));
			else if (keyCode == KeyCode.DOWN)
				getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos((int)getCurrentFrameSet().getX(), (int)getCurrentFrameSet().getY() + incY));
		}
		else if (isHold(0, 1, 0)) {
			iterateSelectedSprites(sprite -> {
				boolean added = false;
				List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFrom(sprite).getFrameSetTags();
				for (int n = 0; n < list.size(); n++) {
					FrameTag tag = list.get(n);
					if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT || keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
						if (tag instanceof SetOriginSprPos) {
							tag = new SetOriginSprPos(((SetOriginSprPos)tag).getX() + (keyCode == KeyCode.LEFT ? -incX : keyCode == KeyCode.RIGHT ? incX : 0),
																				((SetOriginSprPos)tag).getY() + (keyCode == KeyCode.UP ? -incY : keyCode == KeyCode.DOWN ? incY : 0));
							list.set(n, tag);
							isChangingSprite = true;
							added = true;
						}
						else if (tag instanceof SetOriginSprX || tag instanceof SetOriginSprY || tag instanceof IncOriginSprPos || tag instanceof IncOriginSprX || tag instanceof IncOriginSprY)
							list.remove(n--);
					}
				}
				if (added)
					return;
				if (keyCode == KeyCode.LEFT) {
					isChangingSprite = true;
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprPos(sprite.getOriginSpriteX() - incX, sprite.getOriginSpriteY()));
				}
				else if (keyCode == KeyCode.RIGHT) {
					isChangingSprite = true;
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprPos(sprite.getOriginSpriteX() + incX, sprite.getOriginSpriteY()));
				}
				else if (keyCode == KeyCode.UP) {
					isChangingSprite = true;
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprPos(sprite.getOriginSpriteX(), sprite.getOriginSpriteY() - incY));
				}
				else if (keyCode == KeyCode.DOWN) {
					isChangingSprite = true;
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprPos(sprite.getOriginSpriteX(), sprite.getOriginSpriteY() + incY));
				}
			});
		}
		else if (isHold(0, 1, 1)) {
			iterateSelectedSprites(sprite -> {
				boolean added = false;
				List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFrom(sprite).getFrameSetTags();
				for (int n = 0; n < list.size(); n++) {
					FrameTag tag = list.get(n);
					if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT || keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
						if (tag instanceof SetOriginSprSize) {
							tag = new SetOriginSprSize(((SetOriginSprSize)tag).getWidth() + (keyCode == KeyCode.LEFT ? -incX : keyCode == KeyCode.RIGHT ? incX : 0),
																				((SetOriginSprSize)tag).getHeight() + (keyCode == KeyCode.UP ? -incY : keyCode == KeyCode.DOWN ? incY : 0));
							list.set(n, tag);
							isChangingSprite = true;
							added = true;
						}
						else if (tag instanceof SetOriginSprWidth || tag instanceof SetOriginSprHeight || tag instanceof IncOriginSprSize || tag instanceof IncOriginSprWidth || tag instanceof IncOriginSprHeight)
							list.remove(n--);
					}
				}
				if (added)
					return;
				if (keyCode == KeyCode.LEFT)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprSize((int)sprite.getOriginSpriteWidth() - incX, (int)sprite.getOriginSpriteHeight()));
				else if (keyCode == KeyCode.RIGHT)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprSize((int)sprite.getOriginSpriteWidth() + incX, (int)sprite.getOriginSpriteHeight()));
				else if (keyCode == KeyCode.UP)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprSize((int)sprite.getOriginSpriteWidth(), (int)sprite.getOriginSpriteHeight() - incY));
				else if (keyCode == KeyCode.DOWN)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprSize((int)sprite.getOriginSpriteWidth(), (int)sprite.getOriginSpriteHeight() + incY));
			});
		}
		else if (isHold(1, 1, 0)) {
			iterateSelectedSprites(sprite -> {
				boolean added = false;
				List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFrom(sprite).getFrameSetTags();
				for (int n = 0; n < list.size(); n++) {
					FrameTag tag = list.get(n);
					if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT || keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
						if (tag instanceof SetOutputSprSize) {
							tag = new SetOutputSprSize(((SetOutputSprSize)tag).getWidth() + (keyCode == KeyCode.LEFT ? -incX : keyCode == KeyCode.RIGHT ? incX : 0),
																				((SetOutputSprSize)tag).getHeight() + (keyCode == KeyCode.UP ? -incY : keyCode == KeyCode.DOWN ? incY : 0));
							list.set(n, tag);
							isChangingSprite = true;
							added = true;
						}
						else if (tag instanceof SetOutputSprWidth || tag instanceof SetOutputSprHeight || tag instanceof IncOutputSprSize || tag instanceof IncOutputSprWidth || tag instanceof IncOutputSprHeight)
							list.remove(n--);
					}
				}
				if (added)
					return;
				if (keyCode == KeyCode.LEFT)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprSize((int)sprite.getOutputWidth() - incX, (int)sprite.getOutputHeight()));
				else if (keyCode == KeyCode.RIGHT)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprSize((int)sprite.getOutputWidth() + incX, (int)sprite.getOutputHeight()));
				else if (keyCode == KeyCode.UP)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprSize((int)sprite.getOutputWidth(), (int)sprite.getOutputHeight() - incY));
				else if (keyCode == KeyCode.DOWN)
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprSize((int)sprite.getOutputWidth(), (int)sprite.getOutputHeight() + incY));
			});
		}
	}
	
	private void setKeyboardEvents(Scene scene) {
		scene.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			updateFrameSetTags(e.getCode(), 1, 1);
			if (isNoHolds()) {
				if (e.getCode() == KeyCode.R) {
					currentEntity.restartCurrentFrameSet();
					currentEntity.setPosition(centerX, centerY);
					selectedSprites.clear();
				}
				else {
					List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFromFirstSprite().getFrameSetTags();
					iterateSelectedSprites(sprite -> {
						isChangingSprite = false;
						boolean added = false;
						List<FrameTag> list2 = getCurrentFrameSet().getFrameSetTagsFrom(sprite).getFrameSetTags();
						for (int n = 0; n < list2.size(); n++) {
							FrameTag tag = list2.get(n);
							if (e.getCode() == KeyCode.F && tag instanceof SetSprFlip) {
								tag = new SetSprFlip(sprite.getFlip().getNext());
								list2.set(n, tag);
								added = true;
							}
							else if (e.getCode() == KeyCode.L && tag instanceof SetSprAlign) {
								tag = new SetSprAlign(sprite.getAlignment().getNext());
								list2.set(n, tag);
								added = true;
							}
							else if (tag instanceof DecSprFlip || tag instanceof IncSprFlip || tag instanceof DecSprAlign || tag instanceof IncSprAlign)
								list.remove(n--);
						}
						if (added)
							return;
						if (e.getCode() == KeyCode.F)
							getCurrentFrame().addFrameTagToSprite(sprite, new SetSprFlip(sprite.getFlip().getNext()));
						else if (e.getCode() == KeyCode.L)
							getCurrentFrame().addFrameTagToSprite(sprite, new SetSprAlign(sprite.getAlignment().getNext()));
					});
				}
			}
			else if (isHold(1, 0, 0)) {
				if (e.getCode() == KeyCode.PAGE_UP || e.getCode() == KeyCode.PAGE_DOWN) {
					List<String> list2 = new ArrayList<String>(currentEntity.getFrameSetsNames());
					int i = list2.indexOf(getCurrentFrameSetName());
					if ((e.getCode() == KeyCode.PAGE_UP ? ++i : --i) == list2.size())
						i = 0;
					else if (i == -1)
						i = list2.size() - 1;
					currentEntity.setFrameSet(list2.get(i));
				}
			}
			if (e.getCode() == KeyCode.SPACE)
				isPaused = !isPaused;
			else if (e.getCode() == KeyCode.C) {
				if (!selectedSprites.isEmpty()) {
					if (isCtrlHold() && !isAltHold() && !isShiftHold())
						copiedSprites = new ArrayList<>(selectedSprites);
					else if (isCtrlHold() && !isAltHold() && isShiftHold())
						copiedFrame = getCurrentFrame();
				}
			}
			else if (e.getCode() == KeyCode.V) {
				if (isCtrlHold() && !isAltHold() && !isShiftHold()) {
					for (Sprite sprite : copiedSprites)
						getCurrentFrameSet().addSpriteAtEnd(new Sprite(sprite));
					selectedSprites = new ArrayList<>(copiedSprites);
					saveCtrlZ();
				}
				else if (isCtrlHold() && !isAltHold() && isShiftHold()) {
					Frame frame = new Frame(copiedFrame);
					getCurrentFrameSet().setFrame(getCurrentFrameSet().getFrames().indexOf(copiedFrame), frame);
					copiedFrame = frame;
					saveCtrlZ();
				}
			}
			else if (e.getCode() == KeyCode.Y) {
				if (isCtrlHold() && !isAltHold() && !isShiftHold())
					ctrlY();
			}
			
			else if (e.getCode() == KeyCode.Z) {
				if (isNoHolds() && (zoomScale *= 2) > 10)
					zoomScale = 1;
				else if (isCtrlHold() && !isAltHold() && !isShiftHold())
					ctrlZ();
			}
			else if (e.getCode() == KeyCode.PAGE_DOWN) {
				if (isPaused && getCurrentFrameSet() != null) {
					getCurrentFrameSet().decFrameIndex();
					if (getCurrentFrameSet().getCurrentFrameIndex() < 0)
						getCurrentFrameSet().setCurrentFrameIndex(getCurrentFrameSet().getTotalFrames() - 1);
					getCurrentFrameSet().refreshFramesTags();
				}
			}
			else if (e.getCode() == KeyCode.PAGE_UP) {
				if (isPaused && getCurrentFrameSet() != null) {
					getCurrentFrameSet().incFrameIndex();
					if (getCurrentFrameSet().getCurrentFrameIndex() >= getCurrentFrameSet().getTotalFrames())
						getCurrentFrameSet().setCurrentFrameIndex(0);
					getCurrentFrameSet().refreshFramesTags();
				}
			}
			else if (e.getCode() == KeyCode.ADD) {
				if (!isAltHold()) { // Mover Sprite
					iterateSelectedSprites(sprite -> {
						if (isShiftHold())
							getCurrentFrameSet().moveSpriteToEnd(sprite);
						else
							getCurrentFrameSet().moveSpriteToFront(sprite);
					});
				}
				else { // Mover Frame
					if (isShiftHold())
						getCurrentFrameSet().moveFrameToEnd(getCurrentFrame());
					else
						getCurrentFrameSet().moveFrameToFront(getCurrentFrame());
				}
			}
			else if (e.getCode() == KeyCode.SUBTRACT) {
				if (!isAltHold()) { // Mover Sprite
					iterateSelectedSprites(sprite -> {
						if (isShiftHold())
							getCurrentFrameSet().moveSpriteToStart(sprite);
						else
							getCurrentFrameSet().moveSpriteToBack(sprite);
					});
				}
				else { // Mover Frame
					if (isShiftHold())
						getCurrentFrameSet().moveFrameToStart(getCurrentFrame());
					else
						getCurrentFrameSet().moveFrameToBack(getCurrentFrame());
				}
			}
			else if (e.getCode() == KeyCode.INSERT) { // Adiciona Sprite (Sem ALT) / Frame (Com ALT)
				if (isAltHold()) { // Adiciona Frame
					if (isShiftHold())
						getCurrentFrameSet().addFrameAtStart(new Frame(getCurrentFrame()));
					else if (isCtrlHold())
						getCurrentFrameSet().addFrameAtEnd(new Frame(getCurrentFrame()));
					else
						getCurrentFrameSet().addFrameAt(getCurrentFrameSet().getFrames().indexOf(getCurrentFrame()), new Frame(getCurrentFrame()));
				}
				else
					iterateSelectedSprites(sprite -> { // Adiciona Sprite
						if (isCtrlHold())
							getCurrentFrameSet().addSpriteAtTop(new Sprite(sprite));
						else if (isShiftHold())
							getCurrentFrameSet().addSpriteAtEnd(new Sprite(sprite));
						else
							getCurrentFrameSet().addSpriteAt(getCurrentFrameSet().getSprites().indexOf(selectedSprites.get(0)), new Sprite(sprite));
					});
			}
			else if (e.getCode() == KeyCode.F1)
				openHelpWindow();
			else if (e.getCode() == KeyCode.F3)
				linkEntityToCursor = linkEntityToCursor != 1 ? 1 : 0;
			else if (e.getCode() == KeyCode.F4)
				linkEntityToCursor = linkEntityToCursor != 2 ? 2 : 0;
			else if (e.getCode() == KeyCode.F12) {
				if (++bgType == 3)
					bgType = 0;
				if (bgType == 2)
					SquaredBg.setSquaredBg(3, 3, 50, 255);
			}
			System.out.println("KeyCode: " + e.getCode());
		});		
		scene.setOnKeyReleased(e -> {
			holdedKeys.remove(e.getCode());
			if (!isCtrlHold())
				isChangingSprite = false;
		});		
	}
	
	private void openHelpWindow() {
		if (stageHelpWindow != null) {
			stageHelpWindow.close();
			stageHelpWindow = null;
			return;
		}
		String[] str = {
				"F3|FAZ A ENTITY SEGUIR O CURSOR CAMINHANDO",
				"F4|FAZ A ENTITY FICAR COLADA NO CURSOR",
				"F|FLIP (SPRITE(S) SELECIONADO(S))",
				"L|ALINHAMENTO (SPRITE(S) SELECIONADO(S))",
				"SETAS|MOVER SPRITE(S) SELECIONADO(S)",
				"SHIFT SETAS|MOVER FRAMESET ATUAL",
				"CTRL SHIFT SETAS|ALTERAR TAMANHO DE SAIDA DO(S) SPRITE(S) SELECIONADO(S)",
				"CTRL SETAS|ALTERAR COORDENADAS INICIAIS DO(S) SPRITE(S) SELECIONADO(S)",
				"CTRL ALT SETAS|ALTERAR TAMANHO DO(S) SPRITE(S) SELECIONADO(S)",
				"R|RESETAR TAGS E POSIÇÕES",
				"SPACE|PAUSAR / RESUMIR",
				"PAGE DOWN|FRAME ANTERIOR (SE PAUSADO)",
				"PAGE UP|PRÓXIMO FRAME (SE PAUSADO)",
				"CTRL Z|DESFAZER",
				"CTRL Y|REFAZER",
				"",
				"CTRL C|COPIAR SPRITE SELECIONADO",
				"CTRL V|COLAR SPRITE COPIADO",
				"+|MOVER SPRITE FOCADO PARA CAMADA POSTERIOR",
				"-|MOVER SPRITE FOCADO PARA CAMADA ANTERIOR",
				"SHIFT +|MOVER SPRITE FOCADO PARA PRIMEIRA CAMADA",
				"SHIFT -|MOVER SPRITE FOCADO PARA ÚLTIMA CAMADA",
				"INSERT|ADICIONAR NOVO SPRITE NA PRÓXIMA CAMADA",
				"SHIFT INSERT|ADICIONAR NOVO SPRITE A CAMADA FINAL",
				"CTRL INSERT|ADICIONAR NOVO SPRITE A CANADA INICIAL",
				"",
				"CTRL SHIFT C|COPIAR FRAME ATUAL",
				"CTRL SHIFT V|COLAR FRAME COPIADO",
				"ALT +|MOVER FRAME ATUAL PARA FRENTE",
				"ALT -|MOVER FRAME ATUAL PARA TRÁS",
				"ALT SHIFT +|MOVER FRAME ATUAL O INICIO",
				"ALT SHIFT -|MOVER FRAME ATUAL PARA O FINAL",
				"ALT INSERT|ADICIONAR NOVO FRAME APÓS O FRAME ATUAL",
				"ALT SHIFT INSERT|ADICIONAR NOVO FRAME AO FINAL",
				"ALT CTRL INSERT|ADICIONAR NOVO FRAME AO INICIO"
		};
		int ww, w = 0, h = 20 * str.length + 20;
		for (String s : str) {
			Text text = new Text(s);
			text.setFont(font);
			if ((ww = (int)text.getBoundsInLocal().getWidth() + 110) > w)
				w = ww;
		}
		stageHelpWindow = new Stage();
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		Scene sc = new Scene(new VBox(c));
		sc.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.F1)
				openHelpWindow();
		});
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, w, h);
		gc.setFont(font);
		for (int n = 0; n < str.length; n++)
			if (!str[n].isBlank()) {
				String[] split = str[n].split("\\|");
				gc.setFill(Color.LIGHTGREEN);
				gc.setTextAlign(TextAlignment.RIGHT);
				gc.fillText(split[0], 180, 25 + 20 * n);
				gc.setFill(Color.YELLOW);
				gc.setTextAlign(TextAlignment.LEFT);
				gc.fillText(split[1], 200, 25 + 20 * n);
			}
		stageHelpWindow.setScene(sc);
		stageHelpWindow.showAndWait();				
	}

	private void saveCtrlZ() {
		System.out.println("saveCtrlZ");
		backupFrameSets.add(new FrameSet(getCurrentFrameSet(), currentEntity));
		backupFrameSetsMap.add(currentEntity.getFrameSetsMap());
		backupIndex++;
	}

	private void rollback(int indexInc) {
		if (backupIndex >= 0) {
			if ((backupIndex += indexInc) == -1)
				backupIndex = backupFrameSets.size() - 1;
			else if (backupIndex == backupFrameSets.size())
				backupIndex = 0;
			currentEntity.setFrameSetMap(backupFrameSetsMap.get(backupIndex));
			selectedSprites.clear();
			focusedSprite = null;
		}
	}
	
	private void ctrlZ()
		{ rollback(-1); }

	private void ctrlY()
		{ rollback(1); }

	private String getScrollMode() {
		return isNoHolds() ? "Index" :
					 isHold(1, 0, 0) ? "Alignment" :
					 isHold(0, 1, 0) ? "Alpha" :
					 isHold(0, 0, 1) ? "Rotation" :
					 isHold(1, 1, 0) ? "Flip" :
					 isHold(0, 1, 1) ? "Sprite Size" :
					 isHold(1, 0, 1) ? "Output Size" : "None";
	}

	private String getMoveMode() {
		return isNoHolds() ? "Move Entity Pos" :
					 isHold(1, 0, 0) ? "Move FrameSet Pos" :
					 isHold(0, 1, 0) ? "Move Origin Sprite Pos" :
					 isHold(0, 1, 1) ? "Change Origin Sprite Size" :
					 isHold(1, 1, 0) ? "Change Output Sprite Size" : "None";
	}

	private void setMouseEvents() {
		canvasMain.setOnScroll(e -> {
			int inc = (isShiftHold() ? e.getDeltaX() : e.getDeltaY()) < 0 ? -1 : 1;
			iterateSelectedSprites(sprite -> {
				isChangingSprite = false;
				List<FrameTag> list = getCurrentFrameSet().getFrameSetTagsFrom(sprite).getFrameSetTags();
				for (int n = 0; n < list.size(); n++) {
					FrameTag tag = list.get(n);
					if (isNoHolds() && tag instanceof SetSprIndex) {
						tag = new SetSprIndex(((SetSprIndex)tag).getValue() + inc);
						list.set(n, tag);
						return;
					}
					else if (isHold(0, 0, 1) && tag instanceof SetSprRotate) {
						tag = new SetSprRotate(((SetSprRotate)tag).getValue() + 9 * inc);
						list.set(n, tag);
						return;
					}
					else if (isHold(0, 1, 0) && tag instanceof SetSprAlpha) {
						tag = new SetSprAlpha(((SetSprAlpha)tag).getValue() + 0.05f * inc);
						list.set(n, tag);
						return;
					}
					else if (isHold(1, 0, 0) && tag instanceof SetSprAlign) {
						tag = (inc == -1 ? new SetSprAlign(sprite.getAlignment().getPreview()) : new SetSprAlign(sprite.getAlignment().getNext()));
						list.set(n, tag);
						return;
					}
					else if (isHold(1, 1, 0) && tag instanceof SetSprFlip) {
						tag = (inc == -1 ? new SetSprFlip(sprite.getFlip().getPreview()) : new SetSprFlip(sprite.getFlip().getNext()));
						list.set(n, tag);
						return;
					}
					else if (isHold(0, 1, 1) && tag instanceof SetOriginSprSize) {
						tag = new SetOriginSprSize(((SetOriginSprSize)tag).getWidth() + inc, ((SetOriginSprSize)tag).getHeight() + inc);
						list.set(n, tag);
						return;
					}
					else if (isHold(1, 0, 1) && tag instanceof SetOutputSprSize) {
						tag = new SetOutputSprSize(((SetOutputSprSize)tag).getWidth() + inc, ((SetOutputSprSize)tag).getHeight() + inc);
						list.set(n, tag);
						return;
					}
				}
				if (isNoHolds())
					getCurrentFrame().addFrameTagToSprite(sprite, new SetSprIndex(inc));
				else if (isHold(0, 0, 1))
					getCurrentFrame().addFrameTagToSprite(sprite, new SetSprRotate(inc * 9));
				else if (isHold(0, 1, 0))
					getCurrentFrame().addFrameTagToSprite(sprite, new SetSprAlpha(inc * 0.05f));
				else if (isHold(1, 0, 0))
					getCurrentFrame().addFrameTagToSprite(sprite, (inc == -1 ? new SetSprAlign(sprite.getAlignment().getPreview()) : new SetSprAlign(sprite.getAlignment().getNext())));
				else if (isHold(1, 1, 0))
					getCurrentFrame().addFrameTagToSprite(sprite, (inc == -1 ? new SetSprFlip(sprite.getFlip().getPreview()) : new SetSprFlip(sprite.getFlip().getNext())));
				else if (isHold(0, 1, 1))
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprSize(inc, inc));
				else if (isHold(1, 0, 1))
					getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprSize(inc, inc));
			});
		});
		canvasMain.setOnMousePressed(e -> {
			defaultContextMenu.hide();
			spriteContextMenu.hide();
			if (e.getButton() == MouseButton.SECONDARY) {
				if (focusedSprite != null) {
					setSpriteContextMenu();
					spriteContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
				}
				else {
					setDefaultContextMenu();
					defaultContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
				}
			}
			else if (e.getButton() == MouseButton.PRIMARY) {
				if (!isCtrlHold() || focusedSprite == null)
					selectedSprites.clear();
				if (focusedSprite != null && !selectedSprites.contains(focusedSprite))
					selectedSprites.add(focusedSprite);
			}
			dragX = (int)e.getX() / zoom;
			dragY = (int)e.getY() / zoom;
			deltaSprites.clear();
			for (Sprite sprite : selectedSprites)
				deltaSprites.add(new Sprite(sprite));
		});
		canvasMain.setOnMouseMoved(e -> {
			mouseX = (int)e.getX() / zoom;
			mouseY = (int)e.getY() / zoom;
		});
		canvasMain.setOnMouseDragged(e -> {
			int oldX = mouseX, oldY = mouseY;
			mouseX = (int)e.getX() / zoom;
			mouseY = (int)e.getY() / zoom;
			if (e.getButton() == MouseButton.PRIMARY) {
				updateFrameSetTags(oldX < mouseX ? KeyCode.RIGHT : KeyCode.LEFT, Math.abs(mouseX - dragX), 0);
				updateFrameSetTags(oldY < mouseY ? KeyCode.DOWN : KeyCode.UP, Math.abs(mouseY - dragY), 0);
			}
		});
		canvasMain.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY)
				saveCtrlZ();
		});
	}
	
	public void drawDrawCanvas() {
		gcDraw.setFill(bgType == 0 ? Color.valueOf("#00FF00") : Color.BLACK);
		gcDraw.fillRect(0, 0, winW, winH);
		if (bgType == 2)
			SquaredBg.draw(gcDraw);
		else if (bgType == 1)
			mapSet.draw(gcDraw);
		if (linkEntityToCursor > 0) {
			if (linkEntityToCursor == 2)
				currentEntity.setPosition(mouseX, mouseY);
			else {
				boolean move = true;
				if (currentEntity.isPerfectTileCentred()) {
					int mx = mouseX / tileSize, my = mouseY / tileSize;
					if (mx > currentEntity.getTileX())
						currentEntity.setDirection(Direction.RIGHT);
					else if (mx < currentEntity.getTileX())
						currentEntity.setDirection(Direction.LEFT);
					else if (my > currentEntity.getTileY())
						currentEntity.setDirection(Direction.DOWN);
					else if (my < currentEntity.getTileY())
						currentEntity.setDirection(Direction.UP);
					else
						move = false;
				}
				if (move)
					currentEntity.incPositionByDirection(currentEntity.getDirection());
			}
		}
		entities.sort((e1, e2) -> e1.getCurrentFrameSet().getMaxY() - e2.getCurrentFrameSet().getMaxY());
		entities.forEach(e -> e.run(gcDraw, isPaused));
		if (isChangingSprite) {
			Sprite sprite = selectedSprites.get(0);
			int x = (int)sprite.getOutputDrawCoords().getX() * zoom,
					y = (int)sprite.getOutputDrawCoords().getY() * zoom;
			gcDraw.drawImage(sprite.getSpriteSource(),
					sprite.getOriginSpriteX() - 160, sprite.getOriginSpriteY() - 120,
					480, 360, x - 160, y - 120, 480, 360);
		}
		if (currentEntity != null)
			currentEntity.run(gcDraw, isPaused);
		gcDraw.setGlobalAlpha(1);
		gcDraw.setLineWidth(1);
		gcDraw.setStroke(Color.WHITE);
		gcDraw.strokeRect(centerX, centerY, tileSize, tileSize);
		if (getCurrentFrame() == null)
			currentEntity.restartCurrentFrameSet();
	}
	
	public void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
    gcMain.drawImage(canvasDraw.snapshot(null, null), 0, 0, winW, winH, 0, 0, winW * zoom, winH * zoom);
		if (currentEntity.getTotalFrameSets() > 0 && !currentEntity.getFrameSet(getCurrentFrameSetName()).isEmptyFrames() && getCurrentFrame() != null) {
			focusedSprite = null;
			Sprite focused = null;
			int max = 0;
			for (Sprite sprite : getCurrentFrameSet().getSprites()) {
				int x = (int)sprite.getOutputDrawCoords().getX() * zoom,
						y = (int)sprite.getOutputDrawCoords().getY() * zoom;
				if (sprite.getMaxOutputSpriteY() > max &&
						mouseX * zoom >= x && mouseY * zoom >= y &&
						mouseX * zoom <= x + sprite.getOutputWidth() * zoom &&
						mouseY * zoom <= y + sprite.getOutputHeight() * zoom) {
							focused = sprite;
							max = sprite.getMaxOutputSpriteY();
				}
			}
			if (focused != null) {
				int x = (int)focused.getOutputDrawCoords().getX() * zoom,
						y = (int)focused.getOutputDrawCoords().getY() * zoom;
				focusedSprite = focused;
				gcMain.setFill(Color.LIGHTBLUE);
				gcMain.setStroke(Color.BLACK);
				gcMain.setFont(font);
				gcMain.setLineWidth(3);
				int n = 6;
				String str = 
						"Index: " + focused.getSpriteIndex() + 
						"\nSprite Coords: " + (int)focused.getAbsoluteX() + "," + (int)focused.getAbsoluteY() + " " + 
																	(int)focused.getOriginSpriteWidth() + "x" + (int)focused.getOriginSpriteHeight() +
						"\nOutput Size: " + (int)focused.getOutputWidth() + "x" + (int)focused.getOutputHeight() +
						"\nFlip: " + focused.getFlip().name() +
						"\nRotate: " + focused.getRotation() +
						"\nAlpha: " + focused.getAlpha() +
						"\nAlignment: " + focused.getAlignment().name();
				if (getCurrentFrameSet().getFrameSetTagsFrom(focused) != null &&
						!getCurrentFrameSet().getFrameSetTagsFrom(focused).getFrameSetTags().isEmpty()) {
					str += "\n\nFrameTags:";
					n += 3;
				}
				for (FrameTag tag : getCurrentFrameSet().getFrameSetTagsFrom(focused).getFrameSetTags()) {
					str += "\n" + tag.toString();
					n += 2;
				}
				gcMain.strokeText(str, x, y - 60 - 7.5 * n);
				gcMain.fillText(str, x, y - 60 - 7.5 * n);
			}
		}
		if (GameMisc.blink()) { 
			if (focusedSprite != null) {
				int x = (int)focusedSprite.getOutputDrawCoords().getX() * zoom,
						y = (int)focusedSprite.getOutputDrawCoords().getY() * zoom;
				gcMain.setStroke(Color.YELLOW);
				gcMain.setLineWidth(2);
				gcMain.strokeRect(x, y, focusedSprite.getOutputWidth() * zoom, focusedSprite.getOutputHeight() * zoom);
			}
			for (Sprite sprite : selectedSprites) {
				int x = (int)sprite.getOutputDrawCoords().getX() * zoom,
						y = (int)sprite.getOutputDrawCoords().getY() * zoom,
						w = (int)(sprite.getOutputWidth() * zoom),
						h = (int)(sprite.getOutputHeight() * zoom);
				gcMain.setStroke(Color.GREEN);
				gcMain.setLineWidth(2);
				gcMain.strokeRect(x, y, w, h);
			}
		}
		if (zoomScale > 1)
	    gcMain.drawImage(canvasDraw.snapshot(null, null),
	    		mouseX - winW / 2 / zoomScale,
	    		mouseY - winH / 2 / zoomScale,
	    		winW / zoomScale, winH / zoomScale,
	    		0, 0, winW * zoom, winH * zoom);
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

	public void close() {
	}

}