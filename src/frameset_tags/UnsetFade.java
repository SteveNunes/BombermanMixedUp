package frameset_tags;

import frameset.Sprite;
import tools.Draw;

public class UnsetFade extends FrameTag {

	public UnsetFade(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public UnsetFade getNewInstanceOfThis() {
		return new UnsetFade(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Draw.setFade(null);
	}

}