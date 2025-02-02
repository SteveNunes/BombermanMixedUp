package frameset_tags;

import frameset.Sprite;

public class IncSprFlip extends FrameTag {

	public IncSprFlip(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public IncSprFlip getNewInstanceOfThis() {
		return new IncSprFlip(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setFlip(sprite.getFlip().getNext());
	}

}
