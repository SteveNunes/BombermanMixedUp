package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetMapFrameSet extends FrameTag {

	public String frameSetName;

	public SetMapFrameSet(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		frameSetName = params[0];
	}

	@Override
	public SetMapFrameSet getNewInstanceOfThis() {
		return new SetMapFrameSet(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.mapFrameSets.setFrameSet(frameSetName);
	}

}
