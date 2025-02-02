package frameset_tags;

import frameset.Sprite;
import tools.Sound;

public class StopAllMp3s extends FrameTag {

	public StopAllMp3s(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public StopAllMp3s getNewInstanceOfThis() {
		return new StopAllMp3s(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Sound.stopAllMp3s();
	}

}