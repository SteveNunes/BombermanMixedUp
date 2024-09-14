package tools;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import gameutil.FPSHandler;
import javafx.scene.Node;
import util.IniFile;

public abstract class GameMisc {
	
	static Random random;
	static FPSHandler fpsHandler; 
	static IniFile iniFile;
	
	static {
		fpsHandler = new FPSHandler(60);
		random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));
	}
	
	public static FPSHandler getFPSHandler()
		{ return fpsHandler; }

	public static <T> void moveItemTo(List<T> list, T item, int index) {
		if (list.contains(item)) {
			int max = list.size();
			if (index < -1 || index > max)
				GameMisc.throwRuntimeException(index + " - Invalid Index (Min: -1, Max: " + max + ")");
			if (index == -1)
				index = max - 1;
			else if (index == max)
				index = 0;
			list.remove(item);
			list.add(index, item);
		}
	}
	
	public static boolean blink()
		{ return blink(50); }
	
	public static boolean blink(int speed)
		{ return System.currentTimeMillis() / speed % 2 == 0; }
	
	public static DrawImageEffects loadEffectsFromString(String string) {
		// NOTA: Implementar método
		return null;
	}

	public static int getRandom(int min, int max)
		{ return random.nextInt(++max - min) + min; }

	public static void throwRuntimeException(String string) {
		Main.close();
		throw new RuntimeException(string);
	}

	public static void sleep(int delay) {
		try { Thread.sleep(delay); }
		catch (InterruptedException e) {}
	}

	public static void setNodeFont(Node buttonPlay, String fontName, int fontSize)
		{ buttonPlay.setStyle("-fx-font-family: '" + fontName + "'; -fx-font-size: " + fontSize + "px;"); }

}
