package tools;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import gameutil.FPSHandler;
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
	
	public static DrawImageEffects loadEffectsFromString(String string) {
		// NOTA: Implementar método
		return null;
	}

	public static void throwRuntimeException(String string) {
		Main.close();
		throw new RuntimeException(string);
	}

}
