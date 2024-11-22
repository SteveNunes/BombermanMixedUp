package tools;

import util.IniFile;

public abstract class IniFiles {

	public static IniFile monsters = IniFile.getNewIniFileInstance("appdata/configs/Monsters.ini");
	public static IniFile characters = IniFile.getNewIniFileInstance("appdata/configs/Characters.ini");
	public static IniFile frameSets = IniFile.getNewIniFileInstance("appdata/configs/FrameSets.ini");

}
