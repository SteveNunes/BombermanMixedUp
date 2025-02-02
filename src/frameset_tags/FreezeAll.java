package frameset_tags;

import application.Main;
import frameset.Sprite;

public class FreezeAll extends FrameTag {

	public FreezeAll(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public FreezeAll getNewInstanceOfThis() {
		return new FreezeAll(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Main.freezeAll();
	}

}