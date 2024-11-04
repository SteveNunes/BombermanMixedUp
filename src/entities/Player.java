package entities;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import enums.GameInputMode;
import enums.GameInputs;
import joystick.JInputEX;
import joystick.JXInputEX;
import util.IniFile;

/* COMO Definir os controles de um jogador:
 * 
 * Adicione pelo menos 1 jogador, usando o método	'Player.addPlayer();'
 * 
 * Pegue um Player com 'Player player = Player.getPlayer(0)';
 * 
 * Se não for teclado, use um dos 2 métodos abaixo:
 * 		'player.setXinputDevice(JXInputEX xinput);'
 * 		'player.setDinputDevice(JInputEX dinput);'
 * 
 * Use o método 'player.setGameInputMode();' para definir o tipo de entrada.
 * 
 * Ative o mapeamento de botões com 'player.setMappingMode(true);'
 * 
 * Faça um loop infinito usando a condição 'player.getNextMappingInput() != null'
 * onde 'player.getNextMappingInput()' retorna o botão que deve ser pressionado no momento.
 * 'player.setBomberMan();' define o BomberMan que os inputs do jogador atual irão controlar.
 */

public class Player {
	
	private static List<Player> players = new ArrayList<>();
	private static Map<Integer, Player> keyOwner = new HashMap<>();
	
	private int playerId;
	private Map<Integer, enums.GameInputs> xinputMap;
	private Map<Integer, enums.GameInputs> dinputMap;
	private Map<Integer, enums.GameInputs> keyboardMap;
	private Consumer<enums.GameInputs> onPressInputEvent;
	private Consumer<enums.GameInputs> onReleaseInputEvent;
	private JXInputEX xinputDevice;
	private JInputEX dinputDevice;
	private GameInputMode inputMode;
	private BomberMan bomberMan;
	private int mappingIndex;
	
	private Player(int playerId) {
		this.playerId = playerId;
		xinputDevice = null;
		dinputDevice = null;
		inputMode = GameInputMode.KEYBOARD;
		bomberMan = null;
		onPressInputEvent = null;
		onReleaseInputEvent = null;
		xinputMap = new HashMap<>();
		dinputMap = new HashMap<>();
		keyboardMap = new HashMap<>();
		mappingIndex = -1;
		// Teclas padrão do jogador 1
		if (playerId == 0) {
			mapGameInput(KeyEvent.VK_LEFT, GameInputs.LEFT);
			mapGameInput(KeyEvent.VK_UP, GameInputs.UP);
			mapGameInput(KeyEvent.VK_RIGHT, GameInputs.RIGHT);
			mapGameInput(KeyEvent.VK_DOWN, GameInputs.DOWN);
			mapGameInput(KeyEvent.VK_ENTER, GameInputs.START);
			mapGameInput(KeyEvent.VK_SPACE, GameInputs.SELECT);
			mapGameInput(KeyEvent.VK_Z, GameInputs.A);
			mapGameInput(KeyEvent.VK_X, GameInputs.B);
			mapGameInput(KeyEvent.VK_A, GameInputs.C);
			mapGameInput(KeyEvent.VK_S, GameInputs.D);
		}
	}
	
	public GameInputMode getInputMode() {
		return inputMode;
	}

	public void setInputMode(GameInputMode inputMode) {
		this.inputMode = inputMode;
	}

	public static int getTotalPlayers() {
		return players.size();
	}
	
	public static List<Player> getPlayers() {
		return players;		
	}

	public static void addPlayer() {
		players.add(new Player(players.size()));
	}
	
	public static Player getPlayer(int playerIndex) {
		return players.get(playerIndex);
	}
	
	public BomberMan getBomberMan() {
		return bomberMan;
	}

	public void setBomberMan(BomberMan bomberMan) {
		this.bomberMan = bomberMan;
	}

	public JXInputEX getXinputDevice() {
		return xinputDevice;
	}

	public JInputEX getDinputDevice() {
		return dinputDevice;
	}

	public void setXinputDevice(JXInputEX xinputDevice) {
		this.dinputDevice = null;
		this.xinputDevice = xinputDevice;
		setInputMode(GameInputMode.XINPUT);
		this.xinputDevice.setOnPressAnyComponentEvent(i -> pressInput(i));
		this.xinputDevice.setOnReleaseAnyComponentEvent((i, l) -> releaseInput(i));
	}

	public void setDinputDevice(JInputEX dinputDevice) {
		this.xinputDevice = null;
		this.dinputDevice = dinputDevice;
		setInputMode(GameInputMode.DINPUT);
		this.dinputDevice.setOnPressComponentEvent((j, c) -> pressInput(c.getButtonId()));
		this.dinputDevice.setOnReleaseComponentEvent((j, c) -> releaseInput(c.getButtonId()));
	}
	
	public static void convertOnKeyPressEvent(Integer keyRawCode) {
		if (keyOwner.containsKey(keyRawCode))
			keyOwner.get(keyRawCode).pressInput(keyRawCode);
	}
	
	public static void convertOnKeyReleaseEvent(Integer keyRawCode) {
		if (keyOwner.containsKey(keyRawCode))
			keyOwner.get(keyRawCode).releaseInput(keyRawCode);
	}
	
	public boolean mappingIsActive() {
		return mappingIndex > -1;
	}
	
	public void setMappingMode(boolean state) {
		mappingIndex = state ? 0 : -1;
		if (state) {
			inputMode = GameInputMode.DETECTING;
			entities.GameInputs.refreshJoysticks();
		}
	}

	public enums.GameInputs getNextMappingInput() {
		return mappingIsActive() ? enums.GameInputs.getList()[mappingIndex] : null;
	}
	
	public void pressInput(int buttonId) {
		if (mappingIndex > -1) {
			mapGameInput(buttonId, enums.GameInputs.getList()[mappingIndex]);
			if (++mappingIndex == enums.GameInputs.getList().length)
				mappingIndex = -1;
			if (onPressInputEvent != null)
				onPressInputEvent.accept(getGameInputFromId(buttonId));
		}
		else if (getGameInputFromId(buttonId) != null) {
			if (bomberMan != null)
				bomberMan.keyPress(getGameInputFromId(buttonId));
			if (onPressInputEvent != null)
				onPressInputEvent.accept(getGameInputFromId(buttonId));
		}
	}

	private void releaseInput(int buttonId) {
		if (getGameInputFromId(buttonId) != null) {
			if (bomberMan != null)
				bomberMan.keyRelease(getGameInputFromId(buttonId));
			if (onReleaseInputEvent != null)
				onReleaseInputEvent.accept(getGameInputFromId(buttonId));
		}
	}
	
	private enums.GameInputs getGameInputFromId(Integer inputId) {
		if (inputMode == GameInputMode.XINPUT)
			return !xinputMap.containsKey(inputId) ? null : xinputMap.get(inputId);
		if (inputMode == GameInputMode.DINPUT)
			return !dinputMap.containsKey(inputId) ? null : dinputMap.get(inputId);
		return !keyboardMap.containsKey(inputId) ? null : keyboardMap.get(inputId);
	}

	public void mapGameInput(Integer inputId, enums.GameInputs gameInput) {
		if (inputMode == GameInputMode.XINPUT)
			xinputMap.put(inputId, gameInput);
		else if (inputMode == GameInputMode.DINPUT)
			dinputMap.put(inputId, gameInput);
		else {
			if (keyboardMap.containsKey(inputId)) {
				for (int id : keyboardMap.keySet())
					if (keyboardMap.get(id) == gameInput) {
						if (keyboardMap.containsKey(inputId))
							keyboardMap.put(id, keyboardMap.get(inputId));
						else
							keyOwner.remove(id);
						break;
					}
			}
			keyOwner.put(inputId, this);
			keyboardMap.put(inputId, gameInput);
		}
	}

	public void setOnPressInputEvent(Consumer<enums.GameInputs> onPressInputEvent) {
		this.onPressInputEvent = onPressInputEvent;
	}
	
	public void setOnReleaseInputEvent(Consumer<enums.GameInputs> onReleaseInputEvent) {
		this.onReleaseInputEvent = onReleaseInputEvent;
	}

	public void saveConfigs() {
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Inputs.ini");
		StringBuilder sb;
		if (!xinputMap.isEmpty()) {
			sb = new StringBuilder();
			for (int id : xinputMap.keySet()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(id + ":" + xinputMap.get(id).name());
			}
			ini.write("XINPUT", "" + playerId, sb.toString());
		}
		if (!dinputMap.isEmpty()) {
			sb = new StringBuilder();
			for (int id : dinputMap.keySet()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(id + ":" + dinputMap.get(id).name());
			}
			ini.write("DINPUT", "" + playerId, sb.toString());
		}
		if (!keyboardMap.isEmpty()) {
			sb = new StringBuilder();
			for (int id : keyboardMap.keySet()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(id + ":" + keyboardMap.get(id).name());
			}
			ini.write("KEYBOARD", "" + playerId, sb.toString());
		}
	}
	
}
