package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class SoftResetAllBomberMansAfterMapChange extends FrameTag {

	public String tags;
	
	public SoftResetAllBomberMansAfterMapChange() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public SoftResetAllBomberMansAfterMapChange(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public SoftResetAllBomberMansAfterMapChange getNewInstanceOfThis() {
		return new SoftResetAllBomberMansAfterMapChange(tags);
	}

	@Override
	public void process(Sprite sprite) {
		BomberMan.softResetAllBomberMansAfterMapChange();
	}

}