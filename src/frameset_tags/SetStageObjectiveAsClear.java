package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetStageObjectiveAsClear extends FrameTag {

	public String tags;
	
	public SetStageObjectiveAsClear() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public SetStageObjectiveAsClear(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public SetStageObjectiveAsClear getNewInstanceOfThis() {
		return new SetStageObjectiveAsClear(tags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.setStageObjectiveAsClear();
	}

}