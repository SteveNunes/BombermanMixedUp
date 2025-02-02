package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetStageAsClear extends FrameTag {

	public SetStageAsClear(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public SetStageAsClear getNewInstanceOfThis() {
		return new SetStageAsClear(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.setStageAsClear();
	}

}