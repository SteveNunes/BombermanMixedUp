package frameset_tags;

import application.Main;
import frameset.Sprite;

public class UnFreezeAll extends FrameTag {

	public UnFreezeAll(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public UnFreezeAll getNewInstanceOfThis() {
		return new UnFreezeAll(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Main.unFreezeAll();
	}

}