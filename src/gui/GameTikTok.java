package gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
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
import gui.util.Alerts;
import gui.util.ImageUtils;
import io.github.jwdeveloper.dependance.injector.api.util.Pair;
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
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import joystick.JInputEX;
import joystick.JXInputEX;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
import player.Player;
import tools.Draw;
import tools.GameFonts;
import tools.Tools;
import util.DurationTimerFX;
import util.FindFile;
import util.FrameTimerFX;
import util.IniFile;

public class GameTikTok {

	private static List<String> echos = new ArrayList<>();
	private static final int WIN_W = Main.TILE_SIZE * 32;
	private static final int WIN_H = (int)(Main.TILE_SIZE * 16.5);

	@FXML
	private Canvas canvasMain;

	private boolean connectToLive = false;
	private String liveOwnerUserName = "flaviosphgamer2";
	private String liveUserToConnect = "flaviosphgamer2";
	private IniFile tiktokIniFile = IniFile.getNewIniFileInstance("tiktok.ini");
	private Map<Integer, Pair<String, Consumer<String>>> likeEvents;
	private Map<Integer, Pair<String, Consumer<String>>> giftEvents;
	private Map<String, Integer> userLikes = new HashMap<>();
	private Map<String, Image> userPics = new HashMap<>();
	private Map<String, Score> userScores = new HashMap<>();
	private Map<String, List<Entity>> userEntityOwner = new HashMap<>();
	private Map<String, List<FixedBomberMan>> bomberMans = new HashMap<>();
	private Set<TileCoord> settedEntities = new HashSet<>();
	private Set<TileCoord> droppedWalls = new HashSet<>();
	private LiveClient liveClient;
	private Set<PassThrough> wallTilePassThrough = Set.of(PassThrough.BOMB, PassThrough.BRICK, PassThrough.HOLE, PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER, PassThrough.WATER);
	private Set<PassThrough> itemPassThrough = Set.of(PassThrough.MONSTER, PassThrough.PLAYER);
	private Set<PassThrough> passThrough = Set.of(PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER);
	private List<KeyCode> holdedKeys;
	private List<String> userNames = new ArrayList<>();
	private List<SampleGift> sampleGifts = new ArrayList<>();
	private int sampleGiftPos = 0;
	private int pallete;
	private GraphicsContext gcMain;
	private Font font = new Font("Lucida Console", 40);
	private boolean showBlockMarks;
	private boolean reconnectionIsDisabled;
	private String gameMap = "TikTok-Small-Battle-01";
	private Duration displayAvatarDelay = Duration.seconds(3);
	private List<TikTokGiftEvent> acumulatedGifts = new ArrayList<>();
	private List<String> alreadyFollowed = new ArrayList<>();
	private List<String> alreadyShared = new ArrayList<>();
	
	private void loadLiveEvents() {
		giftEvents = new HashMap<>();
		likeEvents = new HashMap<>();

		// 100 Likes - Dropa 1 bomba
		likeEvents.put(100, new Pair<>(null, userName -> dropRandomTileBomb(userName)));
		
		// 1000 Likes - (Adiciona avatar com 3 itens basicos e que participará de 1 round)
		likeEvents.put(1000, new Pair<>("@USER entrou na arena por 1 round", userName -> dropCpu(userName, 1,
			ItemType.BOMB_UP, ItemType.FIRE_UP, ItemType.SPEED_UP)));

		// Rosa (Dropa 1 Bomba)
		giftEvents.put(5655, new Pair<>(null, userName -> dropRandomTileBomb(userName)));
		
		// Mini Dino (Dropa 12 Bombas)
		giftEvents.put(6560, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 12)));
		giftEvents.put(7553, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 12)));
		giftEvents.put(7591, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 12)));
		giftEvents.put(9615, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 12)));
		giftEvents.put(9962, new Pair<>("Chuva de bombas de @USER", userName -> dropRandomTileBomb(userName, 12)));
		
		// Sorvete (Dropa 1 Item)
		giftEvents.put(5827, new Pair<>(null, userName -> dropRandomTileItem(userName)));
		
		// Coração (Dropa 12 Items)
		giftEvents.put(5327, new Pair<>("Chuva de itens de @USER", userName -> dropRandomTileItem(userName, 12)));
		giftEvents.put(5576, new Pair<>("Chuva de itens de @USER", userName -> dropRandomTileItem(userName, 12)));
		
		// GG (Dropa 1 Caveira)
		giftEvents.put(5827, new Pair<>(null, userName -> dropRandomTileCurse(userName)));
		
		// Fantasminha (Dropa 12 Caveiras)
		giftEvents.put(5576, new Pair<>("Chuva de caveiras de @USER", userName -> dropRandomTileCurse(userName, 12)));
		
		// Bola de futebol (Ativar Hurry up)
		giftEvents.put(5852, new Pair<>("@USER ativou o hurry up", userName -> MapSet.setHurryUpState(true)));
		
		// Coração com os dedos (Adiciona avatar sem itens e que participará de 1 round)
		giftEvents.put(5487, new Pair<>("@USER entrou na arena por 1 round", userName -> dropCpu(userName)));
		
		// Bracelete da equipe (Adiciona avatar com 3 itens basicos e que participará de 1 round)
		giftEvents.put(9139, new Pair<>("@USER entrou na arena por 1 round", userName -> dropCpu(userName, 1,
				ItemType.BOMB_UP, ItemType.FIRE_UP, ItemType.SPEED_UP)));
		
		// Rosquinha (Adiciona avatar com alguns itens e que participará de 3 rounds (Os itens são perdidos ao morrer))
		giftEvents.put(5879, new Pair<>("Super @USER entrou na arena por 3 rounds", userName -> dropCpu(userName, 3, 
				ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.FIRE_UP,
				ItemType.FIRE_UP, ItemType.SPEED_UP, ItemType.SPEED_UP, ItemType.PASS_BOMB)));
		
		// Heart Me (Adiciona avatar sem itens e que participará de 10 rounds (So pode enviar 1 vez por dia))
		giftEvents.put(7934, new Pair<>("@USER entrou na arena por 10 rounds", userName -> dropCpu(userName, 10)));
		
		// Boné (Adiciona avatar com muitos itens e que participará de 6 rounds (Os itens são perdidos ao morrer))
		giftEvents.put(6104, new Pair<>("Supremo @USER entrou na arena por 6 rounds", userName -> dropCpu(userName, 6, 
				ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP,
				ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP, ItemType.BOMB_UP,
				ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.FIRE_UP, 
				ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.FIRE_UP, ItemType.SPEED_UP, 
				ItemType.SPEED_UP, ItemType.SPEED_UP, ItemType.SPEED_UP, ItemType.SPEED_UP,
				ItemType.PASS_BOMB, ItemType.PASS_BRICK, ItemType.HEART_UP)));
		
		// Rosa branca (Reviver o flavio SE ELE ESTIVER MORTO, caso contrario, da 1 item aleatorio para o flavio)
		giftEvents.put(8239, new Pair<>(null, userName -> reviveSomeone(userName, liveOwnerUserName)));
		
		// Te amo (Dropa 1 caveira no flavio)
		giftEvents.put(6890, new Pair<>(null, userName -> curseToSomeone(userName, liveOwnerUserName)));
		
		// Poder da equipe (Revive todos os bombermans mortos)
		giftEvents.put(12356, new Pair<>(null, userName -> reviveAll(userName)));

		// Fatia de bolo (Revive 1 dos bombermans pertencentes ao usuario. Se não houver bombermans do usuario mortos, da 1 item aleatorio para um deles)
		giftEvents.put(6784, new Pair<>(null, userName -> reviveAll(userName, userName)));
		
		// TikTok (Da 1 item aleatoriamente para um dos bombermans da pessoa)
		giftEvents.put(5269, new Pair<>(null, userName -> giveItenToSomeone(userName, userName, 1)));

		// Colar da amizade (Dá 12 itens aleatoriamente para bombermans aleatorios da pessoa)
		giftEvents.put(9947, new Pair<>(null, userName -> giveItenToSomeone(userName, userName, 12)));
		
		// Rosa vermelha grande (Dá 12 itens aleatoriamente para bombermans aleatorios do flavio)
		giftEvents.put(8913, new Pair<>(null, userName -> giveItenToSomeone(userName, liveOwnerUserName, 12)));
	}
	
	private void curseToSomeone(String userName, String target) {
		BomberMan bomber = bomberMans.get(target).get(0).getBomberMan();
		if (!bomber.isDead()) {
			Item.addItem(bomber.getTileCoordFromCenter(), ItemType.CURSE_SKULL);
			speech(removeNonAlphanumeric(userName) + " dropou 1 caveira para " + target);
		}
		else
			dropRandomTileCurse(userName);
	}

	private void reviveSomeone(String userName, String target) {
		if (!bomberMans.containsKey(target)) {
			speech(removeNonAlphanumeric(userName) + " tentou reviver " + liveOwnerUserName + " mas ele não está jogando.");
			return;
		}
		BomberMan bomber = bomberMans.get(target).get(0).getBomberMan();
		if (bomber.isDead()) {
			speech(removeNonAlphanumeric(userName) + " reviveu " + target);
			bomber.softResetAfterMapChange(240);
		}
		else {
			Item.addItem(bomber.getTileCoordFromCenter(), ItemType.getRandomForBattle());
			speech(removeNonAlphanumeric(userName) + " dropou 1 item para " + liveOwnerUserName);
		}				
	}

	private void reviveAll(String userName) {
		reviveAll(userName, null);
	}

	private void reviveAll(String userName, String onlyFromUser) {
		if (onlyFromUser != null)
			speech(removeNonAlphanumeric(userName) + " reviveu todos os seus bombermans.");
		else
			speech(removeNonAlphanumeric(userName) + " reviveu todos os bombermans mortos.");
		for (String user : bomberMans.keySet()) {
			if (onlyFromUser == null || user.equals(onlyFromUser))
				bomberMans.get(user).forEach(b -> {
					BomberMan bomber = b.getBomberMan();
					if (bomber.isDead())
						bomber.softResetAfterMapChange(240);
				});
		}
	}
	
	private void giveItenToSomeone(String userName) {
		giveItenToSomeone(userName, null, 1);
	}
	
	private void giveItenToSomeone(String userName, String onlyFromUser) {
		giveItenToSomeone(userName, onlyFromUser, 1);
	}
	
	private void giveItenToSomeone(String userName, int quant) {
		giveItenToSomeone(userName, null, quant);
	}
	
	private void giveItenToSomeone(String userName, String onlyFromUser, int quant) {
		if (quant == 1) {
			if (onlyFromUser != null)
				speech(removeNonAlphanumeric(userName) + " deu 1 item para alguém do seu grupo");
			else
				speech(removeNonAlphanumeric(userName) + " deu 1 item para alguém aleatóriamente");
		}
		else if (onlyFromUser != null)
			speech(removeNonAlphanumeric(userName) + " deu " + quant + " itens para alguém do seu grupo");
		else
			speech(removeNonAlphanumeric(userName) + " deu " + quant + " itens para alguém aleatóriamente");
		for (String user : bomberMans.keySet()) {
			if (onlyFromUser == null || user.equals(onlyFromUser))
				bomberMans.get(user).forEach(b -> {
					BomberMan bomber = b.getBomberMan();
					if (bomber.isDead())
						Item.addItem(bomber.getTileCoordFromCenter(), ItemType.getRandomForBattle());
				});
		}
	}
	
	public void init() {
		Main.setZoom(3);
		loadGiftList();
		loadUserPics();
		loadConfigs();
		loadLiveEvents();
		canvasMain.setWidth(WIN_W * Main.getZoom());
		canvasMain.setHeight(WIN_H * Main.getZoom());
		Main.setMainCanvas(canvasMain);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		holdedKeys = new ArrayList<>();
		liveClient = null;
		reconnectionIsDisabled = false;
		setEvents();
		addPlayerOne();
		MapSet.setOnStageObjectiveClearEvent(() -> {
			for (String user : bomberMans.keySet())
				for (FixedBomberMan fixedBomber : bomberMans.get(user))
					if (!fixedBomber.getBomberMan().isDead()) {
						if (!userScores.containsKey(user))
							userScores.put(user, new Score(user));
						userScores.get(user).incScore();
						tiktokIniFile.write("Scores", user, "" + userScores.get(user).getScore());
					}
		});
		MapSet.setOnMapLoadEvent(() -> {
			List<String> removeUsers = new ArrayList<>();
			for (String user : bomberMans.keySet())
				for (FixedBomberMan fixedBomber : bomberMans.get(user)) {
					fixedBomber.decLeftRounds();
					if (fixedBomber.getLeftRounds() == 0) {
						BomberMan.removeBomberMan(fixedBomber.getBomberMan());
						removeUsers.add(user);
					}
				}
			for (String user : removeUsers)
				bomberMans.remove(user);
			settedEntities.clear();
			droppedWalls.clear();
			userEntityOwner.clear();
		});
		MapSet.loadMap(gameMap);
		pallete = Player.getTotalPlayers();
		setJoysticksOnDisconnectEvents();
		if (connectToLive)
			startTikTokEvents();
		mainLoop();
		showBlockMarks = false;
		DurationTimerFX.createTimer("ChangeSampleGiftPos", Duration.seconds(2), 0, () -> {
			if (++sampleGiftPos == sampleGifts.size())
				sampleGiftPos = 0;
		});
	}
	
	private void addPlayerOne() {
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		bomberMans.put("abazabinha", new ArrayList<>());
		bomberMans.get("abazabinha").add(new FixedBomberMan(BomberMan.getBomberMan(0), "abazabinha", -1));
	}

	private void loadConfigs() {
		Integer today = (int)(System.currentTimeMillis() / 86400);
		if (tiktokIniFile.readAsInteger("CONFIG", "Today", null) != today) { // Remove os scores se o programa estiver rodando num dia diferente da ultima vez
			tiktokIniFile.write("CONFIG", "Today", "" + today);
			tiktokIniFile.remove("Scores");
			tiktokIniFile.remove("CONFIG", "AlreadyFollowed");
			tiktokIniFile.remove("CONFIG", "AlreadyShared");
		}
		else {
			tiktokIniFile.getItemList("Scores").forEach(user ->
				userScores.put(user, new Score(user, tiktokIniFile.readAsInteger("Scores", user, 0))));
			for (String item : Arrays.asList("AlreadyFollowed", "AlreadyShared"))
				if (tiktokIniFile.read("CONFIG", item) != null) {
					String[] split = tiktokIniFile.getLastReadVal().split(" ");
					for (String user : split)
						(item.equals("AlreadyShared") ? alreadyShared : alreadyFollowed).add(user);
				}
		}
	}

	private void loadUserPics() {
		FindFile.findFile("appdata/userPics/","*.png").forEach(file -> {
			String userName = file.getName().replace(".png", "");
			Image image = roundedBordersImage(new Image("file:" + file.getAbsolutePath()));
			userPics.put(userName, image);
		});
	}

	private void loadGiftList() {
		FindFile.findFile("appdata/gifts/","*.png").forEach(file -> {
			String name = file.getName().replace(".png", "");
			Image image = new Image("file:" + file.getAbsolutePath());
			sampleGifts.add(new SampleGift(name, image));
		});
		sampleGifts.sort((s1, s2) -> s1.getOrder() - s2.getOrder());
	}

	private int testGiftId = 0;
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
			if (e.getCode() == KeyCode.G)
				runOnGiftEvent("abazabinha", "Gift", testGiftId, 1);
			if (e.getCode() == KeyCode.H) {
				List<String> list = new ArrayList<>();
				for (int i : giftEvents.keySet())
					list.add("" + i);
				String s = Alerts.choiceCombo("Gift", "Selecione o ID do Gift\npara disparar com a tecla G", list);
				if (s != null)
					testGiftId = Integer.parseInt(s);
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
				w = (int)image.getWidth(), ww = (int)(Main.TILE_SIZE * Main.getZoom() * 1.3),
				x = (int)entity.getX() * Main.getZoom() - 32 * Main.getZoom(),
				y = (int)((entity.getY() - entity.getHeight()) * Main.getZoom() - 32 * Main.getZoom() - ww * 1.5);
		if (y < 0)
			y += (sprHeight * Main.getZoom()) + ww * 1.3;
		gcMain.drawImage(image, 0, 0, w, w, x + Main.TILE_SIZE / 2 * Main.getZoom() - ww / 2, y, ww, ww);
	}

	void mainLoop() {
		try {
			Tools.getFPSHandler().fpsCounter();
			MapSet.run();
			Draw.applyAllDraws(canvasMain, Main.getZoom(), -32 * Main.getZoom(), -32 * Main.getZoom());
			drawScores();
			displayStageTimer();
			drawUserPicsOverEntities();
			drawEchoMessages();
			runGiftEventsQueue();
			drawSampleGifts();
			setupTextToSpeech();
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

	private void setupTextToSpeech() {
		TextToSpeechGoogle.setSpeechSpeed(1.5f);
		TextToSpeechGoogle.setVolumeGain(0.5f);
		TextToSpeechGoogle.setLanguage(GoogleLanguages.pt);
		TextToSpeechGoogle.setVolumeGain(1);
	}

	private void drawSampleGifts() {
		SampleGift sampleGift = sampleGifts.get(sampleGiftPos);
		String name = sampleGift.getName();
		Image image = sampleGift.getImage();
		gcMain.setFill(Color.YELLOW);
		gcMain.setStroke(Color.YELLOW);
		gcMain.setLineWidth(1);
		gcMain.setFont(font);
		Text text = new Text();
		text.setFont(font);
		gcMain.fillText(name, 100, canvasMain.getHeight() - 25);
		gcMain.strokeText(name, 100, canvasMain.getHeight() - 25);
		gcMain.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 10, canvasMain.getHeight() - 68, 64, 64);
	}

	private void drawScores() {
		List<Score> scores = new ArrayList<>(userScores.values());
		scores.sort((o1, o2) -> o2.getScore() - o1.getScore());
		for (int n = 0, hh = Main.TILE_SIZE * Main.getZoom(); n < scores.size(); n++) {
			Score score = scores.get(n);
			Image i = userPics.get(score.getUserName());
			gcMain.drawImage(i, 0, 0, i.getWidth(), i.getHeight(), canvasMain.getWidth() - 135, 60 + n * hh * 1.1, hh, hh);
			gcMain.setFill(Color.WHITE);
			gcMain.setFont(GameFonts.fontBomberMan20);
			gcMain.fillText("x " + score.getScore(), canvasMain.getWidth() - 80, 93 + n * hh * 1.1);
		}
	}

	private void runGiftEventsQueue() {
		if (!acumulatedGifts.isEmpty() && !MapSet.stageObjectiveIsCleared() && Draw.getFade() == null) {
			for (TikTokGiftEvent giftEvent : acumulatedGifts)
				DurationTimerFX.createTimer("RunningOnGiftEventQueue" + Main.uniqueTimerId++, Duration.seconds(10), () ->
					runOnGiftEvent(giftEvent.getUser().getName(), giftEvent.getGift().getName(), giftEvent.getGift().getId(), giftEvent.getCombo()));
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
		int x = (int)(canvasMain.getWidth() - text.getLayoutBounds().getWidth() + 40), y = 40;
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
		gcMain.setTextAlign(TextAlignment.CENTER);
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
			for (String user : bomberMans.keySet())
				for (int n = 0; n < bomberMans.get(user).size(); n++) {
					BomberMan bomber = bomberMans.get(user).get(n).getBomberMan();
					if (userPics.containsKey(user) && userPics.get(user) != null && bomber.isVisible())
						drawUserImage(user, bomber);
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
		Draw.drawBlockTypeMarks(gcMain, -32 * Main.getZoom(), -32 * Main.getZoom(), Main.getZoom(), true, false, false, t -> {
			return Bomb.haveBombAt(t.getTileCoord()) ? Color.RED : null;
		});
	}

	private void startTikTokEvents() {
		liveClient = TikTokLive.newClient(liveUserToConnect )
				.configure((settings) -> {
					settings.setClientLanguage("pt");
					settings.setLogLevel(Level.OFF);
					settings.setPrintToConsole(false);
	        settings.setRetryOnConnectionFailure(false);
				})
				.onConnected((liveClient, event) -> {
					String text  ="Conectado á live de " + liveClient.getRoomInfo().getHostName();
					speech(text);
					System.out.println(text);
				})
				.onDisconnected((liveClient, event) -> {
					if (!reconnectionIsDisabled) {
						String text = "Desconectado da live de " + liveClient.getRoomInfo().getHostName() + ". Reconectando em 5 segundos...";
						speech(text);
						System.out.println(text);
						liveClient.disconnect();
						DurationTimerFX.createTimer("TiTokLiveReconnection", Duration.seconds(5), () -> liveClient.connectAsync());
					}
				})
				.onError((liveClient, event) -> {
					String erro = event.getException().getMessage();
					if (erro.contains("request timed out"))
						erro = "Falha ao conectar á live de " + liveClient.getRoomInfo().getHostName() + ". Reconectando em 5 segundos...";
					else if (erro.contains("User is offline:"))
						erro = "A live de " + liveClient.getRoomInfo().getHostName() + " está offline";
					else if (erro.contains("Sign server rate limit reached.")) {
						erro = "Você alcançou o limite diário de conexões.";
						reconnectionIsDisabled = true;
					}
					System.out.println(erro);
					speech(erro);
					liveClient.disconnect();
				})
				.onFollow((liveClient, event) -> {
					if (!MapSet.stageObjectiveIsCleared()) {
						String userName = event.getUser().getName();
						downloadUserImage(event.getUser().getName(), event.getUser().getPicture());
						if (!alreadyFollowed.contains(userName)) {
							// Seguir (Adiciona avatar sem itens e que participará de 10 round (So funciona 1 vez por dia)
							speech("Novo seguidor " + removeNonAlphanumeric(userName) + " entrou na arena por 5 rounds");
							dropCpu(userName, 5);
							alreadyFollowed.add(userName);
						}
					}
				})
				.onComment((liveClient, event) -> downloadUserImage(event.getUser().getName(), event.getUser().getPicture()))
				.onEmote((liveClient, event) -> downloadUserImage(event.getUser().getName(), event.getUser().getPicture()))
				.onShare((liveClient, event) -> {
					if (!MapSet.stageObjectiveIsCleared()) {
						String userName = event.getUser().getName();
						downloadUserImage(event.getUser().getName(), event.getUser().getPicture());
						if (!alreadyShared.contains(userName)) {
							// Compartilhar a live (Adiciona avatar sem itens e que participará de 1 round (So funciona 1 vez a cada 5 minutos)
							speech(removeNonAlphanumeric(userName) + " entrou na arena por 1 round por compartilhar a live");
							dropCpu(userName);
							alreadyShared.add(userName);
						}
						DurationTimerFX.createTimer("Share@" + userNames, Duration.minutes(5), () -> alreadyShared.remove(userName));
					}
				})
				.onGift((liveClient, event) -> {
					String userName = event.getUser().getName();
					downloadUserImage(userName, event.getUser().getPicture());
					if (!MapSet.stageObjectiveIsCleared())
						runOnGiftEvent(userName, event.getGift().getName(), event.getGift().getId(), event.getCombo());
					else
						acumulatedGifts.add(event);
				})
				.onLike((liveClient, event) -> {
					if (!MapSet.stageObjectiveIsCleared()) {
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
								if (likes % i == 0) {
									DurationTimerFX.createTimer("likeEvent@" + Main.uniqueTimerId++, Duration.millis(n * 100), () -> {
										likeEvents.get(i).getValue().accept(userName);
										if (likeEvents.get(i).getKey() != null)
											speech(likeEvents.get(i).getKey().replace("@USER", removeNonAlphanumeric(userName)));
									});
								}
							}
						}
						userLikes.put(userName, likes);
					}
				}).build();
		liveClient.connectAsync();
	}

	private void runOnGiftEvent(String userName, String giftName, int giftId, int combo) {
		List<Integer> list = new ArrayList<>(giftEvents.keySet());
		System.out.println(giftName + " " + giftId);
		list.sort((n1, n2) -> n1 - n2);
		for (int n = 0; n < combo; n++)
			for (Integer id : list)
				if (giftId == id)
					FrameTimerFX.createTimer("likeEvent@" + Main.uniqueTimerId++, n * 20, () -> {
						System.out.println("ATIVADO EVENTO: " + giftName + " " + giftEvents.get(id).getKey());
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
					Image i = SwingFXUtils.toFXImage(bufferedImage, null);
					ImageUtils.saveImageToFile(i, "appdata/userPics/" + userName + ".png");
					userPics.put(userName, roundedBordersImage(i));
				}));
			}).start();
  }

	private Image roundedBordersImage(Image image) {
		final double targetSize = 200;
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
		return canvas.snapshot(params, roundImage);
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
					FrameTimerFX.createTimer("bombDrop@" + Main.uniqueTimerId++, n2 * 10, () -> {
						Entity entity = function.apply(coord);
						if (userName != null && entity != null) {
							if (!userEntityOwner.containsKey(userName))
								userEntityOwner.put(userName, new ArrayList<>());
							userEntityOwner.get(userName).add(entity);
						}
						settedEntities.add(coord);
						final TileCoord c = coord.getNewInstance();
						DurationTimerFX.createTimer("removeSettedBomb@" + Main.uniqueTimerId++, displayAvatarDelay , () -> {
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
		dropCpu(userName, 1, null);
	}

	private void dropCpu(String userName, ItemType ... initialItens) {
		dropCpu(userName, 1, initialItens);
	}
	
	private void dropCpu(String userName, int rounds) {
		dropCpu(userName, rounds, null);
	}

	private void dropCpu(String userName, int rounds, ItemType ... initialItens) {
		MapSet.getRandomFreeTileAsync().thenAccept(coord -> {
			List<ItemType> list = null;
			if (initialItens != null) {
				list = new ArrayList<>();
				for (ItemType item : initialItens)
					list.add(item);
			}
			BomberMan bomber = BomberMan.dropNewCpu(coord, 1, pallete, CpuDificult.VERY_HARD, 180, list);
			if (!bomberMans.containsKey(userName))
				bomberMans.put(userName, new ArrayList<>());
			bomberMans.get(userName).add(new FixedBomberMan(bomber, userName, rounds));
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

	public boolean addNewPlayer(BomberMan bomber) {
		List<String> users = new ArrayList<>(userPics.keySet());
		for (String user : bomberMans.keySet())
			if (userNames.contains(user))
				users.remove(user);
		users.remove("Random CPU");
		users.remove("GM");
		users.add(0, "Random CPU");
		String user = Alerts.choiceCombo("Prompt", "Adicionar jogador", "Escolha o nome de usuário para associar ao jogador", users);
		boolean ok = user != null && !user.equals("Random CPU");
		if (user == null)
			user = "Random CPU";
		if (!bomberMans.containsKey(user))
			bomberMans.put(user, new ArrayList<>());
		bomberMans.get(user).add(new FixedBomberMan(bomber, user, -1));
		return ok;
	}
	
	private void setJoysticksOnDisconnectEvents() {
		BiConsumer<String, Player> consumer = (deviceName, player) -> {
			System.out.println("Joystick " + deviceName + " foi desconectado. CPU irá controlar o personagem agora.");
			player.getBomberMan().setCpuPlay(new CpuPlay(player.getBomberMan(), CpuDificult.VERY_HARD));
			player.setInputMode(GameInputMode.CPU);
			Player.removePlayer(player);
			out:
			for (List<FixedBomberMan> list : bomberMans.values())
				for (FixedBomberMan fixedBomber : list)
					if (fixedBomber.getBomberMan() == player.getBomberMan()) {
						String user = fixedBomber.getUserName();
						fixedBomber.setUserName("Random CPU");
						List<FixedBomberMan> list2 = new ArrayList<>(bomberMans.get(user));
						if (bomberMans.containsKey("Random CPU"))
							list2.addAll(bomberMans.get("Random CPU"));
						bomberMans.put("Random CPU", list2);
						bomberMans.remove(user);
						break out;
					}
		};
		JInputEX.setOnJoystickDisconnectedEvent(device -> {
			if (!Main.close)
				Platform.runLater(() -> {
					for (Player player : new ArrayList<>(Player.getPlayers()))
						if (player.getDinputDevice() == device)
							consumer.accept(device.getName(), player);
				});
		});
		JXInputEX.setOnJoystickDisconnectedEvent(device -> {
			if (!Main.close)
				Platform.runLater(() -> {
					for (Player player : new ArrayList<>(Player.getPlayers()))
						if (player.getXinputDevice() == device) 
							consumer.accept(device.getJoystickName(), player);
				});
		});
	}

}

class Score {
	
	private String userName;
	private int score;
	
	public Score(String userName) {
		this(userName, 0);
	}
	
	public Score(String userName, int score) {
		this.userName = userName;
		this.score = score;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int value) {
		score = value;
	}
	
	public void incScore() {
		score++;
	}
	
}

class FixedBomberMan {
	
	private BomberMan bomberMan;
	private String userName;
	private int leftRounds;
	
	public FixedBomberMan(BomberMan bomberMan, String userName, int leftRounds) {
		this.bomberMan = bomberMan;
		this.userName = userName;
		this.leftRounds = leftRounds;
	}
	
	public BomberMan getBomberMan() {
		return bomberMan;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getLeftRounds() {
		return leftRounds;
	}
	
	public void setLeftRounds(int rounds) {
		leftRounds = rounds;
	}

	public void decLeftRounds() {
		leftRounds--;
	}
	
}

class SampleGift {
	
	private String name;
	private Image image;
	private int order;
	
	public SampleGift(String name, Image image) {
		this.name = name.substring(name.indexOf(' ') + 1);
		this.image = image;
		order = Integer.parseInt(name.substring(0, name.indexOf(' ')));
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return image;
	}
	
	public int getOrder() {
		return order;
	}
	
}