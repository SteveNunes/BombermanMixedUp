package tools;

import java.util.List;

import util.IniFile;

public abstract class GameMisc {
	
	static IniFile iniFile = IniFile.getNewIniFileInstance("D:/Java/Bomberman - Mixed Up!/appdata/configs/Monsters.cfg");

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
	

}
