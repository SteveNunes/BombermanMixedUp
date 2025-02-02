package frameset_tags;

import frameset.Sprite;
import tools.Sound;

public class StopAllWaves extends FrameTag {

	public StopAllWaves(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public StopAllWaves getNewInstanceOfThis() {
		return new StopAllWaves(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Sound.stopAllWaves();
	}

}