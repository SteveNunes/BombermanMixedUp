package gui;

import java.util.ArrayList;
import java.util.List;

import application.Main;
import entities.BomberMan;
import entities.CpuPlay;
import enums.CpuDificult;
import enums.GameInput;
import enums.GameInputMode;
import gui.util.Alerts;
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
import javafx.util.Duration;
import maps.MapSet;
import player.Player;
import tools.Draw;
import tools.Tools;
import util.DurationTimerFX;

public class Game {

	private final int WIN_W = Main.TILE_SIZE * 17;
	private final int WIN_H = Main.TILE_SIZE * 14;
	private List<KeyCode> holdedKeys;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Font font;

	public void init() {
		Main.setZoom(3);
		font = new Font("Lucida Console", 15);
		canvasMain.setWidth(WIN_W * Main.getZoom());
		canvasMain.setHeight(WIN_H * Main.getZoom());
		Main.setMainCanvas(canvasMain);
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

	private static KeyCode[] setInputKeys = new KeyCode[] {
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
		boolean[] done = { false };
		if ((bomber = player.getBomberMan()) == null) {
			bomber = BomberMan.addBomberMan(1, n);
			bomber.setPosition(MapSet.getInitialPlayerPosition(n));
			player.setBomberMan(bomber);
		}
		if (Main.gameTikTok != null && !Main.gameTikTok.addNewPlayer(bomber)) {
			bomber.setCpuPlay(new CpuPlay(bomber, CpuDificult.VERY_HARD));
			return;
		}
		Stage stage = new Stage();
		stage.setTitle("Input config (Player " + (n + 1) + ")");
		VBox vBox = new VBox();
		Scene scene = new Scene(vBox);
		final GameInputMode prevInputMode = player.getInputMode();
		Runnable closeEvent = () -> {
			DurationTimerFX.stopTimer("WaitForDevice");
			player.setOnPressInputEvent(null);
			player.setMappingMode(false);
			player.setInputMode(prevInputMode);
			confirmToDeletePlayer(n);
		};
		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				done[0] = true;
				Platform.runLater(closeEvent);
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
		int[] nextText = { 0 };
		GameInput[] inputs = GameInput.values();
		DurationTimerFX.createTimer("WaitForDevice", Duration.millis(20), 0, () -> {
			if (Main.close)
				DurationTimerFX.stopTimer("WaitForDevice");
			else if (!done[0] && !player.isDetectingInput()) {
				String str;
				if (player.getInputMode() == GameInputMode.XINPUT)
					str = player.getXinputDevice().getJoystickName(); 
				else if (player.getInputMode() == GameInputMode.DINPUT)
					str = player.getDinputDevice().getName(); 
				else
					str = "Teclado";
				texts[0].setText("Pressione para definir: " + player.getNextMappingInput().getName());
				text.setText(str + "\n");
				DurationTimerFX.stopTimer("WaitForDevice");
			}
		});
		player.setOnPressInputEvent(i -> {
			nextText[0]++;
			for (int z = 0; z <= nextText[0]; z++) {
				if (player.getNextMappingInput() != null && !done[0] && z == nextText[0])
					texts[z].setText("Pressione para definir: " + player.getNextMappingInput().getName());
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
			done[0] = true;
			Platform.runLater(closeEvent);
		});
		stage.showAndWait();
	}
	
	private static void confirmToDeletePlayer(int bomberIndex) {
		if (Alerts.confirmation("Confirmação", "Deseja mesmo excluir o bomberman " + (bomberIndex + 1) + "?"))
			BomberMan.removeBomberMan(bomberIndex);
	}

	void mainLoop() {
		try {
			Tools.getFPSHandler().fpsCounter();
			MapSet.run();
			Draw.applyAllDraws(canvasMain, Main.getZoom(), -32 * Main.getZoom(), -32 * Main.getZoom());
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