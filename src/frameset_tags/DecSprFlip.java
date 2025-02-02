package frameset_tags;

import frameset.Sprite;

public class DecSprFlip extends FrameTag {

	public DecSprFlip(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public DecSprFlip getNewInstanceOfThis() {
		return new DecSprFlip(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setFlip(sprite.getFlip().getPreview());
	}

}
