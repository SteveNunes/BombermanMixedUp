package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import entities.BomberMan;
import enums.GameInput;
import enums.GameInputMode;
import javafx.scene.input.KeyCode;
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
 * 		'player.setXInputDevice(JXInputEX xinput);'
 * 		'player.setDInputDevice(JInputEX dinput);'
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
	private Map<Integer, enums.GameInput> xinputButtonToGameInputMap;
	private Map<enums.GameInput, Integer> xinputGameInputToButtonMap;
	private Map<Integer, enums.GameInput> dinputButtonToGameInputMap;
	private Map<enums.GameInput, Integer> dinputGameInputToButtonMap;
	private Map<Integer, enums.GameInput> keyboardKeyToGameInputMap;
	private Map<enums.GameInput, Integer> keyboardGameInputToKeyMap;
	private Consumer<enums.GameInput> onPressInputEvent;
	private Consumer<enums.GameInput> onReleaseInputEvent;
	private Map<GameInputMode, Map<enums.GameInput, ButtonInfos>> buttonsInfos;
	private JXInputEX xInputDevice;
	private JInputEX dInputDevice;
	private GameInputMode inputMode;
	private BomberMan bomberMan;
	private int mappingIndex;
	private int dInputDeviceId;
	private int xInputDeviceId;
	boolean isDetectingInput;
	
	private Player(int playerId) {
		this.playerId = playerId;
		xInputDevice = null;
		dInputDevice = null;
		dInputDeviceId = -1;
		xInputDeviceId = -1;
		inputMode = GameInputMode.KEYBOARD;
		bomberMan = null;
		onPressInputEvent = null;
		onReleaseInputEvent = null;
		isDetectingInput = false;
		xinputButtonToGameInputMap = new HashMap<>();
		xinputGameInputToButtonMap = new HashMap<>();
		dinputButtonToGameInputMap = new HashMap<>();
		dinputGameInputToButtonMap = new HashMap<>();
		keyboardKeyToGameInputMap = new HashMap<>();
		keyboardGameInputToKeyMap = new HashMap<>();
		buttonsInfos = new HashMap<>();
		mappingIndex = -1;
		loadConfigs();
		// Teclas padrão do jogador 1
		if (playerId == 0 && keyboardKeyToGameInputMap.isEmpty()) {
			mapGameInput(KeyCode.A.getCode(), GameInput.LEFT, KeyCode.A.getName());
			mapGameInput(KeyCode.W.getCode(), GameInput.UP, KeyCode.W.getName());
			mapGameInput(KeyCode.D.getCode(), GameInput.RIGHT, KeyCode.D.getName());
			mapGameInput(KeyCode.S.getCode(), GameInput.DOWN, KeyCode.S.getName());
			mapGameInput(KeyCode.ENTER.getCode(), GameInput.START, KeyCode.ENTER.getName());
			mapGameInput(KeyCode.SPACE.getCode(), GameInput.SELECT, KeyCode.SPACE.getName());
			mapGameInput(KeyCode.DELETE.getCode(), GameInput.A, KeyCode.DELETE.getName());
			mapGameInput(KeyCode.END.getCode(), GameInput.B, KeyCode.END.getName());
			mapGameInput(KeyCode.INSERT.getCode(), GameInput.C, KeyCode.INSERT.getName());
			mapGameInput(KeyCode.HOME.getCode(), GameInput.D, KeyCode.HOME.getName());
			mapGameInput(KeyCode.PAGE_UP.getCode(), GameInput.E, KeyCode.PAGE_UP.getName());
			mapGameInput(KeyCode.PAGE_DOWN.getCode(), GameInput.F, KeyCode.PAGE_DOWN.getName());
		}
	}
	
	public GameInputMode getInputMode() {
		return inputMode;
	}

	public void setInputMode(GameInputMode inputMode) {
		this.inputMode = inputMode;
		if (inputMode == GameInputMode.XINPUT && xInputDevice != null)
			setXInputDevice(xInputDevice);
		else if (inputMode == GameInputMode.DINPUT && dInputDevice != null)
			setDInputDevice(dInputDevice);
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
		bomberMan.setPlayer(this);
	}

	public JXInputEX getXinputDevice() {
		return xInputDevice;
	}

	public JInputEX getDinputDevice() {
		return dInputDevice;
	}

	public void setXInputDeviceId(int xInputDeviceId) {
		this.xInputDeviceId = xInputDeviceId;
		setXInputDevice(player.GameInput.getXinput(xInputDeviceId));
	}

	public void setXInputDevice(JXInputEX xInputDevice) {
		this.xInputDevice = xInputDevice;
		xInputDeviceId = xInputDevice == null ? -1 : player.GameInput.getXInputId(xInputDevice);
		if (xInputDevice != null) {
			inputMode = GameInputMode.XINPUT;
			this.xInputDevice.setOnPressAnyComponentEvent((i, s) -> pressInput(i, s));
			this.xInputDevice.setOnReleaseAnyComponentEvent((i, l) -> releaseInput(i.getKey(), i.getValue()));
			if (dInputDevice != null) {
				dInputDevice.setOnPressComponentEvent(null);
				dInputDevice.setOnReleaseComponentEvent(null);
			}
		}
	}
	
	public void setDInputDeviceId(int dInputDeviceId) {
		this.dInputDeviceId = dInputDeviceId;
		setDInputDevice(player.GameInput.getDinput(dInputDeviceId));
	}

	public void setDInputDevice(JInputEX dInputDevice) {
		this.dInputDevice = dInputDevice;
		dInputDeviceId = dInputDevice == null ? -1 : player.GameInput.getDInputId(dInputDevice);
		if (dInputDevice != null) {
			inputMode = GameInputMode.DINPUT;
			this.dInputDevice.setOnPressComponentEvent((j, c) -> pressInput(c.getComponentId(), c.getName()));
			this.dInputDevice.setOnReleaseComponentEvent((j, c) -> releaseInput(c.getComponentId(), c.getName()));
			if (xInputDevice != null) {
				xInputDevice.setOnPressAnyComponentEvent(null);
				xInputDevice.setOnReleaseAnyComponentEvent(null);
			}
		}
	}
	
	public static void convertOnKeyPressEvent(javafx.scene.input.KeyEvent keyEvent) {
		int keyCode = keyEvent.getCode().getCode();
		if (keyOwner.containsKey(keyCode)) {
			if (keyOwner.get(keyCode).isDetectingInput()) {
				keyOwner.get(keyCode).setInputMode(GameInputMode.KEYBOARD);
				keyOwner.get(keyCode).setDetectingInput(false);
			}
			keyOwner.get(keyCode).pressInput(keyCode, keyEvent.getCode().getName());
		}
	}
	
	public static void convertOnKeyReleaseEvent(javafx.scene.input.KeyEvent keyEvent) {
		int keyCode = keyEvent.getCode().getCode();
		if (keyOwner.containsKey(keyCode))
			keyOwner.get(keyCode).releaseInput(keyCode, keyEvent.getCode().getName());
	}
	
	public boolean mappingIsActive() {
		return mappingIndex > -1;
	}
	
	public void setMappingMode(boolean state) {
		mappingIndex = state ? 0 : -1;
		isDetectingInput = state;
		if (state)
			player.GameInput.refreshJoysticks();
	}

	public enums.GameInput getNextMappingInput() {
		return mappingIsActive() ? enums.GameInput.values()[mappingIndex] : null;
	}
	
	public int getButtonId(enums.GameInput gameInput) {
		return getButtonInfosMap().get(gameInput).getId();
	}

	public String getButtonName(enums.GameInput gameInput) {
		return getButtonInfosMap().get(gameInput).getName();
	}

	public Map<enums.GameInput, ButtonInfos> getButtonInfosMap() {
		if (!buttonsInfos.containsKey(inputMode))
			buttonsInfos.put(inputMode, new HashMap<>());
		return buttonsInfos.get(inputMode);
	}
	
	public void pressInput(int buttonId, String buttonName) {
		if (mappingIndex > -1) {
			mapGameInput(buttonId, getNextMappingInput(), buttonName);
			if (++mappingIndex == enums.GameInput.values().length)
				mappingIndex = -1;
		}
		else if (getGameInputFromId(buttonId) != null && bomberMan != null)
			bomberMan.keyPress(getGameInputFromId(buttonId));
		if (onPressInputEvent != null)
			onPressInputEvent.accept(getGameInputFromId(buttonId));
	}

	private void releaseInput(int buttonId, String buttonName) {
		if (getGameInputFromId(buttonId) != null) {
			if (bomberMan != null)
				bomberMan.keyRelease(getGameInputFromId(buttonId));
			if (onReleaseInputEvent != null)
				onReleaseInputEvent.accept(getGameInputFromId(buttonId));
		}
	}
	
	private enums.GameInput getGameInputFromId(Integer inputId) {
		if (inputMode == GameInputMode.XINPUT)
			return !xinputButtonToGameInputMap.containsKey(inputId) ? null : xinputButtonToGameInputMap.get(inputId);
		if (inputMode == GameInputMode.DINPUT)
			return !dinputButtonToGameInputMap.containsKey(inputId) ? null : dinputButtonToGameInputMap.get(inputId);
		return !keyboardKeyToGameInputMap.containsKey(inputId) ? null : keyboardKeyToGameInputMap.get(inputId);
	}

	public void mapGameInput(Integer inputId, enums.GameInput gameInput, String buttonName) {
		for (int n = 0; n < 2; n++) {
			GameInputMode inputMode = n == 0 ? GameInputMode.XINPUT : GameInputMode.DINPUT;
			if (this.inputMode == inputMode) {
				if (!buttonsInfos.containsKey(inputMode))
					buttonsInfos.put(inputMode, new HashMap<>());
				Map<GameInput, ButtonInfos> buttonInfos = buttonsInfos.get(inputMode);
				Map<GameInput, Integer> gameInputToButtonMap = n == 0 ? xinputGameInputToButtonMap : dinputGameInputToButtonMap;
				Map<Integer, GameInput> buttonToGameInputMap = n == 0 ? xinputButtonToGameInputMap : dinputButtonToGameInputMap;
				if (gameInputToButtonMap.containsKey(gameInput)) {
					int id = gameInputToButtonMap.get(gameInput);
					GameInput input = buttonToGameInputMap.get(inputId);
					buttonToGameInputMap.put(id, input);
					gameInputToButtonMap.put(input, id);
					buttonInfos.put(input, new ButtonInfos(id, buttonInfos.get(gameInput).getName()));
				}
				else if (buttonToGameInputMap.containsKey(inputId)) {
					GameInput input = buttonToGameInputMap.get(inputId);
					gameInputToButtonMap.remove(input);
					buttonInfos.remove(input);
				}
				buttonToGameInputMap.put(inputId, gameInput);
				gameInputToButtonMap.put(gameInput, inputId);
				buttonInfos.put(gameInput, new ButtonInfos(inputId, buttonName));
				return;
			}
		}
		if (inputMode == GameInputMode.KEYBOARD) {
			if (keyOwner.containsKey(inputId)) {
				Player otherPlayer = keyOwner.get(inputId);
				GameInput otherGameInput = otherPlayer.keyboardKeyToGameInputMap.get(inputId);
				if (otherPlayer != this) {
					if (keyboardGameInputToKeyMap.containsKey(otherGameInput)) {
						int otherInputId = keyboardGameInputToKeyMap.get(otherGameInput);
						GameInput otherGameInput2 = keyboardKeyToGameInputMap.get(otherInputId);
						int otherInputId2 = otherPlayer.keyboardGameInputToKeyMap.get(otherGameInput2);
						String buttonName1 = getButtonInfosMap().get(otherGameInput).getName();
						String buttonName2 = otherPlayer.getButtonInfosMap().get(otherGameInput2).getName();
						otherPlayer.keyboardGameInputToKeyMap.put(otherGameInput2, otherInputId);
						otherPlayer.keyboardKeyToGameInputMap.put(otherInputId, otherGameInput2);
						otherPlayer.getButtonInfosMap().put(otherGameInput2, new ButtonInfos(otherInputId, buttonName1));
						keyOwner.put(otherInputId, otherPlayer);
						keyboardGameInputToKeyMap.put(otherGameInput, otherInputId2);
						keyboardKeyToGameInputMap.put(otherInputId2, otherGameInput);
						getButtonInfosMap().put(otherGameInput, new ButtonInfos(otherInputId2, buttonName2));
						keyOwner.put(otherInputId2, this);
					}
					else {
						otherPlayer.keyboardGameInputToKeyMap.remove(otherGameInput);
						otherPlayer.keyboardKeyToGameInputMap.remove(inputId);
						otherPlayer.getButtonInfosMap().remove(otherGameInput);
					}
				}
				else {
					if (keyboardGameInputToKeyMap.containsKey(gameInput)) {
						int otherInputId = keyboardGameInputToKeyMap.get(gameInput);
						if (otherInputId != inputId) {
							keyboardGameInputToKeyMap.put(otherGameInput, otherInputId);
							keyboardKeyToGameInputMap.put(otherInputId, otherGameInput);
							getButtonInfosMap().put(otherGameInput, new ButtonInfos(otherInputId, getButtonInfosMap().get(gameInput).getName()));
						}
					}
					else {
						keyboardGameInputToKeyMap.remove(otherGameInput);
						getButtonInfosMap().remove(otherGameInput);
					}
				}
			}
			keyOwner.put(inputId, this);
			keyboardKeyToGameInputMap.put(inputId, gameInput);
			keyboardGameInputToKeyMap.put(gameInput, inputId);
			getButtonInfosMap().put(gameInput, new ButtonInfos(inputId, buttonName));
		}
	}

	public void setOnPressInputEvent(Consumer<enums.GameInput> onPressInputEvent) {
		this.onPressInputEvent = onPressInputEvent;
	}
	
	public void setOnReleaseInputEvent(Consumer<enums.GameInput> onReleaseInputEvent) {
		this.onReleaseInputEvent = onReleaseInputEvent;
	}
	
	public void loadConfigs() {
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Inputs.ini");
		if (ini.read("DEVICE_ID", "" + playerId) != null) {
			try {
				String[] split = ini.getLastReadVal().split(" ");
				dInputDeviceId = Integer.parseInt(split[0]);
				xInputDeviceId = Integer.parseInt(split[1]);
			}
			catch (Exception e) { e.printStackTrace();
				throw new RuntimeException(ini.getLastReadVal() + " - Invalid value in DEVICE_ID section, item " + playerId);
			}
		}
		String[] inputTypes = { "DINPUT", "XINPUT", "KEYBOARD" };
		for (String inputType : inputTypes) {
			setInputMode(GameInputMode.valueOf(inputType));
			String str = ini.read(inputType, "" + playerId);
			if (str == null)
				continue;
			String[] split = str.split("\\|");
			for (String s : split) {
				String[] split2 = s.split(":");
				if (split2.length < 3)
					throw new RuntimeException(s + " - Invalid value ([" + inputType + "] section)");
				int buttonId = -1;
				GameInput input = null;
				String name = null;
				try {
					buttonId = Integer.parseInt(split2[0]); 
					input = split2[1].equals("null") ? null : GameInput.valueOf(split2[1]); 
					name = split2[2]; 
				}
				catch (Exception e) { e.printStackTrace();
					if (buttonId == -1)
						throw new RuntimeException(s + " - Invalid integer value at left (" + split2[0] + ") ([" + inputType + "] section, " + playerId + "= item)");
					throw new RuntimeException(s + " - Invalid integer value at middle (" + split2[1] + ") ([" + inputType + "] section, " + playerId + "= item)");
				}
				mapGameInput(buttonId, input, name);
			}
		}
		if (ini.read("INPUT_MODE", "" + playerId) != null) {
			try {
				GameInputMode mode = GameInputMode.valueOf(ini.getLastReadVal());
				setInputMode(mode);
			}
			catch (Exception e) {
				throw new RuntimeException(ini.getLastReadVal() + " - Invalid GameInputMode name");
			}
		}
		if (getInputMode() != GameInputMode.KEYBOARD && dInputDeviceId == -1 && xInputDeviceId == -1)
			setInputMode(GameInputMode.KEYBOARD);
		else if (xInputDeviceId > -1)
			setXInputDeviceId(xInputDeviceId);
		else if (dInputDeviceId > -1)
			setDInputDeviceId(dInputDeviceId);
	}

	public void saveConfigs() {
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Inputs.ini");
		StringBuilder sb;
		for (GameInputMode inputMode : GameInputMode.values())
			if (buttonsInfos.containsKey(inputMode)) {
				sb = new StringBuilder();
				for (GameInput i : buttonsInfos.get(inputMode).keySet()) {
					if (!sb.isEmpty())
						sb.append("|");
					sb.append(buttonsInfos.get(inputMode).get(i).getId() + ":" + i + ":" + buttonsInfos.get(inputMode).get(i).getName());
				}
				ini.write(inputMode.name(), "" + playerId, sb.toString());
			}
		ini.write("INPUT_MODE", "" + playerId, getInputMode().name());
		ini.write("DEVICE_ID", "" + playerId, dInputDeviceId + " " + xInputDeviceId);
	}

	public boolean isDetectingInput() {
		return isDetectingInput;
	}

	public void setDetectingInput(boolean state) {
		isDetectingInput = false;
	}
}
