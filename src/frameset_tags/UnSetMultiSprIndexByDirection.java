package frameset_tags;

import frameset.Sprite;

public class UnSetMultiSprIndexByDirection extends FrameTag {

	public UnSetMultiSprIndexByDirection(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public UnSetMultiSprIndexByDirection getNewInstanceOfThis() {
		return new UnSetMultiSprIndexByDirection(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.unsetMultiFrameIndexByDirection();
	}

}
