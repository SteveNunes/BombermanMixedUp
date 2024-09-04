package tools;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import gameutil.FPSHandler;
import util.IniFile;

public abstract class GameMisc {
	
	static Random random;
	static FPSHandler fpsHandler; 
	static IniFile iniFile;
	
	static {
		fpsHandler = new FPSHandler(60);
		random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));
		iniFile = IniFile.getNewIniFileInstance("D:/Java/Bomberman - Mixed Up!/appdata/configs/Monsters.cfg");
	}
	
	public static FPSHandler getFPSHandler()
		{ return fpsHandler; }

	public static <T> void moveItemTo(List<T> list, T item, int index) {
		if (list.contains(item)) {
			int max = list.size();
			if (index < -1 || index > max)
				throw new RuntimeException(index + " - Invalid Index (Min: -1, Max: " + max + ")");
			if (index == -1)
				index = max - 1;
			else if (index == max)
				index = 0;
			list.remove(item);
			list.add(index, item);
		}
	}
	
	public static int getRandom(int min, int max)
		{ return random.nextInt(++max - min) + min; }

}
