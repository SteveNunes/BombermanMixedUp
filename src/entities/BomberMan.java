package entities;

import tools.IniFiles;

public class BomberMan extends Entity {
	
	public BomberMan(int bomberIndex) {
		String section = "" + bomberIndex;
		for (String item : IniFiles.characters.getItemList(section)) {
			if (item.length() > 9 && item.substring(0, 9).equals("FrameSet."))
				addNewFrameSetFromString(item.substring(9), IniFiles.characters.read(section, item));
		}
		setFrameSet("Stand");
	}
	
}
