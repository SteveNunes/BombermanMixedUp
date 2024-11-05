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
	private Map<Integer, enums.GameInputs> xinputButtonToGameInputMap;
	private Map<enums.GameInputs, Integer> xinputGameInputToButtonMap;
	private Map<Integer, enums.GameInputs> dinputButtonToGameInputMap;
	private Map<enums.GameInputs, Integer> dinputGameInputToButtonMap;
	private Map<Integer, enums.GameInputs> keyboardKeyToGameInputMap;
	private Map<enums.GameInputs, Integer> keyboardGameInputToKeyMap;
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
		xinputButtonToGameInputMap = new HashMap<>();
		xinputGameInputToButtonMap = new HashMap<>();
		dinputButtonToGameInputMap = new HashMap<>();
		dinputGameInputToButtonMap = new HashMap<>();
		keyboardKeyToGameInputMap = new HashMap<>();
		keyboardGameInputToKeyMap = new HashMap<>();
		mappingIndex = -1;
		loadConfigs();
		// Teclas padrão do jogador 1
		if (playerId == 0 && keyboardKeyToGameInputMap.isEmpty()) {
			mapGameInput(KeyEvent.VK_A, GameInputs.LEFT);
			mapGameInput(KeyEvent.VK_W, GameInputs.UP);
			mapGameInput(KeyEvent.VK_D, GameInputs.RIGHT);
			mapGameInput(KeyEvent.VK_S, GameInputs.DOWN);
			mapGameInput(KeyEvent.VK_ENTER, GameInputs.START);
			mapGameInput(KeyEvent.VK_SPACE, GameInputs.SELECT);
			mapGameInput(KeyEvent.VK_NUMPAD1, GameInputs.A);
			mapGameInput(KeyEvent.VK_NUMPAD2, GameInputs.B);
			mapGameInput(KeyEvent.VK_NUMPAD4, GameInputs.C);
			mapGameInput(KeyEvent.VK_NUMPAD5, GameInputs.D);
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
		return mappingIsActive() ? enums.GameInputs.values()[mappingIndex] : null;
	}
	
	public void pressInput(int buttonId) {
		if (mappingIndex > -1) {
			mapGameInput(buttonId, enums.GameInputs.values()[mappingIndex]);
			if (++mappingIndex == enums.GameInputs.values().length)
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
			return !xinputButtonToGameInputMap.containsKey(inputId) ? null : xinputButtonToGameInputMap.get(inputId);
		if (inputMode == GameInputMode.DINPUT)
			return !dinputButtonToGameInputMap.containsKey(inputId) ? null : dinputButtonToGameInputMap.get(inputId);
		return !keyboardKeyToGameInputMap.containsKey(inputId) ? null : keyboardKeyToGameInputMap.get(inputId);
	}

	public void mapGameInput(Integer inputId, enums.GameInputs gameInput) {
		if (inputMode == GameInputMode.XINPUT) {
			xinputButtonToGameInputMap.put(inputId, gameInput);
			xinputGameInputToButtonMap.put(gameInput, inputId);
		}
		else if (inputMode == GameInputMode.DINPUT) {
			dinputButtonToGameInputMap.put(inputId, gameInput);
			dinputGameInputToButtonMap.put(gameInput, inputId);
		}
		else {
			if (keyboardKeyToGameInputMap.containsKey(inputId)) {
				for (int id : keyboardKeyToGameInputMap.keySet())
					if (keyboardKeyToGameInputMap.get(id) == gameInput) {
						if (keyboardKeyToGameInputMap.containsKey(inputId))
							keyboardKeyToGameInputMap.put(id, keyboardKeyToGameInputMap.get(inputId));
						else
							keyOwner.remove(id);
						break;
					}
			}
			keyOwner.put(inputId, this);
			keyboardKeyToGameInputMap.put(inputId, gameInput);
			keyboardGameInputToKeyMap.put(gameInput, inputId);
		}
	}

	public void setOnPressInputEvent(Consumer<enums.GameInputs> onPressInputEvent) {
		this.onPressInputEvent = onPressInputEvent;
	}
	
	public void setOnReleaseInputEvent(Consumer<enums.GameInputs> onReleaseInputEvent) {
		this.onReleaseInputEvent = onReleaseInputEvent;
	}
	
	public void loadConfigs() {
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Inputs.ini");
		if (ini.read("INPUT_MODE", "" + playerId) != null) {
			try {
				GameInputMode mode = GameInputMode.valueOf(ini.getLastReadVal());
				setInputMode(mode);
			}
			catch (Exception e) {
				throw new RuntimeException(ini.getLastReadVal() + " - Invalid GameInputMode name");
			}
		}
		String[] inputTypes = { "DINPUT", "XINPUT", "KEYBOARD" };
		for (String inputType : inputTypes) {
			String str = ini.read(inputType, "" + playerId);
			if (str == null)
				continue;
			String[] split = str.split(" ");
			for (String s : split) {
				String[] split2 = s.split(":");
				if (split2.length < 2)
					throw new RuntimeException(s + " - Invalid value ([" + inputType + "] section)");
				int buttonId = -1;
				GameInputs input = null;
				try {
					buttonId = Integer.parseInt(split2[0]); 
					input = GameInputs.valueOf(split2[1]); 
				}
				catch (Exception e) {
					if (buttonId == -1)
						throw new RuntimeException(s + " - Invalid integer value at left (" + split2[0] + ") ([" + inputType + "] section, " + playerId + "= item)");
					throw new RuntimeException(s + " - Invalid integer value at right (" + split2[1] + ") ([" + inputType + "] section, " + playerId + "= item)");
				}
				mapGameInput(buttonId, input);
			}
		}

	}

	public void saveConfigs() {
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Inputs.ini");
		StringBuilder sb;
		if (!xinputButtonToGameInputMap.isEmpty()) {
			sb = new StringBuilder();
			for (int id : xinputButtonToGameInputMap.keySet()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(id + ":" + xinputButtonToGameInputMap.get(id).name());
			}
			ini.write("XINPUT", "" + playerId, sb.toString());
		}
		if (!dinputButtonToGameInputMap.isEmpty()) {
			sb = new StringBuilder();
			for (int id : dinputButtonToGameInputMap.keySet()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(id + ":" + dinputButtonToGameInputMap.get(id).name());
			}
			ini.write("DINPUT", "" + playerId, sb.toString());
		}
		if (!keyboardKeyToGameInputMap.isEmpty()) {
			sb = new StringBuilder();
			for (int id : keyboardKeyToGameInputMap.keySet()) {
				if (!sb.isEmpty())
					sb.append(" ");
				sb.append(id + ":" + keyboardKeyToGameInputMap.get(id).name());
			}
			ini.write("KEYBOARD", "" + playerId, sb.toString());
		}
		ini.write("INPUT_MODE", "" + playerId, getInputMode().name());
	}
	
}
