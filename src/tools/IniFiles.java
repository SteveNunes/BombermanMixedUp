package tools;

import util.IniFile;

public abstract class IniFiles {
	
	public static IniFile stages = IniFile.getNewIniFileInstance("appdata/configs/Stages.cfg");
	public static IniFile monsters = IniFile.getNewIniFileInstance("appdata/configs/Monsters.ini");

}