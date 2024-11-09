package gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import entities.BomberMan;
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

	private final int ZOOM = 2;
	private final int WIN_W = Main.TILE_SIZE * 47;
	private final int WIN_H = Main.TILE_SIZE * 23;
	private List<KeyCode> holdedKeys;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Position canvasTileCoord;
	private Font font;

	public void init() {
		font = new Font("Lucida Console", 15);
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
		mainLoop();
	}

	private Integer[] setInputKeys = new Integer[] {
			KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, 
			KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8, 
			KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12};
	
	void setEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
			Player.convertOnKeyPressEvent(e);
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.ESCAPE)
				Main.close();
			for (int n = 0; n < setInputKeys.length; n++)
				if (e.getCode().getCode() == setInputKeys[n])					
					openInputSetup(n, canvasTileCoord.getTileCoord());
		});
		Main.sceneMain.setOnKeyReleased(e -> {
			Player.convertOnKeyReleaseEvent(e);
			holdedKeys.add(e.getCode());
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
		scene.setOnKeyPressed(e -> {
			if (player.getInputMode() == GameInputMode.DETECTING)
				player.setInputMode(GameInputMode.KEYBOARD);
			else
				player.pressInput(e.getCode().getCode(), e.getCode().getName());
		});
		stage.setScene(scene);
		vBox.setPrefSize(400, 240);
		vBox.setPadding(new Insets(10, 10, 10, 10));
		vBox.setAlignment(Pos.TOP_CENTER);
		Text text = new Text("Pressione um botão para\nidentificar o dispositivo...");
		text.setFont(new Font("Lucida Console", 20));
		text.setTextAlignment(TextAlignment.CENTER);
		vBox.getChildren().add(text);
		Text[] texts = {new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text(""), new Text("")};
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
				texts[10].setText("\nConfiguração concluida!");
				done[0] = true;
			}
		});
		stage.setOnCloseRequest(e -> player.setMappingMode(false));
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

}