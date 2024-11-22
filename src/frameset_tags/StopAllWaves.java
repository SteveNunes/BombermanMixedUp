package frameset_tags;

import frameset.Sprite;
import tools.Sound;

public class StopAllWaves extends FrameTag {

	public String tags;
	
	public StopAllWaves() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public StopAllWaves(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public StopAllWaves getNewInstanceOfThis() {
		return new StopAllWaves(tags);
	}

	@Override
	public void process(Sprite sprite) {
		Sound.stopAllWaves();
	}

}