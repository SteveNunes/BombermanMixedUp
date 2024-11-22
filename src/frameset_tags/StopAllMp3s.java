package frameset_tags;

import frameset.Sprite;
import tools.Sound;

public class StopAllMp3s extends FrameTag {

	public String tags;
	
	public StopAllMp3s() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public StopAllMp3s(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public StopAllMp3s getNewInstanceOfThis() {
		return new StopAllMp3s(tags);
	}

	@Override
	public void process(Sprite sprite) {
		Sound.stopAllMp3s();
	}

}