package player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javafx.util.Duration;
import joystick.JInputEX;
import joystick.JInputEXComponent;
import joystick.JXInputEX;
import util.DurationTimerFX;

public class GameInput {

	private static List<JXInputEX> xinputList = null;
	private static List<JInputEX> dinputList = null;
	private static Map<JXInputEX, BiConsumer<Integer, String>> restoreJXInputBiConsumer = new HashMap<>();
	private static Map<JInputEX, BiConsumer <JInputEX, JInputEXComponent>> restoreJInputBiConsumer = new HashMap<>();
	
	public static void init() {
		loadJoysticks();
		if (xinputList != null) {
			JXInputEX.setOnJoystickConnectedEvent(x -> {
				System.out.println(x.getJoystickName() + " foi conectado");
			});
			JXInputEX.setOnJoystickDisconnectedEvent(x -> {
				System.out.println(x.getJoystickName() + " foi desconectado");
			});
		}
		JInputEX.setOnJoystickConnectedEvent(d -> {
			System.out.println(d.getName() + " foi conectado");
		});
		JInputEX.setOnJoystickDisconnectedEvent(d -> {
			System.out.println(d.getName() + " foi desconectado");
		});
	}
	
	public static void close() {
		for (Player player : Player.getPlayers())
			player.saveConfigs();
		JInputEX.closeAllJoysticks();
	}
	
	public static void loadJoysticks() {
		DurationTimerFX.stopTimer("PoolJoysticksTimer");
		JInputEX.init();
		dinputList = JInputEX.getJoysticks();
		try {
			JXInputEX.refreshJoysticks();
			xinputList = JXInputEX.getJoystickList();
		}
		catch (UnsatisfiedLinkError e) {
			xinputList = null;
		}
		DurationTimerFX.createTimer("PoolJoysticksTimer", Duration.millis(1), 0, () -> {
			JInputEX.pollAllJoysticks();
			if (xinputList != null)
				JXInputEX.pollJoysticks();
		});
		refreshJoysticks();
	}
	
	public static void saveCurrentXInputConsumer(JXInputEX device, BiConsumer<Integer, String> consumer) {
		restoreJXInputBiConsumer.put(device, consumer);
	}	
	
	public static void saveCurrentDInputConsumer(JInputEX device, BiConsumer<JInputEX, JInputEXComponent> consumer) {
		restoreJInputBiConsumer.put(device, consumer);
	}	
	
	public static void refreshJoysticks() {
		if (xinputList != null)
			for (JXInputEX x : xinputList)
				x.setOnPressAnyComponentEvent((i, s) -> {
					for (Player player : Player.getPlayers())
						if (player.isDetectingInput()) {
							player.setXInputDevice(x);
							player.setDetectingInput(false);
							for (JXInputEX device : restoreJXInputBiConsumer.keySet())
								if (device != x)
									device.setOnPressAnyComponentEvent(restoreJXInputBiConsumer.get(device));
							for (JInputEX device : restoreJInputBiConsumer.keySet())
								device.setOnPressComponentEvent(restoreJInputBiConsumer.get(device));
							return;
						}
				});
		if (dinputList != null)
			for (JInputEX d : dinputList) {
				d.setOnPressComponentEvent((j, c) -> {
					for (Player player : Player.getPlayers())
						if (player.isDetectingInput()) {
							player.setDInputDevice(d);
							player.setDetectingInput(false);
							for (JXInputEX device : restoreJXInputBiConsumer.keySet())
									device.setOnPressAnyComponentEvent(restoreJXInputBiConsumer.get(device));
							for (JInputEX device : restoreJInputBiConsumer.keySet())
								if (device != d)
									device.setOnPressComponentEvent(restoreJInputBiConsumer.get(device));
							return;
						}
				});
			}
	}
	
	public static int getTotalXinputs() {
		return xinputList.size();
	}
	
	public static int getXInputId(JXInputEX xinput) {
		return xinputList.indexOf(xinput);
	}
	
	public static int getDInputId(JInputEX dinput) {
		return dinputList.indexOf(dinput);
	}
	
	public static JXInputEX getXinput(int index) {
		return xinputList == null || index < 0 || index >= xinputList.size() || xinputList.isEmpty() ? null : xinputList.get(index);
	}
	
	public static int getTotalDinputs() {
		return dinputList.size();
	}
	
	public static JInputEX getDinput(int index) {
		return dinputList == null || index < 0 || index >= dinputList.size() || dinputList.isEmpty() ? null : dinputList.get(index);
	}
	
}
