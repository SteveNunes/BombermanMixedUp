package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class SoftResetAllBomberMansAfterMapChange extends FrameTag {

	public SoftResetAllBomberMansAfterMapChange(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public SoftResetAllBomberMansAfterMapChange getNewInstanceOfThis() {
		return new SoftResetAllBomberMansAfterMapChange(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		BomberMan.softResetAllBomberMansAfterMapChange();
	}

}