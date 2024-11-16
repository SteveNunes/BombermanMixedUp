package gui;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import maps.MapSet;
import player.Player;
import tools.Draw;
import tools.Tools;
import util.TimerFX;

public class Game {

	private final int ZOOM = 3;
	private final int WIN_W = Main.TILE_SIZE * 17;
	private final int WIN_H = Main.TILE_SIZE * 14;
	private List<KeyCode> holdedKeys;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Font font;

	public void init() {
		font = new Font("Lucida Console", 15);
		canvasMain.setWidth(WIN_W * ZOOM);
		canvasMain.setHeight(WIN_H * ZOOM);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		holdedKeys = new ArrayList<>();
		setEvents();
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		MapSet.loadMap("SBM2_1-1");
		mainLoop();
	}

	static KeyCode[] setInputKeys = new KeyCode[] {
			KeyCode.F1, KeyCode.F2, KeyCode.F3, KeyCode.F4, KeyCode.F5, KeyCode.F6,
			KeyCode.F7, KeyCode.F8, KeyCode.F9, KeyCode.F10, KeyCode.F11, KeyCode.F12};
	
	void setEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
			Player.convertOnKeyPressEvent(e);
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.ESCAPE)
				Main.close();
			for (int n = 0; n < setInputKeys.length; n++)
				if (e.getCode() == setInputKeys[n])					
					openInputSetup(n);
		});
		Main.sceneMain.setOnKeyReleased(e -> {
			Player.convertOnKeyReleaseEvent(e);
			holdedKeys.add(e.getCode());
		});
	}
	
	static void openInputSetup(int n) {
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
				player.getBomberMan().setCpuPlay(null);
				stage.close();
			}
			else if (player.isDetectingInput()) {
				player.setInputMode(GameInputMode.KEYBOARD);
				player.setDetectingInput(false);
			}
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
			if (!player.isDetectingInput()) {
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
					texts[z].setText(inputs[z].getName() + " = " + (!player.getButtonInfosMap().containsKey(inputs[z]) ? "-" : player.getButtonInfosMap().get(inputs[z]).getName()));
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
			if (player.isDetectingInput()) {
				player.setInputMode(GameInputMode.CPU);
				player.getBomberMan().setCpuPlay(new CpuPlay(player.getBomberMan(), CpuDificult.EASY));
			}
		});
		stage.showAndWait();
	}

	void mainLoop() {
		try {
			Tools.getFPSHandler().fpsCounter();
			MapSet.run();
			Draw.applyAllDraws(canvasMain, ZOOM, -32 * ZOOM, -32 * ZOOM);
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

	public static void checkFunctionKeys(KeyEvent e) {
		for (int n = 0; n < setInputKeys.length; n++)
			if (e.getCode() == setInputKeys[n])					
				Game.openInputSetup(n);
	}

}