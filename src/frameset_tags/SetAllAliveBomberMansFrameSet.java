package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class SetAllAliveBomberMansFrameSet extends FrameTag {

	public String frameSet;
	public String originalTags;

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + frameSet + "}";
	}

	public SetAllAliveBomberMansFrameSet(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		originalTags = tags;
		frameSet = params[0];
	}

	@Override
	public SetAllAliveBomberMansFrameSet getNewInstanceOfThis() {
		return new SetAllAliveBomberMansFrameSet(originalTags);
	}

	@Override
	public void process(Sprite sprite) {
		BomberMan.setAllAliveBomberMansFrameSet(frameSet);
	}

}
