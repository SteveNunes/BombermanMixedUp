package player;

import java.util.List;

import joystick.JInputEX;
import joystick.JXInputEX;
import util.TimerFX;

public class GameInput {

	private static List<JXInputEX> xinputList = null;
	private static List<JInputEX> dinputList = null;
	
	public static void init() {
		JXInputEX.setOnJoystickConnectedEvent(x -> {
			System.out.println(x.getJoystickName() + " foi conectado");
		});
		JXInputEX.setOnJoystickDisconnectedEvent(x -> {
			System.out.println(x.getJoystickName() + " foi desconectado");
		});
		JInputEX.setOnJoystickConnectedEvent(d -> {
			System.out.println(d.getName() + " foi conectado");
		});
		JInputEX.setOnJoystickDisconnectedEvent(d -> {
			System.out.println(d.getName() + " foi desconectado");
		});
		loadJoysticks();
	}
	
	public static void close() {
		TimerFX.stopTimer("PoolJoysticksTimer");
		for (Player player : Player.getPlayers())
			player.saveConfigs();
		JInputEX.closeAllJoysticks();
	}
	
	public static void loadJoysticks() {
		TimerFX.stopTimer("PoolJoysticksTimer");
		JInputEX.init();
		JXInputEX.refreshJoysticks();
		xinputList = null;//JXInputEX.getJoystickList();
		dinputList = JInputEX.getJoysticks();
		TimerFX.createTimer("PoolJoysticksTimer", 1, 0, () -> {
			JInputEX.pollAllJoysticks();
			JXInputEX.pollJoysticks();
		});
		refreshJoysticks();
	}
	
	public static void refreshJoysticks() {
		if (xinputList != null)
			for (JXInputEX x : xinputList)
				x.setOnPressAnyComponentEvent((i, s) -> {
					for (Player player : Player.getPlayers())
						if (player.isDetectingInput()) {
							player.setXInputDevice(x);
							player.setDetectingInput(false);
						}
				});
		if (dinputList != null)
			for (JInputEX d : dinputList) {
				d.setOnPressComponentEvent((j, c) -> {
					for (Player player : Player.getPlayers())
						if (player.isDetectingInput()) {
							player.setDInputDevice(d);
							player.setDetectingInput(false);
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
