package gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import enums.BombType;
import enums.CpuDificult;
import enums.FindType;
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
import io.github.jwdeveloper.tiktok.exceptions.TikTokLiveOfflineHostException;
import io.github.jwdeveloper.tiktok.exceptions.TikTokLiveRequestException;
import io.github.jwdeveloper.tiktok.exceptions.TikTokLiveUnknownHostException;
import io.github.jwdeveloper.tiktok.exceptions.TikTokSignServerException;
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
import objmoveutils.Position;
import objmoveutils.TileCoord;
import player.Player;
import tools.Draw;
import tools.FindProps;
import tools.GameFonts;
import tools.Tools;
import util.DurationTimerFX;
import util.FindFile;
import util.FrameTimerFX;
import util.IniFile;
import util.Misc;
import util.MyMath;

public class GameTikTok {

	public static int pallete = 0;
	private static List<String> echos = new LinkedList<>();
	private static final int WIN_W = Main.TILE_SIZE * 32;
	private static final int WIN_H = (int)(Main.TILE_SIZE * 16.5);

	@FXML
	private Canvas canvasMain;

	private boolean connectToLive;
	private String liveOwnerUserName;
	private String liveUserToConnect;
	private IniFile tiktokIniFile = IniFile.getNewIniFileInstance("tiktok.ini");
	private IniFile scoresIniFile = IniFile.getNewIniFileInstance("scores.ini");
	private IniFile fixedBombersIniFile = IniFile.getNewIniFileInstance("fixedBombers.ini");
	private Map<Integer, String> eventsDescription = new LinkedHashMap<>();
	private Map<Integer, Pair<String, Consumer<String>>> likeEvents = new LinkedHashMap<>();
	private Map<Integer, Pair<String, Consumer<String>>> giftEvents = new LinkedHashMap<>();
	private Map<String, Integer> userLikes = new LinkedHashMap<>();
	private Map<String, Image> userPics = new LinkedHashMap<>();
	private Map<String, Score> userScores = new LinkedHashMap<>();
	private Map<String, List<Entity>> userEntityOwner = new LinkedHashMap<>();
	private Map<String, List<FixedBomberMan>> bomberMans = new LinkedHashMap<>();
	private Set<TileCoord> settedEntities = new LinkedHashSet<>();
	private Set<TileCoord> droppedWalls = new LinkedHashSet<>();
	private LiveClient liveClient;
	private Set<PassThrough> wallTilePassThrough = Set.of(PassThrough.BOMB, PassThrough.BRICK, PassThrough.HOLE, PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER, PassThrough.WATER);
	private Set<PassThrough> itemPassThrough = Set.of(PassThrough.MONSTER, PassThrough.PLAYER);
	private Set<PassThrough> passThrough = Set.of(PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER);
	private List<KeyCode> holdedKeys = new LinkedList<>();
	private List<String> userNames = new LinkedList<>();
	private List<SampleGift> sampleGifts = new LinkedList<>();
	private int sampleGiftPos = 0;
	private int testGiftId = 0;
	private GraphicsContext gcMain;
	private Font font12 = new Font("Lucida Console", 12);
	private Font font15 = new Font("Lucida Console", 15);
	private Font font40 = new Font("Lucida Console", 40);
	private int showBlockMarks;
	private boolean reconnectionIsDisabled;
	private String gameMap = "TikTok-Small-Battle-01";
	private String lastGiftSel;
	private Duration displayAvatarDelay = Duration.seconds(3);
	private Position mousePos = new Position();
	private List<TikTokGiftEvent> acumulatedGifts = new LinkedList<>();
	private List<String> alreadyFollowed = new LinkedList<>();
	private List<String> alreadyShared = new LinkedList<>();
	
	public void init() {
		setBasics();
		loadGiftList();
		loadUserPics();
		loadConfigs();
		loadLiveEvents();
		setEvents();
		loadMap();
		restoreFixedBomberMansFromIni();
		setJoysticksOnDisconnectEvents();
		if (connectToLive)
			startTikTokEvents();
		mainLoop();
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
			if (showBlockMarks > 0)
				showBlockMarks();
			if (!Misc.alwaysTrue()) { // Sinalizar objetos proximos do cursor, para testar o findInRect()
				List<FindProps> founds = Tools.findInRect(mousePos.getTileCoord(), null, 2, Set.of(FindType.GOOD_ITEM, FindType.BAD_ITEM));
				if (founds != null) {
					if (Misc.blink(100))
						Draw.markTile(founds.get(0).getCoord(), Color.WHITE);
					else
						Draw.markTile(founds.get(0).getCoord(), founds.get(0).getFoundType() == FindType.BAD_ITEM ? Color.RED : Color.LIGHTGREEN);
				}
			}
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

	private void loadLiveEvents() {
		giftEvents = new LinkedHashMap<>();
		likeEvents = new LinkedHashMap<>();

		eventsDescription.put(-1, "Compartilhar");
		eventsDescription.put(-2, "Novo seguidor");
		
		// 50 Likes - (Dropa 1 Bomba)
		eventsDescription.put(-3, "50 Likes");
		likeEvents.put(50, new Pair<>(null, userName -> dropRandomTileBomb(userName)));
		
		// 500 Likes - (Entrar na arena por 1 round)
		eventsDescription.put(-4, "500 Likes");
		likeEvents.put(500, new Pair<>("@USER entrou na arena por 1 rodada.", userName -> dropCpu(userName, 1)));

		// Coração circulo azul (Tira 2 itens do flavio)
		eventsDescription.put(6247, "Coração circulo azul");
		giftEvents.put(6247, new Pair<>(null, userName -> {
			if (bomberMans.containsKey(liveOwnerUserName)) {
				speech(removeNonAlphanumeric(userName) + " removeu 2 itens do Flavio.");
				bomberMans.get(liveOwnerUserName).get(0).getBomberMan().dropItem(2, true, true);
			}
			else
				dropRandomTileBomb(userName);
		}));
		
		// Rosa (Dropa 1 Bomba)
		eventsDescription.put(5655, "Rosa");
		giftEvents.put(5655, new Pair<>(null, userName -> dropRandomTileBomb(userName)));
		
		// Thumbs Up (Planta uma mina em um local aleatório)
		eventsDescription.put(6246, "Thumbs Up");
		giftEvents.put(6246, new Pair<>(null, userName -> setRandomTileMine(userName)));
		
		// Mini Dino (Dropa 12 Bombas)
		eventsDescription.put(6560, "Mini Dino");
		eventsDescription.put(7553, "Mini Dino");
		eventsDescription.put(7591, "Mini Dino");
		giftEvents.put(6560, new Pair<>("Chuva de bombas de @USER.", userName -> dropRandomTileBomb(userName, 12)));
		giftEvents.put(7553, new Pair<>("Chuva de bombas de @USER.", userName -> dropRandomTileBomb(userName, 12)));
		giftEvents.put(7591, new Pair<>("Chuva de bombas de @USER.", userName -> dropRandomTileBomb(userName, 12)));

		// Perfume (Dropa 10 bombas de coração)
		eventsDescription.put(5658, "Perfume");
		giftEvents.put(5658, new Pair<>("Chuva de bombas de coração de @USER.", userName -> dropRandomTileBomb(userName, BombType.HEART, 4, 10)));
		
		// Bouquet (Dropa 4 super-bombas)
		eventsDescription.put(5780, "Bouquet");
		giftEvents.put(5780, new Pair<>("Chuva de super-bombas de @USER.", userName -> dropRandomTileBomb(userName, BombType.MAGMA, 4, 4)));
		
		// Bastão brilhante (Dropa 1 bloco permanente)
		eventsDescription.put(6788, "Bastão brilhante");
		giftEvents.put(6788, new Pair<>(null, userName -> dropRandomTileWall(userName)));

		// Anime-se (Dropa 11 blocos permanente)
		eventsDescription.put(8243, "Anime-se");
		giftEvents.put(8243, new Pair<>("@USER dropou 11 blocos.", userName -> dropRandomTileWall(userName, 11)));

		// Sorvete (Dropa 1 Item)
		eventsDescription.put(5827, "Sorvete");
		giftEvents.put(5827, new Pair<>(null, userName -> dropRandomTileItem(userName)));
		
		// Coração (Dropa 12 Items)
		eventsDescription.put(5327, "Coração");
		eventsDescription.put(5576, "Coração");
		giftEvents.put(5327, new Pair<>("Chuva de itens de @USER.", userName -> dropRandomTileItem(userName, 12)));
		giftEvents.put(5576, new Pair<>("Chuva de itens de @USER.", userName -> dropRandomTileItem(userName, 12)));
		
		// GG (Dropa 1 Caveira)
		eventsDescription.put(5827, "GG");
		giftEvents.put(5827, new Pair<>(null, userName -> dropRandomTileCurse(userName)));
		
		// Fantasminha (Dropa 12 Caveiras)
		eventsDescription.put(5576, "Fantasminha");
		giftEvents.put(5576, new Pair<>("Chuva de caveiras de @USER.", userName -> dropRandomTileCurse(userName, 12)));
		
		// Rosquinha (Ativa o Hurry Up acelerado)
		eventsDescription.put(5879, "Rosquinha");
		giftEvents.put(5879, new Pair<>("@USER ativou o hurry up acelerado.", userName -> MapSet.setHurryUpState(true, Duration.millis(100))));
		
		// Bracelete da equipe (Entra na arena por 2 rodadas)
		eventsDescription.put(9139, "Bracelete da equipe");
		giftEvents.put(9139, new Pair<>("@USER entrou na arena por 2 rodadas.", userName -> dropCpu(userName, 2)));
		
		// Coração com os dedos (Entra na arena por 5 rodadas)
		eventsDescription.put(5487, "Coração com os dedos");
		giftEvents.put(5487, new Pair<>("@USER entrou na arena por 5 rodadas.", userName -> dropCpu(userName, 5)));
		
		// Heart Me (Entra na arena por 10 rodadas (So pode enviar 1 vez por dia))
		eventsDescription.put(7934, "Heart Me");
		giftEvents.put(7934, new Pair<>("@USER entrou na arena por 10 rounds.", userName -> dropCpu(userName, 10)));
		
		// Rosa branca (Reviver o flavio SE ELE ESTIVER MORTO, caso contrario, da 1 item aleatorio para o flavio)
		eventsDescription.put(8239, "Rosa branca");
		giftEvents.put(8239, new Pair<>(null, userName -> reviveSomeone(userName, liveOwnerUserName)));
		
		// Te amo (Dropa 1 caveira no flavio)
		eventsDescription.put(6890, "Te amo");
		giftEvents.put(6890, new Pair<>(null, userName -> curseToSomeone(userName, liveOwnerUserName)));
		
		// Poder da equipe (Revive todos os bombermans mortos)
		eventsDescription.put(12356, "Poder da equipe");
		giftEvents.put(12356, new Pair<>(null, userName -> reviveAll(userName)));

		// Fatia de bolo (Revive 1 dos bombermans pertencentes ao usuario. Se não houver bombermans do usuario mortos, da 1 item aleatorio para um deles)
		eventsDescription.put(6784, "Fatia de bolo");
		giftEvents.put(6784, new Pair<>(null, userName -> reviveAll(userName, userName)));
		
		// TikTok (Da 1 item aleatoriamente para um dos bombermans da pessoa)
		eventsDescription.put(5269, "TikTok");
		giftEvents.put(5269, new Pair<>(null, userName -> giveItenToSomeone(userName, userName, 1)));

		// Colar da amizade (Dá 12 itens aleatoriamente para bombermans aleatorios da pessoa)
		eventsDescription.put(9947, "Colar da amizade");
		giftEvents.put(9947, new Pair<>(null, userName -> giveItenToSomeone(userName, userName, 12)));
		
		// Rosa vermelha grande (Dá 12 itens aleatoriamente para bombermans aleatorios do flavio)
		eventsDescription.put(8913, "Rosa vermelha grande");
		giftEvents.put(8913, new Pair<>(null, userName -> giveItenToSomeone(userName, liveOwnerUserName, 12)));
	}
	
	private void curseToSomeone(String userName, String target) {
		if (!bomberMans.containsKey(target)) {
			dropRandomTileCurse(userName);
			return;
		}
		BomberMan bomber = bomberMans.get(target).get(0).getBomberMan();
		if (!bomber.isDead()) {
			Item.addItem(bomber.getTileCoordFromCenter(), ItemType.CURSE_SKULL);
			speech(removeNonAlphanumeric(userName) + " dropou 1 caveira para " + target + ".");
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
			speech(removeNonAlphanumeric(userName) + " reviveu " + target + ".");
			bomber.softResetAfterMapChange(240);
		}
		else {
			Item.addItem(bomber.getTileCoordFromCenter(), ItemType.getRandomForBattle());
			speech(removeNonAlphanumeric(userName) + " dropou 1 item para " + liveOwnerUserName + ".");
		}				
	}

	private void reviveAll(String userName) {
		reviveAll(userName, null);
	}

	private void reviveAll(String userName, String targetUser) {
		String target = targetUser == null ? "os" : "os seus";
		speech(removeNonAlphanumeric(userName) + " reviveu todos " + target + " bombermans.");
		for (String user : bomberMans.keySet()) {
			if (targetUser == null || user.equals(targetUser))
				bomberMans.get(user).forEach(b -> {
					BomberMan bomber = b.getBomberMan();
					if (bomber.isDead())
						bomber.reviveAndClearItens(300);
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
	
	private void giveItenToSomeone(String userName, String targetUser, int quant) {
		String target = targetUser == null ? " aleatório" : targetUser.equals(userName) ? " do seu grupo" : " do grupo de " + targetUser;
		String itens = quant == 1 ? "1 item" : quant + " itens";
		speech(removeNonAlphanumeric(userName) + " deu " + itens + " para alguém " + target + ".");
		for (int n = 0; n < quant; n++)
			for (String user : bomberMans.keySet()) {
				if (targetUser == null || user.equals(targetUser))
					bomberMans.get(user).forEach(b -> {
						BomberMan bomber = b.getBomberMan();
						if (!bomber.isDead())
							Item.addItem(bomber.getTileCoordFromCenter(), ItemType.getRandomForBattle());
					});
			}
	}
	
	private void setBasics() {
		Main.setZoom(3);
		canvasMain.setWidth(WIN_W * Main.getZoom());
		canvasMain.setHeight(WIN_H * Main.getZoom());
		Main.setMainCanvas(canvasMain);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		liveClient = null;
		reconnectionIsDisabled = false;
		showBlockMarks = 0;
		Platform.runLater(() -> {
			DurationTimerFX.createTimer("ChangeSampleGiftPos", Duration.seconds(2), 0, () -> {
				if (++sampleGiftPos == sampleGifts.size())
					sampleGiftPos = 0;
			});
		});
		connectToLive = tiktokIniFile.readAsBoolean("LIVE", "ConnectToLive", false);
		liveOwnerUserName = tiktokIniFile.read("LIVE", "UserNameForAssociateToMainBomberMan");
		liveUserToConnect = tiktokIniFile.read("LIVE", "LiveUserNameToConnect");
		lastGiftSel = null;
	}

	private void loadMap() {
		MapSet.loadMap(gameMap);
		MapSet.setOnStageObjectiveClearEvent(() -> {
			Platform.runLater(() -> {
				DurationTimerFX.createTimer("IncScore", Duration.seconds(1), () -> {
					for (String user : bomberMans.keySet())
						for (FixedBomberMan fixedBomber : bomberMans.get(user))
							if (!fixedBomber.getBomberMan().isDead()) {
								if (!userScores.containsKey(user))
									userScores.put(user, new Score(user));
								userScores.get(user).incScore();
								scoresIniFile.write(liveUserToConnect, user, "" + userScores.get(user).getScore());
							}
				});
			});
		});
		MapSet.setOnMapLoadEvent(() -> {
			/* NAO ADICIONAR AQUI EVENTOS QUE DEVEM RODAR APOS CARREGAR O MAPA DE PRIMEIRA
			 * (QUANDO ABRE O PROGRAMA) POIS ESSE EVENTO SO IRA DISPARAR APOS CARREGAR O MAPA
			 * DEPOIS DA PRIMEIRA PARTIDA. ISSO DEVE SE MANTER ASSIM DEVIDO A FORMA COMO
			 * O PROGRAMA CARREGA OS BOMBERMANS FIXOS AO ABRIR O PROGRAMA, E COMO ELE ATUALIZA
			 * CADA VEZ QUE O MAPA RECARREGA
			 */
			List<Pair<String, FixedBomberMan>> removeUsers = new LinkedList<>();
			for (String user : bomberMans.keySet())
				for (FixedBomberMan fixedBomber : bomberMans.get(user)) {
					if (fixedBomber.getLeftRounds() > 0) {
						fixedBomber.decLeftRounds();
						if (fixedBomber.getLeftRounds() == 0) {
							BomberMan.removeBomberMan(fixedBomber.getBomberMan());
							removeUsers.add(new Pair<>(user, fixedBomber));
						}
					}
				}
			for (Pair<String, FixedBomberMan> pair : removeUsers) {
				BomberMan.getBomberManList().remove(pair.getValue().getBomberMan());
				bomberMans.get(pair.getKey()).remove(pair.getValue());
				if (bomberMans.get(pair.getKey()).isEmpty())
					bomberMans.remove(pair.getKey());
			}
			settedEntities.clear();
			droppedWalls.clear();
			userEntityOwner.clear();
			updateFixedBomberMansOnIniFile();
		});
	}

	private void restoreFixedBomberMansFromIni() {
		for (int n = 0; n < 2; n++) {
			final int n2 = n;
			fixedBombersIniFile.getItemList(liveUserToConnect).forEach(user -> {
				for (String s : fixedBombersIniFile.read(liveUserToConnect, user).split(" ")) {
					String[] split = s.split("¡");
					if (n2 == 0 && (split.length < 4 || !split[3].equals("-"))) {
						if (!bomberMans.containsKey(user))
							bomberMans.put(user, new LinkedList<>());
						bomberMans.get(user).add(new FixedBomberMan(user, s));
					}
				}
			});
		}
	}

	private void loadConfigs() {
		ZoneOffset offset = ZoneOffset.ofHours(-3); // Horário local (GMT-3)
		long millisOffset = offset.getTotalSeconds() * 1000L;
		int today = (int)((System.currentTimeMillis() + millisOffset) / Duration.hours(24).toMillis());
		// Se o dia mudou, limpar scores, bombermans fixos salvos, registro dos que ja seguiram ou ja compartilharam e imagens dos usuarios (pois elas podem mudar)
		if (tiktokIniFile.readAsInteger("CONFIG", "Today", null) != today) {
			scoresIniFile.clearFile();
			fixedBombersIniFile.clearFile();
			tiktokIniFile.remove("CONFIG", "AlreadyFollowed");
			tiktokIniFile.remove("CONFIG", "AlreadyShared");
			Platform.runLater(() -> {
				FindFile.findDir("./appdata/userPics/", "*").forEach(dir -> {
					for (File file : new ArrayList<>(FindFile.findFile(dir.getAbsolutePath(), "*.png")))
						file.delete();
				});
			});
		}
		else {
			// Restaura scores dos jogadores
			scoresIniFile.getItemList(liveUserToConnect).forEach(user ->
				userScores.put(user, new Score(user, scoresIniFile.readAsInteger(liveUserToConnect, user, 0))));
			// Restaura estado dos que ja seguiram/compartilharam
			for (String item : Arrays.asList("AlreadyFollowed", "AlreadyShared"))
				if (scoresIniFile.read("CONFIG", item) != null) {
					String[] split = scoresIniFile.getLastReadVal().split(" ");
					for (String user : split) {
						(item.equals("AlreadyShared") ? alreadyShared : alreadyFollowed).add(user);
						if (item.equals("AlreadyShared"))
							Platform.runLater(() -> DurationTimerFX.createTimer("Share@" + user, Duration.minutes(5), () -> alreadyShared.remove(user)));
					}
				}
		}
		tiktokIniFile.write("CONFIG", "Today", "" + today);
	}

	private void createUserPicsDir() {
		Path path = Paths.get("./appdata/userPics/" + liveUserToConnect + "/");
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			}
			catch (IOException e) {
				throw new RuntimeException("falha ao criar o diretório \"" + path.getFileName() + "\"");
			}
		}
	}
	
	private void loadUserPics() {
		createUserPicsDir();
		for (String path : Arrays.asList("appdata/userPics/" + liveUserToConnect + "/","*.png", "appdata/userPics/","*.png"))
			FindFile.findFile(path).forEach(file -> {
				String userName = file.getName().replace(".png", "");
				Image image = roundedBordersImage(new Image("file:" + file.getAbsolutePath()));
				userPics.put(userName, image);
			});
	}

	private void loadGiftList() {
		createUserPicsDir();
		FindFile.findFile("appdata/gifts/","*.png").forEach(file -> {
			String name = file.getName().replace(".png", "");
			Image image = new Image("file:" + file.getAbsolutePath());
			sampleGifts.add(new SampleGift(name, image));
		});
		sampleGifts.sort((s1, s2) -> s1.getOrder() - s2.getOrder());
	}
	
	void setEvents() {
		canvasMain.setOnMouseMoved(e -> {
			mousePos.setPosition((e.getX() + 32 * Main.getZoom()) / Main.getZoom(), (e.getY() + 32 * Main.getZoom()) / Main.getZoom());
		});
		Main.sceneMain.setOnKeyPressed(e -> {
			Player.convertOnKeyPressEvent(e);
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.Z) {
				int n = (int)MyMath.getRandom(0, BomberMan.getTotalBomberMans() - 1);
				for (int x = 0; x < BomberMan.getTotalBomberMans(); x++)
					if (x != n)
						BomberMan.getBomberMan(x).takeDamage();
			}
			if (e.getCode() == KeyCode.X) {
				Bomb.addBomb(mousePos.getTileCoord(), BombType.REMOTE, 2);
			}
			if (e.getCode() == KeyCode.C)
				Bomb.addBomb(mousePos.getTileCoord(), BombType.MAGMA, 3);
			if (e.getCode() == KeyCode.P) {
				for (Brick brick : new LinkedList<>(Brick.getBricks()))
					brick.destroy();
				for (Item item : new LinkedList<>(Item.getItems()))
					item.forceDestroy();
			}
			if (e.getCode() == KeyCode.G) {
				String[] users = userPics.keySet().toArray(new String[userPics.size()]);
				runOnGiftEvent(users[(int)MyMath.getRandom(0, users.length - 1)], "Gift", testGiftId, 1);
			}
			if (e.getCode() == KeyCode.H) {
				List<String> list = new LinkedList<>();
				list.add(-1 + " - " + eventsDescription.get(-1));
				list.add(-2 + " - " + eventsDescription.get(-2));
				list.add(-3 + " - " + eventsDescription.get(-3));
				list.add(-4 + " - " + eventsDescription.get(-4));
				for (int i : giftEvents.keySet())
					list.add(i + " - " + eventsDescription.get(i));
				lastGiftSel = Alerts.choiceCombo("Gift", "Selecione o ID do Gift\npara disparar com a tecla G", list, lastGiftSel);
				if (lastGiftSel != null)
					testGiftId = Integer.parseInt(lastGiftSel.split(" ")[0]);
			}
			if (e.getCode() == KeyCode.U)
				dropRandomTileBomb("GM");
			if (e.getCode() == KeyCode.J)
				dropRandomTileWall("GM");
			if (e.getCode() == KeyCode.I)
				dropRandomTileItem("GM");
			if (e.getCode() == KeyCode.K)
				dropRandomTileBrick("GM");
			if (e.getCode() == KeyCode.M && ++showBlockMarks == 4)
				showBlockMarks = 0;
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
		if (image == null)
			image = userPics.get("no-photo.png");
		int sprHeight = entity.getCurrentFrameSet().getSprite(0).getOutputWidth(), 
				w = (int)image.getWidth(), ww = (int)(Main.TILE_SIZE * Main.getZoom() * 1.3),
				x = (int)entity.getX() * Main.getZoom() - 32 * Main.getZoom(),
				y = (int)((entity.getY() - entity.getHeight()) * Main.getZoom() - 32 * Main.getZoom() - ww * 1.5);
		if (y < 0)
			y += (sprHeight * Main.getZoom()) + ww * 1.3;
		gcMain.drawImage(image, 0, 0, w, w, x + Main.TILE_SIZE / 2 * Main.getZoom() - ww / 2, y, ww, ww);
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
		gcMain.setFont(font40);
		Text text = new Text();
		text.setFont(font40);
		gcMain.fillText(name, 100, canvasMain.getHeight() - 25);
		gcMain.strokeText(name, 100, canvasMain.getHeight() - 25);
		gcMain.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 10, canvasMain.getHeight() - 68, 64, 64);
	}

	private void drawScores() {
		List<Score> scores = new LinkedList<>(userScores.values());
		scores.sort((o1, o2) -> o2.getScore() - o1.getScore());
		for (int n = 0, hh = Main.TILE_SIZE * Main.getZoom(); n < scores.size(); n++) {
			Score score = scores.get(n);
			Image i = userPics.get(score.getUserName());
			if (i == null)
				i = userPics.get("no-photo.png");
			gcMain.drawImage(i, 0, 0, i.getWidth(), i.getHeight(), canvasMain.getWidth() - 135, 60 + n * hh * 1.1, hh, hh);
			gcMain.setFill(Color.WHITE);
			gcMain.setFont(GameFonts.fontBomberMan20);
			gcMain.fillText("x " + score.getScore(), canvasMain.getWidth() - 80, 93 + n * hh * 1.1);
		}
	}

	private void runGiftEventsQueue() {
		if (!acumulatedGifts.isEmpty() && !MapSet.stageObjectiveIsCleared() && Draw.getFade() == null) {
			Platform.runLater(() -> {
				for (TikTokGiftEvent giftEvent : acumulatedGifts)
					DurationTimerFX.createTimer("RunningOnGiftEventQueue" + Main.uniqueTimerId++, Duration.seconds(10), () ->
						runOnGiftEvent(giftEvent.getUser().getName(), giftEvent.getGift().getName(), giftEvent.getGift().getId(), giftEvent.getCombo()));
				acumulatedGifts.clear();
			});
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
		gcMain.setFont(font12);
		int y = (int)canvasMain.getHeight();
		for (String s : echos)
			gcMain.fillText(s, canvasMain.getWidth() - 130, y -= 11);
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
		Draw.drawBlockTypeMarks(gcMain, -32 * Main.getZoom(), -32 * Main.getZoom(), Main.getZoom(), showBlockMarks == 1, showBlockMarks == 2, showBlockMarks == 3, t -> {
			return Bomb.haveBombAt(t.getTileCoord()) ? Color.RED :
				Brick.haveBrickAt(t.getTileCoord()) ? Color.GREEN :
				Item.haveItemAt(t.getTileCoord()) ? Color.DARKORANGE : null;
		});
		Draw.drawTileTagsOverCursor(canvasMain, font15, (int)mousePos.getX() * Main.getZoom(), (int)mousePos.getY() * Main.getZoom(), -32 * Main.getZoom(), -32 * Main.getZoom());
		Draw.drawTilePropsOverCursor(canvasMain, font15, (int)mousePos.getX() * Main.getZoom(), (int)mousePos.getY() * Main.getZoom(), -32 * Main.getZoom(), -32 * Main.getZoom());
	}

	private void startTikTokEvents() {
		liveClient = TikTokLive.newClient(liveUserToConnect)
				.configure((settings) -> {
					settings.setClientLanguage("pt");
					settings.setLogLevel(Level.OFF);
					settings.setPrintToConsole(false);
	        settings.setRetryOnConnectionFailure(true);
	        settings.setRetryConnectionTimeout(java.time.Duration.ofSeconds(5));
				})
				.onConnected((liveClient, event) -> {
					String text  ="Conectado á live de " + liveClient.getRoomInfo().getHostName();
					speech(text);
					System.out.println(text);
				})
				.onDisconnected((liveClient, event) -> {
					if (!Main.close && !reconnectionIsDisabled) {
						String text = "Desconectado da live de " + liveClient.getRoomInfo().getHostName() + ". Reconectando em 10 segundos...";
						speech(text);
						System.out.println(text);
						Platform.runLater(() -> DurationTimerFX.createTimer("TiTokLiveReconnection", Duration.seconds(10), () -> liveClient.connectAsync()));
					}
					liveClient.disconnect();
				})
				.onError((liveClient, event) -> {
					if (!Main.close) {
						String erro = event.getException().getMessage();
						if (event.getException() instanceof TikTokLiveUnknownHostException)
							erro = "Username inválido: " + liveClient.getRoomInfo().getHostName();
						else if (event.getException() instanceof TikTokSignServerException)
							erro = "Falha ao conectar (Sign server Error. Try again later)";
						else if (event.getException() instanceof TikTokLiveOfflineHostException)
							erro = "Usuário " + liveClient.getRoomInfo().getHostName() + " está offline.";
						else if (event.getException() instanceof TikTokLiveRequestException)
							erro = "* Erro 404 ao conectar em " + liveClient.getRoomInfo().getHostName();
						else if (erro.contains("request timed out"))
							erro = "Falha ao conectar (Request timed out)";
						else if (erro.contains("Error while handling Message: WebcastLikeMessage")) {
							speech("Webcast Erro.");
							event.getException().printStackTrace();
						}
						else if (erro.contains("Sign server rate limit reached."))
							erro = "Você alcançou o limite diário de conexões.";
						reconnectionIsDisabled = true;
						System.out.println(erro);
						event.getException().printStackTrace();
						if (erro.length() > 100)
							erro = erro.substring(0, 100);
						speech(erro);
					}
				})
				.onFollow((liveClient, event) -> {
					downloadUserImage(event.getUser().getName(), event.getUser().getPicture());
					onFollowEvent(event.getUser().getName());
				})
				.onComment((liveClient, event) -> downloadUserImage(event.getUser().getName(), event.getUser().getPicture()))
				.onEmote((liveClient, event) -> downloadUserImage(event.getUser().getName(), event.getUser().getPicture()))
				.onShare((liveClient, event) -> {
					downloadUserImage(event.getUser().getName(), event.getUser().getPicture());
					onShareEvent(event.getUser().getName());
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
					String userName = event.getUser().getName();
					downloadUserImage(userName, event.getUser().getPicture());
					onLikeEvent(userName, event.getLikes());
				}).build();
		liveClient.connectAsync();
	}

	private void onLikeEvent(String userName, int totalLikes) {
		if (!MapSet.stageObjectiveIsCleared()) {
			Platform.runLater(() -> {
				if (!userLikes.containsKey(userName))
					userLikes.put(userName, 0);
				int likes = userLikes.get(userName);
				List<Integer> list = new LinkedList<>(likeEvents.keySet());
				list.sort((n1, n2) -> n1 - n2);
					for (int n = 0; n < totalLikes; n++) {
						likes++;
						for (Integer i : list)
							if (likes % i == 0) {
								if (n == 0 && likeEvents.get(i).getKey() != null)
									speech(likeEvents.get(i).getKey().replace("@USER", removeNonAlphanumeric(userName)));
								DurationTimerFX.createTimer("likeEvent@" + Main.uniqueTimerId++, Duration.ZERO, () -> likeEvents.get(i).getValue().accept(userName));
							}
					}
					userLikes.put(userName, likes);
			});
		}
	}

	private void onShareEvent(String userName) {
		if (!MapSet.stageObjectiveIsCleared()) {
			if (!alreadyShared.contains(userName)) {
				// Compartilhar a live (Adiciona avatar sem itens e que participará de 1 round (So funciona 1 vez a cada 5 minutos)
				speech(removeNonAlphanumeric(userName) + " entrou na arena por 1 round por compartilhar a live");
				dropCpu(userName);
				alreadyShared.add(userName);
			}
			Platform.runLater(() -> DurationTimerFX.createTimer("Share@" + userNames, Duration.minutes(5), () -> alreadyShared.remove(userName)));
		}
	}

	private void onFollowEvent(String userName) {
		if (!MapSet.stageObjectiveIsCleared()) {
			if (!alreadyFollowed.contains(userName)) {
				// Seguir (Adiciona avatar sem itens e que participará de 10 round (So funciona 1 vez por dia)
				speech("Novo seguidor " + removeNonAlphanumeric(userName) + " entrou na arena por 5 rounds");
				dropCpu(userName, 5);
				alreadyFollowed.add(userName);
			}
		}
	}

	private void runOnGiftEvent(String userName, String giftName, int giftId, int combo) {
		List<Integer> list = new LinkedList<>(giftEvents.keySet());
		list.sort((n1, n2) -> n1 - n2);
		if (giftId < 0) {
			for (int n = 0; n < combo; n++) {
					if (giftId == -1)
						onShareEvent(userName);
					else if (giftId == -2)
						onFollowEvent(userName);
					else if (giftId == -3)
						onLikeEvent(userName, 50);
					else if (giftId == -4)
						onLikeEvent(userName, 500);
				}
		}
		else
			Platform.runLater(() -> {
				for (int n = 0; n < combo; n++)
					for (Integer id : list)
						if (giftId == id)
							FrameTimerFX.createTimer("likeEvent@" + Main.uniqueTimerId++, n * 20, () -> {
								giftEvents.get(id).getValue().accept(userName);
								if (giftEvents.get(id).getKey() != null)
									speech(giftEvents.get(id).getKey().replace("@USER", removeNonAlphanumeric(userName)));
							});
			});
	}

	private void downloadUserImage(String userName, Picture picture) {
		if (userPics.containsKey(userName))
			return;
		userPics.put(userName, userPics.get("no-photo.png"));		
		new Thread(() -> {
			picture.downloadImageAsync().thenAccept(image ->
				Platform.runLater(() -> {
					BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = bufferedImage.createGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.drawImage(image, 0, 0, null);
					g2d.dispose();
					Image i = SwingFXUtils.toFXImage(bufferedImage, null);
					ImageUtils.saveImageToFile(i, "appdata/userPics/" + liveUserToConnect + "/" + userName + ".png");
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
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Bomb.dropBombFromSky(t, 4));
	}

	private void dropRandomTileBomb(String userName, BombType bombType, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Bomb.dropBombFromSky(t, bombType, 4));
	}

	private void dropRandomTileBomb(String userName, int fireRange, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Bomb.dropBombFromSky(t, fireRange));
	}

	private void dropRandomTileBomb(String userName, BombType bombType, int fireRange, int quant) {
		dropRandomTileStuff(userName, quant, false, passThrough, t -> Bomb.dropBombFromSky(t, bombType, fireRange));
	}
	
	private void setRandomTileMine(String userName) {
		Platform.runLater(() -> MapSet.getRandomFreeTileAsync(passThrough, false, t -> !droppedWalls.contains(t) && !settedEntities.contains(t)).thenAccept(coord -> Bomb.addBomb(coord, BombType.LAND_MINE, 3)));
	}

	private void dropRandomTileStuff(String userName, int quant, boolean testCoord, Set<PassThrough> passThrough, Function<TileCoord, Entity> function) {
		Platform.runLater(() -> {
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
									userEntityOwner.put(userName, new LinkedList<>());
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
		});
	}

	private void dropCpu(String userName) {
		dropCpu(userName, 1, true, null);
	}

	private void dropCpu(String userName, ItemType ... initialItens) {
		dropCpu(userName, 1, true, initialItens);
	}
	
	private void dropCpu(String userName, int rounds) {
		dropCpu(userName, rounds, true, null);
	}

	private void dropCpu(String userName, int rounds, ItemType ... initialItens) {
		dropCpu(userName, rounds, true, initialItens);
	}
	
	private void dropCpu(String userName, boolean updateIniFile) {
		dropCpu(userName, 1, updateIniFile, null);
	}

	private void dropCpu(String userName, boolean updateIniFile, ItemType ... initialItens) {
		dropCpu(userName, 1, updateIniFile, initialItens);
	}
	
	private void dropCpu(String userName, int rounds, boolean updateIniFile) {
		dropCpu(userName, rounds, updateIniFile, null);
	}

	private void dropCpu(String userName, int rounds, boolean updateIniFile, ItemType ... initialItens) {
		MapSet.getRandomFreeTileAsync().thenAccept(coord -> {
			if (!bomberMans.containsKey(userName))
				bomberMans.put(userName, new LinkedList<>());
			FixedBomberMan fixedBomber;
			bomberMans.get(userName).add(fixedBomber = new FixedBomberMan(userName, rounds, coord, 1, getNextPallete(1), CpuDificult.VERY_HARD));
			if (updateIniFile)
				updateFixedBomberMansOnIniFile();
			if (initialItens != null) {
				for (ItemType item : initialItens)
					fixedBomber.getBomberMan().getItemList().add(item);
				fixedBomber.getBomberMan().updateStatusByItems();
			}
		});
	}

	private void updateFixedBomberMansOnIniFile() {
		if (fixedBombersIniFile.sectionExists(liveUserToConnect))
			fixedBombersIniFile.clearSection(liveUserToConnect);
		for (String user : new ArrayList<>(bomberMans.keySet()))
			for (FixedBomberMan fixedBomber : bomberMans.get(user))
				fixedBombersIniFile.write(liveUserToConnect, user, fixedBomber.toString());
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
	
	public static Integer getCurrentPalleteIndex(int bomberManIndex) {
		return pallete;
	}

	public static int getNextPallete(int bomberManIndex) {
		int p = pallete;
		if (++pallete == 17)
			pallete = 0;
		return p;
	}

	public boolean addNewPlayer(BomberMan bomber) {
		List<String> users = new LinkedList<>(userPics.keySet());
		for (String user : bomberMans.keySet())
			if (userNames.contains(user))
				users.remove(user);
		users.sort((s1, s2) -> s1.compareTo(s2));
		users.remove("Random CPU");
		users.remove("GM");
		users.add(0, "Random CPU");
		String user = Alerts.choiceCombo("Prompt", "Adicionar jogador", "Escolha o nome de usuário para associar ao jogador", users);
		boolean ok = user != null && !user.equals("Random CPU");
		if (user == null)
			user = "Random CPU";
		if (!bomberMans.containsKey(user))
			bomberMans.put(user, new LinkedList<>());
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
						List<FixedBomberMan> list2 = new LinkedList<>(bomberMans.get(user));
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
					for (Player player : new LinkedList<>(Player.getPlayers()))
						if (player.getDinputDevice() == device)
							consumer.accept(device.getName(), player);
				});
		});
		JXInputEX.setOnJoystickDisconnectedEvent(device -> {
			if (!Main.close)
				Platform.runLater(() -> {
					for (Player player : new LinkedList<>(Player.getPlayers()))
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
	
	private String userName;
	private BomberMan bomberMan;
	private int bomberManIndex;
	private int palleteIndex;
	private int leftRounds;
	private CpuDificult dificult;

	public FixedBomberMan(String userName, int leftRounds, int bomberManIndex, int palleteIndex) {
		set(null, userName, leftRounds, null, bomberManIndex, palleteIndex, null);
	}

	public FixedBomberMan(String userName, int leftRounds, int bomberManIndex, int palleteIndex, CpuDificult dificult) {
		set(null, userName, leftRounds, null, bomberManIndex, palleteIndex, dificult);
	}
	
	public FixedBomberMan(String userName, int leftRounds, TileCoord coord, int bomberManIndex, int palleteIndex) {
		set(null, userName, leftRounds, coord, bomberManIndex, palleteIndex, null);
	}

	public FixedBomberMan(String userName, int leftRounds, TileCoord coord, int bomberManIndex, int palleteIndex, CpuDificult dificult) {
		set(null, userName, leftRounds, coord, bomberManIndex, palleteIndex, dificult);
	}
	
	public FixedBomberMan(BomberMan bomberMan, String userName, int leftRounds) {
		set(bomberMan, userName, leftRounds, null, null, null, null);
	}

	public FixedBomberMan(BomberMan bomberMan, String userName, int leftRounds, TileCoord coord, Integer bomberManIndex, Integer palleteIndex, CpuDificult dificult) {
		set(bomberMan, userName, leftRounds, coord, bomberManIndex, palleteIndex, dificult);
	}
	
	private void set(BomberMan bomberMan, String userName, int leftRounds, TileCoord coord, Integer bomberManIndex, Integer palleteIndex, CpuDificult dificult) {
		if (coord == null)
			coord = MapSet.getInitialPlayerPosition(BomberMan.getTotalBomberMans()).getTileCoordFromCenter();
		if (bomberMan == null) {
			if (dificult != null)
				bomberMan = BomberMan.dropNewCpu(coord, bomberManIndex, palleteIndex, dificult);
			else {
				int n = BomberMan.getTotalBomberMans();
				bomberMan = BomberMan.addBomberMan(bomberManIndex, palleteIndex);
				Player.addPlayer();
				Player.getPlayer(n).setBomberMan(bomberMan);
			}
			this.bomberManIndex = bomberManIndex;
			this.palleteIndex = palleteIndex;
		}
		else {
			this.bomberManIndex = bomberMan.getBomberIndex();
			this.palleteIndex = bomberMan.getPalleteIndex();
		}
		this.bomberMan = bomberMan;
		this.dificult = dificult;
		this.userName = userName;
		this.leftRounds = leftRounds;
		bomberMan.setInvencibleFrames(300);
		int maxItens = 0;
		BomberMan b = null;
		for (BomberMan bomber : BomberMan.getBomberManList())
			if (bomber.getItemList().size() > maxItens) {
				b = bomber;
				maxItens = b.getItemList().size();
			}
		if (b != null) {
			for (ItemType type : new ArrayList<>(b.getItemList()))
				bomberMan.getItemList().add(type);
			bomberMan.updateStatusByItems();
		}
		if (palleteIndex >= GameTikTok.getCurrentPalleteIndex(bomberManIndex))
			GameTikTok.getNextPallete(bomberManIndex);
	}
	
	public FixedBomberMan(String userName, String fromIniData) {
		String[] split = fromIniData.split("¡");
		bomberManIndex = Integer.parseInt(split[0]);
		palleteIndex = Integer.parseInt(split[1]);
		leftRounds = split.length < 3 ? -1 : Integer.parseInt(split[2]);
		dificult = split.length < 4 || split[3].equals("-") ? null : CpuDificult.valueOf(split[3]);
		TileCoord coord = MapSet.getInitialPlayerPosition(BomberMan.getTotalBomberMans()).getTileCoordFromCenter();
		set(null, userName, leftRounds, coord.getNewInstance(), bomberManIndex, palleteIndex, dificult);
	}
	
	public boolean isCpu() {
		return dificult != null;
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
	
	public int getBomberManIndex() {
		return bomberManIndex;
	}

	public CpuDificult getDificult() {
		return dificult;
	}

	public int getPalleteIndex() {
		return palleteIndex;
	}
	
	public void decLeftRounds() {
		leftRounds--;
	}
	
	@Override
	public String toString() {
		return bomberManIndex + "¡" + palleteIndex + "¡" + leftRounds + "¡" + (dificult == null ? "-" : dificult.name());
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