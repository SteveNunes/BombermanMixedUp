package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetStageObjectiveAsClear extends FrameTag {

	public SetStageObjectiveAsClear(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public SetStageObjectiveAsClear getNewInstanceOfThis() {
		return new SetStageObjectiveAsClear(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.setStageObjectiveAsClear();
	}

}