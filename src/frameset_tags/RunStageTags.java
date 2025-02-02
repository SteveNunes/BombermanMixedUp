package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class RunStageTags extends FrameTag {

	public String stageTagsName;

	public RunStageTags(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		stageTagsName = params[0];
	}

	@Override
	public RunStageTags getNewInstanceOfThis() {
		return new RunStageTags(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.runStageTag(stageTagsName);
	}

}
