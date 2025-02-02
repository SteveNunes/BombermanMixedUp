package frameset_tags;

import frameset.Sprite;

public class UnsetGhosting extends FrameTag {

	public UnsetGhosting(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public UnsetGhosting getNewInstanceOfThis() {
		return new UnsetGhosting(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().unsetGhosting();
	}

}