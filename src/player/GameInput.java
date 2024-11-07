package player;

import java.util.List;

import enums.GameInputMode;
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
		xinputList = JXInputEX.getJoystickList();
		dinputList = null; //JInputEX.getJoysticks();
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
						if (player.getInputMode() == GameInputMode.DETECTING)
							player.setXinputDevice(x);
				});
		if (dinputList != null)
			for (JInputEX d : dinputList)
				d.setOnPressComponentEvent((j, c) -> {
					for (Player player : Player.getPlayers())
						if (player.getInputMode() == GameInputMode.DETECTING)
							player.setDinputDevice(d);
				});
	}
	
	public static int getTotalXinputs() {
		return xinputList.size();
	}
	
	public static JXInputEX getXinput(int index) {
		return xinputList.get(index);
	}
	
	public static int getTotalDinputs() {
		return dinputList.size();
	}
	
	public static JInputEX getDinput(int index) {
		return dinputList.get(index);
	}
	
}
