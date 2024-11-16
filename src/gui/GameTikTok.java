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
import enums.PassThrough;
import io.github.jwdeveloper.tiktok.TikTokLive;
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
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
import player.Player;
import tools.Draw;
import tools.Tools;
import util.TimerFX;

public class GameTikTok {

	private static Map<String, Integer> userLikes = new HashMap<>();
	private static Map<String, Image> userPics = new HashMap<>();
	private static Map<String, List<Entity>> userOwner = new HashMap<>();
	private static Set<TileCoord> settedEntities = new HashSet<>();
	private static Long uniqueId = 0L;
	
	@FXML
	private Canvas canvasMain;

	private LiveClient liveClient;
	public static final int ZOOM = 3;
	private final int WIN_W = Main.TILE_SIZE * 32;
	private final int WIN_H = Main.TILE_SIZE * 17;
	private Set<PassThrough> wallTilePassThrough = Set.of(PassThrough.BOMB, PassThrough.BRICK, PassThrough.HOLE, PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER, PassThrough.WATER);
	private Set<PassThrough> itemPassThrough = Set.of(PassThrough.MONSTER, PassThrough.PLAYER);
	private Set<PassThrough> passThrough = Set.of(PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER);
	private List<KeyCode> holdedKeys;
	private GraphicsContext gcMain;
	private Font font;
	private static List<String> echos = new ArrayList<>();
	private boolean showBlockMarks;
	private String gameMap = "TikTok-Small-Battle-01";
	private String liveUserToConnect = "tigerlabu";
	private int dropBomb = 5;
	private int dropBombRain = 50;

	public void init() {
		font = new Font("Lucida Console", 9);
		canvasMain.setWidth(WIN_W * ZOOM);
		canvasMain.setHeight(WIN_H * ZOOM);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		holdedKeys = new ArrayList<>();
		liveClient = null;
		setEvents();
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		MapSet.loadMap(gameMap);
		fillWithCpu(1);
		//startTikTokEvents();
		addUserPic("steve", new Image("file:E:\\Audio & Video\\Imagens\\Eu\\2024\\perfil3.png"));
		mainLoop();
		showBlockMarks = false;
	}
	
	private void fillWithCpu(int total) { // TEMP
		for (int n = 1; n <= total; n++) {
			Player.addPlayer();
			Player player = Player.getPlayer(n);
			BomberMan bomber = BomberMan.addBomberMan(1, BomberMan.getTotalBomberMans());
			bomber.setPosition(MapSet.getInitialPlayerPosition(BomberMan.getTotalBomberMans() - 1));
			player.setBomberMan(bomber);
			player.setInputMode(GameInputMode.CPU);
			player.getBomberMan().setCpuPlay(new CpuPlay(bomber, CpuDificult.VERY_HARD));
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
					item.destroy();
			}
			if (e.getCode() == KeyCode.U)
				dropRandomTileBomb("steve");
			if (e.getCode() == KeyCode.J)
				dropRandomTileWall("steve");
			if (e.getCode() == KeyCode.I)
				dropRandomTileItem("steve");
			if (e.getCode() == KeyCode.K)
				dropRandomTileBrick("steve");
			if (e.getCode() == KeyCode.SPACE)
				CpuPlay.markTargets = !CpuPlay.markTargets;
			if (e.getCode() == KeyCode.ESCAPE)
				Main.close();
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

	void mainLoop() {
		try {
			Tools.getFPSHandler().fpsCounter();
			MapSet.run();
			Draw.applyAllDraws(canvasMain, ZOOM, -32 * ZOOM, -32 * ZOOM);
			out:
			for (String user : userOwner.keySet())
				for (Entity entity : userOwner.get(user)) {
					if (!userPics.containsKey(user) || userPics.get(user) == null)
						break out;
					Image i = userPics.get(user);
					int w = (int)i.getWidth(), h = (int)i.getWidth(), size = Main.TILE_SIZE * ZOOM,
							x = (int)entity.getX() * ZOOM - 32 * ZOOM,
							y = (int)entity.getY() * ZOOM - 32 * ZOOM;
					gcMain.setStroke(Color.YELLOW);
					gcMain.strokeRect(x, y, size, size);
					gcMain.drawImage(i, 0, 0, w, h, 
							x + Main.TILE_SIZE / 2 * ZOOM - w / 4, 
							(y - entity.getHeight()) - h * ZOOM * 1.1,
							w / 2, h / 2);
				}
			gcMain.setFill(Color.YELLOW);
			gcMain.setFont(font);
			int y = (int)canvasMain.getHeight();
			for (String s : echos) {
				gcMain.fillText(s, canvasMain.getWidth() - 120, y -= 11);
			}
			if (showBlockMarks) // TEMP
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
				.onFollow((liveClient, event) -> {
					String userName = event.getUser().getName();
					downloadUserImage(userName, event.getUser().getPicture());
					speech("Blocos do novo seguidor " + removeNonAlphanumeric(userName));
					dropRandomTileWall(userName, 5);
				})
				.onLike((liveClient, event) -> {
					for (int n = 0; n < event.getLikes(); n++) {
						String userName = event.getUser().getName();
						downloadUserImage(userName, event.getUser().getPicture());
						if (!userLikes.containsKey(userName))
							userLikes.put(userName, 0);
						int likes = userLikes.get(userName) + 1;
						userLikes.put(userName, likes);
						if (likes % dropBombRain == 0) {
							speech("Chuva de bombas de " + removeNonAlphanumeric(userName));
							dropRandomTileBomb(userName, 10);
						}
						else if (likes % dropBomb == 0) {
							speech("Bomba de " + removeNonAlphanumeric(userName));
							dropRandomTileBomb();
						}
					}
				}).build();
		liveClient.connectAsync();
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
		double width = image.getWidth(),
					 height = image.getHeight(),
					 diameter = Math.min(width, height);
		Canvas canvas = new Canvas(diameter, diameter);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.TRANSPARENT);
		gc.clearRect(0, 0, diameter, diameter);
		gc.beginPath();
		gc.arc(diameter / 2, diameter / 2, diameter / 2, diameter / 2, 0, 360);
		gc.clip();
		gc.drawImage(image, (diameter - width) / 2, (diameter - height) / 2);
		WritableImage roundImage = new WritableImage((int) diameter, (int) diameter);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		userPics.put(userName, canvas.snapshot(params, roundImage));
	}

	private void dropRandomTileWall() {
		dropRandomTileWall(1);
	}
	
	private void dropRandomTileWall(String userName) {
		dropRandomTileWall(userName, 1);
	}
	
	private void dropRandomTileWall(int quant) {
		dropRandomTileStuff(null, quant, false, wallTilePassThrough, t -> MapSet.dropWallFromSky(t));
	}

	private void dropRandomTileWall(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, wallTilePassThrough, t -> MapSet.dropWallFromSky(t));
	}

	private void dropRandomTileItem() {
		dropRandomTileItem(1);
	}
	
	private void dropRandomTileItem(String userName) {
		dropRandomTileItem(userName, 1);
	}
	
	private void dropRandomTileItem(int quant) {
		dropRandomTileStuff(null, quant, false, itemPassThrough, t -> Item.dropItemFromSky(t));
	}

	private void dropRandomTileItem(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, itemPassThrough, t -> Item.dropItemFromSky(t));
	}

	private void dropRandomTileBrick() {
		dropRandomTileBrick(1);
	}
	
	private void dropRandomTileBrick(String userName) {
		dropRandomTileBrick(userName, 1);
	}
	
	private void dropRandomTileBrick(int quant) {
		dropRandomTileStuff(null, quant, false, passThrough, t -> Brick.dropBrickFromSky(t));
	}

	private void dropRandomTileBrick(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Brick.dropBrickFromSky(t));
	}

	private void dropRandomTileBomb() {
		dropRandomTileBomb(1);
	}
	
	private void dropRandomTileBomb(String userName) {
		dropRandomTileBomb(userName, 1);
	}
	
	private void dropRandomTileBomb(int quant) {
		dropRandomTileStuff(null, quant, false, passThrough, t -> Bomb.dropBombFromSky(t));
	}

	private void dropRandomTileBomb(String userName, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Bomb.dropBombFromSky(t));
	}

	private void dropRandomTileStuff(String userName, int quant, Function<TileCoord, Entity> function) {
		dropRandomTileStuff(userName, quant, false, wallTilePassThrough, function);
	}

	private void dropRandomTileStuff(String userName, int quant, boolean testCoord, Set<PassThrough> passThrough, Function<TileCoord, Entity> function) {
		for (int n = 0; n < quant; n++) {
			final int n2 = n;
			MapSet.getRandomFreeTileAsync(passThrough, testCoord, t -> !settedEntities.contains(t)).thenAccept(coord -> {
				if (coord != null)
					TimerFX.createTimer("bombDrop@" + uniqueId++, n2 * 100, () -> {
						Entity entity = function.apply(coord);
						if (userName != null && entity != null) {
							if (!userOwner.containsKey(userName))
								userOwner.put(userName, new ArrayList<>());
							userOwner.get(userName).add(entity);
						}
						settedEntities.add(coord);
						final TileCoord c = coord.getNewInstance();
						TimerFX.createTimer("removeSettedBomb@" + uniqueId++, 5000, () -> {
							if (userName != null)
								userOwner.get(userName).remove(entity);
							settedEntities.remove(c);
						});
					});
			});
		}
	}

	private void speech(String string) {
		new Thread(() -> {
			try {
				TextToSpeechGoogle.speech(string);
			}
			catch (Exception e) {}
		}).start();
	}

	public static String removeNonAlphanumeric(String input) {
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