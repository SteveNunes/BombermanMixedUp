package gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import application.Main;
import application.TextToSpeechGoogle;
import entities.Bomb;
import entities.BomberMan;
import entities.CpuPlay;
import entities.Entity;
import enums.CpuDificult;
import enums.GameInputMode;
import enums.GoogleLanguages;
import enums.ItemType;
import enums.PassThrough;
import fades.DefaultFade;
import io.github.jwdeveloper.tiktok.TikTokLive;
import io.github.jwdeveloper.tiktok.data.events.gift.TikTokGiftEvent;
import io.github.jwdeveloper.tiktok.data.models.Picture;
import io.github.jwdeveloper.tiktok.live.LiveClient;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
import player.Player;
import tools.Draw;
import tools.GameFonts;
import tools.Materials;
import tools.Sound;
import tools.Tools;
import util.TimerFX;

public class GameTikTok {

	private static List<String> echos = new ArrayList<>();
	public static final int ZOOM = 3;
	private static final int WIN_W = Main.TILE_SIZE * 32;
	private static final int WIN_H = Main.TILE_SIZE * 17;

	@FXML
	private Canvas canvasMain;

	private Map<Integer, Pair<String, Consumer<String>>> likeEvents;
	private Map<Integer, Pair<String, Consumer<String>>> giftEvents;
	private Map<String, Integer> userLikes = new HashMap<>();
	private Map<String, Image> userPics = new HashMap<>();
	private List<Score> scores = new ArrayList<>();
	private Map<String, List<Entity>> userEntityOwner = new HashMap<>();
	private Map<String, List<BomberMan>> userBomberManOwner = new HashMap<>();
	private List<BomberMan> fixedBomberMans = new ArrayList<>();
	private Set<TileCoord> settedEntities = new HashSet<>();
	private Set<TileCoord> droppedWalls = new HashSet<>();
	private LiveClient liveClient;
	private Set<PassThrough> wallTilePassThrough = Set.of(PassThrough.BOMB, PassThrough.BRICK, PassThrough.HOLE, PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER, PassThrough.WATER);
	private Set<PassThrough> itemPassThrough = Set.of(PassThrough.MONSTER, PassThrough.PLAYER);
	private Set<PassThrough> passThrough = Set.of(PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER);
	private List<KeyCode> holdedKeys;
	private int pallete;
	private GraphicsContext gcMain;
	private Font font;
	private boolean showBlockMarks;
	private String gameMap = "TikTok-Small-Battle-01";
	private String liveUserToConnect = "flaviosphgamer";
	private long displayAvatarDelay = 3000;
	private List<TikTokGiftEvent> acumulatedGifts = new ArrayList<>();
	
	private void loadLiveEvents() {
		likeEvents = new HashMap<>();
		likeEvents.put(100, new Pair<>(null, userName -> dropRandomTileBomb(userName)));
		likeEvents.put(500, new Pair<>("@USER entrou na arena", userName -> dropCpu(userName)));
		likeEvents.put(1000, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 10)));
		giftEvents = new HashMap<>();
		// Rosa (Dropa 1 Bomba)
		giftEvents.put(5655, new Pair<>(null, userName -> dropRandomTileBomb(userName)));
		// Mini Dino (Dropa 10 Bombas)
		giftEvents.put(6560, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 10)));
		giftEvents.put(7553, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 10)));
		giftEvents.put(7591, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 10)));
		giftEvents.put(9615, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 10)));
		giftEvents.put(9962, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 10)));
		// Sorvete (Dropa 1 Item)
		giftEvents.put(5827, new Pair<>(null, userName -> dropRandomTileItem(userName)));
		// Coração (Dropa 10 Items)
		giftEvents.put(5327, new Pair<>("Chuva de itens de @USER", userName -> dropRandomTileItem(userName, 10)));
		giftEvents.put(5576, new Pair<>("Chuva de itens de @USER", userName -> dropRandomTileItem(userName, 10)));
		// GG (Dropa 1 Caveira)
		giftEvents.put(5827, new Pair<>(null, userName -> dropRandomTileCurse(userName)));
		// Fantasminha (Dropa 10 Caveiras)
		giftEvents.put(5576, new Pair<>("Chuva de caveiras de @USER", userName -> dropRandomTileCurse(userName, 10)));
		// Coração com os dedos (Adicionar avatar sem itens)
		giftEvents.put(5487, new Pair<>("@USER entrou na arena", userName -> dropCpu(userName)));
		// Bola de futebol (Ativar Hurry up)
		giftEvents.put(5852, new Pair<>("@USER ativou o hurry up", userName -> {
			MapSet.setHurryUpState(true);
		}));
		// Rosquinha (Adiciona avatar com alguns itens)
		giftEvents.put(5879, new Pair<>("Super @USER entrou na arena", userName -> dropCpu(userName,
				ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.FIRE_UP,
				ItemType.FIRE_UP, ItemType.SPEED_UP, ItemType.SPEED_UP, ItemType.PASS_BOMB)));
		// Boné (Adiciona avatar super bombado)
		giftEvents.put(6104, new Pair<>("Supremo @USER entrou na arena", userName -> dropCpu(userName,
				ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP,
				ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP,
				ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.FIRE_UP, 
				ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.SPEED_UP, 
				ItemType.SPEED_UP, ItemType.SPEED_UP, ItemType.SPEED_UP, ItemType.SPEED_UP,
				ItemType.PASS_BOMB, ItemType.PASS_BRICK, ItemType.HEART_UP)));
	}

	public void init() {
		font = new Font("Lucida Console", 9);
		canvasMain.setWidth(WIN_W * ZOOM);
		canvasMain.setHeight(WIN_H * ZOOM);
		Main.setMainCanvas(canvasMain);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		holdedKeys = new ArrayList<>();
		liveClient = null;
		setEvents();
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		//fillWithCpu(3);
		fixedBomberMans.add(BomberMan.getBomberMan(0));
		MapSet.setConsumerWhenMapLoads(mapName -> {
			MapSet.setStageTimeLimitInSecs(180);
		});
		MapSet.setConsumerWhenStageIsCleared(mapName -> {
			for (BomberMan bomber : BomberMan.getBomberManList())
				if (!bomber.isDead()) {
					if (fixedBomberMans.contains(bomber)) {
						boolean found = false;
						for (Score score : scores)
							if (score.bomberMan == bomber) {
								score.score++;
								found = true;
								break;
							}
						if (!found)
							scores.add(new Score(bomber));
					}
					else {
						for (String user : userBomberManOwner.keySet())
							for (BomberMan bomber2 : userBomberManOwner.get(user))
								if (bomber == bomber2) {
									boolean found = false;
									for (Score score : scores)
										if (score.userName != null && score.userName.equals(user)) {
											score.score++;
											found = true;
											break;
										}
									if (!found)
										scores.add(new Score(user));
									}
								}
			}
			TimerFX.createTimer("FadeOut" + Main.uniqueTimerId++, 2000, () -> {
				Draw.setFade(new DefaultFade().fadeOut().setOnFadeDoneEvent(() -> {
					for (String string : userBomberManOwner.keySet()) 
						userBomberManOwner.get(string).forEach(bomber -> BomberMan.removeBomberMan(bomber));
					settedEntities.clear();
					droppedWalls.clear();
					userEntityOwner.clear();
					userBomberManOwner.clear();
					MapSet.clearStuffs();
					MapSet.reloadMap();
					System.out.println(BomberMan.getBomberAlives());
					Sound.stopAllMp3s();
					TimerFX.createTimer("FadeIn" + Main.uniqueTimerId++, 100, () -> fadeIn());
				}));
			});
		});
		MapSet.loadMap(gameMap);
		/**
		 * ADICIONAR TABELA DE PONTUACAO, QUE INCLUIRA PONTOS DAS CPUS ADICIONADAS POR PRESENTES
		 * CRIAR ALGUNS STAGES EXTRAS PARA REVEZAR A CADA BATALHA, INCLUINDO STAGE COM SETAS REDIRECIONADORAS DE BOMBA
		 */
		//startTikTokEvents();
		addUserPic("GM", new Image("file:./appdata/sprites/gm.png"));
		pallete = Player.getTotalPlayers();
		mainLoop();
		showBlockMarks = false;
		fadeIn();
	}
	
	private void fadeIn() {
		Draw.setFade(new DefaultFade().fadeIn().setOnFadeDoneEvent(() -> {
			Draw.setFade(null);
			Sound.playMp3("Battle-Theme1");
		}));
	}

	private void fillWithCpu(int total) { // TEMP
		for (int n = 1; n <= total; n++) {
			BomberMan bomber = BomberMan.dropNewCpu(MapSet.getInitialPlayerPosition(Player.getTotalPlayers() + BomberMan.getTotalBomberMans() - 1).getTileCoord(), 1, BomberMan.getTotalBomberMans(), CpuDificult.VERY_HARD);
			fixedBomberMans.add(bomber);
		}
	}

	void setEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
			Player.convertOnKeyPressEvent(e);
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.P) {
				for (Brick brick : new ArrayList<>(Brick.getBricks()))
					brick.destroy();
				for (Item item : new ArrayList<>(Item.getItems()))
					item.forceDestroy();
			}
			if (e.getCode() == KeyCode.U)
				dropRandomTileBomb("GM");
			if (e.getCode() == KeyCode.J)
				dropRandomTileWall("GM");
			if (e.getCode() == KeyCode.I)
				dropRandomTileItem("GM");
			if (e.getCode() == KeyCode.K)
				dropRandomTileBrick("GM");
			if (e.getCode() == KeyCode.M)
				showBlockMarks = !showBlockMarks;
			if (e.getCode() == KeyCode.SPACE)
				CpuPlay.markTargets = !CpuPlay.markTargets;
			if (e.getCode() == KeyCode.ESCAPE)
				Main.close();
			for (int n = 0; n < Game.setInputKeys.length; n++)
				if (e.getCode() == Game.setInputKeys[n]) {					
					fixedBomberMans.add(Game.openInputSetup(n));
					return;
				}
			Game.checkFunctionKeys(e);
		});
		Main.sceneMain.setOnKeyReleased(e -> {
			Player.convertOnKeyReleaseEvent(e);
			holdedKeys.add(e.getCode());
		});
	}
	
	public static void addEcho(String string) {
		echos.add(0, string.toUpperCase());
		if (echos.size() == 66)
			echos.remove(echos.size() - 1);
	}
	
	private void drawUserImage(String userName, Entity entity) {
		Image image = userPics.get(userName);
		int sprHeight = entity.getCurrentFrameSet().getSprite(0).getOutputWidth(), 
				w = (int)image.getWidth(), h = (int)image.getHeight(),
				x = (int)entity.getX() * ZOOM - 32 * ZOOM,
				y = (int)((entity.getY() - entity.getHeight()) * ZOOM - 32 * ZOOM - h * 1.4);
		if (y < 0)
			y += sprHeight * 3.2 * ZOOM;
		gcMain.drawImage(image, 0, 0, w, h, x + Main.TILE_SIZE / 2 * ZOOM - w / 2, y, w, h);
	}

	void mainLoop() {
		try {
			Tools.getFPSHandler().fpsCounter();
			MapSet.run();
			Draw.applyAllDraws(canvasMain, ZOOM, -32 * ZOOM, -32 * ZOOM);
			drawScores();
			displayStageTimer();
			drawUserPicsOverEntities();
			drawEchoMessages();
			runGiftEventsQueue();
			if (showBlockMarks)
				showBlockMarks();
			if (!Main.close)
				Platform.runLater(() -> {
					String title = "BomberMan Mixed Up!     FPS: " + Tools.getFPSHandler().getFPS() + "     Sobrecarga: " + Tools.getFPSHandler().getFreeTaskTicks();
					Main.stageMain.setTitle(title);
					mainLoop();
				});
		}
		catch (Exception e) {
			e.printStackTrace();
			Main.close();
		}
	}

	private void drawScores() {
		scores.sort((o1, o2) -> o1.score - o2.score);
		for (int n = 0; n < scores.size(); n++) {
			Score score = scores.get(n);
			if (score.bomberMan != null)
				gcMain.drawImage(Materials.getCharacterSprite(score.bomberMan.getBomberIndex(),score.bomberMan.getPalleteIndex()), 208, 13, 15, 15, canvasMain.getWidth() - 130, 60 + n * 20 * ZOOM, 15 * ZOOM, 15 * ZOOM);
			else {
				Image i = userPics.get(score.userName);
				gcMain.drawImage(i, 0, 0, i.getWidth(), i.getHeight(), canvasMain.getWidth() - 135, 60 + n * 20 * ZOOM, i.getWidth() * 0.75, i.getHeight() * 0.75);
			}
			gcMain.setFill(Color.WHITE);
			gcMain.setFont(GameFonts.fontBomberMan20);
			gcMain.fillText("x " + score.score, canvasMain.getWidth() - 80, 100 + n * 20 * ZOOM);
		}
	}

	private void runGiftEventsQueue() {
		if (!acumulatedGifts.isEmpty() && !MapSet.stageIsCleared() && Draw.getFade() == null) {
			for (TikTokGiftEvent giftEvent : acumulatedGifts)
				TimerFX.createTimer("RunningOnGiftEventQueue" + Main.uniqueTimerId++, 10000, () -> runOnGiftEvent(giftEvent));
			acumulatedGifts.clear();
		}
	}

	private void displayStageTimer() {
		String time;
    int minutes = MapSet.getMapTimeLeftInSecs() / 60, seconds = MapSet.getMapTimeLeftInSecs() % 60;
		if (!MapSet.haveTimeLimit())
			time = "--:--";
		else
			time = String.format("%02d:%02d", minutes, seconds);
		Text text = new Text(time);
		text.setFont(GameFonts.fontBomberMan40);
		int x = (int)(canvasMain.getWidth() - text.getLayoutBounds().getWidth() - 10), y = 40;
		if (MapSet.getMapTimeLeftInSecs() > 60) {
			gcMain.setStroke(Color.valueOf("008800"));
			gcMain.setFill(Color.valueOf("00DD00"));
		}
		else if (MapSet.getMapTimeLeftInSecs() > 15) {
			gcMain.setStroke(Color.valueOf("888800"));
			gcMain.setFill(Color.valueOf("DDDD00"));
		}
		else {
			gcMain.setStroke(Color.valueOf("880000"));
			gcMain.setFill(Color.valueOf("DD0000"));
		}
		gcMain.save();
		gcMain.setLineWidth(3);
		gcMain.setFont(GameFonts.fontBomberMan40);
		gcMain.fillText(time, x, y);
		gcMain.strokeText(time, x, y);
		gcMain.restore();
	}

	private void drawEchoMessages() {
		gcMain.setFill(Color.YELLOW);
		gcMain.setFont(font);
		int y = (int)canvasMain.getHeight();
		for (String s : echos)
			gcMain.fillText(s, canvasMain.getWidth() - 120, y -= 11);
	}

	private void drawUserPicsOverEntities() {
		for (String user : userEntityOwner.keySet())
			if (userPics.containsKey(user) && userPics.get(user) != null)
				for (Entity entity : userEntityOwner.get(user))
					if (entity.isVisible())
						drawUserImage(user, entity);
		if (System.currentTimeMillis() % 1000 < 750)
			for (String user : userBomberManOwner.keySet())
				for (int n = 0; n < userBomberManOwner.get(user).size(); n++) {
					BomberMan bomber = userBomberManOwner.get(user).get(n);
					if (userPics.containsKey(user) && userPics.get(user) != null && bomber.isVisible())
						drawUserImage(user, bomber);
					if (!bomber.isVisible() && bomber.isDead())
						userBomberManOwner.get(user).remove(n--);
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

	void close() {}

	private void showBlockMarks() {
		Draw.drawBlockTypeMarks(gcMain, -32 * ZOOM, -32 * ZOOM, ZOOM, true, null);
	}

	private void startTikTokEvents() {
		TextToSpeechGoogle.setSpeechSpeed(1.5f);
		TextToSpeechGoogle.setVolumeGain(0.5f);
		TextToSpeechGoogle.setLanguage(GoogleLanguages.pt);
		TextToSpeechGoogle.setVolumeGain(1);
		liveClient = TikTokLive.newClient(liveUserToConnect )
				.configure((settings) -> {
					settings.setClientLanguage("pt");
					settings.setLogLevel(Level.OFF);
					settings.setPrintToConsole(false);
	        settings.setRetryOnConnectionFailure(false);
				})
				.onConnected((liveClient, event) -> {
					System.out.println("Conectado a live");
				})
				.onDisconnected((liveClient, event) -> {
					System.out.println("Desconectado a live. Reconectando em 5 segundos...");
					liveClient.disconnect();
					TimerFX.createTimer("TiTokLiveReconnection", 5000, () -> liveClient.connectAsync());
				})
				.onError((liveClient, event) -> {
					event.getException().printStackTrace();
					liveClient.disconnect();
					Main.close();
				})
				.onFollow((liveClient, event) -> {
					if (!MapSet.stageIsCleared()) {
						String userName = event.getUser().getName();
						downloadUserImage(userName, event.getUser().getPicture());
						speech("Blocos do novo seguidor " + removeNonAlphanumeric(userName));
						dropRandomTileWall(userName, 3);
					}
				})
				.onGift((liveClient, event) -> {
					String userName = event.getUser().getName();
					downloadUserImage(userName, event.getUser().getPicture());
					if (!MapSet.stageIsCleared())
						runOnGiftEvent(event);
					else
						acumulatedGifts.add(event);
				})
				.onLike((liveClient, event) -> {
					if (!MapSet.stageIsCleared()) {
						String userName = event.getUser().getName();
						downloadUserImage(userName, event.getUser().getPicture());
						if (!userLikes.containsKey(userName))
							userLikes.put(userName, 0);
						int likes = userLikes.get(userName);
						List<Integer> list = new ArrayList<>(likeEvents.keySet());
						list.sort((n1, n2) -> n1 - n2);
						for (int n = 0; n < event.getLikes(); n++) {
							likes++;
							for (Integer i : list) {
								if (likes % i == 0)
									TimerFX.createTimer("likeEvent@" + Main.uniqueTimerId++, n * 100, () -> {
										likeEvents.get(i).getValue().accept(userName);
										if (likeEvents.get(i).getKey() != null)
											speech(likeEvents.get(i).getKey().replace("@USER", removeNonAlphanumeric(userName)));
									});
							}
						}
						userLikes.put(userName, likes);
					}
				}).build();
		liveClient.connectAsync();
		loadLiveEvents();
	}

	private void runOnGiftEvent(TikTokGiftEvent event) {
		String userName = event.getUser().getName();
		List<Integer> list = new ArrayList<>(giftEvents.keySet());
		list.sort((n1, n2) -> n1 - n2);
		for (int n = 0; n < event.getCombo(); n++)
			for (Integer id : list)
				if (event.getGift().getId() == id)
					TimerFX.createTimer("likeEvent@" + Main.uniqueTimerId++, n * 100, () -> {
						giftEvents.get(id).getValue().accept(userName);
						if (giftEvents.get(id).getKey() != null)
							speech(giftEvents.get(id).getKey().replace("@USER", removeNonAlphanumeric(userName)));
					});
	}

	private void downloadUserImage(String userName, Picture picture) {
		if (userPics.containsKey(userName))
			return;
		userPics.put(userName, null);
		new Thread(() -> {
			picture.downloadImageAsync().thenAccept(image ->
				Platform.runLater(() -> {
					BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = bufferedImage.createGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.drawImage(image, 0, 0, null);
					g2d.dispose();
					addUserPic(userName, SwingFXUtils.toFXImage(bufferedImage, null));
				}));
			}).start();
  }

	private void addUserPic(String userName, Image image) {
		final double targetSize = Main.TILE_SIZE * ZOOM * 1.5;
		Canvas canvas = new Canvas(targetSize, targetSize);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.TRANSPARENT);
		gc.clearRect(0, 0, targetSize, targetSize);
		gc.beginPath();
		gc.arc(targetSize / 2, targetSize / 2, targetSize / 2, targetSize / 2, 0, 360);
		gc.clip();
		gc.drawImage(image, 0, 0, targetSize, targetSize);
		WritableImage roundImage = new WritableImage((int) targetSize, (int) targetSize);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		userPics.put(userName, canvas.snapshot(params, roundImage));
	}

	private void dropRandomTileWall(String userName) {
		dropRandomTileWall(userName, 1);
	}
	
	private void dropRandomTileWall(String userName, int quant) {
		dropRandomTileStuff(userName, quant, true, wallTilePassThrough, t -> MapSet.dropWallFromSky(t));
	}

	private void dropRandomTileCurse(String userName) {
		dropRandomTileCurse(userName, 1);
	}
	
	private void dropRandomTileCurse(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, itemPassThrough, t -> Item.dropItemFromSky(t, ItemType.CURSE_SKULL));
	}
	
	private void dropRandomTileItem(String userName) {
		dropRandomTileItem(userName, 1);
	}
	
	private void dropRandomTileItem(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, itemPassThrough, t -> Item.dropItemFromSky(t, ItemType.getRandomForBattle()));
	}

	private void dropRandomTileBrick(String userName) {
		dropRandomTileBrick(userName, 1);
	}
	
	private void dropRandomTileBrick(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Brick.dropBrickFromSky(t));
	}

	private void dropRandomTileBomb(String userName) {
		dropRandomTileBomb(userName, 1);
	}
	
	private void dropRandomTileBomb(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Bomb.dropBombFromSky(t));
	}

	private void dropRandomTileStuff(String userName, int quant, boolean testCoord, Set<PassThrough> passThrough, Function<TileCoord, Entity> function) {
		for (int n = 0; n < quant; n++) {
			final int n2 = n;
			MapSet.getRandomFreeTileAsync(passThrough, testCoord, t -> !droppedWalls.contains(t) && !settedEntities.contains(t)).thenAccept(coord -> {
				if (coord != null) {
					if (testCoord)
						droppedWalls.add(coord.getNewInstance());
					TimerFX.createTimer("bombDrop@" + Main.uniqueTimerId++, n2 * 100, () -> {
						Entity entity = function.apply(coord);
						if (userName != null && entity != null) {
							if (!userEntityOwner.containsKey(userName))
								userEntityOwner.put(userName, new ArrayList<>());
							userEntityOwner.get(userName).add(entity);
						}
						settedEntities.add(coord);
						final TileCoord c = coord.getNewInstance();
						TimerFX.createTimer("removeSettedBomb@" + Main.uniqueTimerId++, displayAvatarDelay , () -> {
							if (userName != null)
								userEntityOwner.get(userName).remove(entity);
							settedEntities.remove(c);
						});
					});
				}
			});
		}
	}

	private void dropCpu(String userName) {
		dropCpu(userName, null);
	}

	private void dropCpu(String userName, ItemType ... initialItens) {
		MapSet.getRandomFreeTileAsync().thenAccept(coord -> {
			List<ItemType> list = null;
			if (initialItens != null) {
				list = new ArrayList<>();
				for (ItemType item : initialItens)
					list.add(item);
			}
			BomberMan bomber = BomberMan.dropNewCpu(coord, 1, pallete, CpuDificult.VERY_HARD, 180, list);
			if (!userBomberManOwner.containsKey(userName))
				userBomberManOwner.put(userName, new ArrayList<>());
			userBomberManOwner.get(userName).add(bomber);
			if (++pallete == 17)
				pallete = 0;
		});
	}

	public static void speech(String string) {
		new Thread(() -> {
			try {
				TextToSpeechGoogle.speech(string);
			}
			catch (Exception e) {}
		}).start();
	}

	public String removeNonAlphanumeric(String input) {
    if (input == null || input.isEmpty())
      return input;
	  String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
	  String withoutAccents = normalized.replaceAll("\\p{M}", "");
	  String cleanString = withoutAccents.replaceAll("[^a-zA-Z\\s]", "");
	  return cleanString.substring(0, cleanString.length() <= 15 ? cleanString.length() : 15);
  }
	
	public void disconnectTikTokLive() {
		if (liveClient != null)
			liveClient.disconnect();
	}
	
}

class Score {
	
	public String userName;
	public BomberMan bomberMan;
	public int score;
	
	public Score(String userName) {
		this.userName = userName;
		bomberMan = null;
		score = 1;
	}
	
	public Score(BomberMan bomberMan) {
		this.bomberMan = bomberMan;
		userName = null;
		score = 1;
	}
	
}