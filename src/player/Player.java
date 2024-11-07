package player;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import entities.BomberMan;
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
	private Map<GameInputMode, Map<enums.GameInputs, ButtonInfos>> buttonsInfos;
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
		buttonsInfos = new LinkedHashMap<>();
		mappingIndex = -1;
		loadConfigs();
		// Teclas padrão do jogador 1
		if (playerId == 0 && keyboardKeyToGameInputMap.isEmpty()) {
			mapGameInput(KeyEvent.VK_A, GameInputs.LEFT, "Left");
			mapGameInput(KeyEvent.VK_W, GameInputs.UP, "Up");
			mapGameInput(KeyEvent.VK_D, GameInputs.RIGHT, "Right");
			mapGameInput(KeyEvent.VK_S, GameInputs.DOWN, "Down");
			mapGameInput(KeyEvent.VK_ENTER, GameInputs.START, "Enter");
			mapGameInput(KeyEvent.VK_SPACE, GameInputs.SELECT, "Space");
			mapGameInput(KeyEvent.VK_DELETE, GameInputs.A, "Delete");
			mapGameInput(KeyEvent.VK_END, GameInputs.B, "End");
			mapGameInput(KeyEvent.VK_INSERT, GameInputs.C, "Insert");
			mapGameInput(KeyEvent.VK_HOME, GameInputs.D, "Home");
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
		this.xinputDevice.setOnPressAnyComponentEvent((i, s) -> pressInput(i, s));
		this.xinputDevice.setOnReleaseAnyComponentEvent((i, l) -> releaseInput(i.getKey(), i.getValue()));
	}

	public void setDinputDevice(JInputEX dinputDevice) {
		this.xinputDevice = null;
		this.dinputDevice = dinputDevice;
		setInputMode(GameInputMode.DINPUT);
		this.dinputDevice.setOnPressComponentEvent((j, c) -> pressInput(c.getButtonId(), c.getName()));
		this.dinputDevice.setOnReleaseComponentEvent((j, c) -> releaseInput(c.getButtonId(), c.getName()));
	}
	
	public static void convertOnKeyPressEvent(javafx.scene.input.KeyEvent keyEvent) {
		int keyCode = keyEvent.getCode().getCode();
		if (keyOwner.containsKey(keyCode))
			keyOwner.get(keyCode).pressInput(keyCode, keyEvent.getCode().getName());
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
		if (state) {
			inputMode = GameInputMode.DETECTING;
			player.GameInputs.refreshJoysticks();
		}
	}

	public enums.GameInputs getNextMappingInput() {
		return mappingIsActive() ? enums.GameInputs.values()[mappingIndex] : null;
	}
	
	public int getButtonId(enums.GameInputs gameInput) {
		return getButtonInfosMap().get(gameInput).getId();
	}

	public String getButtonName(enums.GameInputs gameInput) {
		return getButtonInfosMap().get(gameInput).getName();
	}

	public Map<enums.GameInputs, ButtonInfos> getButtonInfosMap() {
		if (!buttonsInfos.containsKey(inputMode))
			buttonsInfos.put(inputMode, new HashMap<>());
		return buttonsInfos.get(inputMode);
	}
	
	public void pressInput(int buttonId, String buttonName) {
		if (mappingIndex > -1) {
			mapGameInput(buttonId, getNextMappingInput(), buttonName);
			if (++mappingIndex == enums.GameInputs.values().length)
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
	
	private enums.GameInputs getGameInputFromId(Integer inputId) {
		if (inputMode == GameInputMode.XINPUT)
			return !xinputButtonToGameInputMap.containsKey(inputId) ? null : xinputButtonToGameInputMap.get(inputId);
		if (inputMode == GameInputMode.DINPUT)
			return !dinputButtonToGameInputMap.containsKey(inputId) ? null : dinputButtonToGameInputMap.get(inputId);
		return !keyboardKeyToGameInputMap.containsKey(inputId) ? null : keyboardKeyToGameInputMap.get(inputId);
	}

	public void mapGameInput(Integer inputId, enums.GameInputs gameInput, String buttonName) {
		if (inputMode == GameInputMode.XINPUT) {
			xinputButtonToGameInputMap.put(inputId, gameInput);
			xinputGameInputToButtonMap.put(gameInput, inputId);
		}
		else if (inputMode == GameInputMode.DINPUT) {
			dinputButtonToGameInputMap.put(inputId, gameInput);
			dinputGameInputToButtonMap.put(gameInput, inputId);
		}
		else {
			if (keyOwner.containsKey(inputId)) { // Se a nova tecla ja esta designada
				Player otherPlayer = keyOwner.get(inputId);
				GameInputs otherGameInput = otherPlayer.keyboardKeyToGameInputMap.get(inputId);
				if (otherPlayer != this) { 
					if (keyboardGameInputToKeyMap.containsKey(otherGameInput)) { // Se o GameInput que estava nessa tecla tambem esta designado no player atual, trocar as teclas entre os players
						int otherInputId = keyboardGameInputToKeyMap.get(otherGameInput);
						GameInputs otherGameInput2 = keyboardKeyToGameInputMap.get(otherInputId);
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
					if (keyboardGameInputToKeyMap.containsKey(gameInput)) { // Se o GameInput que estava nessa tecla tambem esta designado no player atual, trocar as teclas entre os players
						int otherInputId = keyboardGameInputToKeyMap.get(gameInput);
						if (otherInputId != inputId) {
							keyboardGameInputToKeyMap.put(otherGameInput, otherInputId);
							keyboardKeyToGameInputMap.put(otherInputId, otherGameInput);
							getButtonInfosMap().put(otherGameInput, new ButtonInfos(otherInputId, getButtonInfosMap().get(gameInput).getName()));
						}
					}
				}
			}
			keyOwner.put(inputId, this);
			keyboardKeyToGameInputMap.put(inputId, gameInput);
			keyboardGameInputToKeyMap.put(gameInput, inputId);
			getButtonInfosMap().put(gameInput, new ButtonInfos(inputId, buttonName));
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
				if (split2.length < 3)
					throw new RuntimeException(s + " - Invalid value ([" + inputType + "] section)");
				int buttonId = -1;
				GameInputs input = null;
				String name = null;
				try {
					buttonId = Integer.parseInt(split2[0]); 
					input = GameInputs.valueOf(split2[1]); 
					name = split2[2]; 
				}
				catch (Exception e) {
					if (buttonId == -1)
						throw new RuntimeException(s + " - Invalid integer value at left (" + split2[0] + ") ([" + inputType + "] section, " + playerId + "= item)");
					throw new RuntimeException(s + " - Invalid integer value at middle (" + split2[1] + ") ([" + inputType + "] section, " + playerId + "= item)");
				}
				mapGameInput(buttonId, input, name);
			}
		}

	}

	public void saveConfigs() {
		IniFile ini = IniFile.getNewIniFileInstance("appdata/configs/Inputs.ini");
		StringBuilder sb;
		for (GameInputMode inputMode : GameInputMode.values())
			if (buttonsInfos.containsKey(inputMode)) {
				sb = new StringBuilder();
				for (GameInputs i : buttonsInfos.get(inputMode).keySet()) {
					if (!sb.isEmpty())
						sb.append(" ");
					sb.append(buttonsInfos.get(inputMode).get(i).getId() + ":" + i + ":" + buttonsInfos.get(inputMode).get(i).getName());
				}
				ini.write(inputMode.name(), "" + playerId, sb.toString());
			}
		ini.write("INPUT_MODE", "" + playerId, getInputMode().name());
	}
	
}
