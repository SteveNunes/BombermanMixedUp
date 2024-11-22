package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetStageAsClear extends FrameTag {

	public String tags;
	
	public SetStageAsClear() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public SetStageAsClear(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public SetStageAsClear getNewInstanceOfThis() {
		return new SetStageAsClear(tags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.setStageAsClear();
	}

}