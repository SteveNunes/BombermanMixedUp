package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetMapFrameSet extends FrameTag {

	public String frameSetName;
	private String tags;

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + frameSetName + "}";
	}

	public SetMapFrameSet(String tags) {
		this.tags = tags;
		String[] params = validateStringTags(this, tags, 1);
		frameSetName = params[0];
	}

	@Override
	public SetMapFrameSet getNewInstanceOfThis() {
		return new SetMapFrameSet(tags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.mapFrameSets.setFrameSet(frameSetName);
	}

}
