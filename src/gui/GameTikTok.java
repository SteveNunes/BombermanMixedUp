package gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import entities.BomberMan;
import entities.CpuPlay;
import enums.CpuDificult;
import enums.GameInput;
import enums.GameInputMode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import maps.MapSet;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import player.Player;
import tools.Draw;
import tools.Tools;
import util.TimerFX;

public class GameTikTok {

	public static final int ZOOM = 2;
	private final int WIN_W = Main.TILE_SIZE * 47;
	private final int WIN_H = Main.TILE_SIZE * 23;
	private List<KeyCode> holdedKeys;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Position canvasTileCoord;
	private Font font;
	private static List<String> echos = new ArrayList<>();
	private boolean showBlockMarks;

	public void init() {
		font = new Font("Lucida Console", 9);
		canvasMain.setWidth(WIN_W * ZOOM);
		canvasMain.setHeight(WIN_H * ZOOM);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		holdedKeys = new ArrayList<>();
		canvasTileCoord = new Position();
		setEvents();
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		MapSet.loadMap("TikTok-Battle-01");
		fillWithCpu(1);
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
			player.getBomberMan().setCpuPlay(new CpuPlay(player.getBomberMan(), CpuDificult.EASY));
		}
	}

	private Integer[] setInputKeys = new Integer[] {
			KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, 
			KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8, 
			KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12};
	
	void setEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.SPACE)
				CpuPlay.markTargets = !CpuPlay.markTargets; 
			if (e.getCode() == KeyCode.ESCAPE)
				Main.close();
			for (int n = 0; n < setInputKeys.length; n++)
				if (e.getCode().getCode() == setInputKeys[n])					
					openInputSetup(n, canvasTileCoord.getTileCoord());
			Player.convertOnKeyPressEvent(e);
		});
		Main.sceneMain.setOnKeyReleased(e -> {
			holdedKeys.add(e.getCode());
			Player.convertOnKeyReleaseEvent(e);
		});
		canvasMain.setOnMouseMoved(e -> canvasTileCoord.setPosition((e.getX() + 32 * ZOOM) / ZOOM, (e.getY() + 32 * ZOOM) / ZOOM));
	}
	
	private void openInputSetup(int n, TileCoord coord) {
		BomberMan bomber;
		while (n >= Player.getTotalPlayers())
			Player.addPlayer();
		Player player = Player.getPlayer(n);
		if ((bomber = player.getBomberMan()) == null) {
			bomber = BomberMan.addBomberMan(1, BomberMan.getTotalBomberMans());
			bomber.setPosition(MapSet.getInitialPlayerPosition(BomberMan.getTotalBomberMans() - 1));
			player.setBomberMan(bomber);
		}
		Stage stage = new Stage();
		stage.setTitle("Input config (Player " + (n + 1) + ")");
		VBox vBox = new VBox();
		Scene scene = new Scene(vBox);
		GameInputMode prevInputMode = player.getInputMode();
		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				TimerFX.stopTimer("WaitForDevice");
				player.setOnPressInputEvent(null);
				player.setMappingMode(false);
				player.setInputMode(prevInputMode);
				stage.close();
			}
			else if (player.getInputMode() == GameInputMode.DETECTING)
				player.setInputMode(GameInputMode.KEYBOARD);
			else
				player.pressInput(e.getCode().getCode(), e.getCode().getName());
		});
		stage.setScene(scene);
		vBox.setPrefSize(400, 240);
		vBox.setPadding(new Insets(10, 10, 10, 10));
		vBox.setAlignment(Pos.TOP_CENTER);
		Text text = new Text("Pressione um botão para\nidentificar o dispositivo,\nou pressione ESC para\nmanter configuração atual.");
		text.setFont(new Font("Lucida Console", 20));
		text.setTextAlignment(TextAlignment.CENTER);
		vBox.getChildren().add(text);
		Text[] texts = {new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text("")};
		for (Text t : texts) {
			t.setFont(new Font("Lucida Console", 15));
			t.setTextAlignment(TextAlignment.LEFT);
			vBox.getChildren().add(t);
		}
		player.setMappingMode(true);
		boolean[] done = { false };
		int[] nextText = { 0 };
		GameInput[] inputs = GameInput.values();
		TimerFX.createTimer("WaitForDevice", 20, 0, () -> {
			if (player.getInputMode() != GameInputMode.DETECTING) {
				String str;
				if (player.getInputMode() == GameInputMode.XINPUT)
					str = player.getXinputDevice().getJoystickName(); 
				else if (player.getInputMode() == GameInputMode.DINPUT)
					str = player.getDinputDevice().getName(); 
				else
					str = "Teclado";
				texts[0].setText("Pressione para definir: " + player.getNextMappingInput().name());
				text.setText(str + "\n");
				TimerFX.stopTimer("WaitForDevice");
			}
		});
		player.setOnPressInputEvent(i -> {
			nextText[0]++;
			for (int z = 0; z <= nextText[0]; z++) {
				if (player.getNextMappingInput() != null && !done[0] && z == nextText[0])
					texts[z].setText("Pressione para definir: " + player.getNextMappingInput().name());
				else if (z < 10)
					texts[z].setText(inputs[z] + " = " + (!player.getButtonInfosMap().containsKey(inputs[z]) ? "-" : player.getButtonInfosMap().get(inputs[z]).getName()));
			}
			if (done[0]) {
				player.setOnPressInputEvent(null);
				stage.close();
			}
			else if (player.getNextMappingInput() == null) {
				texts[texts.length - 1].setText("\nConfiguração concluida!");
				done[0] = true;
			}
		});
		stage.setOnCloseRequest(e -> {
			player.setMappingMode(false);
			TimerFX.stopTimer("WaitForDevice");
			if (player.getInputMode() == GameInputMode.DETECTING) {
				player.setInputMode(GameInputMode.CPU);
				player.getBomberMan().setCpuPlay(new CpuPlay(player.getBomberMan(), CpuDificult.EASY));
			}
		});
		stage.showAndWait();
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

}