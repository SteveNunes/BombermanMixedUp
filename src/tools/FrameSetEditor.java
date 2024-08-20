package tools;
	
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import application.Main;
import frameset.Frame;
import frameset.FrameSet;
import frameset.Sprite;
import frameset_tags.FrameTag;
import frameset_tags.SetObjPos;
import frameset_tags.SetOriginSprHeight;
import frameset_tags.SetOriginSprSize;
import frameset_tags.SetOriginSprWidth;
import frameset_tags.SetOriginSprX;
import frameset_tags.SetOriginSprY;
import frameset_tags.SetOutputSprHeight;
import frameset_tags.SetOutputSprPos;
import frameset_tags.SetOutputSprSize;
import frameset_tags.SetOutputSprWidth;
import frameset_tags.SetSprAlign;
import frameset_tags.SetSprAlpha;
import frameset_tags.SetSprFlip;
import frameset_tags.SetSprIndex;
import frameset_tags.SetSprRotate;
import gui.util.Alerts;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import util.IniFile;
import util.MyFile;


public abstract class FrameSetEditor {
	
	private static IniFile iniFile = IniFile.getNewIniFileInstance("./config.ini");
	private static List<KeyCode> holdedKeys;
	private static Map<String, FrameSet> frameSets;
	private static List<FrameSet> backupFrameSets;
	private static List<Map<String, FrameSet>> backupFrameSetsMap;
	private static String currentFrameSetName;
	private static FrameSet currentFrameSet;
	private static FrameSet resetedFrameSet;
	private static Frame currentFrame;
	private static Frame copiedFrame;
	private static Sprite focusedSprite;
	private static List<Sprite> deltaSprites;
	private static List<Sprite> selectedSprites;
	private static List<Sprite> copiedSprites;
	private static ContextMenu defaultContextMenu;
	private static ContextMenu spriteContextMenu;
	static boolean isPaused;
	private static boolean isChangingSprite;
	private static int zoomScale;
	private static int mouseX = 0;
	private static int mouseY = 0;
	private static int dragX = 0;
	private static int dragY = 0;
	private static int oldX = 0;
	private static int oldY = 0;
	private static int backupIndex;
	
	public static void start(Scene scene) {
		frameSets = new LinkedHashMap<>();
		holdedKeys = new ArrayList<>();
		backupFrameSets = new ArrayList<>();
		backupFrameSetsMap = new ArrayList<>();
		deltaSprites = new ArrayList<>();
		selectedSprites = new ArrayList<>();
		copiedSprites = new ArrayList<>();
		currentFrameSetName = null;
		currentFrameSet = null;
		currentFrame = null;
		focusedSprite = null;
		copiedFrame = null;
		isPaused = false;
		isChangingSprite = false;
		zoomScale = 1;
		backupIndex = -1;
		
		currentFrameSetName = "BossMoedão";
		currentFrameSet = new FrameSet(Main.gcDraw, 4, Main.winW / 2, Main.winH / 2);
		frameSets.put(currentFrameSetName, currentFrameSet);

		String tags = iniFile.read("FRAMESET_EDITOR", "FrameSet");
		
		tags = 
				"{SetSprSource;MainSprites;0;456;20;45;0;0;0;0;20;45},{SetSprIndex;0},{SetOutputSprPos;-20;0},{SetSprFlip;NONE},,{SetSprSource;MainSprites;0;456;20;45;0;0;0;0;20;45},{SetSprIndex;1},{SetOutputSprPos;0;0},{SetSprFlip;NONE},,{SetSprSource;MainSprites;0;456;20;45;0;0;0;0;20;45},{SetSprIndex;2},{SetOutputSprPos;20;0},{SetSprFlip;NONE},,{SetSprSource;MainSprites;0;456;20;45;0;0;0;0;20;45},{SetSprIndex;-1},{SetSprFlip;NONE}|" +
				"{SetSprIndex;3},{SetOutputSprPos;-10;0},,{SetSprIndex;4},{SetOutputSprPos;10;0},,{SetSprIndex;-1}|" +
				"{SetSprIndex;5},,{SetSprIndex;6}|" +
				"{SetSprIndex;7},,{SetSprIndex;8}|" +
				"{SetSprIndex;9},{SetOutputSprPos;0;0},,{SetSprIndex;-1},,{SetSprIndex;-1}|" +
				"{SetSprIndex;10},{SetOutputSprPos;-10;0},,{SetSprIndex;11},{SetOutputSprPos;10;0}|" +
				"{SetSprIndex;12},,{SetSprIndex;13}|" +
				"{SetSprIndex;14},,{SetSprIndex;15}|" +
				"{SetSprIndex;16},{SetOutputSprPos;-20;0},,{SetSprIndex;17},{SetOutputSprPos;0;0},,{SetSprIndex;17},{SetOutputSprPos;0;0},{SetSprFlip;HORIZONTAL},,{SetSprIndex;16},{SetOutputSprPos;20;0},{SetSprFlip;HORIZONTAL}|" +
				"{SetSprIndex;15},{SetOutputSprPos;-10;0},{SetSprFlip;HORIZONTAL},,{SetSprIndex;14},{SetOutputSprPos;10;0},{SetSprFlip;HORIZONTAL},,{SetSprIndex;-1},,{SetSprIndex;-1}|" +
				"{SetSprIndex;13},,{SetSprIndex;12}|" +
				"{SetSprIndex;11},,{SetSprIndex;10}|" +
				"{SetSprIndex;-1},,{SetSprIndex;9},{SetOutputSprPos;0;0}|" +
				"{SetSprIndex;8},{SetOutputSprPos;-10;0},,{SetSprIndex;7},{SetOutputSprPos;10;0}|" +
				"{SetSprIndex;6},,{SetSprIndex;5}|" +
				"{SetSprIndex;4},,{SetSprIndex;3},,{SetSprIndex;-1}|" +
				"{Goto;0;1}";

		currentFrameSet.loadFromString(tags);
		resetedFrameSet = new FrameSet(currentFrameSet);
		setDefaultContextMenu();
		setSpriteContextMenu();
		setMouseEvents(scene);
		setKeyboardEvents(scene);
	}
	
	private static void setDefaultContextMenu() {
		defaultContextMenu = new ContextMenu();
		Menu menu = new Menu("Adicionar FrameSet");
		MenuItem item1 = new MenuItem("FrameSet em branco");
		MenuItem item2 = new MenuItem("Cópia do FrameSet atual");
		item1.setOnAction(e -> addFrameSet(null));
		item2.setOnAction(e -> addFrameSet(currentFrameSet));
		menu.getItems().addAll(item1, item2);
		defaultContextMenu.getItems().add(menu);
		menu = new Menu("Remover FrameSet");
		for (String frameSet : frameSets.keySet()) {
			MenuItem i = new MenuItem(frameSet);
			i.setOnAction(e -> {
				if (Alerts.confirmation("Excluir FrameSet", "Deseja mesmo excluir o FrameSet \"" + frameSet + "\"?")) {
					frameSets.remove(frameSet);
					Alerts.information("Info", "FrameSet excluido com sucesso!");
					currentFrameSetName = frameSets.isEmpty() ? null : frameSets.keySet().iterator().next();
					currentFrameSet = frameSets.isEmpty() ? null : frameSets.get(currentFrameSetName);
				}
			});
			menu.getItems().add(i);
		}
		menu.setDisable(frameSets.isEmpty());
		defaultContextMenu.getItems().add(menu);
		if (!frameSets.isEmpty()) {
			defaultContextMenu.getItems().add(new SeparatorMenuItem());
			menu = new Menu("Adicionar Frame ao FrameSet atual");
			Menu menu1 = new Menu("Frame em branco");
			MenuItem item11 = new MenuItem("No inicio do FrameSet");
			MenuItem item12 = new MenuItem("Após o Frame atual");
			MenuItem item13 = new MenuItem("No final do FrameSet");
			item11.setOnAction(e -> addFrame(0, null));
			item12.setOnAction(e -> addFrame(currentFrameSet.getCurrentFrameIndex(), null));
			item13.setOnAction(e -> addFrame(currentFrameSet.getTotalFrames(), null));
			menu1.getItems().addAll(item11, item12, item13);
			Menu menu2 = new Menu("Cópia do frame atual");
			MenuItem item21 = new MenuItem("No inicio do FrameSet");
			MenuItem item22 = new MenuItem("Após o Frame atual");
			MenuItem item23 = new MenuItem("No final do FrameSet");
			item21.setOnAction(e -> addFrame(0, currentFrame));
			item22.setOnAction(e -> addFrame(currentFrameSet.getCurrentFrameIndex(), currentFrame));
			item23.setOnAction(e -> addFrame(currentFrameSet.getTotalFrames(), currentFrame));
			menu2.getItems().addAll(item21, item22, item23);
			menu.getItems().addAll(menu1, menu2);
			defaultContextMenu.getItems().add(menu);
			MenuItem item = new MenuItem("Remover Frame atual");
			item.setOnAction(e -> { 
				currentFrameSet.removeFrame(currentFrame);
				currentFrame = null;
			});
			item.setDisable(currentFrameSet.isEmptyFrames());
			defaultContextMenu.getItems().add(item);
			if (currentFrame != null) {
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
						Rectangle rect = new Rectangle(Main.winW / 2 - sw / 2, Main.winH / 2 - sh / 2, sw, sh);
						currentFrameSet.addSpriteAtEnd(new Sprite(currentFrameSet, image, new Rectangle(sx, sy, sw, sh), rect, perLine, index));
					}
					catch (Exception ex) {
						Alerts.error("Erro", "O valor informado não é válido");
					}
				});
				defaultContextMenu.getItems().add(item);
				if (!currentFrameSet.isEmptySprites()) {
					item = new MenuItem("Adicionar Sprite clonado");
					item.setOnAction(e -> {
						spriteContextMenu.hide();
						Sprite sprite = currentFrameSet.getSprite(0);
						Sprite sprite2 = new Sprite(sprite);
						sprite2.setX((int)(sprite.getX() + sprite.getOutputWidth() / 3));
						sprite2.setY((int)(sprite.getY() + sprite.getOutputHeight() / 3));
						currentFrameSet.addSpriteAtTop(sprite2);
					});
					defaultContextMenu.getItems().add(item);
				}
			}
		}
	}

	private static void addFrameSet(FrameSet frameSet) {
		String name = Alerts.textPrompt("Prompt", "Adicionar FrameSet", null, "Digite o nome do FrameSet á ser adicionado:");
		if (frameSets.containsKey(name))
			Alerts.error("Erro", "Já existe um FrameSet com esse nome!");
		else {
			String t = Alerts.textPrompt("Prompt", "Definir ticks", null, "Quantos ticks por frame terá o FrameSet \"" + name + "\"?");
			try {
				int ticks = Integer.parseInt(t);
				if (ticks < 1)
					Alerts.error("Erro", "O valor informado é menor que 1");
				else {
					frameSets.put(currentFrameSetName = name, currentFrameSet = frameSet == null ? new FrameSet(Main.gcDraw, ticks) : currentFrameSet);
					Alerts.information("Info", "FrameSet criado com sucesso!");
				}
			}
			catch (Exception ex)
				{ Alerts.error("Erro", "O valor informado não é um número válido"); ex.printStackTrace(); }
		}
	}

	private static void addFrame(int index, Frame frame) {
		currentFrameSet.addFrameAt(index, new Frame(frame));
		Alerts.information("Info", "Frame(s) adcionado(s) com sucesso!");
	}

	private static void iterateSelectedSprites(Consumer<Sprite> consumer) {
		for (int n = 0, n2 = selectedSprites.size(); n < n2; n++)
			consumer.accept(selectedSprites.get(n));
	}
	
	private static void setSpriteContextMenu() {
		spriteContextMenu = new ContextMenu();
		MenuItem item = new MenuItem("Excluir Sprite(s) selecionado(s)");
		item.setOnAction(e -> {
			spriteContextMenu.hide();
			iterateSelectedSprites(sprite -> currentFrameSet.removeSprite(sprite));
			selectedSprites.clear();
			focusedSprite = null;
		});
		spriteContextMenu.getItems().add(item);
	}
	
	private static void setKeyboardEvents(Scene scene) {
		scene.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			if (isNoHolds()) {
				if (e.getCode() == KeyCode.R) {
					System.out.println("RESET ALL");
					currentFrameSet = new FrameSet(resetedFrameSet);
					frameSets.put(currentFrameSetName, currentFrameSet);
					selectedSprites.clear();
					currentFrame = null;
					currentFrameSet.setPosition(Main.winW / 2, Main.winH / 2);
				}
				else 
					iterateSelectedSprites(sprite -> {
						isChangingSprite = false;
						List<FrameTag> list = currentFrameSet.getFrameSetTagsFrom(sprite).getFrameSetTags();
						for (int n = 0; n < list.size(); n++) {
							FrameTag tag = list.get(n);
							if ((e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) && tag instanceof SetOutputSprPos) {
								tag = new SetOutputSprPos(((SetOutputSprPos)tag).getX() + (e.getCode() == KeyCode.LEFT ? -1 : e.getCode() == KeyCode.RIGHT ? 1 : 0),
																		((SetOutputSprPos)tag).getY() + (e.getCode() == KeyCode.UP ? -1 : e.getCode() == KeyCode.DOWN ? 1 : 0));
								list.set(n, tag);
								return;
							}
							else if (e.getCode() == KeyCode.F && tag instanceof SetSprFlip) {
								tag = new SetSprFlip(sprite.getFlip().getNext());
								list.set(n, tag);
								return;
							}
							else if (e.getCode() == KeyCode.L && tag instanceof SetSprAlign) {
								tag = new SetSprAlign(sprite.getAlignment().getNext());
								list.set(n, tag);
								return;
							}
						}
						if (e.getCode() == KeyCode.LEFT)
							currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprPos(-1, 0));
						else if (e.getCode() == KeyCode.RIGHT)
							currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprPos(1, 0));
						else if (e.getCode() == KeyCode.UP)
							currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprPos(0, -1));
						else if (e.getCode() == KeyCode.DOWN)
							currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprPos(0, 1));
						else if (e.getCode() == KeyCode.F)
							currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetSprFlip(sprite.getFlip().getNext()));
						else if (e.getCode() == KeyCode.L)
							currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetSprAlign(sprite.getAlignment().getNext()));
				});
			}
			else if (isHold(1, 0, 0)) {
				isChangingSprite = false;
				List<FrameTag> list = currentFrameSet.getFrameSetTagsFromFirstSprite(currentFrame).getFrameSetTags();
				for (int n = 0; n < list.size(); n++) {
					FrameTag tag = list.get(n);
					if ((e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) && tag instanceof SetObjPos) {
						tag = new SetObjPos(((SetObjPos)tag).getX() + (e.getCode() == KeyCode.LEFT ? -1 : e.getCode() == KeyCode.RIGHT ? 1 : 0),
																((SetObjPos)tag).getY() + (e.getCode() == KeyCode.UP ? -1 : e.getCode() == KeyCode.DOWN ? 1 : 0));
						list.set(n, tag);
						return;
					}
				}
				if (e.getCode() == KeyCode.LEFT)
					currentFrameSet.getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos(-1, 0));
				else if (e.getCode() == KeyCode.RIGHT)
					currentFrameSet.getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos(1, 0));
				else if (e.getCode() == KeyCode.UP)
					currentFrameSet.getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos(0, -1));
				else if (e.getCode() == KeyCode.DOWN)
					currentFrameSet.getCurrentFrame().addFrameTagToFirstSprite(new SetObjPos(0, 1));
			}
			else if (isHold(0, 1, 0)) {
				iterateSelectedSprites(sprite -> {
					List<FrameTag> list = currentFrameSet.getFrameSetTagsFrom(sprite).getFrameSetTags();
					for (int n = 0; n < list.size(); n++) {
						FrameTag tag = list.get(n);
						if ((e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) && tag instanceof SetOriginSprX) {
							tag = new SetOriginSprX(((SetOriginSprX)tag).getValue() + (e.getCode() == KeyCode.LEFT ? -1 : e.getCode() == KeyCode.RIGHT ? 1 : 0));
							list.set(n, tag);
							isChangingSprite = true;
							return;
						}
						else if ((e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) && tag instanceof SetOriginSprY) {
							tag = new SetOriginSprY(((SetOriginSprY)tag).getValue() + (e.getCode() == KeyCode.UP ? -1 : e.getCode() == KeyCode.DOWN ? 1 : 0));
							list.set(n, tag);
							isChangingSprite = true;
							return;
						}
					}
					if (e.getCode() == KeyCode.LEFT) {
						isChangingSprite = true;
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprX(-1));
					}
					else if (e.getCode() == KeyCode.RIGHT) {
						isChangingSprite = true;
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprX(1));
					}
					else if (e.getCode() == KeyCode.UP) {
						isChangingSprite = true;
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprY(-1));
					}
					else if (e.getCode() == KeyCode.DOWN) {
						isChangingSprite = true;
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprY(1));
					}
				});
			}
			else if (isHold(0, 1, 1)) {
				iterateSelectedSprites(sprite -> {
					isChangingSprite = false;
					List<FrameTag> list = currentFrameSet.getFrameSetTagsFrom(sprite).getFrameSetTags();
					for (int n = 0; n < list.size(); n++) {
						FrameTag tag = list.get(n);
						if ((e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) && tag instanceof SetOriginSprWidth) {
							tag = new SetOriginSprWidth(((SetOriginSprWidth)tag).getValue() + (e.getCode() == KeyCode.LEFT ? -1 : e.getCode() == KeyCode.RIGHT ? 1 : 0));
							list.set(n, tag);
							return;
						}
						else if ((e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) && tag instanceof SetOriginSprHeight) {
							tag = new SetOriginSprHeight(((SetOriginSprHeight)tag).getValue() + (e.getCode() == KeyCode.UP ? -1 : e.getCode() == KeyCode.DOWN ? 1 : 0));
							list.set(n, tag);
							return;
						}
					}
					if (e.getCode() == KeyCode.LEFT)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprWidth(-1));
					else if (e.getCode() == KeyCode.RIGHT)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprWidth(1));
					else if (e.getCode() == KeyCode.UP)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprHeight(-1));
					else if (e.getCode() == KeyCode.DOWN)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprHeight(1));
				});
			}
			else if (isHold(1, 1, 0)) {
				iterateSelectedSprites(sprite -> {
					isChangingSprite = false;
					List<FrameTag> list = currentFrameSet.getFrameSetTagsFrom(sprite).getFrameSetTags();
					for (int n = 0; n < list.size(); n++) {
						FrameTag tag = list.get(n);
						if ((e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) && tag instanceof SetOutputSprWidth) {
							tag = new SetOutputSprWidth(((SetOutputSprWidth)tag).getValue() + (e.getCode() == KeyCode.LEFT ? -1 : e.getCode() == KeyCode.RIGHT ? 1 : 0));
							list.set(n, tag);
							return;
						}
						else if ((e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) && tag instanceof SetOutputSprHeight) {
							tag = new SetOutputSprHeight(((SetOutputSprHeight)tag).getValue() + (e.getCode() == KeyCode.UP ? -1 : e.getCode() == KeyCode.DOWN ? 1 : 0));
							list.set(n, tag);
							return;
						}
					}
					if (e.getCode() == KeyCode.LEFT)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprWidth(-1));
					else if (e.getCode() == KeyCode.RIGHT)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprWidth(1));
					else if (e.getCode() == KeyCode.UP)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprHeight(-1));
					else if (e.getCode() == KeyCode.DOWN)
						currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprHeight(1));
				});
			}
			if (e.getCode() == KeyCode.SPACE)
				isPaused = !isPaused;
			else if (e.getCode() == KeyCode.C) {
				if (!selectedSprites.isEmpty()) {
					if (isCtrlHold() && !isAltHold() && !isShiftHold())
						copiedSprites = new ArrayList<>(selectedSprites);
					else if (isCtrlHold() && !isAltHold() && isShiftHold())
						copiedFrame = currentFrame;
				}
			}
			else if (e.getCode() == KeyCode.V) {
				if (isCtrlHold() && !isAltHold() && !isShiftHold()) {
					for (Sprite sprite : copiedSprites)
						currentFrameSet.addSpriteAtEnd(new Sprite(sprite));
					selectedSprites = new ArrayList<>(copiedSprites);
					saveCtrlZ();
				}
				else if (isCtrlHold() && !isAltHold() && isShiftHold()) {
					Frame frame = new Frame(copiedFrame);
					currentFrameSet.setFrame(currentFrameSet.getFrames().indexOf(copiedFrame), frame);
					currentFrame = frame;
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
				if (isPaused && currentFrameSet != null)
					currentFrameSet.decFrameIndex();
			}
			else if (e.getCode() == KeyCode.PAGE_UP) {
				if (isPaused && currentFrameSet != null)
					currentFrameSet.incFrameIndex();
			}
			else if (e.getCode() == KeyCode.ADD) {
				if (!isAltHold()) { // Mover Sprite
					iterateSelectedSprites(sprite -> {
						if (isShiftHold())
							currentFrameSet.moveSpriteToEnd(sprite);
						else
							currentFrameSet.moveSpriteToFront(sprite);
					});
				}
				else { // Mover Frame
					if (isShiftHold())
						currentFrameSet.moveFrameToEnd(currentFrame);
					else
						currentFrameSet.moveFrameToFront(currentFrame);
				}
			}
			else if (e.getCode() == KeyCode.SUBTRACT) {
				if (!isAltHold()) { // Mover Sprite
					iterateSelectedSprites(sprite -> {
						if (isShiftHold())
							currentFrameSet.moveSpriteToStart(sprite);
						else
							currentFrameSet.moveSpriteToBack(sprite);
					});
				}
				else { // Mover Frame
					if (isShiftHold())
						currentFrameSet.moveFrameToStart(currentFrame);
					else
						currentFrameSet.moveFrameToBack(currentFrame);
				}
			}
			else if (e.getCode() == KeyCode.INSERT) { // Adiciona Sprite (Sem ALT) / Frame (Com ALT)
				if (isAltHold()) { // Adiciona Frame
					if (isShiftHold())
						currentFrameSet.addFrameAtStart(new Frame(currentFrame));
					else if (isCtrlHold())
						currentFrameSet.addFrameAtEnd(new Frame(currentFrame));
					else
						currentFrameSet.addFrameAt(currentFrameSet.getFrames().indexOf(currentFrame), new Frame(currentFrame));
				}
				else
					iterateSelectedSprites(sprite -> { // Adiciona Sprite
						if (isCtrlHold())
							currentFrameSet.addSpriteAtTop(new Sprite(sprite));
						else if (isShiftHold())
							currentFrameSet.addSpriteAtEnd(new Sprite(sprite));
						else
							currentFrameSet.addSpriteAt(currentFrameSet.getSprites().indexOf(selectedSprites.get(0)), new Sprite(sprite));
					});
			}
			else if (e.getCode() == KeyCode.F1) {
				int w = 760, h = 700;
				Stage st = new Stage();
				Canvas c = new Canvas(w, h);
				GraphicsContext gc = c.getGraphicsContext2D();
				Scene sc = new Scene(new VBox(c));
				gc.setFill(Color.BLACK);
				gc.fillRect(0, 0, w, h);
				String[] str = {
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
				gc.setFont(new Font("Lucida Console", 15));
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
				st.setScene(sc);
				st.showAndWait();				
			}
			System.out.println("KeyCode: " + e.getCode());
		});		
		scene.setOnKeyReleased(e -> {
			holdedKeys.remove(e.getCode());
			if (!isCtrlHold())
				isChangingSprite = false;
		});		
	}
	
	private static void saveCtrlZ() {
		backupFrameSets.add(new FrameSet(currentFrameSet));
		backupFrameSetsMap.add(frameSets);
		backupIndex++;
	}

	private static void rollback(int indexInc) {
		if (backupIndex >= 0) {
			if ((backupIndex += indexInc) == -1)
				backupIndex = backupFrameSets.size() - 1;
			else if (backupIndex == backupFrameSets.size())
				backupIndex = 0;
			currentFrameSet = new FrameSet(backupFrameSets.get(backupIndex));
			frameSets = backupFrameSetsMap.get(backupIndex);
			selectedSprites.clear();
			focusedSprite = null;
			currentFrame = null;
		}
	}
	
	private static void ctrlZ()
		{ rollback(-1); }

	private static void ctrlY()
		{ rollback(1); }

	private static String getScrollMode() {
		return isNoHolds() ? "Index" :
					 isHold(1, 0, 0) ? "Alignment" :
					 isHold(0, 1, 0) ? "Alpha" :
					 isHold(0, 0, 1) ? "Rotation" :
					 isHold(1, 1, 0) ? "Flip" :
					 isHold(0, 1, 1) ? "Sprite Size" :
					 isHold(1, 0, 1) ? "Output Size" : "None";
	}

	private static void setMouseEvents(Scene scene) {
		scene.setOnScroll(e -> {
			int inc = (isShiftHold() ? e.getDeltaX() : e.getDeltaY()) < 0 ? -1 : 1;
			iterateSelectedSprites(sprite -> {
				isChangingSprite = false;
				List<FrameTag> list = currentFrameSet.getFrameSetTagsFrom(sprite).getFrameSetTags();
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
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetSprIndex(inc));
				else if (isHold(0, 0, 1))
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetSprRotate(inc * 9));
				else if (isHold(0, 1, 0))
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetSprAlpha(inc * 0.05f));
				else if (isHold(1, 0, 0))
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, (inc == -1 ? new SetSprAlign(sprite.getAlignment().getPreview()) : new SetSprAlign(sprite.getAlignment().getNext())));
				else if (isHold(1, 1, 0))
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, (inc == -1 ? new SetSprFlip(sprite.getFlip().getPreview()) : new SetSprFlip(sprite.getFlip().getNext())));
				else if (isHold(0, 1, 1))
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOriginSprSize(inc, inc));
				else if (isHold(1, 0, 1))
					currentFrameSet.getCurrentFrame().addFrameTagToSprite(sprite, new SetOutputSprSize(inc, inc));
			});
		});
		scene.setOnMousePressed(e -> {
			defaultContextMenu.hide();
			spriteContextMenu.hide();
			if (e.getButton() == MouseButton.SECONDARY) {
				if (focusedSprite != null) {
					setSpriteContextMenu();
					spriteContextMenu.show(Main.canvasMain, e.getX(), e.getY());
				}
				else {
					setDefaultContextMenu();
					defaultContextMenu.show(Main.canvasMain, e.getX(), e.getY());
				}
			}
			else if (e.getButton() == MouseButton.PRIMARY) {
				if (!isCtrlHold() || focusedSprite == null)
					selectedSprites.clear();
				if (focusedSprite != null && !selectedSprites.contains(focusedSprite))
					selectedSprites.add(focusedSprite);
			}
			dragX = (int)e.getX() / Main.zoom;
			dragY = (int)e.getY() / Main.zoom;
			oldX = (int)currentFrameSet.getX();
			oldY = (int)currentFrameSet.getY();
			deltaSprites.clear();
			for (Sprite sprite : selectedSprites)
				deltaSprites.add(new Sprite(sprite));
		});
		scene.setOnMouseMoved(e -> {
			mouseX = (int)e.getX() / Main.zoom;
			mouseY = (int)e.getY() / Main.zoom;
		});
		scene.setOnMouseDragged(e -> {
			mouseX = (int)e.getX() / Main.zoom;
			mouseY = (int)e.getY() / Main.zoom;
			if (e.getButton() == MouseButton.PRIMARY) {
				if (isHold(1, 0, 0)) {
					currentFrameSet.setX(oldX + (mouseX - dragX));
					currentFrameSet.setY(oldY + (mouseY - dragY));
				}
				else if (isHold(0, 1, 0))
					for (int n = 0, n2 = selectedSprites.size(); n < n2; n++) {
						isChangingSprite = true;
						Sprite sprite = selectedSprites.get(n);
						sprite.setOriginSpriteX(deltaSprites.get(0).getOriginSpriteX() + (mouseX - dragX));
						sprite.setOriginSpriteY(deltaSprites.get(0).getOriginSpriteY() + (mouseY - dragY));
						
					}
				else
					for (int n = 0, n2 = selectedSprites.size(); n < n2; n++) {
						Sprite sprite = selectedSprites.get(n);
						sprite.setX((int)(deltaSprites.get(n).getX() + (mouseX - dragX)));
						sprite.setY((int)(deltaSprites.get(n).getY() + (mouseY - dragY)));
					}
			}
		});
		scene.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY)
				saveCtrlZ();
		});
	}
	
	public static void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
		boolean blink = System.currentTimeMillis() / 50 % 2 == 0;
		if (!frameSets.isEmpty() && !frameSets.get(currentFrameSetName).isEmptyFrames()) {
			currentFrame = frameSets.get(currentFrameSetName).getCurrentFrame();
			if (currentFrame == null)
				return;
			focusedSprite = null;
			Sprite focused = null;
			int max = 0;
			for (Sprite sprite : currentFrameSet.getSprites()) {
				int[] pos = sprite.getOutputDrawCoords();
				int x = pos[0] * Main.zoom, y = pos[1] * Main.zoom;
				if (sprite.getMaxOutputSpriteY() > max &&
						mouseX * Main.zoom >= x && mouseY * Main.zoom >= y &&
						mouseX * Main.zoom <= x + sprite.getOutputWidth() * Main.zoom &&
						mouseY * Main.zoom <= y + sprite.getOutputHeight() * Main.zoom) {
							focused = sprite;
							max = sprite.getMaxOutputSpriteY();
				}
			}
			if (focused != null) {
				int[] pos = focused.getOutputDrawCoords();
				int x = pos[0] * Main.zoom, y = pos[1] * Main.zoom;
				focusedSprite = focused;
				Main.gcMain.setFill(Color.LIGHTBLUE);
				Main.gcMain.setStroke(Color.BLACK);
				Main.gcMain.setFont(new Font("Lucida Console", 15));
				Main.gcMain.setLineWidth(3);
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
				if (currentFrameSet.getFrameSetTagsFrom(focused) != null &&
						!currentFrameSet.getFrameSetTagsFrom(focused).getFrameSetTags().isEmpty()) {
					str += "\n\nFrameTags:";
					n += 3;
				}
				for (FrameTag tag : currentFrameSet.getFrameSetTagsFrom(focused).getFrameSetTags()) {
					str += "\n" + tag.toString();
					n += 2;
				}
				Main.gcMain.strokeText(str, x, y - 60 - 7.5 * n);
				Main.gcMain.fillText(str, x, y - 60 - 7.5 * n);
			}
		}
		if (blink) { 
			if (focusedSprite != null) {
				int[] pos = focusedSprite.getOutputDrawCoords();
				int x = pos[0] * Main.zoom, y = pos[1] * Main.zoom;
				Main.gcMain.setStroke(Color.YELLOW);
				Main.gcMain.setLineWidth(2);
				Main.gcMain.strokeRect(x, y, focusedSprite.getOutputWidth() * Main.zoom, focusedSprite.getOutputHeight() * Main.zoom);
			}
			for (Sprite sprite : selectedSprites) {
				int[] pos = sprite.getOutputDrawCoords();
				int x = pos[0] * Main.zoom, y = pos[1] * Main.zoom,
						w = (int)(sprite.getOutputWidth() * Main.zoom),
						h = (int)(sprite.getOutputHeight() * Main.zoom);
				Main.gcMain.setStroke(Color.GREEN);
				Main.gcMain.setLineWidth(2);
				Main.gcMain.strokeRect(x, y, w, h);
			}
		}
		if (zoomScale > 1)
	    Main.gcMain.drawImage(Main.canvasDraw.snapshot(null, null),
	    		mouseX - Main.winW / 2 / zoomScale,
	    		mouseY - Main.winH / 2 / zoomScale,
	    		Main.winW / zoomScale, Main.winH / zoomScale,
	    		0, 0, Main.winW * Main.zoom, Main.winH * Main.zoom);
	}

	public static void drawDrawCanvas() {
		if (isChangingSprite) {
			Sprite sprite = selectedSprites.get(0);
			int[] pos = sprite.getOutputDrawCoords();
			Main.gcDraw.drawImage(sprite.getSpriteSource(),
					sprite.getOriginSpriteX() - 160, sprite.getOriginSpriteY() - 120,
					480, 360, pos[0] - 160, pos[1] - 120, 480, 360);
		}
		if (!frameSets.isEmpty() && !frameSets.get(currentFrameSetName).isEmptyFrames())
			currentFrameSet.run(isPaused);
		Main.gcDraw.setGlobalAlpha(1);
		Main.gcDraw.setLineWidth(1);
		Main.gcDraw.setStroke(Color.WHITE);
		Main.gcDraw.strokeRect(Main.winW / 2 - Main.tileSize / 2, Main.winH / 2 - Main.tileSize / 2, Main.tileSize, Main.tileSize);
	}

	public static String getTitle() {
		String title = "";
		if (currentFrameSetName != null) {
			title = "Frame Set: " + currentFrameSetName + 
					" | Frames: " + (currentFrameSet.getCurrentFrameIndex() + 1) +
					"/" + currentFrameSet.getTotalFrames() +
					" | Sprites: " + currentFrameSet.getTotalSprites() +
					(isPaused ? " | (Paused)" : " | (Playing)") +
					" | Scroll mode: " + getScrollMode();
		}
		return title;
	}
	
	public static boolean isHold(int shift, int ctrl, int alt) {
		return ((shift == 0 && !isShiftHold()) || (shift == 1 && isShiftHold())) &&
					 ((ctrl == 0 && !isCtrlHold()) || (ctrl == 1 && isCtrlHold())) &&
					 ((alt == 0 && !isAltHold()) || (alt == 1 && isAltHold()));
	}
	
	public static boolean isCtrlHold()
		{ return holdedKeys.contains(KeyCode.CONTROL); }
	
	public static boolean isShiftHold()
		{ return holdedKeys.contains(KeyCode.SHIFT); }

	public static boolean isAltHold()
		{ return holdedKeys.contains(KeyCode.ALT); }
	
	public static boolean isNoHolds()
		{ return !isAltHold() && !isCtrlHold() && !isShiftHold(); }

	public static void close() {
		String fSet = "";
		for (int frameIndex = 0; frameIndex < currentFrameSet.getTotalFrames(); frameIndex++) {
			if (frameIndex > 0)
				fSet += "|";
			for (int spriteIndex = 0; spriteIndex < currentFrameSet.getTotalSprites(); spriteIndex++) {
				boolean added = false;
				for (int tagIndex = 0; tagIndex < currentFrameSet.getFrameSetTagsFrom(frameIndex, spriteIndex).size(); tagIndex++) {
					FrameTag tag = currentFrameSet.getFrameSetTagsFrom(frameIndex, spriteIndex).get(tagIndex);
					fSet += (spriteIndex > 0 && !added ? ",," : "") + (tagIndex > 0 ? "," : "") + tag;
					added= true;
				}
			}
		}
		iniFile.write("FRAMESET_EDITOR", "FrameSet", fSet);
	}

}